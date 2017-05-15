package ru.foobarbaz.logic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.foobarbaz.entity.ChallengeList;

public interface ChallengeListService {
    Page<ChallengeList> getChallengeLists(Pageable pageable);
    ChallengeList createChallengeList(ChallengeList challengeList);
    ChallengeList getChallengeList(Long challengeListId);
}
