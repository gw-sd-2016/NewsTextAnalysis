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

import javax.xml.soap.Text;

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

        filter.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //get the rss feed url from the text area
                        String rssUrl = feedUrl.getText();

                        //create a new FeedParser to parse feed
                        FeedParser parser = new FeedParser(rssUrl);
                        Feed feed = parser.parseFeed();
                        //TODO: add feed AND ARTICLES to rssFeed VBox
                        System.out.println(feed);
                        for(Article article : feed.getArticles()) {
                            TextExtraction te = new TextExtraction();
                            String plainText = te.getPlainText(article.getLink());
                            System.out.println(plainText);
                            //System.out.println(article);
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
