package controller;

import java.util.List;

public class Article {

    private static int idCounter = 1; // Static counter for generating unique IDs

    private int id;
    private String title;
    private List<String> authors;
    private String abstractText;
    private List<String> keywords;
    private String body;
    private List<String> references;

    // Constructor
    public Article(String title, List<String> authors, String abstractText,
                   List<String> keywords, String body, List<String> references) {
        this.id = idCounter++;
        this.title = title;
        this.authors = authors;
        this.abstractText = abstractText;
        this.keywords = keywords;
        this.body = body;
        this.references = references;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public String getBody() {
        return body;
    }

    public List<String> getReferences() {
        return references;
    }

    // Setters (if needed)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }

    // Optional: Override toString() for easier debugging
    @Override
    public String toString() {
        return "Article ID: " + id +
                "\nTitle: " + title +
                "\nAuthors: " + String.join(", ", authors) +
                "\nAbstract: " + abstractText +
                "\nKeywords: " + String.join(", ", keywords) +
                "\nBody: " + body +
                "\nReferences: " + String.join(", ", references);
    }
}
