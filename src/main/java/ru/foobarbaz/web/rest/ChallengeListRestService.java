package ru.foobarbaz.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.foobarbaz.entity.ChallengeList;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.logic.ChallengeListService;
import ru.foobarbaz.repo.ChallengeListRepository;
import ru.foobarbaz.web.dto.NewChallengeList;
import ru.foobarbaz.web.view.ChallengeView;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/challenge-lists")
public class ChallengeListRestService {
    private ChallengeListService challengeListService;
    private ChallengeListRepository challengeListRepository;

    @Autowired
    public ChallengeListRestService(ChallengeListService challengeListService, ChallengeListRepository challengeListRepository) {
        this.challengeListService = challengeListService;
        this.challengeListRepository = challengeListRepository;
    }

    @RequestMapping
    public Page<ChallengeList> getChallengeLists(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false) String search){
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageable = PageRequest.of(page, 5, sort);
        return search == null
                ? challengeListRepository.findAll(pageable)
                : challengeListRepository.findAllByNameContainsIgnoreCase(search, pageable);
    }

    @JsonView(ChallengeView.Short.class)
    @RequestMapping(path = "{listId}")
    public ChallengeList getChallengeList(@PathVariable long listId){
        return challengeListService.getChallengeList(listId);
    }

    @RequestMapping(path = "author/{username}")
    public List<ChallengeList> getChallengeListsByAuthor(@PathVariable String username){
        return challengeListRepository.findAllByAuthorOrderByCreated(new User(username));
    }

    @RequestMapping(path = "random")
    public ChallengeList getRandomChallengeList(){
        return challengeListService.getRandomChallengeList();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.POST)
    public Long createChallengeList(@RequestBody NewChallengeList input){
        ChallengeList template = new ChallengeList();
        template.setName(input.getName());
        template.setDescription(input.getDescription());
        template.setChallenges(input.getChallenges().stream().map(Challenge::new).collect(Collectors.toList()));
        return challengeListService.createChallengeList(template).getChallengeListId();
    }

    @PreAuthorize("isAuthenticated() && hasPermission(#challengeListId, 'ChallengeList', 'modify')")
    @PostMapping(value = "/{challengeListId}")
    @JsonView(ChallengeView.Short.class)
    public ChallengeList updateChallengeList(
            @PathVariable long challengeListId,
            @RequestBody NewChallengeList input) {
        ChallengeList template = new ChallengeList();
        template.setChallengeListId(challengeListId);
        template.setName(input.getName());
        template.setDescription(input.getDescription());
        template.setChallenges(input.getChallenges().stream().map(Challenge::new).collect(Collectors.toList()));
        return challengeListService.updateChallengeList(template);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{challengeListId}/like", method = RequestMethod.POST)
    public int updateLike(
            @PathVariable long challengeListId,
            @RequestBody String like) {
        return challengeListService.updateLike(challengeListId, Boolean.valueOf(like)).getLikes().size();
    }

    @PreAuthorize("isAuthenticated() && hasPermission(#challengeListId, 'ChallengeList', 'modify')")
    @DeleteMapping(value = "/{challengeListId}")
    public void deleteChallengeList(@PathVariable long challengeListId){
        challengeListService.deleteChallengeList(challengeListId);
    }
}
