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
import ru.foobarbaz.logic.ChallengeListService;
import ru.foobarbaz.repo.ChallengeListRepository;
import ru.foobarbaz.web.dto.NewChallengeList;
import ru.foobarbaz.web.view.ChallengeView;

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
        PageRequest pageable = PageRequest.of(page, 10, sort);
        return search == null
                ? challengeListRepository.findAll(pageable)
                : challengeListRepository.findAllByNameContainsIgnoreCase(search, pageable);
    }

    @JsonView(ChallengeView.Status.class)
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
