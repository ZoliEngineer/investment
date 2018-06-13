package finance.analytics.model;

import java.util.EnumSet;
import java.util.Set;

public class Stock {
	private final String ticker;
	private String companyName;
	private String sector;
	private String industry;
	private MarketData marketData;
	private double price;
	private KeyIndicators indicators;
	private Evaluation evaluation;
	private Set<Index> indices = EnumSet.noneOf(Index.class);
	private String exchange;	
	
	public void addIndex(Index index) {
		indices.add(index);
	}

	public Stock(String ticker) {
		this.ticker = ticker;
	}

	public MarketData getMarketData() {
		if(marketData == null) {
			marketData = new MarketData();
		}
		return marketData;
	}	

	public KeyIndicators getIndicators() {
		if(indicators == null) {
			indicators = new KeyIndicators(this);
		}
		return indicators;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Evaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getTicker() {
		return ticker;
	}

	public Set<Index> getIndices() {
		return indices;
	}
	

	@Override
	public String toString() {
		return "Stock [" + (ticker != null ? "ticker=" + ticker + ", " : "")
				+ (companyName != null ? "companyName=" + companyName + ", " : "")
				+ (sector != null ? "sector=" + sector + ", " : "")
				+ (industry != null ? "industry=" + industry + ", " : "")
				+ (marketData != null ? "marketData=" + marketData + ", " : "") + "price=" + price + ", "
				+ (indicators != null ? "indicators=" + indicators + ", " : "")
				+ (evaluation != null ? "evaluation=" + evaluation + ", " : "")
				+ (indices != null ? "indices=" + indices + ", " : "")
				+ (exchange != null ? "exchange=" + exchange : "") + "]";
	}	
	
	
}
