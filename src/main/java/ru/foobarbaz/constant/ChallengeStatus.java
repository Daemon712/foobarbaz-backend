package ru.foobarbaz.constant;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
public enum ChallengeStatus {
    NOT_STARTED,
    IN_PROGRESS,
    SOLVED
}
