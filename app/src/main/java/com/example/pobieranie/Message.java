package com.example.pobieranie;

public class Message {
    private String title;
    private String description;
    private String date;
    private String author;
    private String content;

    public Message(String title, String description, String date, String author, String content) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.author = author;
        this.content = content;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
}