package ru.foobarbaz.web.view;

public interface SharedSolutionView {
    interface Author { }
    interface ChallengeInfo extends ChallengeView.Description { }
    interface Full extends Author, ChallengeInfo, ChallengeView.Full { }
}
