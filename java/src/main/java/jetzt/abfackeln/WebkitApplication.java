package jetzt.abfackeln;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import sun.nio.cs.UTF_32LE;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@EnableAutoConfiguration
public class WebkitApplication {

    private static final String SVNDIR = "svn";
    private static final String WEBKITURL ="https://svn.webkit.org/repository/webkit/trunk";
    static SVNManager manager;
    private static final Logger log = LoggerFactory.getLogger(WebkitApplication.class);

    @RequestMapping({"/commit/{rev}"})
    @ResponseBody
    String commit(HttpServletRequest request,@PathVariable("rev") int rev){
        StringBuilder commitHTMLBuilder = new StringBuilder();
        List<Commit> commits = SQLManager.queryCommits("SELECT * FROM commits WHERE revision = "+rev);

        if(rev != 0)
            commitHTMLBuilder.append("<a href="+(rev-1)+" class='navigation-button' style='border-left: 3px solid #d84a38;left:0px;'>Previous</a>");
        if(rev <= manager.getLastSQLKnown().revision)
            commitHTMLBuilder.append("<a href="+(rev+1)+" class='navigation-button' style='border-right: 3px solid #384ad8;right:0px;'>Next</a>");
        commitHTMLBuilder.append("<br><br>");

        if(commits.size() == 0){
            commitHTMLBuilder.append("<h1>404!</h1>\n");
        }
        else {
            Commit c = commits.get(0);
            commitHTMLBuilder.append("<h2 style='border: 1px solid #d8d8d8; decoration: none;'><a href=../diff/"+c.revision+">diff: "+c.revision+"</a></h2>\n");
            commitHTMLBuilder.append(getCommitHtml(c));
        }
        return Templates.bug.replace("{{bug}}",commitHTMLBuilder.toString());
    }

    @RequestMapping({"/", "/bug", "/diff", ""})
    @ResponseBody
    String root(HttpServletRequest request,
                @RequestParam(required = false, defaultValue = "", value="date") String date,
                @RequestParam(required = false, defaultValue = "false", value="private") String priv,
                @RequestParam(required = false, defaultValue = "50", value="limit") String lim){
        StringBuilder rootHTMLBuilder = new StringBuilder();
        try {
            Date d  = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(date);
            boolean priva = priv.equals("true");
            Integer limit = new Integer(lim);
            String dateString = new SimpleDateFormat("yyyy-MM-dd").format(d);
            List<Bug> bugs = SQLManager.queryBugs("SELECT  b.* FROM commits AS c JOIN bugs AS b ON c.revision = b.commit WHERE c.date > '"
                    +dateString+"' "+(priva?"AND b.state = 'PRIVATE'":"")+" ORDER BY c.date ASC LIMIT "+limit);

            int BugQueue = BugStateRunner.getQueueLength();
            String queue = "";
            if(BugQueue != 0){
                queue = " ("+BugQueue+" Bugs not yet tested)";
            }

            if(bugs.size() == 0){
                rootHTMLBuilder.append("<h1>No bugs found in the Database. Maybe you should come back later"+queue+"</h1>\n");
            }
            else {
                rootHTMLBuilder.append("<h1> Showing up to " + limit + " Bugs - beginning at " + dateString + queue +"</h1>\n");
        }
            for(Bug b:bugs){
                String type = "background-color: #aaa;";
                switch (b.status){
                    case PRIVATE:
                        type = "background-color: #fdd;";
                        break;
                    case PUBLIC:
                        type = "background-color: #dfd;";
                        break;
                    case UNKNOWN:
                        type = "background-color: #ddf;";
                        break;
                }
                rootHTMLBuilder.append("<div>\n");
                rootHTMLBuilder.append("<a href=../bug/"+b.id+">");
                rootHTMLBuilder.append("<h2 style='"+type+"'>Bugid: "+b.id+" | State: "+b.status.name()+" | Mentioned in commit: "+b.commitID+"</h2>");
                rootHTMLBuilder.append("</a>\n");
                rootHTMLBuilder.append("</div>\n");
            }
            rootHTMLBuilder.append("<br><br>");
        }catch (Exception e){
            rootHTMLBuilder.append("<div><p>\n");
            rootHTMLBuilder.append("Please provide a <i>valid</i> date since when you want to search for bugs in the url.<br>\n");
            rootHTMLBuilder.append("eg: <a href='/?date=27.06.2016'>http://webkit.abfackeln.jetzt/?date=27.06.2016</a>.<br>\n");
            rootHTMLBuilder.append("use &private=true to search only for non public bugs<br>\n");
            rootHTMLBuilder.append("use &limit=100 to limit the search to 100 entries. default 50.<br>\n");
            rootHTMLBuilder.append("</p></div>");
            log.info(Utils.exceptionToString(e));
        }

        return Templates.bug.replace("{{bug}}",rootHTMLBuilder.toString());

    }
    private String getCommitHtml(Commit c){
        StringBuilder commitHTMLBuilder = new StringBuilder();
        commitHTMLBuilder.append("<div class='commit'>\n");
        commitHTMLBuilder.append("<h2>r"+c.revision+" | "+c.author+" | "+c.date+"</h2>\n");
        commitHTMLBuilder.append("<div class='diff'>\n");
        String escapedMessage = HtmlUtils.htmlEscape(c.message);

        Pattern folderPattern = Pattern.compile("^[A-Z][A-Za-z0-9/]+:");
        Pattern pathPattern = Pattern.compile("^\\* [A-Za-z0-9/\\.\\-]+:");

        String lastFolder = "";
        for(String line : escapedMessage.split("\r?\n")){
            Matcher folderMatcher = folderPattern.matcher(line);
            Matcher pathMatcher = pathPattern.matcher(line);


            while(folderMatcher.find()){
                lastFolder = folderMatcher.group().substring(0,folderMatcher.group().length()-1);
            }

            while(pathMatcher.find()){
                String match = pathMatcher.group();
                line = "* <a href='../svn/"+lastFolder+"/"
                       +match.substring(2,match.length()-1)+"'>"+match.substring(2,match.length()-1)
                        +"</a>"+line.substring(match.length())+":";
            }

            if(line.contains("https://bugs.webkit.org/show_bug.cgi?id="))
                line = "<a href='"+line+"'>"+line+"</a>";

            commitHTMLBuilder.append("<pre class='commit'>"+line.trim()+"</pre>");
            commitHTMLBuilder.append("<br>\n");
        }
        commitHTMLBuilder.append("</div>\n");
        commitHTMLBuilder.append("</div><br>\n");
        return commitHTMLBuilder.toString();
    }

