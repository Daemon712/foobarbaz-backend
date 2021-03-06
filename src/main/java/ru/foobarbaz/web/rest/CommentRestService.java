package ru.foobarbaz.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.challenge.solution.SharedSolution;
import ru.foobarbaz.entity.comment.ChallengeComment;
import ru.foobarbaz.entity.comment.SharedSolutionComment;
import ru.foobarbaz.logic.CommentService;
import ru.foobarbaz.web.dto.NewComment;

import java.util.List;

@RestController
@RequestMapping("api/comments")
public class CommentRestService {

    private CommentService<Challenge, ChallengeComment> challengeCommentService;
    private CommentService<SharedSolution, SharedSolutionComment> solutionCommentService;

    @Autowired
    public CommentRestService(
            CommentService<Challenge, ChallengeComment> challengeCommentService,
            CommentService<SharedSolution, SharedSolutionComment> solutionCommentService) {
        this.challengeCommentService = challengeCommentService;
        this.solutionCommentService = solutionCommentService;
    }

    @RequestMapping(path = "challenge")
    public List<ChallengeComment> getChallengeComments(@RequestParam long parentId) {
        return challengeCommentService.getComments(new Challenge(parentId));
    }


    @RequestMapping(path = "solution")
    public List<SharedSolutionComment> getSolutionComments(@RequestParam long parentId) {
        return solutionCommentService.getComments(new SharedSolution(parentId));
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "challenge", method = RequestMethod.POST)
    public ChallengeComment addChallengeComment(@RequestBody NewComment input) {
        Challenge challenge = new Challenge(input.getParentId());
        ChallengeComment challengeComment = new ChallengeComment(input.getText(), challenge);
        return challengeCommentService.addComment(challengeComment);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "solution", method = RequestMethod.POST)
    public SharedSolutionComment addSolutionComment(@RequestBody NewComment input) {
        SharedSolution solution = new SharedSolution(input.getParentId());
        SharedSolutionComment solutionComment = new SharedSolutionComment(input.getText(), solution);
        return solutionCommentService.addComment(solutionComment);
    }

    @PreAuthorize("hasPermission(#commentId, 'ChallengeComment', 'modify')")
    @PostMapping("challenge/{commentId}")
    public ChallengeComment updateChallengeComment(@PathVariable long commentId, @RequestBody String text) {
        ChallengeComment comment = new ChallengeComment();
        comment.setCommentId(commentId);
        comment.setText(text);
        return challengeCommentService.updateComment(comment);
    }

    @PreAuthorize("hasPermission(#commentId, 'SharedSolutionComment', 'modify')")
    @PostMapping("solution/{commentId}")
    public SharedSolutionComment updateSolutionComment(@PathVariable long commentId, @RequestBody String text) {
        SharedSolutionComment comment = new SharedSolutionComment();
        comment.setCommentId(commentId);
        comment.setText(text);
        return solutionCommentService.updateComment(comment);
    }


    @PreAuthorize("hasPermission(#commentId, 'ChallengeComment', 'modify')")
    @DeleteMapping("challenge/{commentId}")
    public void deleteChallengeComment(@PathVariable long commentId) {
        challengeCommentService.deleteComment(commentId);
    }

    @PreAuthorize("hasPermission(#commentId, 'SharedSolutionComment', 'modify')")
    @DeleteMapping("solution/{commentId}")
    public void deleteSolutionComment(@PathVariable long commentId) {
        solutionCommentService.deleteComment(commentId);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("challenge/{commentId}/like")
    public int likeChallengeComment(@PathVariable long commentId, @RequestBody String like) {
        return challengeCommentService.updateLikes(commentId, Boolean.parseBoolean(like));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("solution/{commentId}/like")
    public int likeSolutionComment(@PathVariable long commentId, @RequestBody String like) {
        return solutionCommentService.updateLikes(commentId, Boolean.parseBoolean(like));
    }
}
