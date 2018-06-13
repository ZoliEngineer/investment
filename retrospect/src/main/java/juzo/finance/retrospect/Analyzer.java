package juzo.finance.retrospect;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.histquotes2.HistoricalDividend;

public class Analyzer {
	private final double baseAmount = 1000;

	private final Portfolio portfolio;

	public Analyzer(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public double analyze() throws IOException {
		double originalNetWorth = 0;
		double newNetWorth = 0;

		for (Entry<String, Double> entry : portfolio.getStockList().entrySet()) {
			String stock = entry.getKey();
			double weight = entry.getValue();

			originalNetWorth += baseAmount * weight;

			Calendar baseDate = Calendar.getInstance();
			baseDate.setTime(Date.from(portfolio.getBaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

			yahoofinance.Stock yahooStock = YahooFinance.get(stock);
			List<HistoricalDividend> dividendHistory = yahooStock.getDividendHistory(baseDate);
			double originalPrice = getHistoricPrice(baseDate, yahooStock);
			double newPrice = yahooStock.getQuote().getPrice().doubleValue();

			
			double numberOfStocks = (baseAmount * weight) / originalPrice;

			double newWorth = numberOfStocks * newPrice;

			// dividends are NOT reinvested
			for (HistoricalDividend dividend : dividendHistory) {
				newWorth += dividend.getAdjDividend().doubleValue() * numberOfStocks;
			}

			newNetWorth += newWorth;
		}

		return ((newNetWorth / originalNetWorth) - 1) * 100;
	}

	private double getHistoricPrice(Calendar baseDate, yahoofinance.Stock yahooStock) {
		List<HistoricalQuote> priceHistory = getPriceUntilSucceed(baseDate, yahooStock);
		
		double originalPrice = priceHistory.get(0).getClose().doubleValue();
		return originalPrice;
	}
	
	private List<HistoricalQuote> getPriceUntilSucceed(Calendar baseDate, yahoofinance.Stock yahooStock){
		try {
			return yahooStock.getHistory(baseDate, baseDate, Interval.DAILY);
		} catch (IOException e) {
			baseDate.add(Calendar.DAY_OF_MONTH, 1);
			System.err.println(baseDate.getTime());
			return getPriceUntilSucceed(baseDate, yahooStock);
		}
	}
}
