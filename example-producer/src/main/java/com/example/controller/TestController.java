package com.example.controller;

import com.example.req.TestRequest;
import com.example.resp.TestResponse;
import com.example.service.TestService;
import com.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private TestService testService;

    @PostMapping(value = "/api/v1/hello", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Result<TestResponse> hello(@RequestBody TestRequest request){
        Result<TestResponse> result = testService.hello(request);
        return result;
    }
}
