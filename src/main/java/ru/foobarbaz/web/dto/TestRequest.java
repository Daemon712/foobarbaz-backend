package ru.foobarbaz.web.dto;

import javax.validation.constraints.NotNull;

public class TestRequest {

    @NotNull
    private String test;

    @NotNull
    private String code;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
