package com.example.service;

import com.example.req.TestRequest;
import com.example.resp.TestResponse;
import com.example.vo.Result;

public interface TestService {
    Result<TestResponse> hello(TestRequest testRequest);
}
