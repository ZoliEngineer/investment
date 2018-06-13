package juzo.finance.retrospect;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Portfolio {
	private static final Logger LOGGER = LoggerFactory.getLogger(Portfolio.class);

	private final Map<String, Double> stockList = new HashMap<>();
	private final LocalDate baseDate;
		

	public Portfolio(File file) {
		try {
			extractStocks(file);
			
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		baseDate = extractBaseDate(file);
	}

	private LocalDate extractBaseDate(File file) {		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");		
		
		int endposition = file.getName().indexOf(".");
		String dateString = file.getName().substring(endposition-8, endposition);
		
		return LocalDate.parse(dateString, formatter);		
	}

	private void extractStocks(File file) throws URISyntaxException, IOException {
		CSVParser content = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.DEFAULT);
		for (CSVRecord record : content.getRecords()) {
			String ticker = record.get(0);
			String weightString = record.size() > 1 ? record.get(1) : "";
			
			double weight = NumberUtils.isParsable(weightString) ? Double.valueOf(weightString) : 1;

			stockList.put(ticker, weight);
		}
	}
	
	public Map<String, Double> getStockList() {
		return stockList;
	}

	public LocalDate getBaseDate() {
		return baseDate;
	}


}
