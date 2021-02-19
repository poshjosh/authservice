package com.looseboxes.service.auth.web.rest.errors;

import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import static com.looseboxes.service.auth.web.rest.errors.ErrorConstants.PROBLEM_BASE_URL;

/**
 * @author hp
 */
public class AuthenticationRequiredException extends AbstractThrowableProblem{

    private static final long serialVersionUID = 1L;

    public AuthenticationRequiredException() {
        this(null);
    }

    public AuthenticationRequiredException(String detail) {
        super(URI.create(PROBLEM_BASE_URL + "/authentication-required"), 
                "Authentication required", Status.UNAUTHORIZED, detail);
    }
}
