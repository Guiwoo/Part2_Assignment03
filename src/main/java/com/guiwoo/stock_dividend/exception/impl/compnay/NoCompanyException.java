package com.guiwoo.stock_dividend.exception.impl.compnay;

import com.guiwoo.stock_dividend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NoCompanyException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_GATEWAY.value();
    }

    @Override
    public String getMessage() {
        return "❌ There's no Company name";
    }
}
