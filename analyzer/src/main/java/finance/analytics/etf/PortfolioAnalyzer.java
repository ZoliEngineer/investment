package finance.analytics.etf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

import finance.analytics.model.Stock;
import finance.analytics.model.SumRatings;

public class PortfolioAnalyzer {

	private Map<String, Double> composition;
	private Map<String, String> results = new LinkedHashMap<>();

	public PortfolioAnalyzer(String portfolioName, String fileName) {
		results.put("Name", portfolioName);
		this.composition = new ISharesExcelReader(fileName).getComposition();
	}

	public PortfolioAnalyzer compareWith(Collection<Stock> stocks) {
		Map<String, Stock> stockMap = new HashMap<>();
		stocks.forEach(entry -> stockMap.put(entry.getTicker(), entry));

		List<Double> weightList = new ArrayList<>();
		List<Double> scoreList = new ArrayList<>();
		List<Double> peList = new ArrayList<>();
		List<Double> pricetobookList = new ArrayList<>();

		List<Double> priceList = new ArrayList<>();
		List<Double> epsList = new ArrayList<>();
		List<Double> bvpsList = new ArrayList<>();

		List<Double> eps100List = new ArrayList<>();
		List<Double> bvps100List = new ArrayList<>();

		for (Map.Entry<String, Double> entry : composition.entrySet()) {
			Stock stock = stockMap.get(entry.getKey());
			if (stock != null) {
				weightList.add(entry.getValue());
				peList.add(stock.getIndicators().getPricePerEarnings().value);
				pricetobookList.add(stock.getIndicators().getPriceToBook().value);
				bvpsList.add(stock.getMarketData().getBookValuePerShare());
				priceList.add(stock.getPrice());
				epsList.add(stock.getMarketData().getEps());
				double score = new SumRatings(stock).getPointRating();
				scoreList.add(Double.isNaN(score) ? 0 : score);

				bvps100List.add(stock.getIndicators().getBookValuePer100$());
				eps100List.add(stock.getIndicators().getEarningsPer100$());
				// System.out.println(entry.getKey() + "/" + entry.getValue() + "/" +
				// stock.getIndicators().getPriceToBook().value);
			} else {
				// System.out.println(entry.getKey() + " not found in S&P 500");
			}
		}

		double[] weights = weightList.stream().mapToDouble(Double::doubleValue).toArray();
		double[] pes = peList.stream().mapToDouble(Double::doubleValue).toArray();
		double[] priceToBooks = pricetobookList.stream().mapToDouble(Double::doubleValue).toArray();
		double[] scores = scoreList.stream().mapToDouble(Double::doubleValue).toArray();
		double[] bvps = bvpsList.stream().mapToDouble(Double::doubleValue).toArray();

		double[] prices = priceList.stream().mapToDouble(Double::doubleValue).toArray();
		double[] earnings = epsList.stream().mapToDouble(Double::doubleValue).toArray();

		double[] bvps100 = bvps100List.stream().mapToDouble(Double::doubleValue).toArray();
		double[] eps100 = eps100List.stream().mapToDouble(Double::doubleValue).toArray();

		double averagePE = 100 / new Mean().evaluate(eps100, weights);
		double averagePB = 100 / new Mean().evaluate(bvps100, weights);
		double averageScore = new Mean().evaluate(scores, weights);

		results.put("Average P/E", String.valueOf(round(averagePE)));
		results.put("Average P/B", String.valueOf(round(averagePB)));
		results.put("Average Score", String.valueOf(round(averageScore)));

		return this;

	}

	public Map<String, String> getResults(Map<String, String> additional) {
		results.putAll(additional);
		return results;
	}

	private static double round(double value) {
		return Double.isFinite(value) ? BigDecimal.valueOf(value).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue()
				: Double.NaN;
	}

}
