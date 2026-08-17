package ch.snorpcorp.lmstudiosender.sender;

import ch.snorpcorp.lmstudiosender.sender.messages.Message;

import java.util.List;
import java.util.Map;

public record AIConfig<R> (
        String systemPrompt,
        String model,
        Double temperature,
        Double topP,
        Integer maxTokens,
        List<String> stop,
        Double presencePenalty,
        Double frequencyPenalty,
        Integer seed,
        String user,
        Object responseFormat,
        Class<R> expectedOutput,
        Map<String, Integer> logitBias
) {
    public static class Builder<R> {
        private String systemPrompt;
        private String model;
        private Double temperature;
        private Double topP;
        private Integer maxTokens;
        private List<String> stop;
        private Double presencePenalty;
        private Double frequencyPenalty;
        private Integer seed;
        private String user;
        private Object responseFormat;
        private Class<R> expectedOutput;
        private Map<String, Integer> logitBias;

        public AIConfig<R> build() throws IllegalStateException {
            if ((responseFormat == null) != (expectedOutput == null)) throw new IllegalStateException("ResponseFormat and expectedOutput have to be either both null or both be filled.");
            return new AIConfig<R>(systemPrompt, model, temperature, topP, maxTokens, stop, presencePenalty, frequencyPenalty, seed, user, responseFormat, expectedOutput, logitBias);
        }

        public Builder<R> systemPrompt(String systemPrompt) {
            this.systemPrompt = systemPrompt;
            return this;
        }

        public Builder<R> model(String model) {
            this.model = model;
            return this;
        }

        public Builder<R> temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder<R> topP(Double topP) {
            this.topP = topP;
            return this;
        }

        public Builder<R> maxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public Builder<R> stop(List<String> stop) {
            this.stop = stop;
            return this;
        }

        public Builder<R> presencePenalty(Double presencePenalty) {
            this.presencePenalty  = presencePenalty;
            return this;
        }

        public Builder<R> frequencyPenalty(Double frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
            return this;
        }

        public Builder<R> seed(Integer seed) {
            this.seed = seed;
            return this;
        }

        public Builder<R> user(String user) {
            this.user = user;
            return this;
        }

        public Builder<R> responseFormat(Object responseFormat) {
            this.responseFormat = responseFormat;
            return this;
        }

        public Builder<R> expectedOutput(Class<R> expectedOutput) {
            this.expectedOutput = expectedOutput;
            return this;
        }

        public Builder<R> logitBias(Map<String, Integer> logitBias) {
            this.logitBias = logitBias;
            return this;
        }
    }
}
