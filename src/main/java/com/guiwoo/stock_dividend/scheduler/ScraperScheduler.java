package com.guiwoo.stock_dividend.scheduler;

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
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

//    @Scheduled(cron = "0/5 * * * * *")
//    public void test(){
//        System.out.println(LocalDateTime.now()+" "+"현재시간 5초 마다 실");
//    }

@Component
@AllArgsConstructor
@Slf4j
@EnableCaching
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;
    private static final Marker MESSAGE_MARKER = MarkerFactory.getMarker("SCHEDULE_SCRAP");
    //정책 주기 에 따라서
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrape.yahoo}")
    public void yahooFinanceScheduling(){
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companies = companyRepository.findAll();
        // 배당 정보를 스크래핑
        companies.forEach(
                (c)->{
                    Company comp = new Company(c.getName(),c.getTicker());
                    log.info(MESSAGE_MARKER,"Scrapping Started -> "+comp.getName());
                    ScrapResult scrap = yahooFinanceScraper.scrap(comp);
                    //스크랩 된 배당금 정보를 가지고
                    scrap.getDividendEntities().stream()
                            //dividendEntity 로 매핑
                            .map(a-> new DividendEntity(c.getId(),a))
                            //엘리먼트 하나씩 dividend 레퍼지토리 삽입 or 스
                            .forEach(
                                    (e)->{
                                        boolean exists = dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                                        if(!exists){
                                            dividendRepository.save(e);
                                        }
                                    }
                            );
                    // 사이트 부하 줄이기 위한 딜레이
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        log.info(MESSAGE_MARKER,"Scrapping Finished");
    }
}
