package graduate.itdreams.android.data.model.api;

public class ResponseWrapper<T> {
    private boolean result;
    private T data;
    private String message;
    private String code;
    private Integer httpCode;

    private String firebaseUrl;
    private String urlBase;

    public String getFirebaseUrl() {
        return firebaseUrl;
    }

    public String getUrlBase() {
        return urlBase;
    }

    public boolean isResult() {
        return result;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public Integer getHttpCode() { return  httpCode; }
}
