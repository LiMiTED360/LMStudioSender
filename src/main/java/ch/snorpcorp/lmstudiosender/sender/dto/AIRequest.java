package ch.snorpcorp.lmstudiosender.sender.dto;
import ch.snorpcorp.lmstudiosender.sender.messages.Message;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AIRequest(
        String model,
        List<Message> messages,
        Double temperature,
        @JsonProperty("top_p") Double topP,
        @JsonProperty("max_tokens") Integer maxTokens,
        List<String> stop,
        @JsonProperty("presence_penalty") Double presencePenalty,
        @JsonProperty("frequency_penalty") Double frequencyPenalty,
        Integer seed,
        String user,
        @JsonProperty("response_format") Object responseFormat,
        @JsonProperty("logit_bias") Map<String, Integer> logitBias
) { }
