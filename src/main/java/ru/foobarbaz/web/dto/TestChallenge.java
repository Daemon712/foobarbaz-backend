package ru.foobarbaz.web.dto;

import javax.validation.constraints.NotNull;

public class TestChallenge {

    @NotNull
    private String unitTest;

    @NotNull
    private String sample;

    public String getUnitTest() {
        return unitTest;
    }

    public void setUnitTest(String unitTest) {
        this.unitTest = unitTest;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }
}
