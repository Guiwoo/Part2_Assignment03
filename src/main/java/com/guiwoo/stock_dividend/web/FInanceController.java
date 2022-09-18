package com.guiwoo.stock_dividend.web;

import com.guiwoo.stock_dividend.model.ScrapResult;
import com.guiwoo.stock_dividend.service.FinanceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance")
@AllArgsConstructor
public class FInanceController {
    private final FinanceService financeService;

    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> findCompany(@PathVariable String companyName) {
        ScrapResult dividendByCompanyName = financeService.getDividendByCompanyName(companyName);
        return ResponseEntity.ok(dividendByCompanyName);
    }


}
