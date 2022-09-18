package com.guiwoo.stock_dividend.exception.impl.user;

import com.guiwoo.stock_dividend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class PasswordDoesNotMatch extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "❌ Password Does not match";
    }
}
