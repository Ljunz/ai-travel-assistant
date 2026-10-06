package org.example.traveljava.vo;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class StreamChunkVO {
    private String type = "chunk";
    private String content;

    public static StreamChunkVO of(String content) {
        //全参构造方法，用于创建StreamChunkVO对象，type默认值为chunk，content为传入的参数
        return new StreamChunkVO("chunk",content);
    }
}
