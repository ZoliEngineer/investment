package finance.analytics.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import finance.analytics.dataimport.QuandlReader;

public class KeyIndicators {
	private static final Logger LOGGER = LoggerFactory.getLogger(KeyIndicators.class);

	private final Stock stock;
	private QuandlReader marketHistory = QuandlReader.INSTANCE;

	private static final double HIGH_GRADE_BOND_YIELD = 6;
	private static final ValueRatingPair<Double> DEFAULT = new ValueRatingPair<Double>(Double.NaN, false);
	private static final ValueRatingPair<String> DEFAULT_ST = new ValueRatingPair<String>("", false);

	public KeyIndicators(Stock stock) {
		this.stock = stock;
	}

	// Revenue or Sales
	public ValueRatingPair<Double> getRevenue() {
		double revenue = stock.getMarketData().getRevenue();
		return new ValueRatingPair<Double>(revenue, revenue > 500);
	}

	public ValueRatingPair<Double> getTotalAssets() {
		double totalAssets = lastYearsOf("assets");

		return new ValueRatingPair<Double>(totalAssets, false);
	}

	public ValueRatingPair<Double> getAssetsPerLiabilities() {
		double currentAssets = stock.getMarketData().getCurrentAssets();
		double currentLiabilities = stock.getMarketData().getCurrentLiabilities();

		return new ValueRatingPair<Double>(currentAssets / currentLiabilities, currentAssets / currentLiabilities > 2);

	}

	public ValueRatingPair<Double> getDebtRatio() {
		double longTermDebt = stock.getMarketData().getLongTermDebt();
		double workingCapital = stock.getMarketData().getNetCurrentAssets();
		double debtRatio = longTermDebt / workingCapital;

		return new ValueRatingPair<Double>(debtRatio, longTermDebt < workingCapital);
	}

	public ValueRatingPair<String> getEarningsHistory() { // should be 10 years
		List<Double> historicEarnings = Collections.emptyList();
		try {
			historicEarnings = marketHistory.getHistoryOf(stock.getTicker(), "eps") // epsdil
					.stream().filter(StringUtils::isNotEmpty).map(Double::valueOf).collect(Collectors.toList());
		} catch (NumberFormatException e) {
			LOGGER.error("Bad format for earnings: " + stock.getTicker() + " "
					+ marketHistory.getHistoryOf(stock.getTicker(), "eps"), e);

			return DEFAULT_ST;
		}
		int deficitYears = historicEarnings.stream().filter(yearlyEPS -> yearlyEPS < 0).collect(Collectors.toList())
				.size();
		return new ValueRatingPair<String>(historicEarnings.toString(), deficitYears == 0);

	}

	public ValueRatingPair<Double> getEarningsGrowth() { // should be 10 years
		List<Double> historicEarnings = Collections.emptyList();
		try {
			historicEarnings = marketHistory.getHistoryOf(stock.getTicker(), "eps") // epsdil
					.stream().filter(StringUtils::isNotEmpty).map(Double::valueOf).collect(Collectors.toList());
		} catch (NumberFormatException e) {
			LOGGER.error("Bad format for earnings: " + stock.getTicker() + " "
					+ marketHistory.getHistoryOf(stock.getTicker(), "eps"), e);
			return DEFAULT;
		}

		int availableYears = historicEarnings.size();
		if (availableYears >= 5) {
			double oldAverage = average(historicEarnings.get(0), historicEarnings.get(1), historicEarnings.get(2));
			double newAverage = average(historicEarnings.get(availableYears - 3),
					historicEarnings.get(availableYears - 2), historicEarnings.get(availableYears - 1));

			return new ValueRatingPair<Double>(newAverage / oldAverage,
					(newAverage / oldAverage > 1.2 && newAverage > 0));
		} else {
			return DEFAULT;
		}

	}

