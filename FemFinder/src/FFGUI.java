import controller.MachineLearning;
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
        feedInfo.setId("feedInfo");
        VBox articles = new VBox();

        //hold articles
        ScrollPane articleContainer = new ScrollPane();
        articleContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        articleContainer.setFitToWidth(true);
        articleContainer.setContent(articles);

        //hold feed info and articles
        VBox rssFeed = new VBox();
        rssFeed.getChildren().addAll(feedInfo, articleContainer);

        //hold user classification system
        VBox classifyContainer = new VBox();
        classifyContainer.setId("classifyContainer");

        Label classifyLabel1 = new Label("Is this article");
        Label classifyLabel2 = new Label("related to women?");
        classifyLabel1.getStyleClass().add("classifyLabels");
        classifyLabel2.getStyleClass().add("classifyLabels");

        Button updateModel = new Button("Update Model");

        HBox optionsContainer = new HBox();
        Label yes = new Label("Yes");
        Label no = new Label("No");
        yes.getStyleClass().add("options");
        no.getStyleClass().add("options");
        optionsContainer.getChildren().addAll(yes, no);

        filter.setOnAction(e1 -> {
                    feedInfo.getChildren().clear();
                    articles.getChildren().clear();

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

                    task.setOnSucceeded(e2 -> {
                        alert.close();

                        //add feed information to ui
                        Label feedTitle = new Label(feed.getTitle());
                        feedTitle.setId("titleText");
                        Hyperlink feedLink = new Hyperlink(feed.getLink());
                        Label feedDescription = new Label(feed.getDescription());
                        Label feedCopyright = new Label(feed.getCopyright());
                        Label feedPubDate = new Label(feed.getPubDate());

                        feedLink.setOnAction(e3 -> {
                            getHostServices().showDocument(feed.getLink());
                        });

                        feedInfo.getChildren().addAll(new Separator(), feedTitle, feedLink, feedDescription, feedCopyright, feedPubDate);

                        List<Article> womenArticles = task.getValue();

                        classifyContainer.getChildren().addAll(classifyLabel1, classifyLabel2, optionsContainer);

                        for (Article article : womenArticles) {
                            Label articleTitle = new Label(article.getTitle());
                            articleTitle.setId("titleText");
                            Label articlePubDate = new Label(article.getPubDate());
                            Hyperlink articleLink = new Hyperlink(article.getLink());

                            articleLink.setOnAction(e4 -> {
                                getHostServices().showDocument(article.getLink());
                            });

                            articles.getChildren().addAll(articleTitle, articlePubDate, articleLink, new Separator());

                            HBox options = new HBox();

                            final ToggleGroup group = new ToggleGroup();

                            RadioButton yesbtn = new RadioButton();
                            yesbtn.getStyleClass().add("options");
                            yesbtn.setToggleGroup(group);

                            RadioButton nobtn = new RadioButton();
                            nobtn.getStyleClass().add("options");
                            nobtn.setToggleGroup(group);

                            options.getChildren().addAll(yesbtn, nobtn);
                            classifyContainer.getChildren().add(options);
                        }
                        classifyContainer.getChildren().add(updateModel);
                        classifyContainer.setMaxHeight(feedInfo.getHeight() + articles.getHeight() + 50);
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
        pane.setRight(classifyContainer);

        Scene scene = new Scene(pane, 900, 800);
        scene.getStylesheets().add("FFStylesheet.css");
        primaryStage.setTitle("FemFinder");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e5 -> {
            boolean result1 = new File("newsfeed.arff").delete();
            boolean result2 = new File("labeledarticles.csv").delete();
        });
    }
}
