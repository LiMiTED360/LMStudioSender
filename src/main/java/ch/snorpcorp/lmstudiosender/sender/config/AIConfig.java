package ch.snorpcorp.lmstudiosender.sender.config;

import java.util.List;
import java.util.Map;

public record AIConfig<R> (
        String systemPrompt,
        String model,
        Double temperature,
        Double topP,
        Integer topK,
        Integer maxTokens,
        List<String> stop,
        Double presencePenalty,
        Double frequencyPenalty,
        Double repeatPenalty,
        Integer seed,
        String responseFormat,
        Class<R> expectedOutput,
        Map<Integer, Double> logitBias
) {
    public static class Builder<R> {
        private String systemPrompt;
        private String model;
        private Double temperature;
        private Double topP;
        private Integer topK;
        private Integer maxTokens = 1024;
        private List<String> stop;
        private Double presencePenalty;
        private Double frequencyPenalty;
        private Double repeatPenalty;
        private Integer seed;
        private String responseFormat;
        private Class<R> expectedOutput;
        private Map<Integer, Double> logitBias;


        public AIConfig<R> build() throws IllegalStateException {
            if ((responseFormat == null) != (expectedOutput == null)) throw new IllegalStateException("ResponseFormat and expectedOutput have to be either both null or both be filled.");
            translateValues();
            return new AIConfig<R>(systemPrompt, model, temperature, topP, topK, maxTokens, stop, presencePenalty, frequencyPenalty, repeatPenalty, seed, responseFormat, expectedOutput, logitBias);
        }

        private void translateValues() {
            if (0 > maxTokens) maxTokens = null;
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

        public Builder<R> topK(Integer topK) {
            this.topK = topK;
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

        public Builder<R> repeatPenalty(Double repeatPenalty) {
            this.repeatPenalty = repeatPenalty;
            return this;
        }
        public Builder<R> seed(Integer seed) {
            this.seed = seed;
            return this;
        }

        public Builder<R> responseFormat(String responseFormat) {
            this.responseFormat = responseFormat;
            return this;
        }

        public Builder<R> expectedOutput(Class<R> expectedOutput) {
            this.expectedOutput = expectedOutput;
            return this;
        }

        public Builder<R> logitBias(Map<Integer, Double> logitBias) {
            this.logitBias = logitBias;
            return this;
        }
    }
}
