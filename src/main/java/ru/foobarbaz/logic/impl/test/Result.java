package ru.foobarbaz.logic.impl.test;

import java.util.List;

public class Result {
    private long runTime;
    private List<ResultItem> items;

    public long getRunTime() {
        return runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }

    public List<ResultItem> getItems() {
        return items;
    }

    public void setItems(List<ResultItem> items) {
        this.items = items;
    }
}
