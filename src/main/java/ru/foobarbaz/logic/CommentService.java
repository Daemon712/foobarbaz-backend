package ru.foobarbaz.logic;

import ru.foobarbaz.entity.comment.BaseComment;

import java.util.List;

public interface CommentService <P, C extends BaseComment> {
    List<C> getComments(P parent);
    C addComment(C comment);
    int updateLikes(Long commentId, boolean like);
}
