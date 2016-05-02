package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ellenlouie on 11/29/15.
 */
public class Feed {

    private String title;
    private String link;
    private String description;
    private String copyright;
    private String pubDate;

    private List<Article> articles = new ArrayList<>();

    public Feed(String title, String link, String description, String copyright, String datePublished) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.copyright = copyright;
        this.pubDate = datePublished;
    }

    /*--- GETTERS ---*/

    public List<Article> getArticles() {
        return articles;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getPubDate() {
        return pubDate;
    }

    @Override
    public String toString() {
        return "Feed[Title: " + title + ", Link: " + link + ", Description: " + description + ", Copyright: "
                + copyright + ", Date Published: " + pubDate + "]";
    }
}
