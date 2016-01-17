import com.sun.deploy.ui.ProgressDialog;
import controller.MachineLearning;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import model.Article;
import model.Feed;
import controller.FeedParser;
import controller.TextExtraction;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

        VBox feedInfo = new VBox();
        VBox articles = new VBox();

        //hold articles
        ScrollPane articleContainer = new ScrollPane();
        articleContainer.setContent(articles);

        //hold feed info and articles
        VBox rssFeed = new VBox();
        rssFeed.getChildren().addAll(feedInfo, articleContainer);

        filter.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.NONE);

                    //get the rss feed url from the text area
                    String rssUrl = feedUrl.getText();

                    //create a new FeedParser to parse feed
                    FeedParser parser = new FeedParser(rssUrl);
                    Feed feed = parser.parseFeed();

                    Task<List<Article>> task = new Task<List<Article>>() {
                        @Override
                        protected List<Article> call() throws Exception {
                            //create new arff file to add articles
                            MachineLearning ml = new MachineLearning();
                            ml.createArffFile();

                            for (Article article : feed.getArticles()) {
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
                            List<Article> womenArticles = new ArrayList<>();

                            //filter out articles unrelated to women
                            if (classAttr.size() == feed.getArticles().size()) {
                                for (int i = 0; i < classAttr.size(); i++) {
                                    if (classAttr.get(i) == 1) {
                                        womenArticles.add(feed.getArticles().get(i));
                                    }
                                }
                            }
                            return womenArticles;
                        }
                    };

                    task.setOnSucceeded(event -> {
                        alert.close();

                        //add feed information to ui
                        Label feedTitle = new Label(feed.getTitle());
                        Label feedLink = new Label(feed.getLink());
                        Label feedDescription = new Label(feed.getDescription());
                        Label feedCopyright = new Label(feed.getCopyright());
                        Label feedPubDate = new Label(feed.getPubDate());
                        feedInfo.getChildren().addAll(new Separator(), feedTitle, feedLink, feedDescription, feedCopyright, feedPubDate, new Separator());

                        List<Article> womenArticles = task.getValue();

                        for (Article article : womenArticles) {
                            Label articleTitle = new Label(article.getTitle());
                            Label articlePubDate = new Label(article.getPubDate());
                            Label articleLink = new Label(article.getLink());

                            articles.getChildren().addAll(articleTitle, articlePubDate, articleLink);
                        }
                    });

                    ProgressBar progressBar = new ProgressBar();
                    alert.setTitle("Busy");
                    alert.setHeaderText("Processing...");
                    alert.setContentText("Analyzing articles...");
                    alert.setGraphic(progressBar);
                    alert.getButtonTypes().add(ButtonType.CLOSE);
                    alert.show();

                    Thread thread = new Thread(task);
                    thread.start();
                }
        );

        //add all elements to border pane
        BorderPane pane = new BorderPane();
        pane.setTop(inputContainer);
        pane.setCenter(rssFeed);

        Scene scene = new Scene(pane, 400, 500);
        scene.getStylesheets().add("FFStylesheet.css");
        primaryStage.setTitle("FemFinder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
