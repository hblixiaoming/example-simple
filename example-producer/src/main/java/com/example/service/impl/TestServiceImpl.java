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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private UserInfoMapper userInfoMapper;

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
        Result<TestResponse> result = new Result<TestResponse>();
        TestResponse response = new TestResponse();
        response.setOutput(outPut);
        result.setCode(1);
        result.setBean(response);
        return result;
    }
}
