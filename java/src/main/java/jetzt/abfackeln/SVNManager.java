package jetzt.abfackeln;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CompositeIterator;
import org.xml.sax.InputSource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SVNManager implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(SVNManager.class);
    public boolean isReady = false;
    public String workingDir = null;
    public String webkitURL = null;

    public SVNManager(String workingDir, String webkitURL){
        this.workingDir = workingDir;
        this.webkitURL = webkitURL;
    }

    public void update(){
        new Thread(this).start();
    }

    public Commit getDiff(int revision){
        Commit c = null;
        logger.info("diff requested: "+revision);
        try {
            List<Commit> cl = SQLManager.queryCommits("SELECT * FROM commits WHERE revision = "+revision);
            if(cl.size() == 0) return null;
            c = cl.get(0);
            if(c.diff == null || c.diff.equals("null") || c.diff.equals("")) {
                logger.debug("Adding new diff to "+revision);
                c.diff = Utils.exec("svn", "diff","--patch-compatible", "--git", "-c", String.valueOf(c.revision), workingDir);
                c.diff = c.diff.replace(workingDir, "");
                SQLManager.updateCommit(c);
            }
        } catch (Exception e) {
            logger.error("GetLog error: "+Utils.exceptionToString(e));
        }
        return c;
    }

    private List<Commit> parseString(String res){
        List<Commit> commitList = new LinkedList<>();
        try {
            InputSource is = new InputSource(new StringReader(res));
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader parser  = factory.createXMLStreamReader(is.getCharacterStream());
            Commit lastCommit = null;
            StringBuilder lastString = new StringBuilder();
            while ( parser.hasNext() )
            {
                switch ( parser.getEventType() )
                {
                    case XMLStreamConstants.START_DOCUMENT:
                        break;

                    case XMLStreamConstants.END_DOCUMENT:
                        parser.close();
                        break;

                    case XMLStreamConstants.NAMESPACE:
                        break;

                    case XMLStreamConstants.START_ELEMENT:
                        if(parser.getLocalName() == "logentry") {
                            lastCommit = new Commit();
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                if (parser.getAttributeLocalName(i) == "revision") {
                                    lastCommit.revision = new Integer(parser.getAttributeValue(i));
                                }
                            }
                        }
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        if (! parser.isWhiteSpace()) {
                            if(lastString.length() != 0){
                                lastString.append(System.getProperty("line.separator"));
                            }
                            lastString.append(parser.getText());

                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        if(parser.getLocalName() == "logentry") {
                            commitList.add(lastCommit);
                            logger.debug("Reading commit "+lastCommit.revision+" from svn");
                        }
                        else if(parser.getLocalName() == "msg") {
                            lastCommit.message = lastString.toString();
                            lastString = new StringBuilder();
                        }
                        else if(parser.getLocalName() == "author") {
                            lastCommit.author = lastString.toString();
                            lastString = new StringBuilder();
                        }
                        else if(parser.getLocalName() == "date") {
                            lastCommit.date = lastString.toString().replace(".000000Z","").replace("T", " ");
                            lastString = new StringBuilder();
                        }
                        break;

                    default:
                        break;
                }
                parser.next();
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("GetLog error: "+sw.toString());
        }
        return commitList;
    }

    private Commit getLog()  {
        try {
            String res = Utils.exec("svn", "log", "--limit", "1", "--xml", workingDir);
            return parseString(res).get(0);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("GetLog error: "+sw.toString());
        }
        return null;
    }

    public List<Commit> getLogCommits(int from, int to)  {
        List<Commit> commitList = null;
        try {
            String res = Utils.exec("svn", "log", "--xml", "-r", from+":"+to, workingDir);
            commitList = parseString(res);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("GetLog error: "+sw.toString());
        }
        return commitList;
    }


    public Commit getLastSQLKnown(){
        Commit lastSQLKnown;
        List<Commit> ret= SQLManager.queryCommits("SELECT * FROM commits ORDER BY revision DESC LIMIT 1");
        if(ret.size() == 0) {
            logger.error("Cannot read from pgsql");
            lastSQLKnown = new Commit(1, "", "", "", "");
        }else {
            lastSQLKnown = ret.get(0);
        }
        return lastSQLKnown;
    }

    @Override
    public void run() {
        try {
            logger.error("Starting SVN");
            File wcDir = new File(workingDir);
            if (wcDir.exists()) {
                isReady = true;
                logger.error("Using existing svn folder");
            }
            wcDir.mkdirs();

            String res;
            if(!isReady) {
                logger.debug("Checking out a working copy from '" + webkitURL + "'...");
                res = Utils.exec("svn", "checkout" , webkitURL , workingDir);
                logger.error(res);
                logger.error("Checkout finished");
            }else{
                logger.debug("Updating svn ...");
                res = Utils.exec("svn", "update", "svn");
                logger.error(res);
                logger.error("update finished");
            }

            Commit head = getLog();
            Commit lastSQLKnown = getLastSQLKnown();
            logger.info("Last sqlknown revision is r"+lastSQLKnown.revision);
            if(head.revision.equals(lastSQLKnown.revision)) return;
            logger.info("Updating from "+ lastSQLKnown.revision + " to " + head.revision );

            Pattern pattern = Pattern.compile("https://bugs.webkit.org/show_bug.cgi\\?id=[0-9]+");
            while(lastSQLKnown.revision < head.revision){
                logger.info("Last sqlknown revision is r"+lastSQLKnown.revision);
                int newHead = lastSQLKnown.revision + 500;
                if(newHead > head.revision) newHead = head.revision;
                for (Commit c : getLogCommits(lastSQLKnown.revision, newHead)) {
                    SQLManager.addCommit(c);

                    Matcher matcher = pattern.matcher(c.message);

                    while (matcher.find()) {
                        if(matcher.group().equals(""))
                            continue;
                        Bug b = new Bug(matcher.group(),c);
                        List<Bug> known = SQLManager.queryBugs("SELECT * FROM bugs WHERE id ="+b.id);
                        if(known.size() == 0) {
                            logger.info("Found bugentry "+b.id+" in "+b.commitID);
                            SQLManager.addBug(b);
                        }
                        BugStateRunner.addBug(b);
                    }
                }
                lastSQLKnown.revision = newHead;
            }
        } catch (Exception e) {
            logger.error("Exception: "+Utils.exceptionToString(e));
        }
    }
}
