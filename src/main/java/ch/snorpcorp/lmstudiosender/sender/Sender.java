package ch.snorpcorp.lmstudiosender.sender;

import ch.snorpcorp.lmstudiosender.sender.config.AIConfig;
import ch.snorpcorp.lmstudiosender.sender.dto.AIRequest;
import ch.snorpcorp.lmstudiosender.sender.dto.AIResponse;
import ch.snorpcorp.lmstudiosender.sender.dto.AIStructuredResponse;
import ch.snorpcorp.lmstudiosender.sender.errorHandling.LMStudioRequestFailedException;
import ch.snorpcorp.lmstudiosender.sender.messages.Message;
import ch.snorpcorp.lmstudiosender.sender.messages.MessageRoles;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class Sender<R> {
    private String url;
    private String authToken;
    private AIConfig<R> aiConfig;
    private int contextLimit;
    private boolean autosaveMessages;
    private boolean strictContextAlternate;
    private Integer lastContextEstimation;

    private final HTTPHelper httpHelper = new HTTPHelper();
    private final ContextHelper contextHelper = new ContextHelper();
    private final ObjectMapper mapper = new ObjectMapper();

    private List<Message> context;

    private Sender(String url, String authToken, AIConfig<R> aiConfig, int contextLimit, List<Message> context, boolean autosaveMessages, boolean strictContextAlternate) {
        this.url = url;
        this.authToken = authToken;
        this.contextLimit = contextLimit;
        this.context = context;
        this.autosaveMessages = autosaveMessages;
        this.aiConfig = aiConfig;
        this.strictContextAlternate = strictContextAlternate;
    }

    public AIResponse sendAIRequest() throws LMStudioRequestFailedException {
        return sendAIRequest(new ArrayList<>());
    }

    public AIResponse sendAIRequest(Message message) throws LMStudioRequestFailedException {
        return sendAIRequest(List.of(message));
    }

    public AIResponse sendAIRequest(List<Message> messages) throws LMStudioRequestFailedException{
        prepareContext(messages);

        AIRequest aiRequest = makeAIRequest(aiConfig, context, null, aiConfig.systemPrompt());
        AIResponse aiResponse = getAIResponse(aiRequest);

        addToContext(aiResponse.choices().getFirst().message());

        return aiResponse;
    }

    public AIStructuredResponse<R> sendAIRequestStructured() throws LMStudioRequestFailedException, IllegalStateException {
        return sendAIRequestStructured(new ArrayList<>());
    }

    public AIStructuredResponse<R> sendAIRequestStructured(Message message) throws LMStudioRequestFailedException, IllegalStateException {
        return sendAIRequestStructured(List.of(message));
    }

    public AIStructuredResponse<R> sendAIRequestStructured(List<Message> messages) throws LMStudioRequestFailedException, IllegalStateException {
        if (aiConfig == null || aiConfig.expectedOutput() == null) {
            throw new IllegalStateException("Config does not have structured output expected class configured.");
        }

        prepareContext(messages);

        JsonNode responseFormat;
        try {
            responseFormat = mapper.readTree(aiConfig.responseFormat());
        } catch (JsonProcessingException e) {
            throw new LMStudioRequestFailedException("Unable to parse responseFormat String: " + e.getMessage(), e.getCause());
        }

        AIRequest aiRequest = makeAIRequest(aiConfig, context, responseFormat, aiConfig.systemPrompt());

        AIResponse aiResponse = getAIResponse(aiRequest);
        String jsonContent = aiResponse.choices().getFirst().message().content();

        R structuredData;
        try {
            structuredData = mapper.readValue(jsonContent, aiConfig.expectedOutput());
        } catch (JsonProcessingException e) {
            throw new LMStudioRequestFailedException("Unable to parse output into given JSON: " + e.getMessage(), e.getCause(), aiResponse);
        }

        AIResponse.Choice originalChoice = aiResponse.choices().getFirst();
        AIStructuredResponse.Usage structuredUsage = new AIStructuredResponse.Usage(
                aiResponse.usage().promptTokens(),
                aiResponse.usage().completionTokens(),
                aiResponse.usage().totalTokens()
        );

        AIStructuredResponse<R> aiStructuredResponse = new AIStructuredResponse<>(
                aiResponse.id(),
                aiResponse.object(),
                aiResponse.created(),
                aiResponse.model(),
                aiResponse.systemFingerprint(),
                List.of(new AIStructuredResponse.Choice<>(
                        originalChoice.index(),
                        new AIStructuredResponse.Choice.AIResponseMessage<>(aiResponse.choices().getFirst().message().role(), structuredData, aiResponse.choices().getFirst().finishReason()),
                        originalChoice.finishReason()
                )),
                structuredUsage
        );

        addToContext(aiResponse.choices().getFirst().message());

        return aiStructuredResponse;
    }

    private AIRequest makeAIRequest(AIConfig<R> aiConfig, List<Message> context, Object responseFormat, String systemPrompt) {
        List<Message> messages = new ArrayList<>(context);
        if (systemPrompt != null) messages.addFirst(new Message(MessageRoles.system, systemPrompt));

        return new AIRequest(
                aiConfig.model(),
                messages,
                aiConfig.temperature(),
                aiConfig.topP(),
                aiConfig.topK(),
                aiConfig.maxTokens(),
                aiConfig.stop(),
                aiConfig.presencePenalty(),
                aiConfig.frequencyPenalty(),
                aiConfig.repeatPenalty(),
                aiConfig.seed(),
                responseFormat,
                aiConfig.logitBias()
        );
    }

    private AIResponse getAIResponse(AIRequest aiRequest) {
        return httpHelper.post(url, aiRequest, AIResponse.class, authToken);
    }

    private void prepareContext(List<Message> messages) {
        context.addAll(messages);
        lastContextEstimation = contextHelper.deleteOldContext(context, aiConfig.systemPrompt(), contextLimit, strictContextAlternate);
    }

    private void addToContext(AIResponse.Choice.AIResponseMessage message) {
        if (autosaveMessages) context.add(new Message(message.role(), message.content()));
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public int getContextLimit() {
        return contextLimit;
    }

    public void setContextLimit(int contextLimit) {
        this.contextLimit = contextLimit;
    }

    public List<Message> getContext() {
        return context;
    }

    public void setContext(List<Message> context) {
        this.context = context;
    }

    public boolean isStrictContextAlternate() {
        return strictContextAlternate;
    }

    public void setStrictContextAlternate(boolean strictContextAlternate) {
        this.strictContextAlternate = strictContextAlternate;
    }

    public boolean isAutosaveMessages() {
        return autosaveMessages;
    }

    public void setAutosaveMessages(boolean autosaveMessages) {
        this.autosaveMessages = autosaveMessages;
    }

    public Integer getLastContextEstimation() {
        return lastContextEstimation;
    }

    public static class Builder<R> {
        private String url = "http://localhost:8123/v1/chat/completions";
        private String authToken = null;
        private AIConfig<R> aiConfig = new AIConfig.Builder<R>().build();
        private int contextLimit = 4096;
        private List<Message> context = new ArrayList<>();
        private boolean autosaveMessages = true;
        private boolean strictContextAlternate = false;

        public Sender<R> build() {
            return new Sender<>(url, authToken, aiConfig, contextLimit, context, autosaveMessages, strictContextAlternate);
        }

        /**
         * Full URL to your LM Studio server.
         * Example (for local): http://localhost:1234/v1/chat/completions
         *
         * @param url Full URL.
         * @return Returns the Builder object.
         */
        public Builder<R> url(String url) {
            this.url = url;
            return this;
        }

        /**
         * Auth token for your server.
         * If you are using a Bearer token, add "Bearer " at the front.
         * If not set, no auth token will be used.
         * Example: Bearer sk-lm-XXXXXXXXXXXXXXXXXXXXXX
         *
         * @param authToken Your auth token.
         * @return Returns the Builder object.
         */
        public Builder<R> authToken(String authToken) {
            this.authToken = authToken;
            return this;
        }

        /**
         * Your AI config, which can be created using AIConfig.Builder.
         *
         * @see AIConfig.Builder
         * @param aiConfig AI Config object.
         * @return Returns the Builder object.
         */
        public Builder<R> aiConfig(AIConfig<R> aiConfig) {
            this.aiConfig = aiConfig;
            return this;
        }

        /**
         * Limit on how long the context can be.
         * WARNING: If a message is very long, the context might exceed this setting (in v1.0.0).
         * Newer versions may introduce a setting to strictly enforce the context limit.
         * WARNING: This currently only estimates the token count of the conversation and intentionally overshoots for safety.
         *
         * @param contextLimit The maximum allowed tokens for the context.
         * @return Returns the Builder object.
         */
        public Builder<R> contextLimit(int contextLimit) {
            this.contextLimit = contextLimit;
            return this;
        }

        /**
         * Sets an initial context if you already have one.
         * WARNING: Some AIs like Mistral (or rather their chat templates) require a strict alternating format: System, User, AI, User, AI...
         *
         * @param context The initial context.
         * @return Returns the Builder object.
         */
        public Builder<R> initContext(List<Message> context) {
            this.context = context;
            return this;
        }

        /**
         * Automatically saves AI response messages into the context.
         * WARNING: The user message is ALWAYS saved automatically. If you do not want this, you must manually delete the last entry in the context.
         *
         * @param autosaveMessages Should the sender always save AI responses?
         * @return Returns the Builder object.
         */
        public Builder<R> autosaveMessages(boolean autosaveMessages) {
            this.autosaveMessages = autosaveMessages;
            return this;
        }

        /**
         * Some AIs like Mistral (or their chat templates) require a strictly alternating format (System, User, AI, User, AI...).
         * This setting ensures that this format is maintained during context deletion.
         * WARNING: This does not validate manually set or added context.
         *
         * @param strictContextAlternate Should strict context alternation be enforced?
         * @return Returns the Builder object.
         */
        public Builder<R> strictContextAlternate(boolean strictContextAlternate) {
            this.strictContextAlternate = strictContextAlternate;
            return this;
        }
    }
}