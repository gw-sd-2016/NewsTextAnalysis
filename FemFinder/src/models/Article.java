package models;

/**
 * Created by ellenlouie on 11/29/15.
 */
public class Article {

    String title;
    String description;
    String link;
    String author;
    String pubDate;

    /*--- GET & SET TITLE ---*/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /*--- GET & SET DESCRIPTION ---*/

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*--- GET & SET LINK ---*/

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    /*--- GET & SET AUTHOR ---*/

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    /*--- GET & SET DATE PUBLISHED ---*/

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    @Override
    public String toString() {
        return "Article[Title: " + title + ", Description: " + description + ", Link: " + link + ", Author: " + author
                +  ", Date Published: " + pubDate + "]";
    }
}