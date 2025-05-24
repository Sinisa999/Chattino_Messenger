package com.example.chattinomessanger;

public class MessageModel {
    private String messageId;
    private String senderId;
    private String message;

    public MessageModel(String messageId, String senderId, String message) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
    }

    public MessageModel() {
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }


}
