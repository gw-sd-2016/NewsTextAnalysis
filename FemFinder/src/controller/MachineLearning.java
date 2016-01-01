package controller;

import java.io.*;

/**
 * Created by ellenlouie on 12/31/15.
 */
public class MachineLearning {

    public void createArffFile() {

        Writer writer = null;
        String arffFileHeader = "@relation women\n\n@attribute article string\n@attribute class-attr {0,1}\n\n@data\n\n";

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("newsfeed.arff"), "UTF-8"));
            writer.write(arffFileHeader);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addArticleToFile(String article) {
        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new FileWriter("newsfeed.arff", true));
            out.write(article + "\n\n");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

    }
}
