package finance.analytics.dataexport;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import finance.DataCells;
import finance.analytics.model.Stock;
import finance.analytics.model.SumRatings;

public class ExcelReport {
	public void create(Iterable<Stock> stocks, Collection<Map<String, String>> portfolios,
			Map<String, String> watchlist, String fileName) throws IOException {
		exportToExcel(stocks, portfolios, watchlist, fileName);
	}

	private void exportToExcel(Iterable<Stock> stockData, Iterable<Map<String, String>> portfolios,
			Map<String, String> watchlist, String fileName) throws IOException {
		Workbook wb = new XSSFWorkbook();
		createStocksSheet(stockData, wb);
		createPortfoliosSheet(portfolios, wb);
		createWatchlistSheet(watchlist, stockData, wb);

		writeToFile(fileName, wb);

	}

	private void createStocksSheet(Iterable<Stock> stockData, Workbook wb) {

		Sheet sheet = wb.createSheet("Stocks");

		createHeader(sheet);
		createData(sort(stockData), sheet);

		sheet.setAutoFilter(new CellRangeAddress(sheet.getFirstRowNum(), sheet.getLastRowNum(),
				sheet.getRow(0).getFirstCellNum(), sheet.getRow(0).getLastCellNum() - 1));

		IntStream.range(0, sheet.getRow(0).getLastCellNum())
				.forEach((columnIndex) -> sheet.autoSizeColumn(columnIndex));
		sheet.createFreezePane(0, 1);

	}

	private Iterable<Stock> sort(Iterable<Stock> stockData) {
		List<Stock> stockList = IteratorUtils.toList(stockData.iterator());
		stockList.sort(new Comparator<Stock>() {
			@Override
			public int compare(Stock o1, Stock o2) {
				Double rating1 = new SumRatings(o1).getPointRating();				
				if(Double.isNaN(rating1)) rating1 = -1d;
				Double rating2 = new SumRatings(o2).getPointRating();				
				if(Double.isNaN(rating2)) rating2 = -1d;
				Double pe1 = o1.getIndicators().getAdjustedPricePerEarnings().value;
				Double pe2 = o2.getIndicators().getAdjustedPricePerEarnings().value;

				if (rating1.compareTo(rating2) == 0) {
					return pe1.compareTo(pe2);
				} else {
					return rating1.compareTo(rating2) * -1;
				}
			}
		});
		return stockList;
	}

	private void createPortfoliosSheet(Iterable<Map<String, String>> portfolios, Workbook wb) {
		Sheet sheet = wb.createSheet("Portfolio");

		Row headerRow = sheet.createRow(0);
		int headerCol = 0;
		for (String header : portfolios.iterator().next().keySet()) {
			Cell cell = headerRow.createCell(headerCol++);
			cell.setCellValue(header);
			cell.setCellStyle(boldStyle(sheet.getWorkbook()));
		}

		int rowNum = 1;
		for (Map<String, String> portfolio : portfolios) {
			Row row = sheet.createRow(rowNum++);
			int col = 0;
			for (String field : portfolio.values()) {
				Cell cell = row.createCell(col++);
				if (NumberUtils.isParsable(field)) {
					cell.setCellValue(Double.valueOf(field));
				} else {
					cell.setCellValue(field);
				}
			}
		}
		IntStream.range(0, sheet.getRow(0).getLastCellNum())
				.forEach((columnIndex) -> sheet.autoSizeColumn(columnIndex));

	}

	private void createWatchlistSheet(Map<String, String> watchlist, Iterable<Stock> stockData, Workbook wb) {
		Sheet sheet = wb.createSheet("Watchlist");
		EnumSet<DataCells> requiredCells = EnumSet.noneOf(DataCells.class);
		requiredCells.add(DataCells.SYMBOL);
		requiredCells.add(DataCells.NAME);
		requiredCells.add(DataCells.SECTOR);
		requiredCells.add(DataCells.INDUSTRY);
		requiredCells.add(DataCells.GROUP);
		requiredCells.add(DataCells.PROFIT_MARGIN);
		requiredCells.add(DataCells.ROE);
		requiredCells.add(DataCells.ROIC);
		requiredCells.add(DataCells.LAST_DIVIDEND);
		requiredCells.add(DataCells.EPS);
		requiredCells.add(DataCells.EARNINGS_AVG);
		requiredCells.add(DataCells.BVPS);
		requiredCells.add(DataCells.GRAHAM_PRICE);
		requiredCells.add(DataCells.PRICE);
		requiredCells.add(DataCells.LOW_HIGH);
		requiredCells.add(DataCells.PE);
		requiredCells.add(DataCells.PE_AVG);
		requiredCells.add(DataCells.DIV_YIELD);
		requiredCells.add(DataCells.DIV_YIELD_AVG);
		requiredCells.add(DataCells.JUZO_RATING);

		createWLHeader(sheet, requiredCells);
		createWLData(stockData, watchlist, sheet, requiredCells);

		IntStream.range(0, sheet.getRow(0).getLastCellNum())
				.forEach((columnIndex) -> sheet.autoSizeColumn(columnIndex));
	}

	private void writeToFile(String fileName, Workbook wb) throws FileNotFoundException, IOException {
		FileOutputStream fileOut = new FileOutputStream(fileName);
		wb.write(fileOut);
		fileOut.close();
		wb.close();
	}

	private void createData(Iterable<Stock> stockData, Sheet sheet) {
		int rowNum = 1;
		for (Stock stock : stockData) {
			Row row = sheet.createRow(rowNum++);
			for (DataCells col : DataCells.values()) {
				try {
					col.addToRow(row, stock);
				} catch (Exception e) {
					System.out.println(col);
					System.out.println(stock);
					System.out.println(row);
					throw e;
				}
			}
		}
	}

	private void createHeader(Sheet sheet) {
		Row headerRow = sheet.createRow(0);
		for (DataCells col : DataCells.values()) {
			Cell cell = headerRow.createCell(col.ordinal());
			cell.setCellValue(col.name());
			cell.setCellStyle(boldStyle(sheet.getWorkbook()));
		}
	}

	private void createWLHeader(Sheet sheet, EnumSet<DataCells> requiredCells) {
		Row headerRow = sheet.createRow(0);
		int colnum = 0;
		for (DataCells col : requiredCells) {
			Cell cell = headerRow.createCell(colnum++);
			cell.setCellValue(col.name());
			cell.setCellStyle(boldStyle(sheet.getWorkbook()));
		}
		Cell cell = headerRow.createCell(headerRow.getLastCellNum());
		cell.setCellValue("Reason");
	}

	private void createWLData(Iterable<Stock> stockData, Map<String, String> watchlist, Sheet sheet,
			EnumSet<DataCells> requiredCells) {
		int rowNum = 1;
		for (Stock stock : stockData) {
			if (watchlist.containsKey(stock.getTicker())) {
				Row row = sheet.createRow(rowNum++);
				int colnum = 0;
				for (DataCells col : requiredCells) {
					try {
						col.addToRow(row, stock, colnum++);
					} catch (Exception e) {
						System.out.println(col);
						System.out.println(stock);
						System.out.println(row);
						throw e;
					}
				}
				Cell cell = row.createCell(row.getLastCellNum());
				cell.setCellValue(watchlist.get(stock.getTicker()));
			}
		}
	}

	private CellStyle boldStyle(Workbook workbook) {
		CellStyle boldStyle = workbook.createCellStyle();
		Font boldFont = workbook.createFont();
		boldFont.setBold(true);
		boldStyle.setFont(boldFont);

		return boldStyle;
	}

}
