package finance;

import java.math.BigDecimal;
import java.util.EnumSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;

import finance.analytics.model.DividendPattern;
import finance.analytics.model.KeyIndicators;
import finance.analytics.model.KeyIndicators.Rating;
import finance.analytics.model.KeyIndicators.ValueRatingPair;
import finance.analytics.model.Stock;
import finance.analytics.model.SumRatings;

public enum DataCells {

	// Company information
	SYMBOL {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(stock.getTicker());			
		}
	},

	NAME {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(stock.getCompanyName());
		}
	},
	SECTOR {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(stock.getSector());
		}
	},
	INDUSTRY {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(stock.getIndustry());
		}
	},
	GROUP {
		@Override
		public void addValue(Cell cell, Stock stock) {
			EnumSet<SymbolGroup> groups = EnumSet.noneOf(SymbolGroup.class);						
			if (SymbolGroup.DJIA.getSymbols().contains(stock.getTicker())) {
				groups.add(SymbolGroup.DJIA);				
			}
			if (SymbolGroup.NASDAQ100.getSymbols().contains(stock.getTicker())) {
				groups.add(SymbolGroup.NASDAQ100);				
			}
		
			if (SymbolGroup.DIV_GOLD.getSymbols().contains(stock.getTicker())) {
				groups.add(SymbolGroup.DIV_GOLD);				
			} else if (SymbolGroup.DIV_SILVER.getSymbols().contains(stock.getTicker())) {
				groups.add(SymbolGroup.DIV_SILVER);				
			} else if (SymbolGroup.DIVIDEND.getSymbols().contains(stock.getTicker())) {
				groups.add(SymbolGroup.DIVIDEND);				
			} else if (SymbolGroup.BRK.getSymbols().contains(stock.getTicker())) {
				groups.add(SymbolGroup.BRK);				
			}
			cell.setCellValue(StringUtils.removeAll(groups.toString(), "[\\[\\]]"));
		}		
	},
	REVENUE {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(stock.getMarketData().getRevenue());
		}
	},
	NET_INCOME {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(round(stock.getMarketData().getNetIncome()));
		}
	},
	CASH_FLOW {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(round(stock.getMarketData().getCashFlow()));
			if(stock.getMarketData().getCashFlow() < 0) {
				setRed(cell);
			}
		}
	},
	TOTAL_ASSETS {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(stock.getIndicators().getTotalAssets().value / 1_000_000);
		}
	},
	BOOK_VALUE {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(round(stock.getMarketData().getBookvalue()));
		}
	},
	CURRENT_ASSETS {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(round(stock.getMarketData().getCurrentAssets(), 0));
		}
	},
	CURRENT_LIABILITIES {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(round(stock.getMarketData().getCurrentLiabilities(), 0));
		}
	},
	NET_CURRENT_ASSETS {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(round(stock.getMarketData().getNetCurrentAssets()));
		}
	},
	LONG_TERM_DEBT {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(round(stock.getMarketData().getLongTermDebt(), 0));
			if (stock.getMarketData().getLongTermDebt() > stock.getMarketData().getBookvalue()) {
				setRed(cell);
			}
		}
	},

	// Stability
	ASSETS_PER_LIABILITIES {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getAssetsPerLiabilities();
			cell.setCellValue(round(value.value));
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}
	},
	DEBT_RATIO {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getDebtRatio();
			cell.setCellValue(round(value.value));
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}
	},
	PROFIT_MARGIN {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getProfitMargin();
			cell.setCellValue(round(value.value));
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}
	},
	PROFIT_ON_CAPITAL {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getProfitOnCapital();
			cell.setCellValue(round(value.value * 100));
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}
	},
	ROE {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(stock.getMarketData().getROE());
			if (stock.getMarketData().getROE() > 15) {
				setGreen(cell);
			}
		}
	},
	ROIC {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getReturnOnInvestedCapital();
			cell.setCellValue(value.value);
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}
	},
	TIE {

		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getTimesInterestEarned();
			cell.setCellValue(round(value.value));
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}

	},

	DIVIDEND_HISTORY {
		@Override
		public void addValue(Cell cell, Stock stock) {
			DividendPattern dividendHistory = stock.getMarketData().getDividendHistory();
			cell.setCellValue(dividendHistory == null ? "" : dividendHistory.getPattern());
			if (dividendHistory != null && dividendHistory.getNumberOfDividends() > 12) {
				setGreen(cell);
			}
		}
	},
	DIVIDEND_HISTORY_2 {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<String> value = stock.getIndicators().getDividendHistory();
			cell.setCellValue(value.value);
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}
	},
	EARNINGS_HISTORY {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<String> value = stock.getIndicators().getEarningsHistory();
			cell.setCellValue(value.value);
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}
	},
	BUYBACK_HISTORY {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<String> value = stock.getIndicators().getBuybackHistory();
			cell.setCellValue(value.value);
		}
	},
	RETURN_HISTORY {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<String> value = stock.getIndicators().getReturnHistory();
			cell.setCellValue(value.value);			
		}
	},
	LAST_DIVIDEND {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(round(stock.getMarketData().getLastDividend()));
			if(stock.getMarketData().getLastDividend() > stock.getMarketData().getEps()) {
				setRed(cell);
			}
		}
	
	},
	DIVIDEND_AVG {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getAverageDividend();
			cell.setCellValue(round(value.value));
			if(stock.getIndicators().getAverageDividend().value > (0.8 * stock.getIndicators().getAverageEarnings().value)) {
				setRed(cell);
			}
		}
	},
	EPS {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(stock.getMarketData().getEps());
		}
	},
	EARNINGS_AVG {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getAverageEarnings();
			cell.setCellValue(round(value.value));
		}
	},
	EARNINGS_GROWTH {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getEarningsGrowth();
			cell.setCellValue(round(value.value));
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}
	},
	SALES_GROWTH_5Y {

		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(stock.getMarketData().getSalesGrowth5y());

		}

	},
	EARNINGS_GROWTH_5Y {

		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(stock.getMarketData().getEpsGrowth5y());

		}

	},
	DIVIDEND_GROWTH_5Y {

		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(round(stock.getMarketData().getDividendGrowth5y()));

		}

	},

	DILUTED_EPS_RATIO {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getDilutedEPSRatio();
			if (value.value.isNaN()) {
				cell.setCellValue(value.value);
			} else {
				double val = BigDecimal.valueOf(value.value).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
				cell.setCellValue(val);
			}
		}
	},
	// Price
	BVPS {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getBookValuePerShare();
			cell.setCellValue(value.value);
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}
	},	
	PRICE {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(stock.getPrice());
		}
	},
	LOW_HIGH {

		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<String> value = stock.getIndicators().getPriceLowHigh();
			cell.setCellValue(value.value);
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}
	},
	MARKET_CAP {
		@Override
		public void addValue(Cell cell, Stock stock) {
			cell.setCellValue(stock.getMarketData().getMarketCap());
		}
	},
	GRAHAM_PRICE {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getRecommendedPrice();
			cell.setCellValue(round(value.value));
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}
	},
	PE {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getPricePerEarnings();
			cell.setCellValue(value.value);
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}
	},
	PE_AVG {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getAdjustedPricePerEarnings();
			cell.setCellValue(value.value);
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}
	},
	DIV_YIELD {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getDividentYield();
			cell.setCellValue(round(value.value));
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
			if(value.value > 10) {
				setRed(cell);
			}
		}
	},
	DIV_YIELD_AVG {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getAverageDividentYield();
			cell.setCellValue(round(value.value));
			if (value.rating == Rating.A) {
				setGreen(cell);
			}
		}
	},
	RETURN {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getReturnLast();
			cell.setCellValue(value.value);
		}
	},
	RETURN_AVERAGE {
		@Override
		public void addValue(Cell cell, Stock stock) {
			ValueRatingPair<Double> value = stock.getIndicators().getReturnAverage();
			cell.setCellValue(round(value.value));
		}
	},
