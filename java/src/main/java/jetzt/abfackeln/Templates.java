package jetzt.abfackeln;

public class Templates {

    public static String diff = "<!doctype html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "\t<meta charset=\"utf-8\">\n" +
            "\t<title>Diff</title>\n" +
            "\t<style>\n" +
            "\tbody {\n" +
            "\t\ttext-align: center;\n" +
            "\t\tfont-family: \"Bitstream Vera Sans Mono\", Courier, monospace;\n" +
            "\t}\n" +
            "\t#wrapper {\n" +
            "\t\tdisplay: inline-block;\n" +
            "\t\tmargin-top: 1em;\n" +
            "\t\tmin-width: 800px;\n" +
            "\t\twidth: 80%;\n" +
            "\t\ttext-align: left;\n" +
            "\t}\n" +
            "\th2 {\n" +
            "\t\tbackground: #fafafa;\n" +
            "\t\tbackground: -moz-linear-gradient(#fafafa, #eaeaea);\n" +
            "\t\tbackground: -webkit-linear-gradient(#fafafa, #eaeaea);\n" +
            "\t\t-ms-filter: \"progid:DXImageTransform.Microsoft.gradient(startColorstr='#fafafa',endColorstr='#eaeaea')\";\n" +
            "\t\tborder: 1px solid #d8d8d8;\n" +
            "\t\tborder-bottom: 0;\n" +
            "\t\tcolor: #555;\n" +
            "\t\tfont: 14px sans-serif;\n" +
            "\t\tpadding: 10px 6px;\n" +
            "\t\ttext-shadow: 0 1px 0 white;\n" +
            "\t\tmargin: 0;\n" +
            "\t}\n" +
            "\t.diff {\n" +
            "\t\tborder: 1px solid #d8d8d8;\n" +
            "\t}\n" +
            "\t.diff.div {\n" +
            "\t\tpadding: 0.5em;\n" +
            "\t}\n" +
            "\t.file-diff {\n" +
            "\t\tborder: 1px solid #d8d8d8;\n" +
            "\t\tmargin-bottom: 1em;\n" +
            "\t\toverflow: auto;\n" +
            "\t\tpadding: 0.5em 0;\n" +
            "\t}\n" +
            "\t.file-diff > div {\n" +
            "\t\twidth: 100%:\n" +
            "\t}\n" +
            "\tpre {\n" +
            "\t\tmargin: 0;\n" +
            "\t\tfont-size: 12px;\n" +
            "\t\tline-height: 1.4em;\n" +
            "\t\ttext-indent: 0.5em;\n" +
            "\t}\n" +
            "\t.commit {\n" +
            "\t\tmargin-top: 0.4em;\n" +
            "\t\tfont-size: 16px;\n" +
            "\t\tline-height: 1.0em;\n" +
            "\t\ttext-indent: 1.0em;\n" +
            "\t}\n" +
            "\t.navigation-button {\n"+
            "\tpadding:  14px;   \n" +
            "\tmargin-top: 8px;   \n" +
            "\tpadding-bottom: 8px;   \n" +
            "\tposition: relative;\n"+
            "\tdisplay: inline-block;\n"+
            "\tcursor: pointer;\n" +
            "\toutline: none;\n" +
            "\tborder: none; \n" +
            "\tbackground-color: #eeeeee;\n"+
            "\ttext-shadow: #ffffff 0 1px 0; \n" +
            "\ttext-align: center;  \n" +
            "\ttext-decoration: none;\n"+
            "\t}\n"+
            "\t.file {\n" +
            "\t\tcolor: #aaa;\n" +
            "\t}\n" +
            "\t.delete {\n" +
            "\t\tbackground-color: #fdd;\n" +
            "\t}\n" +
            "\t.insert {\n" +
            "\t\tbackground-color: #dfd;\n" +
            "\t}\n" +
            "\t.info {\n" +
            "\t\tcolor: #a0b;\n" +
            "\t}\n" +
            "\t</style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<div id=\"wrapper\">\n" +
            "{{diff}}\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>\n";

    public static String bug = "<!doctype html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "\t<meta charset=\"utf-8\">\n" +
            "\t<title>Bug</title>\n" +
            "\t<style>\n" +
            "\tbody {\n" +
            "\t\ttext-align: center;\n" +
            "\t\tfont-family: \"Bitstream Vera Sans Mono\", Courier, monospace;\n" +
            "\t}\n" +
            "\t#wrapper {\n" +
            "\t\tdisplay: inline-block;\n" +
            "\t\tmargin-top: 1em;\n" +
            "\t\tmin-width: 800px;\n" +
            "\t\twidth: 80%;\n" +
            "\t\ttext-align: left;\n" +
            "\t}\n" +
            "\th2 {\n" +
            "\t\tborder: 1px solid #d8d8d8;\n" +
            "\t\tborder-bottom: 0;\n" +
            "\t\tcolor: #555;\n" +
            "\t\tfont: 14px sans-serif;\n" +
            "\t\tpadding: 10px 6px;\n" +
            "\t\ttext-shadow: 0 1px 0 white;\n" +
            "\t\tmargin: 0;\n" +
            "\t}\n" +
            "\t.diff {\n" +
            "\t\tborder: 1px solid #d8d8d8;\n" +
            "\t}\n" +
            "\t.diff.div {\n" +
            "\t\tpadding: 0.5em;\n" +
            "\t}\n" +
            "\t.file-diff {\n" +
            "\t\tborder: 1px solid #d8d8d8;\n" +
            "\t\tmargin-bottom: 1em;\n" +
            "\t\toverflow: auto;\n" +
            "\t\tpadding: 0.5em 0;\n" +
            "\t}\n" +
            "\t.file-diff > div {\n" +
            "\t\twidth: 100%:\n" +
            "\t}\n" +
            "\tpre {\n" +
            "\t\tmargin: 0;\n" +
            "\t\tfont-size: 12px;\n" +
            "\t\tline-height: 1.4em;\n" +
            "\t\ttext-indent: 0.5em;\n" +
            "\t}\n" +
            "\t.commit {\n" +
            "\t\tmargin-top: 0.4em;\n" +
            "\t\tfont-size: 16px;\n" +
            "\t\tline-height: 1.0em;\n" +
            "\t\ttext-indent: 1.0em;\n" +
            "\t}\n" +
            "\t.navigation-button {\n"+
            "\tpadding:  14px;   \n" +
            "\tmargin-top: 8px;   \n" +
            "\tpadding-bottom: 8px;   \n" +
            "\tposition: relative;\n"+
            "\tdisplay: inline-block;\n"+
            "\tcursor: pointer;\n" +
            "\toutline: none;\n" +
            "\tborder: none; \n" +
            "\tbackground-color: #eeeeee;\n"+
            "\ttext-shadow: #ffffff 0 1px 0; \n" +
            "\ttext-align: center;  \n" +
            "\ttext-decoration: none;\n"+
            "\t}\n"+
            "\t.file {\n" +
            "\t\tbackground-color: #aaa;\n" +
            "\t}\n" +
            "\t.red {\n" +
            "\t\tbackground-color: #fdd;\n" +
            "\t}\n" +
            "\t.green {\n" +
            "\t\tbackground-color: #dfd;\n" +
            "\t}\n" +
            "\t.blue {\n" +
            "\t\tcolor: #a0f;\n" +
            "\t}\n" +
            "\t</style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<div id=\"wrapper\">\n" +
            "{{bug}}\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>\n";
}
