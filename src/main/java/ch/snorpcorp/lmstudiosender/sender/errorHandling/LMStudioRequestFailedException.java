package ch.snorpcorp.lmstudiosender.sender.errorHandling;

import ch.snorpcorp.lmstudiosender.sender.dto.AIResponse;

public class LMStudioRequestFailedException extends RuntimeException {
    private Integer httpStatus;
    private String url;
    private String requestBody;
    private AIResponse aiResponse;

    public LMStudioRequestFailedException(String message, Throwable cause, AIResponse aiResponse) {
        super(message, cause);
        this.aiResponse = aiResponse;
    }

    public LMStudioRequestFailedException(String message, Throwable cause, String url) {
        super(message, cause);
        this.url = url;
    }

    public LMStudioRequestFailedException(String message, Throwable cause, String url, String requestBody) {
        super(message, cause);
        this.url = url;
        this.requestBody = requestBody;
    }

    public LMStudioRequestFailedException(String message, Throwable cause, String url, String requestBody, Integer httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.url = url;
        this.requestBody = requestBody;
    }

    public LMStudioRequestFailedException(String message, String url, String requestBody, Integer httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.url = url;
        this.requestBody = requestBody;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public String getUrl() {
        return url;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public AIResponse getAiResponse() {
        return aiResponse;
    }
}
