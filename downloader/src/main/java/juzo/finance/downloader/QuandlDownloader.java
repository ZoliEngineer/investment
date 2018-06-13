package juzo.finance.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import finance.SymbolGroup;
import yahoofinance.YahooFinance;
import yahoofinance.util.RedirectableRequest;

public class QuandlDownloader {
	
	public void downloadAll(SymbolGroup tickers) {
		gatherData(tickers.getSymbols());	
	}

	private void gatherData(Collection<String> tickers) {		
		for (String ticker : tickers) {
			if(ticker.charAt(0) > 'M') {
			gatherData(ticker);
			}
		}
	}

	private void gatherData(String ticker) {
		String url = buildUrl(ticker);
		List<String> csv;
		try {
			if (checkIfExist(ticker)) {
				System.out.println(ticker + " is already downloaded");
				return;
			}
			csv = readUrl(url);
			if (csv.size() == 1) {
				System.err.println(ticker + " has no data");
				return;
			}
			writeToFile(csv, ticker);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean checkIfExist(String ticker) {
		Path out = Paths.get("target/history/quandl/" + ticker + ".history.csv");
		return Files.exists(out);
	}

	private void writeToFile(List<String> csv, String ticker) throws IOException {
		Path out = Paths.get("target/history/quandl/" + ticker + ".history.csv");
		Files.write(out, csv, Charset.defaultCharset());
		System.out.println(ticker + " is created");
	}

	private String buildUrl(String ticker) {
		StringBuilder url = new StringBuilder();
		url.append("https://www.quandl.com/api/v3/datatables/SHARADAR/SF1.csv?");
		url.append("dimension=MRY");
		url.append("&calendardate.gte=2004-12-31");
		url.append("&ticker=");
		url.append(ticker);
		url.append("&api_key=rsrBujkaXAQGgXyZEMDm");
		return url.toString();
	}

	private List<String> readUrl(String url) throws IOException {
		URL request = new URL(url);
		RedirectableRequest redirectableRequest = new RedirectableRequest(request, 5);
		redirectableRequest.setConnectTimeout(YahooFinance.CONNECTION_TIMEOUT);
		redirectableRequest.setReadTimeout(YahooFinance.CONNECTION_TIMEOUT);
		URLConnection connection = redirectableRequest.openConnection();

		InputStreamReader is = new InputStreamReader(connection.getInputStream());
		BufferedReader br = new BufferedReader(is);

		// Parse CSV
		List<String> allLines = new ArrayList<>();
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			allLines.add(line);
		}
		br.close();
		is.close();
		return allLines;

	}

}
