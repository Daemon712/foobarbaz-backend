package ru.foobarbaz.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.comment.ChallengeComment;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.repo.ChallengeCommentRepository;
import ru.foobarbaz.web.dto.NewComment;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/challenge-comments")
public class ChallengeCommentRestService {
    private ChallengeCommentRepository challengeCommentRepository;

    @Autowired
    public ChallengeCommentRestService(
            ChallengeCommentRepository challengeCommentRepository) {
        this.challengeCommentRepository = challengeCommentRepository;
    }

    @RequestMapping
    public List<ChallengeComment> getComments(@RequestParam long parentId){
        return challengeCommentRepository.findAllByChallengeOrderByCreated(new Challenge(parentId));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ChallengeComment addComment(@RequestBody NewComment input){
        ChallengeComment comment = new ChallengeComment();
        comment.setChallenge(new Challenge(input.getParentId()));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        comment.setAuthor(new User(username));
        comment.setCreated(new Date());
        comment.setText(input.getText());
        return challengeCommentRepository.save(comment);
    }

    @RequestMapping(path = "{commentId}", method = RequestMethod.POST)
    public int likeComment(@PathVariable long commentId, @RequestBody String like){
        return Boolean.valueOf(like) ? 1 : -1;
    }
}
