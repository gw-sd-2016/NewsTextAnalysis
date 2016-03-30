import controllers.DBConnection;
import controllers.MachineLearning;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import models.Article;
import models.Feed;
import controllers.FeedParser;
import controllers.TextExtraction;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Nonprofit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ellenlouie on 11/29/15.
 */
public class FFGUI extends Application {

    //store list of classified articles
    List<Article> womensArticles = null;

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

        BorderPane articlePane = new BorderPane();

        VBox donationContainer = new VBox();

        VBox donationLabelContainer = new VBox(40);
        donationLabelContainer.setPadding(new Insets(40, 0, 30, 0));
        donationLabelContainer.getStyleClass().add("extras");

        VBox donationBtnContainer = new VBox(35);
        donationBtnContainer.setPadding(new Insets(10, 0, 10, 0));
        donationBtnContainer.getStyleClass().add("extras");

        Label donationLabel = new Label("Want to help?");
        donationLabel.setId("donationLabel");

        donationLabelContainer.getChildren().add(donationLabel);

        VBox feedInfo = new VBox();
        feedInfo.setId("feedInfo");
        VBox articles = new VBox();

        //hold rss feed info, articles, and classification
        ScrollPane articleContainer = new ScrollPane();
        articleContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        articleContainer.setFitToWidth(true);

        //hold feed info and articles
        VBox rssFeed = new VBox();
        rssFeed.getChildren().addAll(feedInfo, articles);

        //hold user classification system
        VBox classifyContainer = new VBox();
        classifyContainer.getStyleClass().add("extras");

        Label classifyLabel1 = new Label("Is this article");
        Label classifyLabel2 = new Label("related to women?");
        classifyLabel1.getStyleClass().add("classifyLabel");
        classifyLabel2.getStyleClass().add("classifyLabel");

        HBox optionsContainer = new HBox();
        Label yes = new Label("Yes");
        Label no = new Label("No");
        yes.getStyleClass().add("options");
        no.getStyleClass().add("options");
        optionsContainer.getChildren().addAll(yes, no);

        articlePane.setLeft(donationContainer);
        articlePane.setCenter(rssFeed);
        articlePane.setRight(classifyContainer);
        articleContainer.setContent(articlePane);

        Button addInstances = new Button("Submit");

        //store toggle groups of radio buttons
        List<ToggleGroup> listOfGroups = new ArrayList<>();

