package juzo.finance.retrospect;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class RetrospectiveAnalyzer {

	@Test
	public void calculateRetrospectivePerformance() throws IOException, URISyntaxException {
		List<String> results = new ArrayList<>();
		
		for(File file : Paths.get(getClass().getClassLoader().getResource("portfolios/").toURI()).toFile().listFiles()) {
			Portfolio portfolio = new Portfolio(file);
			results.add(file.getName() + ": " + round(new Analyzer(portfolio).analyze()) + "%");
		}
		
		
		for(String result : results) {
			System.out.println(result);
		}
		
	}
	
	
	private static double round(double value, int precision) {
		return Double.isFinite(value) ? BigDecimal.valueOf(value).setScale(precision, BigDecimal.ROUND_HALF_DOWN).doubleValue()
				: Double.NaN;
	}

	private static double round(double value) {
		return round(value, 2);
	}
}
