package ru.foobarbaz.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.comment.ChallengeComment;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.logic.CommentService;
import ru.foobarbaz.logic.RatingService;
import ru.foobarbaz.repo.ChallengeCommentRepository;

import javax.transaction.Transactional;
import java.nio.file.ReadOnlyFileSystemException;
import java.util.Date;
import java.util.List;

@Service
public class ChallengeCommentService implements CommentService<Challenge, ChallengeComment> {
    private ChallengeCommentRepository challengeCommentRepository;
    private RatingService ratingService;

    @Autowired
    public ChallengeCommentService(ChallengeCommentRepository challengeCommentRepository, RatingService ratingService) {
        this.challengeCommentRepository = challengeCommentRepository;
        this.ratingService = ratingService;
    }

    @Override
    public List<ChallengeComment> getComments(Challenge parent) {
        return challengeCommentRepository.findAllByChallengeOrderByCreated(parent);
    }

    @Override
    @Transactional
    public ChallengeComment addComment(ChallengeComment template) {
        ChallengeComment comment = new ChallengeComment(template.getText(), template.getChallenge());
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        comment.setAuthor(new User(username));
        comment.setCreated(new Date());
        return challengeCommentRepository.save(comment);
    }

    @Override
    public ChallengeComment updateComment(ChallengeComment template) {
        ChallengeComment comment = challengeCommentRepository.findById(template.getCommentId())
                .orElseThrow(ReadOnlyFileSystemException::new);
        comment.setText(template.getText());
        return challengeCommentRepository.save(comment);
    }

    @Override
    public void deleteComment(long commentId) {
        challengeCommentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public int updateLikes(Long commentId, boolean like) {
        ChallengeComment comment = challengeCommentRepository.findById(commentId)
                .orElseThrow(ResourceNotFoundException::new);
        ratingService.updateLikes(comment, like);
        challengeCommentRepository.save(comment);
        return comment.getRating();
    }
}
