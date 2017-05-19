package ru.foobarbaz.logic.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.foobarbaz.entity.ChallengeList;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.logic.ChallengeListService;
import ru.foobarbaz.logic.ChallengeService;
import ru.foobarbaz.repo.ChallengeListRepository;

import java.util.Date;

@Service
public class ChallengeListServiceImpl implements ChallengeListService {
    private ChallengeListRepository repository;
    private ChallengeService challengeService;

    @Autowired
    public ChallengeListServiceImpl(
            ChallengeListRepository repository,
            ChallengeService challengeService) {
        this.repository = repository;
        this.challengeService = challengeService;
    }

    @Override
    public ChallengeList createChallengeList(ChallengeList template) {
        ChallengeList challengeList = new ChallengeList();
        BeanUtils.copyProperties(template, challengeList);
        String author = SecurityContextHolder.getContext().getAuthentication().getName();
        template.setAuthor(new User(author));
        template.setCreated(new Date());
        return repository.save(template);
    }

    @Override
    public ChallengeList getChallengeList(Long challengeListId) {
        ChallengeList list = repository.findById(challengeListId).orElseThrow(ResourceNotFoundException::new);
        challengeService.fillChallengeStatus(list.getChallenges());
        return list;
    }

    @Override
    public ChallengeList getRandomChallengeList() {
        long count = repository.count();
        if (count == 0) return null;
        int index = (int) Math.floor(Math.random() * count);
        Page<ChallengeList> page = repository.findAll(PageRequest.of(index, 1));
        return page.getContent().get(0);
    }
}
