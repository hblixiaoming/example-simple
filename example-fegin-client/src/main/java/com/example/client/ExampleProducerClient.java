package com.example.client;

import com.example.req.TestRequest;
import com.example.resp.TestResponse;
import com.example.vo.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "example-producer")
public interface ExampleProducerClient {

    @PostMapping(value = "/api/v1/hello",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Result<TestResponse> hello(@RequestBody TestRequest testRequest);
}
