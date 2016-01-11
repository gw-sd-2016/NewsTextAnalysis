import controller.MachineLearning;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import model.Article;
import model.Feed;
import controller.FeedParser;
import controller.TextExtraction;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import weka.filters.unsupervised.attribute.InterquartileRange;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ellenlouie on 11/29/15.
 */
public class FFGUI extends Application {

    @Override //Override the start method in the Application class
    public void start(final Stage primaryStage) {

        //input rss feed url
        Label inputFeed = new Label("RSS Feed: ");
        final TextField feedUrl = new TextField();
        feedUrl.setId("feedInputBox");
        Button filter = new Button("Filter");

        //hold all input elements
        HBox inputContainer = new HBox();
        inputContainer.setId("inputContainer");
        inputContainer.getChildren().addAll(inputFeed, feedUrl, filter);

        //TODO: create some kind of "processing..." modal to popup while this method processes
        filter.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //get the rss feed url from the text area
                        String rssUrl = feedUrl.getText();

                        //create a new FeedParser to parse feed
                        FeedParser parser = new FeedParser(rssUrl);
                        Feed feed = parser.parseFeed();

                        //create new arff file to add articles
                        MachineLearning ml = new MachineLearning();
                        ml.createArffFile();

                        //TODO: add feed AND ARTICLES to rssFeed VBox
                        //TODO: take out when complete
                        System.out.println(feed);
                        for(Article article : feed.getArticles()) {
                            TextExtraction te = new TextExtraction();
                            String plainText = te.getPlainText(article.getLink());

                            //append plain text to arff file for weka processing
                            ml.addArticleToFile(plainText);
                        }
                        //classify articles
                        File labeledArticles = ml.classifyArticles();

                        //extract out class attributes
                        ArrayList<Integer> classAttr = ml.extractClassAttributes(labeledArticles);

                        //create new list of articles that will hold articles related to women
                        List<Article> womenArticles = new ArrayList<Article>();

                        //filter out articles unrelated to women
                        if(classAttr.size() == feed.getArticles().size()) {
                            for(int i = 0; i < classAttr.size(); i++) {
                                if(classAttr.get(i) == 1) {
                                    womenArticles.add(feed.getArticles().get(i));
                                }
                            }
                        }

                        //TODO: take out when complete
                        for(Article article : womenArticles) {
                            System.out.println(article);
                        }
                    }
                }
        );

        //rss feed
        VBox rssFeed = new VBox();

        //hold rss feed
        ScrollPane feedContainer = new ScrollPane();
        feedContainer.setContent(rssFeed);

        //add all elements to border pane
        BorderPane pane = new BorderPane();
        pane.setTop(inputContainer);
        pane.setCenter(feedContainer);

        Scene scene = new Scene(pane, 400, 500);
        scene.getStylesheets().add("FFStylesheet.css");
        primaryStage.setTitle("FemFinder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
