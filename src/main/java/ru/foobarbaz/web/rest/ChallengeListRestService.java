package ru.foobarbaz.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.foobarbaz.entity.ChallengeList;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.logic.ChallengeListService;
import ru.foobarbaz.web.dto.NewChallengeList;

import java.util.stream.Collectors;

@RestController
@RequestMapping("api/challenge-lists")
public class ChallengeListRestService {
    private ChallengeListService challengeListService;

    @Autowired
    public ChallengeListRestService(ChallengeListService challengeListService) {
        this.challengeListService = challengeListService;
    }

    @RequestMapping
    public Page<ChallengeList> getChallengeLists(
            @RequestParam(required = false, defaultValue = "0") Integer page){
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageable = PageRequest.of(page, 10, sort);
        return challengeListService.getChallengeLists(pageable);
    }

    @RequestMapping(path = "{listId}")
    public ChallengeList getChallengeList(@PathVariable long listId){
        return challengeListService.getChallengeList(listId);
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
}
