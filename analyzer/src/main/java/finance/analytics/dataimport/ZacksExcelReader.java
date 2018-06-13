package finance.analytics.dataimport;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import finance.analytics.model.Index;
import finance.analytics.model.Stock;

public class ZacksExcelReader {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZacksExcelReader.class);

	private Map<String, Stock> existingStocks = new HashMap<>();

	public void addExistingStocks(Map<String, Stock> existingStocks) {
		this.existingStocks.putAll(existingStocks);
	}

	public Collection<Stock> readExcel(String fileName) {
		try (Workbook workbook = openWorkbook(fileName)) {
			convertToStocks(workbook);
		} catch (IOException | URISyntaxException | InvalidFormatException e) {
			LOGGER.error("Error opening XLS: " + fileName, e);
		}

		return existingStocks.values();
	}

	private void convertToStocks(Workbook workbook) {

		Sheet dataSheet = workbook.getSheetAt(0);
		Row header = dataSheet.getRow(0);
		int tickerIndex = findTickerColumn(header);
		for (Row row : dataSheet) {
			if (row.getRowNum() != 0) {
				String ticker = row.getCell(tickerIndex).getStringCellValue();
				Stock stock = getStock(ticker);
				for (Cell cell : row) {
					String cellHeader = header.getCell(cell.getColumnIndex()).getStringCellValue().trim();
					saveToProperty(stock, cell, cellHeader);
				}
			}
		}

	}

	private void saveToProperty(Stock stock, Cell cell, String cellHeader) {
		switch (cellHeader) {
		case "Ticker":
			break;
		case "Company Name":
			stock.setCompanyName(cell.getStringCellValue());
			break;
		case "P/E (Trailing 12 Months)":
			stock.getMarketData().setPe(cell.getNumericCellValue());
			break;
		case "Price/Book":
			stock.getMarketData().setPricePerBookvalue(cell.getNumericCellValue());
			break;
		case "Last Close":
			stock.setPrice(cell.getNumericCellValue());
			break;
		case "Shares Outstanding":
			stock.getMarketData().setSharesNum(cell.getNumericCellValue());
			break;
		case "Market Cap":
			stock.getMarketData().setMarketCap(cell.getNumericCellValue());
			break;
		case "Sector":
			stock.setSector(cell.getStringCellValue());
			break;
		case "Industry":
			stock.setIndustry(cell.getStringCellValue());
			break;
		case "Exchange":
			stock.setExchange(cell.getStringCellValue());
			break;
		case "Last Qtr EPS":
			stock.getMarketData().setMrqEPS(cell.getNumericCellValue());
			break;
		case "12 Mo Trailing EPS":
			stock.getMarketData().setEps(cell.getNumericCellValue());
			break;
		case "Annual Sales":
			stock.getMarketData().setAnnualSales(cell.getNumericCellValue());
			stock.getMarketData().setRevenue(cell.getNumericCellValue());
			break;
		case "Net Income":
			stock.getMarketData().setNetIncome(cell.getNumericCellValue());
			break;
		case "Cash Ratio":
			stock.getMarketData().setCashRatio(cell.getNumericCellValue());
			break;
		case "Cash Flow":
			stock.getMarketData().setCashFlow(cell.getNumericCellValue());
			break;
		case "Book Value":
			stock.getMarketData().setBookValuePerShare(cell.getNumericCellValue());
			break;
		case "Long Term Debt":
			stock.getMarketData().setLongTermDebt(cell.getNumericCellValue());
			break;
		case "Current Liabilities":
			stock.getMarketData().setCurrentLiabilities(cell.getNumericCellValue());
			break;
		case "Current Assets":
			stock.getMarketData().setCurrentAssets(cell.getNumericCellValue());
			break;
		case "Dividend":
			stock.getMarketData().setLastDividend(cell.getNumericCellValue());
			break;
		case "S&P 500 - ETF":
			if ("YES".equals(cell.getStringCellValue())) {
				stock.addIndex(Index.SandP500);
			}
			break;
		case "Current ROE (TTM)":
			stock.getMarketData().setROE(cell.getNumericCellValue());
			break;
		case "Current ROI (TTM)":
			stock.getMarketData().setROI(cell.getNumericCellValue());
			break;
		case "52 Week High":
			stock.getMarketData().setPrice52High(cell.getNumericCellValue());
			break;
		case "52 Week Low":
			stock.getMarketData().setPrice52Low(cell.getNumericCellValue());
			break;
		case "5 Yr. Hist. EPS Growth":
			stock.getMarketData().setEpsGrowth5y(cell.getNumericCellValue());
			break;
		case "5 Yr Historical Sales Growth":
			stock.getMarketData().setSalesGrowth5y(cell.getNumericCellValue());
			break;
		case "5 Yr Hist. Div. Growth %":
			stock.getMarketData().setDividendGrowth5y(cell.getNumericCellValue());
			break;
		case "EBIT":
			stock.getMarketData().setEbit(cell.getNumericCellValue());
			break;	
		default:
			LOGGER.debug("No mapping defined for {}", cellHeader);
		}
	}

	private Stock getStock(String ticker) {
		if (existingStocks.containsKey(ticker)) {
			return existingStocks.get(ticker);
		} else {
			Stock stock = new Stock(ticker);
			existingStocks.put(ticker, stock);
			return stock;
		}
	}

	private int findTickerColumn(Row header) {
		for (Cell cell : header) {
			if ("Ticker".equals(cell.getStringCellValue())) {
				return cell.getColumnIndex();
			}
		}
		throw new IndexOutOfBoundsException("Ticker is not found in header");
	}

	private Workbook openWorkbook(String fileName) throws URISyntaxException, InvalidFormatException, IOException {
		File file = Paths.get(getClass().getClassLoader().getResource(fileName).toURI()).toFile();
		Workbook workbook = new XSSFWorkbook(file);
		return workbook;
	}

}
