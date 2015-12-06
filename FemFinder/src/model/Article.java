package model;

/**
 * Created by ellenlouie on 11/29/15.
 */
public class Article {

    String title;
    String description;
    String link;
    String author;

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

    @Override
    public String toString() {
        return "Article[Title: " + title + ", Description: " + description + ", Link: " + link + ", Author: " + author
                + "]";
    }
}