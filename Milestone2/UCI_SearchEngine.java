import java.io.*;
import java.util.*;

public class UCI_SearchEngine
{
	private String rootPath = "/home/sidney/workspace/WebCrawler/crawlData/";
	private String pageTextFolderPath = rootPath + "pageText/";
	private String urlFilePath =  rootPath + "URL.txt";
	private String indexFilePath =  rootPath + "index/1.txt";
	private String tfidfFilePath =  rootPath + "index_weight/index_tfidf.txt";
	private HashSet<Integer> involvedDocs = new HashSet<Integer>();

	private HashMap<String, Double> tfidf_index = new HashMap<String, Double>();

	private HashMap<Integer, String> urls = new HashMap<Integer, String>();

	private HashMap<Integer, String> titles = new HashMap<Integer, String>();

	private SearchResult getQueryDocResult(String[] terms, int docid)
	{
		SearchResult result = new SearchResult();
		result.docid = docid;
		result.url = urls.get(docid);
		result.title = titles.get(docid);

		double score = 0;
		for(String term : terms)
		{
			String key = term + " " + docid;
			Double scoreCurrent =  tfidf_index.get(key);
			if(scoreCurrent == null)
				continue;
			score += scoreCurrent;
		}

		//result.textFragment = getTextFragment(docid, positions.get(0));
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

	private Boolean isStringInArray(String s, String[] array)
	{
		for(String ss : array)
		{
			if(ss.equals(s))
				return true;
		}
		return false;
	}

	private void initURLs()
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
			}
			reader.close();
		}
		catch(Exception ex)
		{
			System.out.println(line);
			System.out.println(ex);
		}
	}

	private void initCaches(String[] terms)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(tfidfFilePath));
			String line;
			while ((line = reader.readLine())!= null) 
			{
				String term = line.substring(0, line.indexOf(' '));
				line = line.substring(line.indexOf(' ') + 1);
				if(isStringInArray(term, terms))
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

			initURLs();
			//initTermDocPositions(terms);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}

	private void clearCaches()
	{
		tfidf_index.clear();
		involvedDocs.clear();
		urls.clear();
		titles.clear();
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

			String[] buffer = sb.toString().replaceAll("^ +| +$|( )+", "$1").trim().toLowerCase().split("[^a-zA-Z0-9]+");
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

	public ArrayList<SearchResult> query(String q, int number)
	{
		String[] terms = q.trim().toLowerCase().split(" ");
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		
		initCaches(terms);

		for(int docid : involvedDocs)
		{
			results.add(getQueryDocResult(terms, docid));
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