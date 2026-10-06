package org.example.traveljava.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

//旅游请求DTO，用于接收校验前端传递的旅游请求参数
@Data
public class TravelRequestDTO {
    @NotNull(message = "城市不能为空")
    private String city;
    @NotNull(message = "天数不能为空")
    @Min(value = 1, message = "天数不能小于1")
    @Max(value = 30, message = "天数不能大于30")
    private Integer days;
    @NotNull(message = "预算不能为空")
    @DecimalMin(value = "100", message = "预算不能小于100")
    private Double budget;
}
