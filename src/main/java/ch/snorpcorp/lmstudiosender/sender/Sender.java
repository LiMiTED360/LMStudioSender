package ch.snorpcorp.lmstudiosender.sender;

import ch.snorpcorp.lmstudiosender.sender.dto.AIRequest;
import ch.snorpcorp.lmstudiosender.sender.dto.AIResponse;
import ch.snorpcorp.lmstudiosender.sender.dto.AIStructuredResponse;
import ch.snorpcorp.lmstudiosender.sender.messages.Message;
import ch.snorpcorp.lmstudiosender.sender.messages.MessageRoles;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class Sender<R> {
    private String url;
    private String authToken;
    private int maxTokens = 4096;
    private AIConfig<R> aiConfig;

    private final HTTPHelper httpHelper = new HTTPHelper();
    private final ContextHelper contextHelper = new ContextHelper();
    private final ObjectMapper mapper = new ObjectMapper();

    private List<Message> context;

    protected Sender(String url, String authToken, int maxTokens, List<Message> context) {
        this.url = url;
        this.authToken = authToken;
        this.maxTokens = maxTokens;
        this.context = context;
    }

    public AIResponse sendAIRequest() {
        return sendAIRequest(new ArrayList<>());
    }

    public AIResponse sendAIRequest(Message message) {
        return sendAIRequest(List.of(message));
    }

    public AIResponse sendAIRequest(List<Message> messages) {
        Message systemPrompt = new Message(MessageRoles.system, aiConfig.systemPrompt());
        context.addAll(messages);
        contextHelper.deleteOldContext(context, systemPrompt, maxTokens);
        return httpHelper.post(url, makeAIRequest(aiConfig, context, systemPrompt), AIResponse.class, authToken);
    }

    public AIStructuredResponse<R> sendAIRequestStructured() throws IllegalStateException, JsonProcessingException {
        return sendAIRequestStructured(new ArrayList<>());
    }

    public AIStructuredResponse<R> sendAIRequestStructured(Message message) throws IllegalStateException, JsonProcessingException {
        return sendAIRequestStructured(List.of(message));
    }

    public AIStructuredResponse<R> sendAIRequestStructured(List<Message> messages) throws IllegalStateException, JsonProcessingException {
        if (aiConfig == null || aiConfig.expectedOutput() == null) {
            throw new IllegalStateException("Config does not have structured output expected class configured.");
        }

        AIResponse aiResponse = sendAIRequest(messages);

        String jsonContent = aiResponse.choices().getFirst().message().content();
        R structuredData = mapper.readValue(jsonContent, aiConfig.expectedOutput());

        AIResponse.Choice originalChoice = aiResponse.choices().getFirst();

        AIStructuredResponse.Usage structuredUsage = new AIStructuredResponse.Usage(
                aiResponse.usage().promptTokens(),
                aiResponse.usage().completionTokens(),
                aiResponse.usage().totalTokens()
        );

        return new AIStructuredResponse<>(
                aiResponse.id(),
                aiResponse.object(),
                aiResponse.created(),
                aiResponse.model(),
                aiResponse.systemFingerprint(),
                List.of(new AIStructuredResponse.Choice<>(
                        originalChoice.index(),
                        structuredData,
                        originalChoice.finishReason()
                )),
                structuredUsage
        );
    }

    private AIRequest makeAIRequest(AIConfig<R> aiConfig, List<Message> context, Message systemPrompt) {
        List<Message> messages = new ArrayList<>(context);
        messages.addFirst(systemPrompt);
        return new AIRequest(aiConfig.model(), messages, aiConfig.temperature(), aiConfig.topP(), aiConfig.maxTokens(), aiConfig.stop(), aiConfig.presencePenalty(), aiConfig.frequencyPenalty(), aiConfig.seed(), aiConfig.user(), aiConfig.responseFormat(), aiConfig.logitBias());
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public AIConfig<R> getAiConfig() {
        return aiConfig;
    }

    public void setAiConfig(AIConfig<R> aiConfig) {
        this.aiConfig = aiConfig;
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

    public static class Builder<R> {
        private String url = "http://localhost:8123/v1/chat/completions";
        private String authToken = null;
        private int maxTokens = 4096;
        private List<Message> context = new ArrayList<>();

        public Sender<R> build() {
            return new Sender<>(url, authToken, maxTokens, context);
        }

        public Builder<R> url(String url) {
            this.url = url;
            return this;
        }

        public Builder<R> authToken(String authToken) {
            this.authToken = authToken;
            return this;
        }

        public Builder<R> maxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public Builder<R> initContext(List<Message> context) {
            this.context = context;
            return this;
        }
    }
}