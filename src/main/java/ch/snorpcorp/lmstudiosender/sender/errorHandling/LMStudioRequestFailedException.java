package ch.snorpcorp.lmstudiosender.sender.errorHandling;

public class LMStudioRequestFailedException extends RuntimeException {
    private Integer httpStatus = null;
    private String url;
    private String requestBody = null;

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
}
