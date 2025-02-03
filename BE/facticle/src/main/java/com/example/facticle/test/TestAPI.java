package com.example.facticle.test;

import lombok.Data;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestAPI {
    @GetMapping("/test")
    public String testApi(){
        return "ok";
    }

    @GetMapping("/requestParam")
    public String requestParam(
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "10") Integer age,
            //or
            @ModelAttribute HelloData helloData
    ){
        return "For requestParam";
    }

    @PostMapping("/RequestBody")
    public String requestBody(
            //request의 body에 있는 내용을 조회 가능
            @RequestBody String messageBody,
            @RequestBody HelloData hellodata //요청시 content-type이 applciation/json이어야 가능
    ){
        return "For request body";
    }
}

@Data
class HelloData{
    private String username;
    private int age;
}
