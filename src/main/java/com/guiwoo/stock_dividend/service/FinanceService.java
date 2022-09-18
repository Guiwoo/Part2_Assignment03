package com.guiwoo.stock_dividend.service;

import com.guiwoo.stock_dividend.exception.impl.compnay.NoCompanyException;
import com.guiwoo.stock_dividend.exception.impl.finance.CanNotFindDividendData;
import com.guiwoo.stock_dividend.model.Company;
import com.guiwoo.stock_dividend.model.Dividend;
import com.guiwoo.stock_dividend.model.ScrapResult;
import com.guiwoo.stock_dividend.model.constant.CacheKey;
import com.guiwoo.stock_dividend.persist.entity.CompanyEntity;
import com.guiwoo.stock_dividend.persist.entity.DividendEntity;
import com.guiwoo.stock_dividend.persist.repository.CompanyRepository;
import com.guiwoo.stock_dividend.persist.repository.DividendRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    /**
     * == 캐쉬 를 써야하는 이유 ==
     * 요청이 빈번하게 일어나는가 ? => 하나의 요청 별로 캐쉬에 저장하고 쓴다면 ?
     * 데이터 의 변경이 빈번하게 일어나는가 ? => 과거 배당 된 경우 는 없고, 회사이름 ? 잘없다.
     */

    @Cacheable(key="#companyName",value = CacheKey.KEY_FINANCE)//여기 키 벨류랑 레디스 서버 랑 다른거임
    public ScrapResult getDividendByCompanyName(String companyName){
        // 1. 회사명을 기준으로 회사 정보 접근
        CompanyEntity comp = companyRepository.findByName(companyName)
                .orElseThrow(() -> new NoCompanyException());
        // 2. 조회된 회사의 아이디로 배당금 을 조회
        List<DividendEntity> dividends = dividendRepository.findAllByCompanyId(comp.getId());
        if(dividends.isEmpty()){
            throw new CanNotFindDividendData(comp.getId());
        }
        List<Dividend> collect = dividends.stream()
                .map(e -> new Dividend(e.getDate(),e.getDividend()))
                .collect(Collectors.toList());

        // 3. 위 자료 를 조합 해서 반환
        return new ScrapResult(new Company(comp.getTicker(),comp.getName()), collect);
    }
}
