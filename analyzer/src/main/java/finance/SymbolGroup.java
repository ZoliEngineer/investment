package finance;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public enum SymbolGroup {
	ALL("all_symbols.txt"),
	DJIA("djia_symbols.txt"),
	SnP500("sp500_symbols.txt"),
	NASDAQ100("nasdaq100_symbols.txt"),
	USA("usa_symbols.txt"),
	DIVIDEND("dividend_aristocrats.txt"),
	DIV_GOLD("dividend_gold.txt"),
	DIV_SILVER("dividend_silver.txt"),
	BRK("brk_symbols.txt");
	
	private String filename;
	
	private SymbolGroup(String fileName) {
		this.filename = fileName;
	}
	
	public Collection<String> getSymbols() {
		List<String> symbols = new ArrayList<>();
		try (Stream<String> stream = 
				Files.lines(Paths.get(getClass().getClassLoader()
					      .getResource("marketIndices/" + filename).toURI()))) {

			stream.forEach(symbols::add);

		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		} 
		
		return symbols;
	}
	
}
