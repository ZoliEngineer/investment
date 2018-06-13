package finance.analytics.model;

import finance.analytics.model.KeyIndicators.Rating;

public class SumRatings {
	private final Stock stock;

	public SumRatings(Stock stock) {
		this.stock = stock;
	}

	public String getRating() {

		String rating = "";

		boolean stable = stock.getIndicators().getDebtRatio().rating.equals(Rating.A)
				&& stock.getIndicators().getAssetsPerLiabilities().rating.equals(Rating.A);
		double actualPrice = stock.getPrice();
		double expectedPrice = stock.getIndicators().getRecommendedPrice().value;
		DividendPattern dividendHistory = stock.getMarketData().getDividendHistory();
		boolean prosperous = stable && stock.getIndicators().getEarningsGrowth().value > 1 && dividendHistory != null
				&& dividendHistory.getNumberOfDividends() >= 12
				&& stock.getIndicators().getEarningsHistory().rating == Rating.A;

		if (stock.getMarketData().getLastDividend() == 0 || stock.getIndicators().getAssetsPerLiabilities().value < 1
				|| stock.getIndicators().getDebtRatio().value > 2
				|| stock.getIndicators().getEarningsHistory().rating == Rating.B
				|| stock.getIndicators().getProfitMargin().value < 0
				|| stock.getIndicators().getDilutedEPSRatio().value < 0.6) {
			rating += "AVOID,";
		}

		if (stock.getIndicators().getDebtRatio().rating.equals(Rating.A)
				&& stock.getIndicators().getAssetsPerLiabilities().rating.equals(Rating.A)) {
			rating += "Stable,";
		}

		if (stock.getIndicators().getEarningsGrowth().value > 1 && dividendHistory != null
				&& dividendHistory.getNumberOfDividends() >= 12
				&& stock.getIndicators().getEarningsHistory().rating == Rating.A) {
			rating += "PaysWell,";
		}
		if (stock.getIndicators().getPricePerEarnings().value < 15) {
			rating += "GoodPrice,";
		}

		if (actualPrice < expectedPrice) {
			rating += "Cheap,";
		}

		if (prosperous && actualPrice < expectedPrice * 1.2) {
			rating += "AlmostCheap,";
		}

		return rating;

	}

	public String getSummary() {

		boolean watch = stock.getIndicators().getProfitMargin().rating == Rating.A
				&& stock.getIndicators().getReturnOnInvestedCapital().rating == Rating.A
				&& stock.getIndicators().getDividendHistory().rating == Rating.A
				&& stock.getIndicators().getEarningsHistory().rating == Rating.A
				&& stock.getIndicators().getEarningsGrowth().rating == Rating.A
				&& stock.getMarketData().getEpsGrowth5y() > 5 && stock.getMarketData().getDividendGrowth5y() > 5;
		boolean buy = stock.getIndicators().getPricePerEarnings().rating == Rating.A
				|| stock.getIndicators().getBookValuePerShare().rating == Rating.A;
		boolean perfect = stock.getIndicators().getDebtRatio().rating == Rating.A
				&& stock.getIndicators().getAssetsPerLiabilities().rating == Rating.A
				&& stock.getIndicators().getTimesInterestEarned().rating == Rating.A;

		String summary = "";

		if (watch) {
			summary += "EARNS,";
		}
		if (buy) {
			summary += "CHEAP,";
		}
		if (perfect) {
			summary += "STABLE,";
		}

		return summary;
	}
	
	public String getShortSummary() {
		
		boolean minimumRequirement = stock.getIndicators().getDividendHistory().rating == Rating.A
				&& stock.getIndicators().getEarningsHistory().rating == Rating.A;
		
		boolean good = stock.getIndicators().getProfitMargin().rating == Rating.A
				&& stock.getIndicators().getReturnOnInvestedCapital().rating == Rating.A
				&& stock.getIndicators().getEarningsGrowth().rating == Rating.A
				&& stock.getMarketData().getEpsGrowth5y() > 0 
				&& stock.getMarketData().getDividendGrowth5y() > 0;
				
		boolean cheap = stock.getIndicators().getPricePerEarnings().rating == Rating.A
				|| stock.getIndicators().getBookValuePerShare().rating == Rating.A;
				
				
		

		String summary = "";		

		if (minimumRequirement && good) {
			summary += "GOOD,";
		}
		if (minimumRequirement && cheap) {
			summary += "CHEAP,";
		}		

		return summary;
	}
	
	public double getPointRating() {
		if(stock.getIndicators().getEarningsHistory().rating == Rating.B
				|| (stock.getMarketData().getDividendHistory() != null && stock.getMarketData().getDividendHistory().getNumberOfDividends() < 6)
				|| stock.getIndicators().getTimesInterestEarned().rating == Rating.B
				) {
			HitCalculator.hits[0]++;
			return Double.NaN;			
		}
		
		int ratingPoint = 0;
		
		double assetsPerLiabilities = stock.getIndicators().getAssetsPerLiabilities().value;
		if(assetsPerLiabilities > 1 || "Finance".equals(stock.getSector())) {
			ratingPoint++;
			HitCalculator.hits[1]++;
		}
		if(assetsPerLiabilities > 2) {
			ratingPoint++;
			HitCalculator.hits[2]++;
		}
		
		double debtRatio = 
				stock.getMarketData().getLongTermDebt() / (stock.getIndicators().getTotalAssets().value / 1_000_000);
		
		
		if(stock.getMarketData().getBookvalue() < 0) {
			ratingPoint--;
		}
		
		if(debtRatio < 0.5) {
			ratingPoint++;
			HitCalculator.hits[3]++;
		}
		if(debtRatio < 0.25) {
			ratingPoint++;
			HitCalculator.hits[4]++;
		}
		
		if(stock.getIndicators().getProfitMargin().rating == Rating.A) {
			ratingPoint++;
			HitCalculator.hits[5]++;
		}
		
		if(stock.getIndicators().getReturnOnInvestedCapital().rating == Rating.A) {
			ratingPoint++;
			HitCalculator.hits[6]++;
		}
	
		if(stock.getMarketData().getDividendHistory() != null && stock.getMarketData().getDividendHistory().getNumberOfDividends() >= 12) {
			ratingPoint++;
			HitCalculator.hits[7]++;
		}
		
		if(stock.getMarketData().getDividendGrowth5y() > 20
				|| (stock.getMarketData().getLastDividend() / stock.getIndicators().getAverageDividend().value) > 1.15) {
			ratingPoint++;
			HitCalculator.hits[8]++;			
		}			
	
		if(stock.getIndicators().getEarningsGrowth().rating == Rating.A
				|| stock.getMarketData().getEpsGrowth5y() > 10
				|| (stock.getMarketData().getEps() / stock.getIndicators().getAverageEarnings().value) > 1.15) {
			ratingPoint++;
			HitCalculator.hits[9]++;
			
		}		
		
		if(stock.getMarketData().getSalesGrowth5y() < 0) {
			ratingPoint--;
			HitCalculator.hits[10]++;
		}
		
		
		return ratingPoint;
	}

}
