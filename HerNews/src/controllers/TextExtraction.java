package controllers;

import de.l3s.boilerpipe.extractors.ArticleExtractor;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

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

        } catch(Exception e) {
            e.printStackTrace();
        }
        return article;
    }

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

    public List<String> getLocations(String article) {
        LinkedHashMap <String, LinkedHashSet<String>> results = new LinkedHashMap<>();
        String serializedClassifier = "english.all.3class.distsim.crf.ser.gz";

        try {
            //deserialize classifier with 3 fields: people, locations, organizations
            CRFClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);
            List<List<CoreLabel>> classify = classifier.classify(article);

            for (List<CoreLabel> coreLabels : classify) {
                for (CoreLabel coreLabel : coreLabels) {
                    //person, location, or organization word found
                    String word = coreLabel.word();
                    //label for the category
                    String category = coreLabel.get(CoreAnnotations.AnswerAnnotation.class);
                    if(!"O".equals(category)) {
                        if(results.containsKey(category)) {
                            //category already exists, just insert word
                            results.get(category).add(word);
                        } else {
                            //category doesn't exist, add category and word
                            LinkedHashSet<String> temp = new LinkedHashSet<>();
                            temp.add(word);
                            results.put(category, temp);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //get rid of people and organizations, just keep locations
        LinkedHashSet<String> location = results.get("LOCATION");
        List<String> locations = new ArrayList<String>(location);
        return locations;
    }
}
