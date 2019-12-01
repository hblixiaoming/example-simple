package com.example.service.impl;

import com.example.client.ExampleProducerClient;
import com.example.req.TestRequest;
import com.example.resp.TestResponse;
import com.example.service.TestService;
import com.example.vo.Result;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private ExampleProducerClient client;

    @Override
    @HystrixCommand(fallbackMethod = "fallback")
    public String test(String name) {
        TestRequest request = new TestRequest();
        request.setName(name);
        Result<TestResponse> result = client.hello(request);
        if(result.getCode()==1){
            return result.getBean().getOutput();
        }else {
            return result.getMessage();
        }
    }

    public String fallback(String name){
        return "fallback";
    }
}
