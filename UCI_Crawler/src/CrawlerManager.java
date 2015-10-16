import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;





//import Tokenizer.TokenFrequencyComparor;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

public class CrawlerManager {
	private static CrawlerManager instance = null;
	private HashSet<String> visitedURLs = new HashSet<String>();
	private HashSet<String> subdomains = new HashSet<String>();
	private HashMap<String, Integer> subdomain_List = new HashMap<String, Integer>();
	static class subdomainComparor implements Comparator<Map.Entry<String, Integer>>
    {
		public int compare(Map.Entry< String, Integer> subdomain_List1, Map.Entry< String, Integer>  subdomain_List2)
		{
			return subdomain_List1.getKey().compareTo(subdomain_List2.getKey());
		}
	}
	
	private Page longestPage = null;
	private int longestPageWordCount = -1;
	private Tokenizer tokenizer = new Tokenizer();

	private static java.util.concurrent.locks.ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

	protected CrawlerManager() {

	}
	public int getURLSize()
	{
		return visitedURLs.size();
	}
	public static CrawlerManager Instance() {
		if (instance == null) {
			instance = new CrawlerManager();
		}
		return instance;
	}

	private void PrintLongestPage() {
		String url = longestPage.getWebURL().getURL().toLowerCase();
		System.out.println("Longest page is :" + url);
		System.out.println("Number of words in the page :"
				+ longestPageWordCount);
	}

	public String dealWithSlash(String url) {
		if (url.charAt(url.length() - 1) == '/')
			url = url.substring(0, url.length() - 1);
		return url;
	}

	public Boolean IsUrlVisited(String url) {
		dealWithSlash(url);
		return visitedURLs.contains(url);
	}

	public void MarkUrlAsVisited(String url) {
		dealWithSlash(url);

		lock.writeLock().lock();
		try {
			visitedURLs.add(url);
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void MarkSubdomainAsVisited(String subdomain) {
		lock.writeLock().lock();
		try {
			subdomains.add(subdomain);
			int count;
			if (subdomain_List.get(subdomain) == null) count = 0;
			else count = subdomain_List.get(subdomain);
			count++;
			subdomain_List.put(subdomain, count);
			
		} finally {
			lock.writeLock().unlock();
		}
	}
	public void downLoadText(String pageText, WebURL webUrl) throws IOException{
		int docid = webUrl.getDocid();
		File file_output = new File("E:/crawlData/pageText/"+docid+".txt");
		if (!file_output.exists()){
	            File parent = new File(file_output.getParent());
	            if (!parent.exists()){
	                parent.mkdirs();
	            }
	        }
		 FileWriter fWriter = new FileWriter(file_output);
		 fWriter.write(pageText);
		 fWriter.close();
	}
	public void AddPageTextIntoStatistics(Page page, String pageText, WebURL webUrl) throws IOException {
		List<String> tokens = tokenizer.Tokenize(pageText);
     //   if (tokens.size()> 300000) return;
		downLoadText(pageText, webUrl);
		lock.writeLock().lock();
		try {
			tokenizer.AddTokensIntoStatistics(tokens,webUrl);
            
			int wordCount = tokens.size();
			if (wordCount > longestPageWordCount) {
				longestPage = page;
				longestPageWordCount = wordCount;
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	private void PrintQuestionNumber(int number) {
		System.out.println("############Question " + number + " ############");
	}

	public void PrintCrawlingInformation(long timeDuration) {
		PrintQuestionNumber(1);
		System.out.println("Time spent: " + timeDuration + " seconds");

		PrintQuestionNumber(2);
		System.out.println("Number of unique pages:" + visitedURLs.size());

		PrintQuestionNumber(3);
		System.out.println("Number of subdomains:" + subdomains.size());
		
		List< Map.Entry< String, Integer> > subdomain_Sorted_List = new ArrayList< Map.Entry<String, Integer> >(subdomain_List.entrySet());
		Collections.sort( subdomain_Sorted_List, new subdomainComparor());
		for(int i=0; i<subdomain_Sorted_List.size(); i++)
		{
			Map.Entry< String, Integer> entry = subdomain_Sorted_List.get(i);
			System.out.println(entry.getKey() +".uci.edu, "+ entry.getValue());
		}

		PrintQuestionNumber(4);
		PrintLongestPage();

		PrintQuestionNumber(5);
		tokenizer.PrintTopWordFrequencies(500);

		PrintQuestionNumber(6);
		tokenizer.PrintTwoGramFrequencies(20);
	}
}
