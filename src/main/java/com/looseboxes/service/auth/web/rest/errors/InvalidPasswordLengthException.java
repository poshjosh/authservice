package com.looseboxes.service.auth.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class InvalidPasswordLengthException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public InvalidPasswordLengthException() {
        super(ErrorConstants.INVALID_PASSWORD_TYPE, "Incorrect password length", Status.BAD_REQUEST);
    }
}
