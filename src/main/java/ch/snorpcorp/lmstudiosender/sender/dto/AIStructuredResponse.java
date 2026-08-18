package ch.snorpcorp.lmstudiosender.sender.dto;

import ch.snorpcorp.lmstudiosender.sender.messages.MessageRoles;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AIStructuredResponse<R>(
        String id,
        String object,
        Long created,
        String model,
        @JsonProperty("system_fingerprint") String systemFingerprint,
        List<Choice<R>> choices,
        Usage usage
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Choice<R>(
            int index,
            AIResponseMessage<R> message,
            @JsonProperty("finish_reason") String finishReason
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record AIResponseMessage<R>(
                MessageRoles role,
                R content,
                String reasoning_content
        ) { }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Usage(
            @JsonProperty("prompt_tokens") int promptTokens,
            @JsonProperty("completion_tokens") int completionTokens,
            @JsonProperty("total_tokens") int totalTokens
    ) {}
}
