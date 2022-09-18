package com.guiwoo.stock_dividend.exception.impl.user;

import com.guiwoo.stock_dividend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class AlreadyHasTakenUserException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "❌ It's already taken that username";
    }
}
