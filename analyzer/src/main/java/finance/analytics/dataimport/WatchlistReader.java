package finance.analytics.dataimport;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class WatchlistReader {
	Map<String, String> watchlist = new HashMap<>();
	
		public WatchlistReader(String fileName) {			
			try {
				File file = Paths.get(getClass().getClassLoader().getResource(fileName).toURI()).toFile();
				CSVParser content  = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.DEFAULT);
				for(CSVRecord record : content.getRecords()) {
					watchlist.put(record.get(0), record.get(1));
				}
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();		
			}		
			
	}

		public Map<String, String> getData() {
			return watchlist;
		}

		
}
