package controllers;

import de.l3s.boilerpipe.extractors.ArticleExtractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for extracting article plain text
 *
 * Created by ellenlouie on 12/20/15.
 */
public class TextExtraction {

    public String getUnclassifiedPlainText(String link) {
        String article = "";

        try {
            URL url = new URL(link);

            article = ArticleExtractor.INSTANCE.getText(url);

            //need to replace for Weka .arff file compatibility
            article = article.replace("'", "’");
            article = article.replace("\n", " ");

        } catch(Exception e) {
            e.printStackTrace();
        }
        //format text for Weka .arff file
        return "'" + article + "',?\n";
    }

    public String getClassifiedPlainText(String link, int classAttr) {
        String article = "";

        try {
            URL url = new URL(link);

            article = ArticleExtractor.INSTANCE.getText(url);

            //need to replace for Weka .arff file compatibility
            article = article.replace("'", "’");
            article = article.replace("\n", " ");

        } catch(Exception e) {
            e.printStackTrace();
        }
        //format text for Weka .arff filed
        return "'" + article + "'," + classAttr + "\n";
    }

    public List getKeywords(String link) {
        String s;
        List<String> keywords = new ArrayList<>();

        try {
            Process p = Runtime.getRuntime().exec("python newspaperkeywords.py " + link);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((s = stdInput.readLine()) != null) {
                keywords.add(s);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return keywords;
    }
}
