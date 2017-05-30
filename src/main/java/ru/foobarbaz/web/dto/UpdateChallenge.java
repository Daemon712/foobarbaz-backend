package ru.foobarbaz.web.dto;

import ru.foobarbaz.constant.AccessOption;

import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

public class UpdateChallenge {
    @NotNull
    @Size(min = 4, max = 50)
    private String name;

    @NotNull
    @Size(min = 50, max = 300)
    private String shortDescription;

    @NotNull
    @Size(min = 100, max = 5000)
    private String fullDescription;

    @NotNull
    @Enumerated
    private AccessOption commentAccess;

    @NotNull
    @Enumerated
    private AccessOption shareAccess;

    @Size(max = 5)
    private Set<String> tags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public AccessOption getCommentAccess() {
        return commentAccess;
    }

    public void setCommentAccess(AccessOption commentAccess) {
        this.commentAccess = commentAccess;
    }

    public AccessOption getShareAccess() {
        return shareAccess;
    }

    public void setShareAccess(AccessOption shareAccess) {
        this.shareAccess = shareAccess;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