	public ValueRatingPair<String> getDividendHistory() { // should be 20 years
		List<Double> historicDividends = marketHistory.getHistoryOf(stock.getTicker(), "dps").stream()
				.map(Double::valueOf).collect(Collectors.toList());

		int deficitYears = historicDividends.stream().filter(yearlyEPS -> yearlyEPS == 0).collect(Collectors.toList())
				.size();
		if (historicDividends.size() == 0) {
			deficitYears = 10;
		}
		return new ValueRatingPair<String>(historicDividends.toString(), deficitYears == 0);
	}

	public ValueRatingPair<Double> getPricePerEarnings() {
		double pe = stock.getMarketData().getPe();
		return new ValueRatingPair<Double>(pe, pe < (100 / HIGH_GRADE_BOND_YIELD));
	}

	public ValueRatingPair<Double> getAdjustedPricePerEarnings() {
		double pe = stock.getPrice() / stock.getIndicators().getAverageEarnings().value;
		return new ValueRatingPair<Double>(pe, pe < (100 / HIGH_GRADE_BOND_YIELD));
	}

	public ValueRatingPair<Double> getBookValuePerShare() {
		double bvps = stock.getMarketData().getBookValuePerShare();
		return new ValueRatingPair<Double>(bvps, bvps * 1.5 > stock.getPrice());
	}

	public ValueRatingPair<Double> getRecommendedPrice() {
		double eps = stock.getMarketData().getEps();
		double bvps = stock.getMarketData().getBookValuePerShare();
		double recommendedPrice = Math.sqrt(100 / HIGH_GRADE_BOND_YIELD * eps * bvps);
		return new ValueRatingPair<Double>(recommendedPrice, recommendedPrice > stock.getPrice());
	}

	public ValueRatingPair<String> getPriceLowHigh() {
		String lowHigh = "" + stock.getMarketData().getPrice52Low() + "/" + stock.getPrice() + "/"
				+ stock.getMarketData().getPrice52High();
		double meanPrice = (stock.getMarketData().getPrice52Low() + stock.getMarketData().getPrice52High()) / 2;

		return new ValueRatingPair<String>(lowHigh, stock.getPrice() < meanPrice);
	}

	public ValueRatingPair<Double> getShillerCAPE() {
		// p/e(10yearsaverage adj inflation) < 25 (shiller cape)
		throw new UnsupportedOperationException("Not implemented");
	}

	public ValueRatingPair<Double> getProfitMargin() {
		double profit = stock.getMarketData().getNetIncome();
		double revenue = stock.getMarketData().getAnnualSales();

		return new ValueRatingPair<Double>(profit / revenue, profit / revenue > 0.2);
	}

	public ValueRatingPair<Double> getProfitOnCapital() {
		double profit = stock.getMarketData().getNetIncome();
		double capital = stock.getMarketData().getBookvalue();

		return new ValueRatingPair<Double>(profit / capital, profit / capital > 0.20); // 12%-25% and up
	}

	// ROIC
	public ValueRatingPair<Double> getReturnOnInvestedCapital() {
		return new ValueRatingPair<Double>(stock.getMarketData().getROI(), stock.getMarketData().getROI() > 10);
	}

	public ValueRatingPair<Double> getTimesInterestEarned() {
		double earningsBeforeInterestAndTaxes = lastYearsOf("ebit");
		double interest = lastYearsOf("intexp");

		if (Double.isNaN(earningsBeforeInterestAndTaxes) || Double.isNaN(interest)) {
			return DEFAULT;
		}

		return new ValueRatingPair<Double>(earningsBeforeInterestAndTaxes / interest,
				earningsBeforeInterestAndTaxes / interest > 5);
	}

	public ValueRatingPair<Double> getDilutedEPSRatio() {

		List<String> historicEarnings = marketHistory.getHistoryOf(stock.getTicker(), "eps");
		List<String> historicDiluted = marketHistory.getHistoryOf(stock.getTicker(), "epsdil");

		if (isAnyEmpty(historicEarnings, historicDiluted)) {
			return DEFAULT;
		}

		int tail = historicDiluted.size() - 1;
		while (tail > 0 && StringUtils.isEmpty(historicEarnings.get(tail))) {
			tail--;
		}

		double currentEPS = Double.valueOf(historicEarnings.get(tail));
		double dilutedEPS = Double.valueOf(historicDiluted.get(tail));

		return new ValueRatingPair<Double>(dilutedEPS / currentEPS, dilutedEPS / currentEPS > 0.9); // 12%-25% and up
	}

