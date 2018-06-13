package finance;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import finance.analytics.model.DividendPattern;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes2.HistoricalDividend;

public class YahooFinanceApiTest {

	@Test
	public void testApi() throws IOException{
		Stock stock = YahooFinance.get("KO");
		 
		BigDecimal price = stock.getQuote().getPrice();
		BigDecimal change = stock.getQuote().getChangeInPercent();
		BigDecimal peg = stock.getStats().getPeg();
		BigDecimal dividend = stock.getDividend().getAnnualYieldPercent();
		 
		stock.print();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(1997, 0, 0);
		
		List<HistoricalDividend> dividentHistory = stock.getDividendHistory(calendar);
		dividentHistory.sort(new Comparator<HistoricalDividend>() {
			@Override
			public int compare(HistoricalDividend o1, HistoricalDividend o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});
		for(HistoricalDividend div : dividentHistory) {
			System.out.println(div);
		}
		
		System.out.println(new DividendPattern(dividentHistory).getPattern());
		
		
	}
	
	@Test
	public void testFileRead() throws URISyntaxException {
		System.out.println(SymbolGroup.SnP500.getSymbols());
	}
}
