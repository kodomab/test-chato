package com.example.shadowbez.chato;

public class ChatRoom {
    private final String id;
    private final String initiatorDatabaseId;
    private final String recipientDatabaseId;

    public ChatRoom(String id, String initiatorDatabaseId, String recipientDatabaseId) {
        this.id = id;
        this.initiatorDatabaseId = initiatorDatabaseId;
        this.recipientDatabaseId = recipientDatabaseId;
    }

    public String getId() {
        return id;
    }

    public String getInitiatorDatabaseId() {
        return initiatorDatabaseId;
    }

    public String getRecipentDatabaseId() {
        return recipientDatabaseId;
    }


}
