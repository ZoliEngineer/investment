package finance.analytics.model;

import java.math.BigDecimal;
import java.util.Date;

public class MarketData {
	private double lastDividend;
	private double currentAssets;
	private double currentLiabilities;
	private double netIncome;
	private double pe;
	private double revenue;
	private double bookValuePerShare;
	private double eps;
	private double pricePerBookvalue;
	private double sharesNum;
	private double marketCap;
	private double mrqEPS;
	private double cashRatio;
	private double bookvalue;
	private double longTermDebt;
	private double dividendGrowth5y;
	private DividendPattern dividendHistory;
	private double roe;
	private double roi;
	private double price52High;
	private double price52Low;
	private double epsGrowth5y;
	private double salesGrowth5y;
	private double ebit;
	private double cashFlow;
	

	public double getLastDividend() {
		return lastDividend;
	}

	public void setLastDividend(double lastDividend) {
		this.lastDividend = lastDividend;
	}

	public double getCurrentAssets() {
		return currentAssets;
	}

	public void setCurrentAssets(double currentAssets) {
		this.currentAssets = currentAssets;
	}

	public double getCurrentLiabilities() {
		return currentLiabilities;
	}

	public void setCurrentLiabilities(double currentLiabilities) {
		this.currentLiabilities = currentLiabilities;
	}

	public double getAnnualSales() {
		return revenue;
	}

	public void setAnnualSales(double annualSales) {
		this.revenue = annualSales;
	}

	public double getNetIncome() {
		return netIncome;
	}

	public void setNetIncome(double netIncome) {
		this.netIncome = netIncome;
	}

	public double getPe() {
		return pe;
	}

	public void setPe(double pe) {
		this.pe = pe;
	}

	public double getRevenue() {
		return revenue;
	}

	public void setRevenue(double revenue) {
		this.revenue = revenue;
	}

	public double getBookValuePerShare() {
		return bookValuePerShare;
	}

	public void setBookValuePerShare(double bookValuePerShare) {
		this.bookValuePerShare = bookValuePerShare;
	}

	public double getEps() {
		return eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}

	public double getPricePerBookvalue() {
		return pricePerBookvalue;
	}

	public void setPricePerBookvalue(double pricePerBookvalue) {
		this.pricePerBookvalue = pricePerBookvalue;
	}

	public double getSharesNum() {
		return sharesNum;
	}

	public void setSharesNum(double sharesNum) {
		this.sharesNum = sharesNum;
	}

	public double getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(double marketCap) {
		this.marketCap = marketCap;
	}

	public double getMrqEPS() {
		return mrqEPS;
	}

	public void setMrqEPS(double mrqEPS) {
		this.mrqEPS = mrqEPS;
	}

	public double getCashRatio() {
		return cashRatio;
	}

	public void setCashRatio(double cashRatio) {
		this.cashRatio = cashRatio;
	}

	public double getBookvalue() {
		return bookValuePerShare * sharesNum;
	}

	// public void setBookvalue(double bookvalue) {
	// this.bookvalue = bookvalue;
	// }
	public double getLongTermDebt() {
		return longTermDebt;
	}

	public void setLongTermDebt(double longTermDebt) {
		this.longTermDebt = longTermDebt;
	}

	public double getDividendGrowth5y() {
		return dividendGrowth5y;
	}

	public void setDividendGrowth5y(double dividendGrow5y) {
		this.dividendGrowth5y = dividendGrow5y;
	}

	@Override
	public String toString() {
		return "MarketData [lastDividend=" + lastDividend + ", currentAssets=" + currentAssets + ", currentLiabilities="
				+ currentLiabilities + ", annualSales=" + revenue + ", netIncome=" + netIncome + ", pe=" + pe
				+ ", revenue=" + revenue + ", bookValuePerShare=" + bookValuePerShare + ", eps=" + eps
				+ ", pricePerBookvalue=" + pricePerBookvalue + ", sharesNum=" + sharesNum + ", marketCap=" + marketCap
				+ ", mrqEPS=" + mrqEPS + ", cashRatio=" + cashRatio + ", bookvalue=" + bookvalue + ", longTermDebt="
				+ longTermDebt + ", dividendGrowth5y=" + dividendGrowth5y + "]";
	}

	public void setDividendHistory(DividendPattern dividendPattern) {
		this.dividendHistory = dividendPattern;
	}

	public DividendPattern getDividendHistory() {
		return dividendHistory;
	}	
	
	public double getNetCurrentAssets() {
		return currentAssets - currentLiabilities;
	}

	public void setROE(double roe) {
		this.roe = roe;		
	}
	
	public double getROE() {
		return roe;
	}

	public void setROI(double roi) {
		this.roi = roi;		
	}
	
	public double getROI() {
		return roi;
	}


	public double getPrice52High() {
		return price52High;
	}

	public void setPrice52High(double price52High) {
		this.price52High = price52High;
	}

	public double getPrice52Low() {
		return price52Low;
	}

	public void setPrice52Low(double price52Low) {
		this.price52Low = price52Low;
	}

	public double getEpsGrowth5y() {
		return epsGrowth5y;
	}

	public void setEpsGrowth5y(double epsGrowth5y) {
		this.epsGrowth5y = epsGrowth5y;
	}

	public double getSalesGrowth5y() {
		return salesGrowth5y;
	}

	public void setSalesGrowth5y(double salesGrowth5y) {
		this.salesGrowth5y = salesGrowth5y;
	}

	public void setEbit(double ebit) {
		this.ebit = ebit;
		
	}
	
	public double getEbit() {
		return ebit;
		
	}

	public void setCashFlow(double cashFlow) {
		this.cashFlow = cashFlow;
	}
	
	public double getCashFlow() {
		return cashFlow;
	}

}
