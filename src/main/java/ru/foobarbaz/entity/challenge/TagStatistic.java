package ru.foobarbaz.entity.challenge;

public class TagStatistic {
    private String tag;
    private long usages;

    public TagStatistic(String tag, long usages) {
        this.tag = tag;
        this.usages = usages;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getUsages() {
        return usages;
    }

    public void setUsages(long usages) {
        this.usages = usages;
    }
}
