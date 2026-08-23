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

        /**
         * AIs will follow the systemPrompt more strictly than any prompt given in a user message.
         *
         * @param systemPrompt Set the system prompt.
         * @return Returns Builder Object.
         */
        public Builder<R> systemPrompt(String systemPrompt) {
            this.systemPrompt = systemPrompt;
            return this;
        }

        /**
         * Specifies the Name / ID of the model to use.
         *
         * @param model The model identifier (e.g., "llama-3", "gpt-4").
         * @return Returns Builder Object.
         */
        public Builder<R> model(String model) {
            this.model = model;
            return this;
        }

        /**
         * Controls randomness. Lower values are more deterministic, higher values make the output more random.
         * Default in LM Studio is Typically 0.8 (LM Studio uses a high temperature as default)
         *
         * @param temperature The sampling temperature.
         * @return Returns Builder Object.
         */
        public Builder<R> temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        /**
         * Nucleus sampling. The model considers the results of the tokens with topP probability mass.
         * Default in LM Studio is Typically 0.95
         *
         * @param topP The top-p sampling value.
         * @return Returns Builder Object.
         */
        public Builder<R> topP(Double topP) {
            this.topP = topP;
            return this;
        }

        /**
         * Reduces the probability of generating nonsense by limiting the vocabulary to the top K tokens.
         * Default in LM Studio is Typically 40
         *
         * @param topK The top-k sampling value.
         * @return Returns Builder Object.
         */
        public Builder<R> topK(Integer topK) {
            this.topK = topK;
            return this;
        }

        /**
         * The maximum number of tokens to generate in the completion.
         * If your using expectedOutput and responseFormat avoid using Max tokens under any circumstances, it can cause errors, try to limit the output using the systemPrompt.
         * -1 or null deactivates token limit (except if it is active in LM Studios)
         *
         * @param maxTokens The token limit.
         * @return Returns Builder Object.
         */
        public Builder<R> maxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        /**
         * Up to 4 sequences where the API will stop generating further tokens.
         *
         * @param stop List of stop sequences.
         * @return Returns Builder Object.
         */
        public Builder<R> stop(List<String> stop) {
            this.stop = stop;
            return this;
        }

        /**
         * Penalizes new tokens based on whether they appear in the text so far, increasing the model's likelihood to talk about new topics.
         * Per Default in LM Studio off
         *
         * @param presencePenalty Value between -2.0 and 2.0.
         * @return Returns Builder Object.
         */
        public Builder<R> presencePenalty(Double presencePenalty) {
            this.presencePenalty  = presencePenalty;
            return this;
        }

        /**
         * Penalizes new tokens based on their existing frequency in the text so far, decreasing the model's likelihood to repeat the same line verbatim.
         *
         * @param frequencyPenalty Value between -2.0 and 2.0.
         * @return Returns Builder Object.
         */
        public Builder<R> frequencyPenalty(Double frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
            return this;
        }

        /**
         * Strictly penalizes repetitive tokens during generation.
         * Default in LM Studio is Typically 1.1
         *
         * @param repeatPenalty The repetition penalty value.
         * @return Returns Builder Object.
         */
        public Builder<R> repeatPenalty(Double repeatPenalty) {
            this.repeatPenalty = repeatPenalty;
            return this;
        }

        /**
         * If you use the same seed in the same prompt, the AI will always give the same answer (Context must be the same too)
         *
         * @param seed The integer seed.
         * @return Returns Builder Object.
         */
        public Builder<R> seed(Integer seed) {
            this.seed = seed;
            return this;
        }

        /**
         * Enforces a specific output format (e.g., "json_object").
         * Only use with expectedOutput.
         *
         *
         * @see <a href="https://lmstudio.ai/docs/developer/openai-compat/structured-output">https://lmstudio.ai/docs/developer/openai-compat/structured-output</a>
         * @param responseFormat The desired response format string.
         * @return Returns Builder Object.
         */
        public Builder<R> responseFormat(String responseFormat) {
            this.responseFormat = responseFormat;
            return this;
        }

        /**
         * The Java class (Preferably a record but not a must) representing the expected structure of the AI's output.
         * Only use with responseFormat.
         *
         * @param expectedOutput The Class object to map the response to.
         * @return Returns Builder Object.
         */
        public Builder<R> expectedOutput(Class<R> expectedOutput) {
            this.expectedOutput = expectedOutput;
            return this;
        }

        /**
         * Modifies the likelihood of specified tokens appearing in the completion.
         * Click Links to get to a side were you can turn Strings into token IDs for specifc models.
         *
         * @see <a href="https://huggingface.co/spaces/Xenova/the-tokenizer-playground">https://huggingface.co/spaces/Xenova/the-tokenizer-playground</a>
         * @param logitBias Map of token IDs to their corresponding bias value (-100 to 100).
         * @return Returns Builder Object.
         */
        public Builder<R> logitBias(Map<Integer, Double> logitBias) {
            this.logitBias = logitBias;
            return this;
        }
    }
}