        filter.setOnAction(e1 -> {
            donationContainer.getChildren().clear();
            donationBtnContainer.getChildren().clear();
            feedInfo.getChildren().clear();
            articles.getChildren().clear();
            classifyContainer.getChildren().clear();
            addInstances.setDisable(false);

            Alert processingAlert = new Alert(Alert.AlertType.NONE);

            //get the rss feed url from the text area
            String rssUrl = feedUrl.getText();

            //create a new FeedParser to parse feed
            FeedParser parser = new FeedParser(rssUrl);
            Feed feed = parser.parseFeed();

            if(feed == null) {
                return;
            }

            Task<List<Article>> task = new Task<List<Article>>() {
                @Override
                protected List<Article> call() throws Exception {
                    //create new arff file to add articles
                    MachineLearning ml = new MachineLearning();
                    ml.createArffFile();

                    for(Article article : feed.getArticles()) {
                        TextExtraction te = new TextExtraction();
                        String plainText = te.getUnclassifiedPlainText(article.getLink());

                        //append plain text to arff file for weka processing
                        ml.addArticleToFile("newsfeed.arff", plainText);
                    }

                    //classify articles
                    File labeledArticles = ml.classifyArticles();

                    //extract out class attributes
                    ArrayList<Integer> classAttr = ml.extractClassAttributes(labeledArticles);

                    //create new list of articles that will hold articles related to women
                    womensArticles = new ArrayList<>();

                    //filter out articles unrelated to women
                    if (classAttr.size() == feed.getArticles().size()) {
                        for (int i = 0; i < classAttr.size(); i++) {
                            if (classAttr.get(i) == 1) {
                                womensArticles.add(feed.getArticles().get(i));
                            }
                        }
                    }
                    return womensArticles;
                }
            };

            task.setOnSucceeded(e2 -> {
                processingAlert.close();

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

                feedInfo.getChildren().addAll(new Separator(), feedTitle, feedLink, feedDescription, feedCopyright, feedPubDate, new Separator());

                List<Article> classifiedWomensArticles = task.getValue();

                classifyContainer.getChildren().addAll(classifyLabel1, classifyLabel2, optionsContainer);

                for (Article article : classifiedWomensArticles) {
                    Label articleTitle = new Label(article.getTitle());
                    articleTitle.setId("titleText");
                    Label articlePubDate = new Label(article.getPubDate());
                    Hyperlink articleLink = new Hyperlink(article.getLink());

                    articleLink.setOnAction(e4 -> {
                        getHostServices().showDocument(article.getLink());
                    });

                    articles.getChildren().addAll(articleTitle, articlePubDate, articleLink, new Separator());

                    Button donationBtn = new Button("Take Action");
                    donationBtnContainer.getChildren().add(donationBtn);

                    donationBtn.setOnAction(e5 -> {
                        Task<List<Nonprofit>> task1 = new Task<List<Nonprofit>>() {
                            @Override
                            protected List<Nonprofit> call() throws Exception {
                                TextExtraction te = new TextExtraction();
                                List<String> keywords = te.getKeywords(article.getLink());
                                List<String> locations = te.getLocations(te.getPlainText(article.getLink()));

                                DBConnection connect = new DBConnection();
                                return connect.getNonprofits(keywords, locations);
                            }
                        };

                        task1.setOnSucceeded(e6 -> {
                            List<Nonprofit> nonprofits = task1.getValue();

                            Alert donationAlert = new Alert(Alert.AlertType.INFORMATION);

                            /* needed to change the style of links in the donation alert dialogs
                            because a dialog is actually a new stage with a new scene,
                            its root node is a DialogPane instance */
                            DialogPane dialogPane = donationAlert.getDialogPane();
                            dialogPane.getStylesheets().add("stylesheets/donationAlert.css");

                            donationAlert.setTitle("Donate");
                            donationAlert.setHeaderText("Nonprofits:");
                            VBox nonprofitContainer = new VBox();

                            if (nonprofits.size() == 0) {
                                Label noNonprofits = new Label("Sorry, no nonprofits found!");
                                nonprofitContainer.getChildren().add(noNonprofits);
                                donationAlert.setGraphic(noNonprofits);
                            } else if (nonprofits.size() <= 3) {
                                for (Nonprofit nonprofit : nonprofits) {
                                    Hyperlink donationLink = new Hyperlink(nonprofit.getName());

                                    donationLink.setOnAction(event1 -> {
                                        getHostServices().showDocument(nonprofit.getDonationLink());
                                    });

                                    nonprofitContainer.getChildren().addAll(donationLink);
                                }
                                donationAlert.setGraphic(nonprofitContainer);
                            } else if(nonprofits.size() > 3) {
                                DBConnection conn = new DBConnection();
                                List<Nonprofit> randomNonprofits = conn.getNRandomNonprofits(nonprofits, 3);

                                for (int i = 0; i < 3; i++) {
                                    Hyperlink donationLink = new Hyperlink(randomNonprofits.get(i).getName());
                                    String link = randomNonprofits.get(i).getDonationLink();

                                    donationLink.setOnAction(event2 -> {
                                        getHostServices().showDocument(link);
                                    });
                                    nonprofitContainer.getChildren().addAll(donationLink);
                                }
                                donationAlert.setGraphic(nonprofitContainer);
                            }
                            donationAlert.show();
                        });

                        Thread thread  = new Thread(task1);
                        thread.start();
                    });

                    HBox options = new HBox();

                    final ToggleGroup group = new ToggleGroup();

                    RadioButton yesbtn = new RadioButton();
                    yesbtn.getStyleClass().add("options");
                    yesbtn.setUserData("Yes");
                    yesbtn.setToggleGroup(group);

                    RadioButton nobtn = new RadioButton();
                    nobtn.getStyleClass().add("options");
                    nobtn.setUserData("No");
                    nobtn.setToggleGroup(group);

                    listOfGroups.add(group);

                    options.getChildren().addAll(yesbtn, nobtn);
                    classifyContainer.getChildren().add(options);
                }
                classifyContainer.getChildren().add(addInstances);
                classifyContainer.setMaxHeight(feedInfo.getHeight() + articles.getHeight() + 50);

                donationContainer.getChildren().addAll(donationLabelContainer, donationBtnContainer);
                donationBtnContainer.setMaxHeight(articles.getHeight());
            });

            ProgressBar progressBar = new ProgressBar();
            processingAlert.setTitle("Busy");
            processingAlert.setHeaderText("Processing...");
            processingAlert.setContentText("Analyzing articles...");
            processingAlert.setGraphic(progressBar);
            processingAlert.getButtonTypes().add(ButtonType.CLOSE);
            processingAlert.show();

            Thread thread = new Thread(task);
            thread.start();
        });

        addInstances.setOnAction(e7 -> {
            TextExtraction te = new TextExtraction();
            MachineLearning ml = new MachineLearning();

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    if(listOfGroups.size() == womensArticles.size()) {
                        for(int i = 0; i < listOfGroups.size(); i++) {
                            if(listOfGroups.get(i).getSelectedToggle() == null) {
                                System.out.println("Not clasified by user");
                                continue;
                            } else if(listOfGroups.get(i).getSelectedToggle().getUserData() == "Yes") {
                                String plainText = te.getClassifiedPlainText(womensArticles.get(i).getLink(), 1);
                                ml.addArticleToFile("women-train.arff", plainText);
                                System.out.println("Yes!");
                            } else if(listOfGroups.get(i).getSelectedToggle().getUserData() == "No") {
                                String plainText = te.getClassifiedPlainText(womensArticles.get(i).getLink(), 0);
                                ml.addArticleToFile("women-train.arff", plainText);
                                System.out.println("No!");
                            }
                        }
                    }
                    return null;
                }
            };

            task.setOnRunning(e8 -> {
                addInstances.setDisable(true);
            });

            Thread thread = new Thread(task);
            thread.start();
        });

        //add all elements to border pane
        BorderPane pane = new BorderPane();
        pane.setTop(inputContainer);
        pane.setCenter(articleContainer);

        Scene scene = new Scene(pane, 900, 800);
        scene.getStylesheets().add("stylesheets/mainApp.css");
        primaryStage.setTitle("FemFinder");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e9 -> {
            boolean result1 = new File("newsfeed.arff").delete();
            boolean result2 = new File("labeledarticles.csv").delete();
        });
    }
}
