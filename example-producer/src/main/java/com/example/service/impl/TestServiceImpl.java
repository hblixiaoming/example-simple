package com.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.dao.user.UserInfoMapper;
import com.example.domain.user.UserInfo;
import com.example.domain.user.UserInfoExample;
import com.example.req.TestRequest;
import com.example.resp.TestResponse;
import com.example.service.TestService;
import com.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public Result<TestResponse> hello(TestRequest request) {
        String name = request.getName();
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.createCriteria().andNameEqualTo(name);

        String outPut = "error";
        List<UserInfo> userInfos = userInfoMapper.selectByExample(userInfoExample);
        if (!CollectionUtils.isEmpty(userInfos)) {
            outPut = JSON.toJSONString(userInfos.get(0));
        }
        redisTemplate.opsForValue().set("test_key_" + request.getName(), outPut);
        redisTemplate.expire("test_key_" + request.getName(), 60, TimeUnit.MINUTES);
        Result<TestResponse> result = new Result<TestResponse>();
        TestResponse response = new TestResponse();
        response.setOutput(outPut);
        result.setCode(1);
        result.setBean(response);
        return result;
    }
}