	public ValueRatingPair<Double> getAverageDividend() {
		List<Double> historicDividends = Collections.emptyList();
		try {
			historicDividends = marketHistory.getHistoryOf(stock.getTicker(), "dps") // epsdil
					.stream().filter(StringUtils::isNotEmpty).map(Double::valueOf).collect(Collectors.toList());
		} catch (NumberFormatException e) {
			LOGGER.error("Bad format for dividends: " + stock.getTicker() + " "
					+ marketHistory.getHistoryOf(stock.getTicker(), "dps"), e);

			return DEFAULT;
		}

		return new ValueRatingPair<Double>(averageOf(historicDividends), false);
	}

	public ValueRatingPair<Double> getAverageEarnings() {
		List<Double> historicEarnings = Collections.emptyList();
		try {
			historicEarnings = marketHistory.getHistoryOf(stock.getTicker(), "eps") // epsdil
					.stream().filter(StringUtils::isNotEmpty).map(Double::valueOf).collect(Collectors.toList());
		} catch (NumberFormatException e) {
			LOGGER.error("Bad format for earnings: " + stock.getTicker() + " "
					+ marketHistory.getHistoryOf(stock.getTicker(), "eps"), e);

			return DEFAULT;
		}

		return new ValueRatingPair<Double>(averageOf(historicEarnings), false);

	}

	public ValueRatingPair<Double> getPriceToBook() {
		double price = stock.getPrice();
		double bookValue = stock.getMarketData().getBookValuePerShare();

		return new ValueRatingPair<Double>(price / bookValue, false);

	}

	public ValueRatingPair<Double> getDividentYield() {
		double price = stock.getPrice();
		double dividend = stock.getMarketData().getLastDividend();

		double yield = (dividend / price) * 100;

		return new ValueRatingPair<Double>(yield, yield >= 3);
	}

	public ValueRatingPair<Double> getAverageDividentYield() {
		double price = stock.getPrice();
		double dividend = getAverageDividend().value;

		double yield = (dividend / price) * 100;

		return new ValueRatingPair<Double>(yield, yield >= 3);
	}
	
	private List<Double> getBuyBacks(){
		List<Double> buybackRatio = Collections.emptyList();
		try {
			List<Double> buybackHistory	 = marketHistory.getHistoryOf(stock.getTicker(), "ncfcommon").stream()
					.map(this::toDouble).map(x -> x * -1)
					.collect(Collectors.toList());
			List<Double> marketCapHistory = marketHistory.getHistoryOf(stock.getTicker(), "marketcap").stream()
					.map(this::toDouble)
					.collect(Collectors.toList());
			
			buybackRatio = IntStream.range(0, Math.min(buybackHistory.size(), marketCapHistory.size()))
				.mapToObj(i -> 100 * (buybackHistory.get(i) / marketCapHistory.get(i))).map(this::round)
				.collect(Collectors.toList());
			return buybackRatio;
			
		} catch (NumberFormatException e) {
			LOGGER.error("Bad format for earnings: " + stock.getTicker() + " "
					+ marketHistory.getHistoryOf(stock.getTicker(), "ncfcommon"), e);

			return Collections.emptyList();
		}
		
		
	}

	public ValueRatingPair<String> getBuybackHistory() {		
		return new ValueRatingPair<String>(getBuyBacks().toString(), false);
	}
	
	public ValueRatingPair<Double> getBuyBackAverage(){
		return new ValueRatingPair<Double>(averageOf(getBuyBacks()), false);
	}
	
	public ValueRatingPair<Double> getBuyBackLast(){
		List<Double> buyBackHistory = getBuyBacks();
		return new ValueRatingPair<Double>(buyBackHistory.get(buyBackHistory.size()-1), false);
	}

