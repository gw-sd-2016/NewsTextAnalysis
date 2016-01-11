package controller;

import de.l3s.boilerpipe.extractors.ArticleExtractor;

import java.net.URL;

/**
 * Class for extracting article plain text
 *
 * Created by ellenlouie on 12/20/15.
 */
public class TextExtraction {

    public String getPlainText(String link) {
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
        return "'" + article + "',?\n";
    }
}