    @RequestMapping({"/"+SVNDIR,"/"+SVNDIR+"/**"})
    public ResponseEntity<String> file(HttpServletRequest request,
                                       @RequestParam(required = false, defaultValue = "false", value="html") String isHTML) throws IOException {
        StringBuilder response = new StringBuilder();
        HttpHeaders responseHeaders = new HttpHeaders();

        File file = new File("."+request.getRequestURI());
        if(!file.exists()||file.getName().equals(".svn")){
            response.append("404");
        }
        else if(file.isDirectory()){
            StringBuilder builder = new StringBuilder();
            for (final File fileEntry : file.listFiles()) {
                String fileName = fileEntry.getName();
                if(fileName.equals(".svn")){
                    continue;
                }
                if (fileEntry.isDirectory()) {
                    builder.append("Folder: <a href="+fileName+"?html="+isHTML+"/>"+fileEntry.getName()+"</a><br>");
                } else {
                    builder.append("File: <a href="+fileName+"?html="+isHTML+"/>"+fileEntry.getName()+"</a><br>");
                }
            }

            responseHeaders.setContentType(MediaType.TEXT_HTML);
            response.append(builder.toString());

        }else{
            response.append(Utils.readFile(file.getAbsolutePath(), Charset.defaultCharset()));
            responseHeaders.setContentType(isHTML.equals("true")?MediaType.TEXT_HTML:MediaType.TEXT_PLAIN);
        }
        return new ResponseEntity<String>(response.toString(), responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping({"/bug/{id}"})
    @ResponseBody
    String bug(HttpServletRequest request, @PathVariable("id") int id) throws IOException {
        StringBuilder bugHTMLBuilder = new StringBuilder();


        List<Bug> smaller = SQLManager.queryBugs("SELECT * FROM bugs WHERE id < "+id+" ORDER BY id DESC LIMIT 1");
        List<Bug> bigger = SQLManager.queryBugs("SELECT * FROM bugs WHERE id > "+id+" ORDER BY id ASC LIMIT 1");
        bugHTMLBuilder.append("<div>\n");
        if(smaller.size() != 0)
            bugHTMLBuilder.append("<a href='"+smaller.get(0).id+"' class='navigation-button' style='border-left: 3px solid #d84a38;left:0px;'>Previous</a>\n");
        if(bigger.size() != 0)
        bugHTMLBuilder.append("<a href='"+bigger.get(0).id+"' class='navigation-button' style='border-right: 3px solid #384ad8;right:0px;'>Next</a>\n");
        bugHTMLBuilder.append("</div><br><br>\n");

        List<Bug> bugs = SQLManager.queryBugs("SELECT * FROM bugs WHERE id = "+id);
        if(bugs.size() == 0){
            bugHTMLBuilder.append("<h1>404!</h1>\n");
        }
        else{
            Bug b = bugs.get(0);
            Commit c = SQLManager.queryCommits("SELECT * FROM commits WHERE revision = "+b.commitID).get(0);
            bugHTMLBuilder.append("<div class='commit'>\n");
            bugHTMLBuilder.append("<h2>"+b.id+"</h2>\n");
            bugHTMLBuilder.append("<div class='diff'>\n");
            bugHTMLBuilder.append("<pre class='commit'>Visibility: "+b.getVisibility().name()+"</pre>\n");
            bugHTMLBuilder.append("<pre class='commit'>Date: "+c.date+"</pre>\n");
            bugHTMLBuilder.append("<a href=../diff/"+c.revision+">");
            bugHTMLBuilder.append("<pre class='commit'>Commit: "+c.revision+"</pre></a>\n");

            bugHTMLBuilder.append("</div></div><br>\n");
            bugHTMLBuilder.append(getCommitHtml(c));

        }
        return Templates.bug.replace("{{bug}}",bugHTMLBuilder.toString());
    }

    @RequestMapping({"/diff/{rev}"})
    @ResponseBody
    String diff(HttpServletRequest request, @PathVariable("rev") int rev) throws IOException {
        StringBuilder diffHTMLBuilder = new StringBuilder();
        Commit c = manager.getDiff(rev);

        diffHTMLBuilder.append("<div>");
        if(rev != 0)
            diffHTMLBuilder.append("<a href="+(rev-1)+" class='navigation-button' style='border-left: 3px solid #d84a38;left:0px;'>Previous</a>");
        if(rev <= manager.getLastSQLKnown().revision)
            diffHTMLBuilder.append("<a href="+(rev+1)+" class='navigation-button' style='border-right: 3px solid #384ad8;right:0px;'>Next</a>");
        diffHTMLBuilder.append("</div><br><br>");

        if(c == null){
            diffHTMLBuilder.append("<h1>404!</h1>");
            return Templates.diff.replace("{{diff}}",diffHTMLBuilder.toString());
        }

        String diff = HtmlUtils.htmlEscape(c.diff);
        String[] diffList = diff.split("Index: (?=.*\n===================================================================)");

        diffHTMLBuilder.append(getCommitHtml(c));

        for(String d : diffList){

            if(d.equals(""))continue;

            String[] lines = d.split("\r?\n");
            diffHTMLBuilder.append("<h2>" + lines[0] + "</h2>\n");

            diffHTMLBuilder.append("<div class='diff'>\n");

            int i = 0;
            String type = "";
            for(i = 0; i < lines.length; i++) {
                if(lines[i].startsWith("---")||lines[i].startsWith("+++"))
                    diffHTMLBuilder.append("<pre class='file'>"+lines[i]+"</pre>\n");
                if(lines[i].startsWith("@@")) break;
            }
            for(; i < lines.length; i++) {
                type="";
                String line = lines[i];
                if(line.startsWith("@@")) type = "info";
                else if(line.startsWith("-")) type = "delete";
                else if(line.startsWith("+")) type = "insert";

                diffHTMLBuilder.append("<pre class='"+type+"'>"+line+"</pre>\n");

            }
            diffHTMLBuilder.append("</div>\n");
            diffHTMLBuilder.append("<br>\n");
        }

        return Templates.diff.replace("{{diff}}",diffHTMLBuilder.toString());
    }

    public static void main(String[] args) throws Exception {
        SQLManager.init();
        BugStateRunner.init();
        manager = new SVNManager(new File(SVNDIR).getAbsolutePath(), WEBKITURL);
        manager.update();
        SpringApplication.run(WebkitApplication.class, args);
    }
}