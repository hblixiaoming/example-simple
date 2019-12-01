package com.example.resp;

import java.io.Serializable;

public class TestResponse implements Serializable {
    private String output;

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