	private List<Double> getReturns() { 	
		List<Double> returnRatio = Collections.emptyList();
		try {
			List<Double> buybackHistory	 = marketHistory.getHistoryOf(stock.getTicker(), "ncfcommon").stream()
					.map(this::toDouble).map(x -> x * -1)
					.collect(Collectors.toList());
			List<Double> dividendHistory	 = marketHistory.getHistoryOf(stock.getTicker(), "ncfdiv").stream()
					.map(this::toDouble).map(x -> x * -1)
					.collect(Collectors.toList());
			List<Double> marketCapHistory = marketHistory.getHistoryOf(stock.getTicker(), "marketcap").stream()
					.map(this::toDouble)
					.collect(Collectors.toList());
			
			returnRatio = IntStream.range(0, Math.min(buybackHistory.size(), marketCapHistory.size()))
				.mapToObj(i -> 100 * ((buybackHistory.get(i) + dividendHistory.get(i)) / marketCapHistory.get(i))).map(this::round)
				.collect(Collectors.toList());
			
			return returnRatio;
			
		} catch (NumberFormatException e) {
			LOGGER.error("Bad format for earnings: " + stock.getTicker() + " "
					+ marketHistory.getHistoryOf(stock.getTicker(), "ncfcommon"), e);

			return Collections.emptyList();
		}
	}
	
	public ValueRatingPair<String> getReturnHistory() {		
		return new ValueRatingPair<String>(getReturns().toString(), false);
	}
	
	public ValueRatingPair<Double> getReturnAverage(){
		return new ValueRatingPair<Double>(averageOf(getReturns()), false);
	}
	
	public ValueRatingPair<Double> getReturnLast(){
		List<Double> buyBackHistory = getReturns();
		double lastReturn = buyBackHistory.isEmpty() ? Double.NaN : buyBackHistory.get(buyBackHistory.size()-1);
		return new ValueRatingPair<Double>(lastReturn, false);
	}

	// institutional overship >60%
	// cost < working capital
	// s%p rating

	public static class ValueRatingPair<T> {
		public final T value;
		public final Rating rating;

		public ValueRatingPair(T value, Rating rating) {
			this.value = value;
			this.rating = rating;
		}

		public ValueRatingPair(T value, boolean ratingCondition) {
			this(value, ratingCondition ? Rating.A : Rating.B);
		}
	}

	public double getEarningsPer100$() {
		double eps = stock.getMarketData().getEps();
		double price = stock.getPrice();

		return (100 / price) * eps;
	}

	public double getBookValuePer100$() {
		double bvps = stock.getMarketData().getBookValuePerShare();
		double price = stock.getPrice();

		return (100 / price) * bvps;
	}

	public enum Rating {
		A, // Graham pass
		B; // Graham no pass
	};

	private double average(double... values) {
		double aggregate = 0;
		for (double value : values) {
			aggregate += value;
		}
		return aggregate / values.length;
	}

	private double lastYearsOf(String indicator) {
		List<String> history = marketHistory.getHistoryOf(stock.getTicker(), indicator);
		if (isAnyEmpty(history)) {
			return Double.NaN;
		}
		String lastValue = history.get(history.size() - 1);
		if (StringUtils.isEmpty(lastValue)) {
			return Double.NaN;
		}
		return Double.valueOf(lastValue);
	}

	private boolean isAnyEmpty(List<String>... lists) {
		for (List<String> list : lists) {
			if (list.isEmpty() || StringUtils.isAllEmpty(list.toArray(new String[list.size()]))) {
				return true;
			}
		}
		return false;
	}
	
	
	private double round(double value, int precision) {
		return Double.isFinite(value) ? BigDecimal.valueOf(value).setScale(precision, BigDecimal.ROUND_HALF_DOWN).doubleValue()
				: Double.NaN;
	}

	private double round(double value) {
		return round(value, 2);
	}
	
	private double toDouble(String value) {
		if(StringUtils.isEmpty(value)) {
			return Double.NaN;
		} else return Double.valueOf(value);
	}

	private double averageOf(List<Double> values) {
		return new Mean()
				.evaluate(ArrayUtils.toPrimitive(values.toArray(new Double[values.size()])));
	}
}
