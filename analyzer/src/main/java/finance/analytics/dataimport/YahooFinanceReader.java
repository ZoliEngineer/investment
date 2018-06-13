package finance.analytics.dataimport;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import finance.analytics.model.DividendPattern;
import finance.analytics.model.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes2.HistoricalDividend;

public class YahooFinanceReader {
	private static final Logger LOGGER = LoggerFactory.getLogger(YahooFinanceReader.class);

	public void updateLatestStats(Iterable<Stock> stocks) {
		for (Stock stock : stocks) {
			updateLatestStats(stock);
		}
	}

	public void updateLatestStats(Stock stock) {
		String symbol = stock.getTicker();
		try {
			yahoofinance.Stock yahooStock = YahooFinance.get(symbol);
			update(stock, yahooStock);
		} catch (IOException e) {
			LOGGER.error("Error connecting to Yahoo Finance. Keeping obsolete stock price for: " + symbol, e);
		}
	}
	
	
	public void addDividentHistory(Stock stock) {
		String symbol = stock.getTicker();
		try {
			yahoofinance.Stock yahooStock = YahooFinance.get(symbol);
			LOGGER.debug("Updating divident history for {}", symbol);
			Calendar calendar = Calendar.getInstance();
			calendar.set(1990, 0, 0);			
			List<HistoricalDividend> dividendHistory = yahooStock.getDividendHistory(calendar);
			stock.getMarketData().setDividendHistory(new DividendPattern(dividendHistory));
		} catch (IOException e) {
			LOGGER.error("Error connecting to Yahoo Finance. Keeping obsolete stock price for: " + symbol, e);
		}
	}

	public void addDividentHistory(Collection<Stock> stocks) {
		for (Stock stock : stocks) {
			addDividentHistory(stock);
		}
	}
	
	private void update(Stock stock, yahoofinance.Stock yahooStock) {
		logChange("price", stock.getTicker(), stock.getPrice(), yahooStock.getQuote().getPrice());		
		logChange("bvps",  stock.getTicker(), stock.getMarketData().getBookValuePerShare(), yahooStock.getStats().getBookValuePerShare());
		logChange("eps",  stock.getTicker(), stock.getMarketData().getEps(), yahooStock.getStats().getEps());
		logChange("pe",  stock.getTicker(), stock.getMarketData().getPe(), yahooStock.getStats().getPe());
		logChange("revenue",  stock.getTicker(), stock.getMarketData().getRevenue(), yahooStock.getStats().getRevenue());
		logChange("roe",  stock.getTicker(), stock.getMarketData().getROE(), yahooStock.getStats().getROE());
		
	
		if (yahooStock.getQuote().getPrice() != null) {			
			stock.setPrice(yahooStock.getQuote().getPrice().doubleValue());
		}
		
		if(yahooStock.getStats().getBookValuePerShare() != null)
			stock.getMarketData().setBookValuePerShare(yahooStock.getStats().getBookValuePerShare().doubleValue());
		if(yahooStock.getStats().getEps() != null)
			stock.getMarketData().setEps(yahooStock.getStats().getEps().doubleValue()); 
		if(yahooStock.getStats().getPe() != null)
			stock.getMarketData().setPe(yahooStock.getStats().getPe().doubleValue());
		if(yahooStock.getStats().getRevenue() != null)
			stock.getMarketData().setRevenue(yahooStock.getStats().getRevenue().doubleValue());
//		if(yahooStock.getStats().getROE() != null || !yahooStock.getStats().getROE().equals(BigDecimal.ZERO))
//			stock.getMarketData().setROE(yahooStock.getStats().getROE().doubleValue());
//		
//		
		
	}
	
	private void logChange(String stock, String metric, double oldValue, BigDecimal newValue) {
		LOGGER.info("Updating {} for {} from {} to {}", stock, metric, oldValue,
				newValue);
	}

}
