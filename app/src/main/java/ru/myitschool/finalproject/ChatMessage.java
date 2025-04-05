package ru.myitschool.finalproject;

public class ChatMessage {
    private String sender;
    private String message;
    private boolean isAI;

    public ChatMessage(String sender, String message, boolean isAI) {
        this.sender = sender;
        this.message = message;
        this.isAI = isAI;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public boolean isAI() {
        return isAI;
    }
} 