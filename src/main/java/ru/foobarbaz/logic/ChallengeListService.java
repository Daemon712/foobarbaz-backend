package ru.foobarbaz.logic;

import ru.foobarbaz.entity.ChallengeList;

public interface ChallengeListService {
    ChallengeList createChallengeList(ChallengeList challengeList);
    ChallengeList getChallengeList(long challengeListId);
    ChallengeList getRandomChallengeList();
    ChallengeList updateLike(long challengeListId, boolean like);
}
