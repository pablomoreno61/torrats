package es.ibrands.torrats.model;

import java.util.List;

public class CalendarError
{
    private String code;

    private String message;

    private ApiError data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApiError getData() {
        return data;
    }

    public void setData(ApiError data) {
        this.data = data;
    }
}
