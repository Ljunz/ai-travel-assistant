package org.example.traveljava.controller;

import org.example.traveljava.service.TravelService;
import org.springframework.web.bind.annotation.*;
import org.example.traveljava.vo.Result;
import jakarta.validation.Valid;
import org.example.traveljava.vo.TravelRecommendVO;
import org.example.traveljava.dto.TravelRequestDTO;
import lombok.RequiredArgsConstructor;
import org.example.traveljava.dto.ChatRequestDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

//主入口控制器
@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor//注解，用于在构造函数中注入final类型的字段
public class TravelController {
    private final TravelService travelService;

    /**
    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }
    **/


    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.ok("hello world");
    }

    @PostMapping("/recommend")
    public Result<TravelRecommendVO> recommend(@Valid @RequestBody TravelRequestDTO travelRequestDTO) {
        System.out.println(travelRequestDTO.getCity());
        System.out.println(travelRequestDTO.getDays());
        System.out.println(travelRequestDTO.getBudget());
        TravelRecommendVO travelRecommendVO = travelService.recommend(travelRequestDTO.getCity(),travelRequestDTO.getDays(),travelRequestDTO.getBudget());
        return Result.ok(travelRecommendVO);
    }

    @PostMapping(value= "/chat",produces = "text/event-stream")
    public SseEmitter chat(@Valid @RequestBody ChatRequestDTO chatRequestDTO){
        System.out.println(chatRequestDTO.getMessage());
        return travelService.chat(chatRequestDTO.getMessage());
    }

}
