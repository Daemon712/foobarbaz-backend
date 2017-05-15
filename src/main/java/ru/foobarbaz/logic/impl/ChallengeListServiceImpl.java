package ru.foobarbaz.logic.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.foobarbaz.entity.ChallengeList;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.logic.ChallengeListService;
import ru.foobarbaz.repo.ChallengeListRepository;

import java.util.Date;

@Service
public class ChallengeListServiceImpl implements ChallengeListService {
    private ChallengeListRepository repository;

    @Autowired
    public ChallengeListServiceImpl(ChallengeListRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<ChallengeList> getChallengeLists(Pageable pageable) {
        return repository.findAll(pageable);
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
        return repository.findById(challengeListId).orElseThrow(ResourceNotFoundException::new);
    }
}
