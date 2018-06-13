package finance.analytics.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import yahoofinance.histquotes2.HistoricalDividend;

public class DividendPattern {
	private Map<Integer, BigDecimal> dividentsPerYear = new TreeMap<>();
	private String dividendPattern;
	private int numberOfDividends;

	public DividendPattern(List<HistoricalDividend> dividendHistory) {
		sortByDate(dividendHistory);
		StringBuilder tempPattern = new StringBuilder();

		for (HistoricalDividend payout : dividendHistory) {
			Integer payoutYear = payout.getDate().get(Calendar.YEAR);
			BigDecimal accruedDivident = dividentsPerYear.get(payoutYear);
			if (accruedDivident == null) {
				accruedDivident = BigDecimal.ZERO;
			}
			accruedDivident = accruedDivident.add(payout.getAdjDividend());
			dividentsPerYear.put(payoutYear, accruedDivident);
		}

		for (Integer i = 1997; i <= 2018; i++) {
			BigDecimal dividend = dividentsPerYear.get(i);
			if (dividend == null) {
				dividend = BigDecimal.ZERO;
			}
			tempPattern.append(dividend.setScale(2, BigDecimal.ROUND_HALF_DOWN));
			tempPattern.append('|');
		}

		int conseqYears = 0;
		Integer year = 2017;
		while (dividentsPerYear.get(year--) != null) {
			conseqYears++;
		}
		tempPattern.append(conseqYears + " years");
		this.numberOfDividends = conseqYears;

		dividendPattern = tempPattern.toString();

	}

	private void sortByDate(List<HistoricalDividend> dividendHistory) {
		dividendHistory.sort(new Comparator<HistoricalDividend>() {
			@Override
			public int compare(HistoricalDividend o1, HistoricalDividend o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});
	}

	public String getPattern() {
		return dividendPattern;
	}

	public int getNumberOfDividends() {
		return numberOfDividends;
	}
}