//	EVALUATION {
//		@Override
//		public void addValue(Cell cell, Stock stock) {
//			cell.setCellValue(new SumRatings(stock).getRating().toString());
//
//		}
//	},
//	CONCUSION {
//		@Override
//		public void addValue(Cell cell, Stock stock) {
//			cell.setCellValue(new SumRatings(stock).getSummary().toString());
//
//		}
//	},
//	SHORT {
//		@Override
//		public void addValue(Cell cell, Stock stock) {
//			cell.setCellValue(new SumRatings(stock).getShortSummary().toString());
//
//		}
//	},
	JUZO_RATING{
		@Override
		public void addValue(Cell cell, Stock stock) {
			double rating = new SumRatings(stock).getPointRating() ;
			if(!Double.isNaN(rating)){
				cell.setCellValue(rating);
			}

		}
	};

	public void addToRow(Row row, Stock stock) {
		addToRow(row, stock, this.ordinal());
	}
	
	public void addToRow(Row row, Stock stock, int position) {
		Cell cell = row.createCell(position);
		addValue(cell, stock);
	}

	public abstract void addValue(Cell cell, Stock stock);

	private static void setGreen(Cell cell) {
		CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
		style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell.setCellStyle(style);
	}
	
	private static void setRed(Cell cell) {
		CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
		style.setFillForegroundColor(IndexedColors.RED.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell.setCellStyle(style);
	}

	private static double round(double value, int precision) {
		return Double.isFinite(value) ? BigDecimal.valueOf(value).setScale(precision, BigDecimal.ROUND_HALF_DOWN).doubleValue()
				: Double.NaN;
	}

	private static double round(double value) {
		return round(value, 2);
	}

}
