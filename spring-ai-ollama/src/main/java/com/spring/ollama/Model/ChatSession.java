package com.spring.ollama.Model;

import java.util.ArrayList;
import java.util.List;

public class ChatSession {
    private String id;
    private String title;
    private List<String> messages = new ArrayList<>();

    public ChatSession() {}

    public ChatSession(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public List<String> getMessages() { return messages; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setMessages(List<String> messages) { this.messages = messages; }
}
