package com.guiwoo.stock_dividend.exception.impl.user;

import com.guiwoo.stock_dividend.exception.AbstractException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
public class CanNotFindUsername extends AbstractException {
    private String username;
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "❌ Can not find username -> "+username;
    }
}
