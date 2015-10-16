import java.io.*;
import java.util.*;

public class Cosine{
	private String rootPath = "/home/sidney/workspace/WebCrawler/crawlData/";
	private String pageTextFolderPath = rootPath + "pageText/";
	private String urlFilePath =  rootPath + "URL.txt";
	private String indexFilePath =  rootPath + "index/1.txt";
	private String tfidfFilePath =  rootPath + "index_weight/index_tfidf_normalized.txt";

	private int unique_Weburl = 80000;
	private HashMap<String, Integer> queryTermFreq = new HashMap<String, Integer>();
	private HashMap<String, Integer> queryTermDocFreq = new HashMap<String, Integer>();
	private HashMap<String, Double> queryTermTFIDF = new HashMap<String, Double>();
	private HashMap<Integer, HashMap<String, Double>> docQueryTermTfidf = new HashMap<Integer, HashMap<String, Double>>();
	private HashMap<Integer, String> urls = new HashMap<Integer, String>();
	private HashMap<Integer, String> titles = new HashMap<Integer, String>();

	private void calQueryTfidf(String[] terms){
		// Count query term freqency
		for(String str:terms){
			if(queryTermFreq.containsKey(str))
				queryTermFreq.put(str, queryTermFreq.get(str)+1);
			else
				queryTermFreq.put(str, 1);
		}
		// Count doc frequency
		for(String str:queryTermFreq.keySet()){
			calQueryIdf(str);
		}

		for(Map.Entry<String, Integer> entry : queryTermFreq.entrySet()){
			String term = entry.getKey();
			Integer tf = entry.getValue();
			Integer df = queryTermDocFreq.get(term);
			System.out.println(term+" df "+df);
			Double tfidf = Math.log10(1+tf) * Math.log10(unique_Weburl/df);
			queryTermTFIDF.put(term, tfidf);
		}

		// Normalize tfidf
		double sum = 0;
		for(Double tfidf : queryTermTFIDF.values()){
			sum += Math.pow(tfidf.doubleValue(),2);
		}
		sum = Math.sqrt(sum);



		// Update tfidf
		for(Map.Entry<String, Double> entry : queryTermTFIDF.entrySet()){
			queryTermTFIDF.put(entry.getKey(), entry.getValue().doubleValue()/sum);
		}

	}

	private void calQueryIdf(String str){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(tfidfFilePath));
			String line;
			while ((line = reader.readLine())!= null) 
			{
				String term = line.substring(0, line.indexOf(' '));
				if(queryTermFreq.containsKey(term)){
					line = line.substring(line.indexOf(' ') + 1);
					String[] docWithScore = line.split(",");
					int docFreq = docWithScore.length;
					queryTermDocFreq.put(term, docFreq);

					// Calculate related Doc based on this term
					for(String doc_score : docWithScore){
						doc_score = doc_score.trim();
						Integer docid = Integer.valueOf(doc_score.substring(0, doc_score.indexOf(':')));
						
						doc_score = doc_score.substring(doc_score.indexOf(':')+1);
						doc_score = doc_score.trim();
						Double tfidf = Double.parseDouble(doc_score);

						if(docQueryTermTfidf.containsKey(docid)){
							HashMap<String, Double> termTfidf = docQueryTermTfidf.get(docid);
							termTfidf.put(term, tfidf);
							docQueryTermTfidf.put(docid, termTfidf);
						}
						else{
							HashMap<String, Double> termTfidf = new HashMap<String, Double>();
							termTfidf.put(term, tfidf);
							docQueryTermTfidf.put(docid, termTfidf);
						}
					}
				}
			}
			reader.close();
		}
		catch(Exception ex){	
			System.out.println(ex);
		}
	}

	private void initURLs(){
		String line = "";
		try{
			BufferedReader reader = new BufferedReader(new FileReader(urlFilePath));
			
			while ((line = reader.readLine())!= null) 
			{
				String url = line.substring(0, line.indexOf(' '));
				Integer docid = Integer.parseInt( line.substring(line.lastIndexOf(' ') + 1).trim() );
				String title = line.substring(line.indexOf(' ') + 1, line.lastIndexOf(' ')).trim();

				if(docQueryTermTfidf.containsKey(docid)){	
					urls.put(docid, url);
					titles.put(docid, title);
				}
			}
			reader.close();
		}
		catch(Exception ex)
		{
			System.out.println(line);
			System.out.println(ex);
		}
	}

	private SearchResult getQueryDocResult(Integer docid){
		SearchResult result = new SearchResult();
		result.docid = docid;
		result.url = urls.get(docid);
		result.title = titles.get(docid);

		double score = 0;

		HashMap<String, Double> termTFIDF = docQueryTermTfidf.get(docid);

		for(Map.Entry<String, Double> entry: queryTermTFIDF.entrySet()){
			String term = entry.getKey();
			if(termTFIDF.containsKey(term))
				score += entry.getValue()*termTFIDF.get(term);
		}

		result.score = score;
		return result;
	}

	private class resultComparator implements Comparator<SearchResult>{   
		public int compare(SearchResult r1, SearchResult r2)
		{
			if(r1.score == r2.score)
				return 0;
			if(r1.score < r2.score)
				return 1;
			else 
				return -1;
		}
	}

	private String getTextFragment(int docid, String[] terms){
		String path = pageTextFolderPath + docid + ".txt";
		StringBuilder result = new StringBuilder();
		try 
		{					
			BufferedReader reader = new BufferedReader(new FileReader(path));

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine())!= null) 
			{
				sb.append(line + " ");
			}

			String[] buffer = sb.toString().replaceAll("^ +| +$|( )+", "$1").trim().toLowerCase().split(" ");
			for(int i=0; i<buffer.length; i++)
			{
				String token = buffer[i];
				if(isStringInArray(token, terms))
				{
					for(int j = i - 10; j < i + 10; j++)
					{
						if(j >= 0 && j < buffer.length)
							result.append(buffer[j] + " ");
					}
					reader.close();
					return result.toString();
				}
			}
			reader.close();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}

		return result.toString();
	}

	private Boolean isStringInArray(String s, String[] array){
		for(String ss : array)
		{
			if(ss.equals(s))
				return true;
		}
		return false;
	}

	private void clearCaches()	{
		queryTermFreq.clear();
		queryTermDocFreq.clear();
		queryTermTFIDF.clear();
		docQueryTermTfidf.clear();
		urls.clear();
		titles.clear();
	}

	public ArrayList<SearchResult> query(String q, int number){
		String[] terms = q.trim().toLowerCase().split(" ");
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();

		calQueryTfidf(terms);

		initURLs();

		for(Integer docid : docQueryTermTfidf.keySet()){
			results.add(getQueryDocResult(docid));
		}

		Collections.sort(results, new resultComparator());
		
		if(results.size() < number)
			number = results.size();
		results = new ArrayList( results.subList(0, number) );
		for(SearchResult result : results){
			result.textFragment = getTextFragment(result.docid, terms);
		}
		clearCaches();
		return results;
	}
}