package ru.foobarbaz.web.view;

public interface ChallengeView {
    interface Description { }
    interface Status { }
    interface Short extends Status, Description { }
    interface Full extends Short, SolutionView.Full { }
}
