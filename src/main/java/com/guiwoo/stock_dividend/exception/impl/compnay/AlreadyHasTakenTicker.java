package com.guiwoo.stock_dividend.exception.impl.compnay;

import com.guiwoo.stock_dividend.exception.AbstractException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class AlreadyHasTakenTicker extends AbstractException {
    private String ticker;
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "❌ It's already taken ticker -> "+ticker;
    }
}
