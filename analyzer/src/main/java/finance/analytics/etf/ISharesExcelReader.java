package finance.analytics.etf;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
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

import finance.analytics.model.Stock;

public class ISharesExcelReader {
	private static final Logger LOGGER = LoggerFactory.getLogger(ISharesExcelReader.class);

	private Map<String, Double> composition = new HashMap<>();

	public ISharesExcelReader(String fileName) {
		try (Workbook workbook = openWorkbook(fileName)) {
			extractComposition(workbook);
		} catch (IOException | URISyntaxException | InvalidFormatException e) {
			LOGGER.error("Error opening XLS: " + fileName, e);
		}
	}

	public Map<String, Double> getComposition() {
		return composition;
	}

	private void extractComposition(Workbook workbook) {
		Sheet dataSheet = workbook.getSheetAt(0);
		Row header = dataSheet.getRow(0);
		for (Row row : dataSheet) {
			if (row.getRowNum() != 0 && row.getCell(3) != null) {
				String ticker = row.getCell(0).getStringCellValue();
				Double weight = row.getCell(3).getNumericCellValue();
				composition.put(ticker, weight);
			}
		}

	}

	private Workbook openWorkbook(String fileName) throws URISyntaxException, InvalidFormatException, IOException {
		File file = Paths.get(getClass().getClassLoader().getResource(fileName).toURI()).toFile();
		Workbook workbook = new XSSFWorkbook(file);
		return workbook;
	}

}
