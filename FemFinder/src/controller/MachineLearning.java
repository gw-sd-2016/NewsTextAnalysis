package controller;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

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

            writer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void addArticleToFile(String article) {
        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new FileWriter("newsfeed.arff", true));
            out.write(article);

            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //TODO: possibly change to a return list of all the articles that are related to women OR have a different function do that
    public void classifyArticles(File file) {

        BufferedReader in = null;
        Instances stdTrain = null;
        Instances stdTest = null;

        try {
            //read in both files
            in = new BufferedReader(new FileReader("women-train.arff"));
            Instances train = new Instances(in);

            in = new BufferedReader(new FileReader("newsfeed.arff"));
            Instances test = new Instances(in);

            in.close();

            //batch filtering with StringToWordVector
            StringToWordVector filter = new StringToWordVector();
            //initialize filter once with training set
            filter.setInputFormat(train);

            //configures filter based on training set and returns word vector & create new test set
            stdTrain = Filter.useFilter(train, filter);
            stdTest = Filter.useFilter(test, filter);

        } catch(Exception e) {
            e.printStackTrace();
        }

        //set class attributes for datasets
        //it is in the first place as opposed to the usual last because of the StringToWordVector filter
        stdTrain.setClassIndex(0);
        stdTest.setClassIndex(0);


    }
}
