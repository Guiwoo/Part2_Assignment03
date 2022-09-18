package com.guiwoo.stock_dividend.service;

import com.guiwoo.stock_dividend.exception.impl.compnay.AlreadyHasTakenTicker;
import com.guiwoo.stock_dividend.exception.impl.compnay.FailToGetDataByTicker;
import com.guiwoo.stock_dividend.exception.impl.compnay.NoCompanyException;
import com.guiwoo.stock_dividend.model.Company;
import com.guiwoo.stock_dividend.model.ScrapResult;
import com.guiwoo.stock_dividend.model.constant.CacheKey;
import com.guiwoo.stock_dividend.persist.entity.CompanyEntity;
import com.guiwoo.stock_dividend.persist.entity.DividendEntity;
import com.guiwoo.stock_dividend.persist.repository.CompanyRepository;
import com.guiwoo.stock_dividend.persist.repository.DividendRepository;
import com.guiwoo.stock_dividend.scapper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Trie;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CompanyService {

    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Trie trie;
    private final CacheManager redisCacheManager;

    public Company save(String ticker){
        boolean exists = companyRepository.existsByTicker(ticker);
        if(exists){
            throw new AlreadyHasTakenTicker(ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }
    private Company storeCompanyAndDividend(String ticker){
        // ticker 를 기준으로 회사를 스크랩
        Company company = this.yahooFinanceScraper.scrapeCompanyByTicker(ticker);
        if(ObjectUtils.isEmpty(company)){
            throw new FailToGetDataByTicker(ticker);
        }
        // 해당 회사가 존재할 경우 , 배당금 정보를 스크랩
        ScrapResult scrap = this.yahooFinanceScraper.scrap(company);
        // 스크랩 결과
        CompanyEntity cEntity = this.companyRepository.save(new CompanyEntity(company));

        List<DividendEntity> dividendEntities = scrap.getDividendEntities().stream()
                .map(e -> new DividendEntity(cEntity.getId(), e))
                .collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntities);
        log.info("Scrap Done => Save total : "+dividendEntities.size());
        return company;
    }
    public Page<CompanyEntity> allCompany(Pageable pageable){
        return companyRepository.findAll(pageable);
    }
    public void addAutoCompleteKeyword(String keyword){
        trie.put(keyword,null);
        log.info("Added AutoComplete Keyword => "+keyword);
    }
    public List<String> autoComplete(String prefix){
        return (List<String>) trie.prefixMap(prefix).keySet()
                .stream().collect(Collectors.toList());
    }
    public void deleteComplete(String keyword){
        trie.remove(keyword);
        log.info("Delete Keyword in Trie => "+keyword);
    }

    public List<String> companyNamesByKeyword(String keyword){
        Pageable limit = PageRequest.of(0,10);
        Page<CompanyEntity> c =
                companyRepository.findByNameStartingWithIgnoreCase(keyword,limit);

        return c.stream().map(CompanyEntity::getName)
                .collect(Collectors.toList());
    }
    public String deleteCompany(String ticker){
        CompanyEntity companyEntity = companyRepository.findByTicker(ticker)
                .orElseThrow(NoCompanyException::new);
        dividendRepository.deleteAllByCompanyId(companyEntity.getId());
        log.info("Success Delete Dividend By Company");
        companyRepository.delete(companyEntity);
        log.info("Success Delete Company");
        return companyEntity.getName();
    }
    public void clearFinanceCache(String companyName){
        redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
        log.info("Cache Data Cleared");
    }
}
