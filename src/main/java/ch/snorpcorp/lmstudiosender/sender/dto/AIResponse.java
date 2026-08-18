package ch.snorpcorp.lmstudiosender.sender.dto;
import ch.snorpcorp.lmstudiosender.sender.messages.Message;
import ch.snorpcorp.lmstudiosender.sender.messages.MessageRoles;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record AIResponse(
        String id,
        String object,
        Long created,
        String model,
        @JsonProperty("system_fingerprint") String systemFingerprint,
        List<Choice> choices,
        Usage usage
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Choice(
            int index,
            AIResponseMessage message,
            @JsonProperty("finish_reason") String finishReason
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record AIResponseMessage(
            MessageRoles role,
            String content,
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
