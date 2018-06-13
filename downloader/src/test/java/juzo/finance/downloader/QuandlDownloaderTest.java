package juzo.finance.downloader;

import org.junit.Test;

import finance.SymbolGroup;

public class QuandlDownloaderTest {
	
	@Test
	public void runDownload() {
		new QuandlDownloader().downloadAll(SymbolGroup.ALL);
	}

}
