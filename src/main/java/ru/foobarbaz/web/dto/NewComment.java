package ru.foobarbaz.web.dto;

import javax.validation.constraints.NotNull;

public class NewComment {
    @NotNull
    private long parentId;

    @NotNull
    private String text;

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
