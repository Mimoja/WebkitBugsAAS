package jetzt.abfackeln;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;


public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    static String readFile(String path, Charset encoding) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    static String exec(String... args) throws IOException, InterruptedException {
        log.error("Executing: ");
        String cmd ="";
        for(String s : args)cmd+=s+" ";
            log.error(cmd);

        Process process = new ProcessBuilder(args).start();

        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        StringBuilder builder = new StringBuilder();
        String line = null;
        while(process.isAlive()){
            while ((line = br.readLine()) != null) {
                //log.debug("-> "+line);
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
        }

        return builder.toString();
    }


    static String exceptionToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
