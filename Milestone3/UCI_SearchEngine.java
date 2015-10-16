import java.io.*;
import java.util.*;

public class UCI_SearchEngine
{
	private String rootPath = "/home/sidney/workspace/WebCrawler/crawlData/";
	private String pageTextFolderPath = rootPath + "pageText/";
	private String urlFilePath =  rootPath + "URL.txt";
	private String singleTfidfFilePath =  rootPath + "index_weight/index_tfidf.txt";
	private String twoGramTfidfFilePath = rootPath + "grams_weight/grams_tfidf.txt";
	private String pageRankFilePath = rootPath + "pagerank.txt";
	private HashSet<Integer> involvedDocs = new HashSet<Integer>();
	private HashMap<String, String> stemmingMap = new HashMap<String, String>();
	private HashMap<String, Double> tfidf_index = new HashMap<String, Double>();
	private HashMap<Integer, String> urls = new HashMap<Integer, String>();
	private HashMap<Integer, String> titles = new HashMap<Integer, String>();
	private HashMap<Integer, String> linkInAnchor = new HashMap<Integer, String>();
	private HashMap<Integer, Double> pageRankScores = new HashMap<Integer, Double>();

	public static double weight_anchorText = 0.0;
	public static double weight_title = 2.4;
	public static double weight_pageRank = 0.0;

	public Boolean usingTwoGram = true;

	enum GramType
	{
		SingleWord,
		TwoGram
	};

	public UCI_SearchEngine()
	{
		try
		{
			String dictionaryFilePath = rootPath + "stemmingDictionary.txt";
			BufferedReader stemFileReader = new BufferedReader(new FileReader(dictionaryFilePath));
			String line;
			while( (line = stemFileReader.readLine()) != null)
			{
				String[] buffer = line.split(" ");
				int size = buffer.length;

				String origin = buffer[0];
				String stemmed = buffer[size - 1];

				stemmingMap.put(origin, stemmed);
			}
			stemFileReader.close();
		}
		catch(Exception ex)
		{

		}
	}

	private SearchResult getQueryDocResult(String[] query, int docid, GramType type)
	{
		ArrayList<String> terms = new ArrayList<String>();
		if(type == GramType.TwoGram)
		{
			for(int i=0; i<query.length -1 ; i++)
			{
				terms.add(query[i] + " " + query[i+1]);
			}
		}
		else if(type == GramType.SingleWord)
		{
			terms = new ArrayList<String>( Arrays.asList(query) );
		}

		SearchResult result = new SearchResult();
		result.docid = docid;
		result.url = urls.get(docid);
		result.title = titles.get(docid);

		if(result.url.contains("luci.ics.uci.edu"))
		{
			result.score = 0;
			return result;
		}

		if(result.url.contains("community/news"))
		{
			result.score = 0;
			return result;
		}

		if(result.url.startsWith("https"))
		{
			result.score = 0;
			return result;
		}

		double score = 0;
		for(String term : terms)
		{
			String key = term + " " + docid;
			Double scoreCurrent =  tfidf_index.get(key);
			if(scoreCurrent == null)
				continue;
			score += scoreCurrent;
		}


		// title 
		String title = titles.get(docid);
		if(title != null)
		{
			for(String term : title.toLowerCase().split(" "))
			{
				if(isStringInArray(term, query, false))
				{	
					score += weight_title;
					//System.out.println("Title:" + title);
				}
			}
		}
		else
		{
			System.out.println("Title not found in titles!!");
		}

		// link anchor
		String anchorText = linkInAnchor.get(docid);
		if(anchorText != null)
		{	
			for(String anchor : anchorText.toLowerCase().split(" "))
			{
				if(isStringInArray(anchor, query, false))
				{
					score += weight_anchorText;
				}
			}
			//System.out.println("AnchorText:" + anchorText);
		}
		else
		{
			//System.out.println("Anchor text not found !!");
		}

		Double pageRankScore = pageRankScores.get(docid);
		if(pageRankScore != null)
		{
			score += pageRankScore * weight_pageRank;
		}
		else 
		{
			System.out.println("pageRank for doc " + docid + " cannot be found, something is wrong !");
		}


		result.score = score;
		return result;
	}

	private class resultComparator implements Comparator<SearchResult>
	{   
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

	private Boolean isStringInArray(String s, String[] array, Boolean exactMatch)
	{
		for(String ss : array)
		{
			if(exactMatch && ss.equals(s))
				return true;
			if(!exactMatch && s.startsWith(ss))
				return true;
		}
		return false;
	}

	private void initPageRankScores()
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(pageRankFilePath));

			int docid = 1;
			String line;
			while( (line = reader.readLine()) != null)
			{
				double pageRank = Double.parseDouble( line.substring(line.indexOf(' ') + 1) );
				
				pageRank = java.lang.Math.log(1 + pageRank);
				pageRankScores.put(docid, pageRank);
				docid++;
			}

