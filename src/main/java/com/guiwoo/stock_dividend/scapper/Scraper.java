package com.guiwoo.stock_dividend.scapper;

import com.guiwoo.stock_dividend.model.Company;
import com.guiwoo.stock_dividend.model.ScrapResult;

public interface Scraper {
    Company scrapeCompanyByTicker(String ticker);
    ScrapResult scrap(Company company);
}
