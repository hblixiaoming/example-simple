package com.example.schedule.job;

import com.example.schedule.quartz.JobContent;
import org.springframework.stereotype.Service;

@Service
public class TestJob extends JobContent {

    public void execute(){
        System.out.println("hello world");
    }
}