			reader.close();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}

	private void initURLs(String[] query)
	{
		String line = "";
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(urlFilePath));
			
			while ((line = reader.readLine())!= null) 
			{
				String url = line.substring(0, line.indexOf(' '));
				Integer docid = Integer.parseInt( line.substring(line.lastIndexOf(' ') + 1).trim() );
				String title = line.substring(line.indexOf(' ') + 1, line.lastIndexOf(' ')).trim();

				if(involvedDocs.contains(docid))
				{	
					urls.put(docid, url);
					titles.put(docid, title);
				}
				else
				{
					for(String term : title.toLowerCase().split(" "))
					{
						if(isStringInArray(term, query, false))
						{
							involvedDocs.add(docid);
							urls.put(docid, url);
							titles.put(docid, title);
						}
					}
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

	private void initCaches(String[] query, GramType type)
	{
		try
		{
			ArrayList<String> terms = new ArrayList<String>();
			String filePath = null;
			if(type == GramType.TwoGram)
			{
				filePath = twoGramTfidfFilePath;

				if(query.length <= 1)
					return;

				for(int i=0; i<query.length - 1; i++)
				{
					terms.add( query[i] + " " + query[i+1] );
				}
			}
			else if(type == GramType.SingleWord)
			{	
				terms = new ArrayList<String>( Arrays.asList(query) );
				filePath = singleTfidfFilePath;
			}

			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = reader.readLine())!= null) 
			{
				String term = line.substring(0, line.indexOf(' '));
				line = line.substring(line.indexOf(' ') + 1);

				if(type == GramType.TwoGram)
				{
					term = term + " " + line.substring(0, line.indexOf(' '));
					line = line.substring(line.indexOf(' ') + 1);
				}

				if(isStringInArray(term, terms.toArray(new String[terms.size()]), true))
				{
					for(String doc_score : line.split(","))
					{
						doc_score = doc_score.trim();
						int docid = Integer.parseInt(doc_score.substring(0, doc_score.indexOf(':')));
						
						doc_score = doc_score.substring(doc_score.indexOf(':')+1);
						doc_score = doc_score.trim();
						Double tfidf = Double.parseDouble(doc_score);
						
						if(tfidf > 0)
						{
							tfidf_index.put(term + " " + docid, tfidf);
							involvedDocs.add(docid);
						}
					}
				}
			}
			reader.close();

			initURLs(query);

			initLinkInAnchor();

			initPageRankScores();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}

	private void initLinkInAnchor()
	{
		try
		{
			String linkFilePath = rootPath + "links.txt";

			BufferedReader reader = new BufferedReader(new FileReader(linkFilePath));

			String line;
			while( (line = reader.readLine()) != null)
			{
				Integer sourceDocID = Integer.parseInt( line.substring(0, line.indexOf(' ')) );
				line = reader.readLine();
				Integer targetDocID = Integer.parseInt( line.substring(0, line.indexOf(' ')) );
				line = reader.readLine();

				if(involvedDocs.contains(targetDocID))
				{
					String anchorText = line;
					String str = linkInAnchor.get(targetDocID);
					if(str == null)
						str = "";
					str = str + " " + anchorText;
					linkInAnchor.put(targetDocID, str);
				}
			}

			reader.close();
		}
		catch(Exception ex)
		{
			System.out.println("Exception in initLinkInAnchor");
			System.out.println(ex);
		}
	}

	private void clearCaches()
	{
		tfidf_index.clear();
		involvedDocs.clear();
		urls.clear();
		titles.clear();
		linkInAnchor.clear();
		pageRankScores.clear();
	}

	private int max(int a, int b)
	{
		return a > b ? a : b;
	}

	private int min(int a, int b)
	{
		return a < b ? a : b;
	}


	private int getBestMatchPosition(String[] query, String[] document)
	{
		int firstMatch = -1;
		int longestMatch = 0;
		Boolean isMatching = false;
		int bestMatch = -1;
		for(int i=0; i<document.length; i++)
		{
			if( isStringInArray(document[i], query, false) )
			{
				if(!isMatching)
				{
					isMatching = true;
					firstMatch = i;
				}
			}
			else
			{
				if(isMatching)
				{
					isMatching = false;

					if( i - firstMatch > longestMatch)
					{
						longestMatch = i - firstMatch;
						bestMatch = (i + firstMatch) / 2;
					}
				}
			}
		}

		if(isMatching)
		{
			if(document.length - firstMatch > longestMatch)
			{
				bestMatch = firstMatch;
			}
		}
		return bestMatch;
	}

	private String getTextFragment(int docid, String[] terms)
	{
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
			reader.close();
			
			String fullText = sb.toString();
			String[] buffer = fullText.replaceAll("^ +| +$|( )+", "$1").trim().toLowerCase().split("[^a-zA-Z0-9]+");
			
			int bestIndex = getBestMatchPosition(terms, buffer);
			if(bestIndex == -1)
				return "";

			for(int i=bestIndex - 10; i < bestIndex + 10; i++)
			{
				if(i < 0 || i >= buffer.length)
					continue;

				result.append(buffer[i]);
				result.append(" ");
			}

			return result.toString();
			
		}
		catch (Exception e)
		{
			System.out.println(e);
		}

		return result.toString();
	}

	public ArrayList<SearchResult> query(String q, int number)
	{
		String[] terms = q.trim().toLowerCase().split(" ");

		for(int i=0; i < terms.length; i++)
		{
			String stemmed = stemmingMap.get( terms[i] );

			if(stemmed != null)
				terms[i] = stemmed;

			//System.out.println(terms[i]);
		}

		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		
		GramType gramType = usingTwoGram ? GramType.TwoGram : GramType.SingleWord;

		initCaches(terms, gramType);

		if(involvedDocs.size() < number)
		{
			gramType = GramType.SingleWord;
			initCaches(terms, gramType);
		}

		for(int docid : involvedDocs)
		{
			results.add(getQueryDocResult(terms, docid, gramType));
		}

		Collections.sort(results, new resultComparator());
		
		if(results.size() < number)
			number = results.size();
		results = new ArrayList( results.subList(0, number) );
		
		for(SearchResult result : results)
		{
			result.textFragment = getTextFragment(result.docid, terms);
		}

		clearCaches();
		return results;
	}
};