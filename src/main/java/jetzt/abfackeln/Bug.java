package jetzt.abfackeln;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.rmi.CORBA.Util;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Bug {

    private static final Logger log = LoggerFactory.getLogger(Bug.class);
    enum VISIBILITY{
        PUBLIC,
        PRIVATE,
        UNKNOWN
    }

    private static final String URL_PATTERN = "https://bugs.webkit.org/show_bug.cgi?id=";
    public String url = "";
    public int id;
    public VISIBILITY status = VISIBILITY.UNKNOWN;
    public int commitID;

    public Bug(String url, Commit c){
        this.url = url;
        try {
            this.id = new Integer(this.url.substring(URL_PATTERN.length()));
        }catch(NumberFormatException e){
            log.info(Utils.exceptionToString(e));
            log.info(this.url);
        }
        this.commitID = c.revision;
    }

    public Bug(int id, String status, int commitID){
        this.id = id;
        this.url = URL_PATTERN +id;
        this.commitID = commitID;
        this.status = VISIBILITY.valueOf(status);
    }

    public VISIBILITY getVisibility(){
        if(status.equals(VISIBILITY.UNKNOWN)){
            log.error("Testing visibility of Bug "+this.id);
            testVisibility();
        }
        return status;
    }

    public void testVisibility(){
        try {
            URL url = new URL(this.url);
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader breader = new BufferedReader(new InputStreamReader(is));

            StringBuilder builder = new StringBuilder();
            String line = "";
            while((line = breader.readLine())!=null){
                builder.append(line);
            }
            String response = builder.toString();

            if(response.contains("You are not authorized to access bug")){
                status = VISIBILITY.PRIVATE;
            }else{
                status = VISIBILITY.PUBLIC;
            }
            SQLManager.updateBug(this);
        }catch (Exception e){
            log.error(Utils.exceptionToString(e));
        }
    }

}
