package com.guiwoo.stock_dividend.scapper;

import com.guiwoo.stock_dividend.model.Company;
import com.guiwoo.stock_dividend.model.Dividend;
import com.guiwoo.stock_dividend.model.ScrapResult;
import com.guiwoo.stock_dividend.model.constant.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper{
    private static final String URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String URL_TICKER ="https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400;

    @Override
    public ScrapResult scrap(Company company){
        var result = new ScrapResult();
        result.setCompany(company);

        try{
            long end = System.currentTimeMillis()/1000;

            String formatUrl = String.format(URL, company.getTicker(), START_TIME, end);
            Connection connect = Jsoup.connect(formatUrl);

            Elements parsingDivs = connect.get()
                    .getElementsByAttributeValue("data-test", "historical-prices");
            Element tbody = parsingDivs.get(0).children().get(1);

            List<Dividend> dividendList = new ArrayList<>();

            for(Element e : tbody.children()){
                String txt = e.text();
                if(!txt.endsWith("Dividend")){
                    continue;
                }
                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.parseInt(splits[1].replace(",",""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                if(month <0 ) throw new RuntimeException("Unexpected Month enum Value" + month);
                dividendList.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0),dividend));
            }
            result.setDividendEntities(dividendList);

        }catch (IOException e){
            // ToDo
            e.printStackTrace();
        }

        return result;
    }
    @Override
    public Company scrapeCompanyByTicker(String ticker){
        String url = String.format(URL_TICKER,ticker,ticker);
        try {
            Document document = Jsoup.connect(url).get();
            Element title_h1 = document.getElementsByTag("h1").get(0);
            String title = title_h1.text().split(" - ")[1].trim();

            return new Company(ticker,title);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
