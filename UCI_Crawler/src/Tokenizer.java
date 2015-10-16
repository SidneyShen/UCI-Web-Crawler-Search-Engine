import java.io.File;
import java.io.FileWriter;
import java.util.*;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

public class Tokenizer 
{
	private Set<String> stopWordList = new HashSet<String>();
	private HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
	private HashMap<TwoGram, Integer> towGramFrequencies = new HashMap<TwoGram, Integer>();
	private static java.util.concurrent.locks.ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();
	Tokenizer() {
	String[] stopWords = {
				"a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "as", 
				"at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "cannot",
				"could", "did", "do", "does", "doing", "down", "during", "each", "few", "for", "from", "further",
				"had", "has", "have", "having", "he", "her", "here", "hers", "herself", "him", "himself", "his",
				"how", "i", "if", "in", "into", "is", "it", "its", "itself", "me", "more", "most", "my", "myself",
				"no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours",
				"ourselves", "out", "over", "own", "same", "she", "should", "so", "some", "such", "than", "that",
				"the", "their", "theirs", "them", "themselves", "then", "there", "these", "they", "this", "those",
				"through", "to", "too", "under", "until", "up", "very", "was", "we", "were", "what", "when", "where",
				"which", "while", "who", "whom", "why", "with", "would", "you", "your", "yours", "yourself", "yourselves"
		};	
	for (String temp:stopWords){
		 stopWordList.add(temp);
		}
	}
	public List<String> Tokenize(String text)
	{
		List<String> tokens = new ArrayList<String>();
		String[] tokensInCurrentLine = text.toLowerCase().split("[^a-zA-Z0-9]+");
		for(String token : tokensInCurrentLine)
		{
			tokens.add(token);
		}
		return tokens;
	}
	
	private Boolean isTokenStopWord(String token)
	{
		return stopWordList.contains(token);
	}
	
	private void AddTokensIntoFrequency(List<String> tokens)
	{
		for(String token : tokens)
		{
			if(isTokenStopWord(token))
				continue;
			
			Integer frequency = frequencies.get(token);
			if(frequency == null)
				frequency = 0;
			frequency++;
			frequencies.put(token, frequency);
		}
	}
	private void WriteTokenIntoFile(List<String> tokens,WebURL webUrl)
	{
		int docid = webUrl.getDocid();
		//docid.toString();
		lock.writeLock().lock();	
		try {
		String path_token = "E:/crawlData/tokens/"+docid+".txt";
		String path_url = "E:/crawlData/URL.txt";
		File file = new File(path_token);
		if (!file.exists()){
	            File parent = new File(file.getParent());
	            if (!parent.exists()){
	                parent.mkdirs();
	            }
		} 
		File file2 = new File(path_url);
		if (!file2.exists()){
            File parent = new File(file2.getParent());
            if (!parent.exists()){
                parent.mkdirs();
            }
		} 
		FileWriter fWriter = new FileWriter(file);
		FileWriter fWriter2 = new FileWriter(file2,true);
		StringBuilder builder = new StringBuilder();
		int position = 0;
			for (String token: tokens)
			{	
				if (token.isEmpty()) 
					continue;
				if (isTokenStopWord(token))
					continue;
				position++;
				builder.append(token+" "+position+" ");
				if (position % 1000 ==0) builder.append("\r\n");
			}
		fWriter.write(builder.toString());
		fWriter2.write(webUrl.getURL()+" "+docid+" \r\n");
		fWriter.close();
		fWriter2.close();
		} catch (Exception e){
			System.err.println("Error when writing");
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	private void AddTokensIntoTwoGrams(List<String> tokens)
	{
		for(int i=0; i<tokens.size()-1; i++)
		{
			String token1 = tokens.get(i);
			String token2 = tokens.get(i+1);
			
			if(isTokenStopWord(token1) || isTokenStopWord(token2))
				continue;
			
			TwoGram twoGram = new TwoGram(token1, token2);
			Integer count = towGramFrequencies.get(twoGram);
			if(count == null) {
				if (CrawlerManager.Instance().getURLSize()>=3000)
				continue;
				else count = 0;
			}
			count++;
			towGramFrequencies.put(twoGram, count);
		}
	}
	
	public void AddTokensIntoStatistics(List<String> tokens, WebURL webUrl)
	{
		AddTokensIntoFrequency(tokens);
		AddTokensIntoTwoGrams(tokens);
		WriteTokenIntoFile(tokens,webUrl);
	}
	
	static class TokenFrequencyComparor implements Comparator<Map.Entry<String, Integer>>
    {
		public int compare(Map.Entry< String, Integer>  freq1, Map.Entry< String, Integer>  freq2)
		{
			return freq2.getValue() - freq1.getValue();
		}
	}
	
	static class TwoGramFrequencyComparor implements Comparator<Map.Entry<TwoGram, Integer>>
    {
		public int compare(Map.Entry< TwoGram, Integer>  freq1, Map.Entry< TwoGram, Integer>  freq2)
		{
			return freq2.getValue() - freq1.getValue();
		}
	}
	
	public void PrintTopWordFrequencies(int count)
	{
		System.out.println("Top " + count + " words in frequency:");
		
		List< Map.Entry< String, Integer> > frequencyList = new ArrayList< Map.Entry<String, Integer> >(frequencies.entrySet());
		Collections.sort( frequencyList, new TokenFrequencyComparor());
		
		for(int i=0; i<count && i<frequencyList.size(); i++)
		{
			Map.Entry< String, Integer> entry = frequencyList.get(i);
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}
	
	public void PrintTwoGramFrequencies(int count)
	{
		System.out.println("Top " + count + " 2-grams in frequency:");
		
		List< Map.Entry< TwoGram, Integer> > frequencyList = new ArrayList< Map.Entry<TwoGram, Integer> >(towGramFrequencies.entrySet());
		Collections.sort( frequencyList, new TwoGramFrequencyComparor());
		
		for(int i=0; i<count && i<frequencyList.size(); i++)
		{
			Map.Entry< TwoGram, Integer> entry = frequencyList.get(i);
			System.out.println(entry.getKey().toString() + ":" + entry.getValue());
		}
	}
}
