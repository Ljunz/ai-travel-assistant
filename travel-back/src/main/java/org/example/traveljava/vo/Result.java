package org.example.traveljava.vo;

import lombok.Data;
@Data

public class Result<T> {
    private Boolean success;
    private Integer code;
    private String message;
    private T data;  //泛型数据
    private String error;
    private String rawResponse;

    /* Data注解会自动生成get和set方法，无需手动编写
    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    */
    public static <T> Result<T> ok() {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(200);
        result.setMessage("成功");
        return result;
    }

    //方法重载，用于返回成功结果并携带数据的Result
    public static <T> Result<T> ok(T data) {
        Result<T> result = ok();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(500);
        result.setMessage("失败");
        return result;
    }

    public static <T> Result<T> fail(Integer code,String message) {
        Result<T> result = fail();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(String error,String rawResponse) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setMessage(rawResponse);
        result.setError(error);
        return result;
    }
}

