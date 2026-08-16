package ch.snorpcorp.lmstudiosender.sender;

import ch.snorpcorp.lmstudiosender.sender.messages.Message;

import java.util.ArrayList;
import java.util.List;

public class SenderBuilder {
    private String url = "http://localhost:8123/v1/chat/completions";
    private String authToken = null;

    private int maxTokens = 4096;

    private List<Message> context = new ArrayList<>();
    private Message systemPrompt = null;

    public Sender build() {
        return new Sender(url, authToken, maxTokens, context, systemPrompt);
    }

    public SenderBuilder url(String url) {
        this.url = url;
        return this;
    }

    public SenderBuilder authToken(String authToken) {
        this.authToken = authToken;
        return this;
    }

    public SenderBuilder maxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
        return this;
    }

    public SenderBuilder initContext(List<Message> context) {
        this.context = context;
        return this;
    }

    public SenderBuilder systemPrompt(Message systemPrompt) {
        this.systemPrompt = systemPrompt;
        return this;
    }
}
