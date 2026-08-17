package ch.snorpcorp.lmstudiosender.sender.messages;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Message (
        MessageRoles role,
        String content
) { }
