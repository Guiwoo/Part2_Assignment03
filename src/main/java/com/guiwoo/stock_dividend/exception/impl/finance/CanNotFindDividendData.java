package com.guiwoo.stock_dividend.exception.impl.finance;

import com.guiwoo.stock_dividend.exception.AbstractException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class CanNotFindDividendData extends AbstractException {
    private Long id;
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "❌ Can't find Dividend by Company Id -> "+ id;
    }
}
