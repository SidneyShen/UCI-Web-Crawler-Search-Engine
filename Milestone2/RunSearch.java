import java.io.*;
import java.util.*;

public class RunSearch
{

	public static void main(String[] args)
	{
		UCI_SearchEngine searchEngine = new UCI_SearchEngine();
		// Cosine searchEngine = new Cosine();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String query;

		try
		{
			System.out.println("Please input your query:");
			while( (query = br.readLine()) != null)
			{

				System.out.println(query);
				ArrayList<SearchResult> results = searchEngine.query(query, 5);

				for(int i=0; i<results.size(); i++)
				{
					SearchResult result = results.get(i);
					System.out.println("Result " + (i+1) + ":");
					//System.out.println("docid: " + result.docid);
					System.out.println(result.title);
					System.out.println(result.url + " with score:" + result.score);
					System.out.println(result.textFragment);
				}

				System.out.println("\n");
				System.out.println("Please input your query:");
			}
		}
		catch(IOException ex)
		{

		}	
	}
};