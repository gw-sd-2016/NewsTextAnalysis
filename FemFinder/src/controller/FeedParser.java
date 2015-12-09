package controller;

import model.Feed;
import model.Article;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ellenlouie on 12/9/15.
 */
public class FeedParser {

    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String COPYRIGHT = "copyright";
    static final String LINK = "link";
    static final String AUTHOR = "author";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";

    final URL url;

    public FeedParser(String feedUrl) {
        //Make sure url is valid
        try {
            this.url = new URL(feedUrl);
        } catch(MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Feed parseFeed() {
        Feed feed = null;

        try {
            boolean isFeedHeader = true;

            //set initial header values to empty strings
            String description = "";
            String title = "";
            String link = "";
            String copyright = "";
            String author = "";
            String pubDate = "";

            //create new factory to get streams
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

            //setup new event reader with xmlEventReader iterator api
            InputStream in = readStream();
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(in);

            //read xml document
            while(xmlEventReader.hasNext()) {
                //beginning or end of an element, group of text, etc
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                //start tag?
                if(xmlEvent.isStartElement()) {
                    //get tag name
                    String localPart = xmlEvent.asStartElement().getName().getLocalPart();

                    switch(localPart) {
                        case ITEM:
                            if(isFeedHeader) {
                                isFeedHeader = false;
                                feed = new Feed(title, link, description, copyright, pubDate);
                            }
                            break;
                        case TITLE:
                            title = getCharacterData(xmlEvent, xmlEventReader);
                            break;
                        case DESCRIPTION:
                            description = getCharacterData(xmlEvent, xmlEventReader);
                            break;
                        case LINK:
                            link = getCharacterData(xmlEvent, xmlEventReader);
                            break;
                        case AUTHOR:
                            author = getCharacterData(xmlEvent, xmlEventReader);
                            break;
                        case PUB_DATE:
                            pubDate = getCharacterData(xmlEvent, xmlEventReader);
                            break;
                        case COPYRIGHT:
                            copyright = getCharacterData(xmlEvent, xmlEventReader);
                            break;
                    }
                } else if(xmlEvent.isEndElement()) {
                    if(xmlEvent.asEndElement().getName().getLocalPart() == (ITEM)) {
                        Article article = new Article();

                        article.setPubDate(pubDate);
                        article.setAuthor(author);
                        article.setDescription(description);
                        article.setLink(link);
                        article.setTitle(title);

                        feed.getArticles().add(article);
                        //get next xmlEvent
                        continue;
                    }
                }
            }
        } catch(XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return feed;
    }

    //get and return character data of an xmlEvent
    private String getCharacterData(XMLEvent xmlEvent, XMLEventReader xmlEventReader) throws XMLStreamException {
        String result = "";
        xmlEvent = xmlEventReader.nextEvent();

        if(xmlEvent instanceof Characters) {
            result = xmlEvent.asCharacters().getData();
        }
        return result;
    }

    //open connection to this url
    //returns InputStream to process connection
    private InputStream readStream() {
        try {
            return url.openStream();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
