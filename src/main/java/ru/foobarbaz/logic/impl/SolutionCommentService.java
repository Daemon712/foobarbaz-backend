package ru.foobarbaz.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.foobarbaz.entity.challenge.solution.SharedSolution;
import ru.foobarbaz.entity.comment.SharedSolutionComment;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.logic.CommentService;
import ru.foobarbaz.logic.RatingService;
import ru.foobarbaz.repo.SolutionCommentRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class SolutionCommentService implements CommentService<SharedSolution, SharedSolutionComment> {
    private SolutionCommentRepository solutionCommentRepository;
    private RatingService ratingService;

    @Autowired
    public SolutionCommentService(SolutionCommentRepository solutionCommentRepository, RatingService ratingService) {
        this.solutionCommentRepository = solutionCommentRepository;
        this.ratingService = ratingService;
    }

    @Override
    public List<SharedSolutionComment> getComments(SharedSolution parent) {
        return solutionCommentRepository.findAllBySharedSolutionOrderByCreated(parent);
    }

    @Override
    @Transactional
    public SharedSolutionComment addComment(SharedSolutionComment template) {
        SharedSolutionComment comment = new SharedSolutionComment(template.getText(), template.getSharedSolution());
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        comment.setAuthor(new User(username));
        comment.setCreated(new Date());
        return solutionCommentRepository.save(comment);
    }

    @Override
    @Transactional
    public int updateLikes(Long commentId, boolean like) {
        SharedSolutionComment comment = solutionCommentRepository.findById(commentId)
                .orElseThrow(ResourceNotFoundException::new);
        ratingService.updateLikes(comment, like);
        solutionCommentRepository.save(comment);
        return comment.getRating();
    }
}
