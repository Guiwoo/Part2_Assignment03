package com.guiwoo.stock_dividend.web;

import com.guiwoo.stock_dividend.model.Company;
import com.guiwoo.stock_dividend.model.constant.CacheKey;
import com.guiwoo.stock_dividend.persist.entity.CompanyEntity;
import com.guiwoo.stock_dividend.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("autocomplete")
    public ResponseEntity<?> autoComplete(@RequestParam String keyword) {
//        List<String> strings = companyService.autoComplete(keyword);
        //DB 부하를 줄수 있으니 조심해야함
        List<String> strings = companyService.companyNamesByKeyword(keyword);
        return ResponseEntity.ok(strings);
    }

    @GetMapping("")
    public ResponseEntity<?> findAllCompany(final Pageable pageable){
        Page<CompanyEntity> companyEntities = companyService.allCompany(pageable);
        return ResponseEntity.ok(companyEntities);
    }

    @GetMapping("find")
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable){
        Page<CompanyEntity> companies = this.companyService.allCompany(pageable);
        return ResponseEntity.ok(companies);

    }

    @PostMapping("")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> createCompany(@RequestBody Company req){
        String ticker = req.getTicker().trim();
        if(ObjectUtils.isEmpty(ticker)){
            throw new RuntimeException("Ticker is empty");
        }
        Company save = companyService.save(ticker);
        companyService.addAutoCompleteKeyword(save.getName());
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker){
        String companyName = companyService.deleteCompany(ticker);
        companyService.clearFinanceCache(companyName);
        return ResponseEntity.ok(companyName);
    }
}
