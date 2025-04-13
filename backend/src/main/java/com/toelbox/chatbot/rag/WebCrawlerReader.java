package com.toelbox.chatbot.rag;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.batch.item.ItemReader;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class WebCrawlerReader implements ItemReader<String> {

	private final Queue<String> urlQueue = new ConcurrentLinkedQueue<>();
	private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
	private final String rootUrl;
	private boolean initialized = false;

	public WebCrawlerReader(String rootUrl) {
		this.rootUrl = rootUrl.endsWith("/") ? rootUrl.substring(0, rootUrl.length() - 1) : rootUrl;
	}

	@Override
	public String read() {
		if (!initialized) {
			initialized = true;
			loadUrlsFromSitemap(rootUrl + "/sitemap.xml");
		}

		while (!urlQueue.isEmpty()) {
			String url = urlQueue.poll();
			if (url != null && visitedUrls.add(url)) {
				return url;
			}
		}
		return null;
	}

	private void loadUrlsFromSitemap(String sitemapUrl) {
		try {
			log.info("🔍 Loading sitemap: {}", sitemapUrl);
			Document sitemapDoc = Jsoup.connect(sitemapUrl)
					.ignoreContentType(true)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
					.referrer("https://www.google.com/")
					.timeout(10_000)
					.parser(Parser.xmlParser())
					.get();

			// If it's an index sitemap
			for (Element sitemapLoc : sitemapDoc.select("sitemap > loc")) {
				String childSitemapUrl = sitemapLoc.text();
				log.info("📂 Found child sitemap: {}", childSitemapUrl);
				loadUrlsFromSitemap(childSitemapUrl); // Recursive
			}

			// If it's a regular sitemap
			for (Element urlLoc : sitemapDoc.select("url > loc")) {
				String pageUrl = urlLoc.text();
				if (!visitedUrls.contains(pageUrl)) {
					urlQueue.add(pageUrl);
					log.debug("✅ Found page URL: {}", pageUrl);
				}
			}

			log.info("🧠 Total URLs loaded so far: {}", urlQueue.size());

		} catch (Exception e) {
			log.warn("⚠️ Failed to load sitemap: {} — {}", sitemapUrl, e.getMessage());
			log.info("Fallback: adding root URL for basic crawling.");
			urlQueue.add(rootUrl);
		}
	}
}
