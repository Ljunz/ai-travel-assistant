package org.example.traveljava.vo;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class StreamErrorVO {
    private String error;

    public static StreamErrorVO of(String error) {
        //全参构造方法，用于创建StreamErrorVO对象，error为传入的参数
        return new StreamErrorVO(error);
    }
}
