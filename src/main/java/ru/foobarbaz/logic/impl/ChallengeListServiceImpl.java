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
import ru.foobarbaz.entity.user.UserAccount;
import ru.foobarbaz.logic.ChallengeListService;
import ru.foobarbaz.logic.ChallengeService;
import ru.foobarbaz.logic.RatingService;
import ru.foobarbaz.repo.ChallengeListRepository;
import ru.foobarbaz.repo.UserAccountRepository;

import javax.transaction.Transactional;
import java.util.Date;

@Service
public class ChallengeListServiceImpl implements ChallengeListService {
    private ChallengeListRepository challengeListRepository;
    private UserAccountRepository userAccountRepository;
    private ChallengeService challengeService;
    private RatingService ratingService;

    @Autowired
    public ChallengeListServiceImpl(
            ChallengeListRepository challengeListRepository,
            UserAccountRepository userAccountRepository,
            RatingService ratingService,
            ChallengeService challengeService) {
        this.challengeListRepository = challengeListRepository;
        this.userAccountRepository = userAccountRepository;
        this.ratingService = ratingService;
        this.challengeService = challengeService;
    }

    @Override
    @Transactional
    public ChallengeList createChallengeList(ChallengeList template) {
        ChallengeList challengeList = new ChallengeList();
        BeanUtils.copyProperties(template, challengeList);
        String author = SecurityContextHolder.getContext().getAuthentication().getName();
        challengeList.setAuthor(new User(author));
        challengeList.setCreated(new Date());

        UserAccount account = userAccountRepository.getOne(author);
        account.setChallengeLists(account.getChallengeLists() + 1);
        userAccountRepository.save(account);
        return challengeListRepository.save(challengeList);
    }

    @Override
    public ChallengeList getChallengeList(long challengeListId) {
        ChallengeList list = challengeListRepository.findById(challengeListId).orElseThrow(ResourceNotFoundException::new);
        challengeService.fillChallengeStatus(list.getChallenges());
        return list;
    }

    @Override
    public ChallengeList getRandomChallengeList() {
        long count = challengeListRepository.count();
        if (count == 0) return null;
        int index = (int) Math.floor(Math.random() * count);
        Page<ChallengeList> page = challengeListRepository.findAll(PageRequest.of(index, 1));
        return page.getContent().get(0);
    }

    @Override
    public ChallengeList updateLike(long challengeListId, boolean like) {
        ChallengeList challengeList = challengeListRepository.findById(challengeListId)
                .orElseThrow(ResourceNotFoundException::new);
        this.ratingService.updateLikes(challengeList, like);
        return challengeListRepository.save(challengeList);
    }
}
