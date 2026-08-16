package ch.snorpcorp.lmstudiosender.sender;

import ch.snorpcorp.lmstudiosender.sender.messages.Message;

import java.util.List;

public class Sender {
    private String url;
    private String AuthToken;

    private int maxTokens = 4096;

    private List<Message> context;
    private Message systemPrompt;

    protected Sender(String url, String authToken, int maxTokens, List<Message> context, Message systemPrompt) {
        this.url = url;
        AuthToken = authToken;
        this.maxTokens = maxTokens;
        this.context = context;
        this.systemPrompt = systemPrompt;
    }

    public void setSystemPrompt(Message systemPrompt) {
        this.systemPrompt = systemPrompt;
    }


    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public List<Message> getContext() {
        return context;
    }

    public void setContext(List<Message> context) {
        this.context = context;
    }
}
