package controller;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.CSVSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.*;
import java.util.ArrayList;

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

    public void addArticleToFile(String file, String article) {
        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new FileWriter(file, true));
            out.write(article);

            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public File classifyArticles() {

        BufferedReader in = null;
        Instances stdTrain = null;
        Instances stdTest = null;
        Instances labeled = null;
        File labeledArticles = new File("labeledarticles.csv");

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

            //set class attributes for datasets
            //it is in the first place as opposed to the usual last because of the StringToWordVector filter
            stdTrain.setClassIndex(0);
            stdTest.setClassIndex(0);

            J48 tree = new J48();
            tree.buildClassifier(stdTrain);
            labeled = new Instances(stdTest);

            //label article instances
            for(int i = 0; i < stdTest.numInstances(); i++) {
                //classify each instance and set the class attribute
                double classLabel = tree.classifyInstance(stdTest.instance(i));
                labeled.instance(i).setClassValue(classLabel);
            }

            //save labeled articles to CSV file
            CSVSaver saver = new CSVSaver();
            saver.setInstances(labeled);
            saver.setFile(labeledArticles);
            saver.writeBatch();

        } catch(Exception e) {
            e.printStackTrace();
        }
        return labeledArticles;
    }

    public ArrayList<Integer> extractClassAttributes(File file) {

        BufferedReader in = null;
        String line = "";
        String splitBy = ",";
        ArrayList<Integer> classAttributes = new ArrayList<>();

        try {
            //read in csv file
            in = new BufferedReader(new FileReader(file));

            //skip the first line that consists of the mappings for each attribute
            in.readLine();

            while((line = in.readLine()) != null) {
                String[] article = line.split(splitBy);
                //change back to int after being split as a string
                classAttributes.add(Integer.parseInt(article[0]));
            }

            in.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return classAttributes;
    }
}
