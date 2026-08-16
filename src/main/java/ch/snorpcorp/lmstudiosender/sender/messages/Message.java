package ch.snorpcorp.lmstudiosender.sender.messages;

import java.io.Serializable;

public class Message {
    private MessageRoles role;
    private String message;

    public Message (MessageRoles role, String message) {
        this.role = role;
        this.message = message;
    }

    public String getRoleString() {
        return role.toString().toLowerCase();
    }

    public MessageRoles getRole() {
        return role;
    }

    public void setRole(MessageRoles role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
