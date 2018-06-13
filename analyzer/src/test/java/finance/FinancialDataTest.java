package finance;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.junit.Test;

import finance.analytics.dataexport.ExcelReport;
import finance.analytics.dataimport.WatchlistReader;
import finance.analytics.dataimport.YahooFinanceReader;
import finance.analytics.dataimport.ZacksExcelReader;
import finance.analytics.etf.PortfolioAnalyzer;
import finance.analytics.model.HitCalculator;
import finance.analytics.model.Stock;

public class FinancialDataTest {
	@Test
	public void testFinancialData() throws IOException {
		Collection<Stock> stocks = new ZacksExcelReader()
				.readExcel("inputData/zacks/zacks_custom_screen_2018-05-18.xlsx");

		new YahooFinanceReader().updateLatestStats(stocks);
		new YahooFinanceReader().addDividentHistory(stocks);

		Collection<Map<String, String>> portfolios = new ArrayList<>();
		

		PortfolioAnalyzer dgroAnalyzer = new PortfolioAnalyzer("DGRO", "etf/DGRO_holdings.xlsx").compareWith(stocks);
		portfolios.add(dgroAnalyzer.getResults(getOfficialData("21.22", "3.23", "0.92", "2.1", "39.92", "0.08")));
		PortfolioAnalyzer dvyAnalyzer = new PortfolioAnalyzer("DVY", "etf/DVY_holdings.xlsx").compareWith(stocks);
		portfolios.add(dvyAnalyzer.getResults(getOfficialData("17.76", "2.15", "0.69", "3.22", "33.85", "0.39")));
		PortfolioAnalyzer hdvAnalyzer = new PortfolioAnalyzer("HDV", "etf/HDV_holdings.xlsx").compareWith(stocks);
		portfolios.add(hdvAnalyzer.getResults(getOfficialData("20.68", "2.7", "0.76", "3.43", "25.43", "0.08")));
		PortfolioAnalyzer iusvAnalyzer = new PortfolioAnalyzer("IUSV", "etf/IUSV_holdings.xlsx").compareWith(stocks);
		portfolios.add(iusvAnalyzer.getResults(getOfficialData("19.66", "2.11", "1", "1.77", "26.8", "0.05")));
		PortfolioAnalyzer ivvAnalyzer = new PortfolioAnalyzer("IVV", "etf/IVV_holdings.xlsx").compareWith(stocks);
		portfolios.add(ivvAnalyzer.getResults(getOfficialData("23.58", "3.24", "1", "1.73", "36.83", "0.04")));
		

		Map<String, String> watchlist = new WatchlistReader("watchlist.csv").getData();

		new ExcelReport().create(stocks, portfolios, watchlist, "target/report_" + LocalDate.now().toString() + ".xlsx");

	}
	
	
	public Map<String, String> getOfficialData(String pe, String pb, String beta, String yield, String return3Year, String fee) {
		Map<String, String> officialData = new LinkedHashMap<>();
		officialData.put("Official P/E", pe);
		officialData.put("Official P/B", pb);
		officialData.put("Beta", beta);
		officialData.put("Yield", yield);
		officialData.put("3y Return", return3Year);
		officialData.put("Fee", fee);
							
		return officialData;
	}
}
