package jetzt.abfackeln;


import org.springframework.web.util.HtmlUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Commit {
    Integer revision;
    String author;
    String date;
    String message;
    String diff;

    public Commit() { }

    public Commit(Integer revision, String author, String date, String message){
        this.revision = revision;
        this.author = author;
        this.date = date;
        this.message = message;
    }

    public Commit(Integer revision, String author, String date, String message,String diff) {
        this(revision, author, date, message);
        this.diff = diff;
    }


    @Override
    public String toString() {
        return "r"+(revision!=null?revision:false)+": message:"+(message!=null||message!="")+" by "+(author!=null?author:false)+" at "+date+" with diff: "+(diff!=null);
    }


}
