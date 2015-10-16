import java.io.*;
import java.util.*;

class NDCG
{
	private static Boolean isPrintResult = false;
	private static HashMap<String, Integer> urlScore = new HashMap<String, Integer>(); 
	private static String[] queries = new String[] 
	{
		"mondego",
		"machine learning",
		"software engineering",
		"security",
		"student affairs",
		"graduate courses",
		"crista lopes",
		"rest",
		"computer games",
		"information retrieval"
	};
	private static void initURLScore(String queryString)
	{
		if(queryString.startsWith("mondego"))
		{
			urlScore.put("http://nile.ics.uci.edu:9000/wifi", 3);
			urlScore.put("http://mondego.ics.uci.edu/", 3);
			urlScore.put("http://mondego.ics.uci.edu/index.html", 3);
			urlScore.put("http://mailman.ics.uci.edu/mailman/listinfo/mondego", 2);
			urlScore.put("http://nile.ics.uci.edu:9000/wifi/user/account/", 2);
			urlScore.put("http://mailman.ics.uci.edu/mailman/admin/mondego", 2);
			urlScore.put("http://sdcl.ics.uci.edu/mondego-group/", 1);
			urlScore.put("http://nile.ics.uci.edu:9000/wifi/forgotpassword", 1);
			urlScore.put("http://nile.ics.uci.edu:9000/wifi/admin/estates/new", 1);
			urlScore.put("http://sdcl.ics.uci.edu/2012/05/calico-for-the-mondego-group/", 1);
			urlScore.put("http://mailman.ics.uci.edu/mailman/private/mondego/", 1);
			return;
		}
		// machine learning
		if(queryString.startsWith("machine learning"))
		{
			urlScore.put("http://archive.ics.uci.edu/ml/machine-learning-databases/", 3);
			urlScore.put("http://archive.ics.uci.edu/ml/about.html", 3);
			urlScore.put("http://cml.ics.uci.edu/", 2);
			urlScore.put("http://archive.ics.uci.edu/ml/", 2);
			urlScore.put("http://mlearn.ics.uci.edu/MLRepository.html", 2);
			urlScore.put("http://sli.ics.uci.edu/Classes/2013F-273a", 1);
			urlScore.put("http://sli.ics.uci.edu/Classes/2015W-273a", 1);
			urlScore.put("http://archive.ics.uci.edu/ml/datasets.html", 1);
			urlScore.put("http://archive.ics.uci.edu/ml/datasets/Credit+Approval", 1);
			urlScore.put("http://archive.ics.uci.edu/ml/datasets/Diabetes", 1);
			return;
		}
		if(queryString.startsWith("software engineering"))
		{
			urlScore.put("http://www.ics.uci.edu/prospective/en/degrees/software-engineering/", 3);
			urlScore.put("http://www.ics.uci.edu/grad/degrees/degree_se.php", 3);
			urlScore.put("http://www.ics.uci.edu/ugrad/degrees/degree_se.php", 2);
			urlScore.put("http://www.ics.uci.edu/prospective/en/degrees/computer-science-engineering/", 2);
			urlScore.put("http://www.ics.uci.edu/~ziv/ooad/intro_to_se/tsld008.htm", 2);
			urlScore.put("http://www.ics.uci.edu/~djr/DebraJRichardson/SE4S.html", 1);
			urlScore.put("http://www.ics.uci.edu/faculty/area/area_software.php", 1);
			urlScore.put("http://se.ics.uci.edu/", 1);
			urlScore.put("http://www.ics.uci.edu/~emilyo/SimSE/se_rules.html", 1);
			urlScore.put("http://se4s.ics.uci.edu/", 1);
			return;
		}
		if(queryString.startsWith("security"))
		{
			urlScore.put("http://ftp.ics.uci.edu/pub/centos0/ics-custom-build/BUILD/nagios-3.0.6/html/docs/security.html", 3);
			urlScore.put("http://sconce.ics.uci.edu/", 3);
			urlScore.put("http://www.ics.uci.edu/computing/linux/security.php", 2);
			urlScore.put("http://www.ics.uci.edu/computing/linux/file-security.php", 2);
			urlScore.put("http://www.ics.uci.edu/~gts/", 2);
			urlScore.put("http://sprout.ics.uci.edu/", 1);
			urlScore.put("http://www.ics.uci.edu/~keldefra/manet.htm", 1);
			urlScore.put("http://sprout.ics.uci.edu/past_projects/odb/", 1);
			urlScore.put("http://sprout.ics.uci.edu/projects/privacy-dna/", 1);
			urlScore.put("http://www.ics.uci.edu/~sbruntha/", 1);
			return;
		}
		if(queryString.startsWith("student affairs"))
		{
			urlScore.put("http://www.ics.uci.edu/prospective/en/contact/student-affairs/", 3);
			urlScore.put("http://www.ics.uci.edu/about/search/search_sao.php", 3);
			urlScore.put("http://www.ics.uci.edu/ugrad/", 2);
			urlScore.put("http://www.ics.uci.edu/grad/sao/", 2);
			urlScore.put("http://www.ics.uci.edu/about/visit/", 2);
			urlScore.put("http://www.ics.uci.edu/about/annualreport/2005-06/sao.php", 1);
			urlScore.put("http://www.ics.uci.edu/ugrad/sao/", 1);
			urlScore.put("http://www.ics.uci.edu/about/annualreport/2006-07/sao.php", 1);
			urlScore.put("http://www.ics.uci.edu/grad/", 1);
			urlScore.put("http://www.ics.uci.edu/about/about_contact.php", 1);
			return;
		}
		if(queryString.startsWith("graduate courses"))
		{
			urlScore.put("http://www.ics.uci.edu/grad/courses/", 3);
			urlScore.put("http://www.ics.uci.edu/grad/courses/listing.php?year=2014&level=ALL&department=CS&program=1COMPSCI", 3);
			urlScore.put("http://www.ics.uci.edu/grad/courses/listing.php?year=2014&level=ALL&department=EECS&program=0CSSS", 2);
			urlScore.put("http://www.ics.uci.edu/grad/courses/details.php?id=69", 2);
			urlScore.put("http://www.ics.uci.edu/grad/courses/details.php?id=48", 2);
			urlScore.put("http://www.ics.uci.edu/grad/courses/details.php?id=59", 1);
			urlScore.put("http://www.ics.uci.edu/grad/courses/details.php?id=89", 1);
			urlScore.put("http://www.ics.uci.edu/grad/courses/details.php?id=72", 1);
			urlScore.put("http://www.ics.uci.edu/grad/courses/details.php?id=51", 1);
			urlScore.put("http://www.ics.uci.edu/grad/courses/details.php?id=56", 1);
			return;
		}
		if(queryString.startsWith("crista lopes"))
		{
			urlScore.put("http://www.ics.uci.edu/~lopes/", 3);
			urlScore.put("http://www.ics.uci.edu/~lopes/patents.html", 3);
			urlScore.put("http://www.ics.uci.edu/~lopes/publications.html", 2);
			urlScore.put("http://www.ics.uci.edu/~tdebeauv/", 2);
			urlScore.put("http://mondego.ics.uci.edu/", 2);
			urlScore.put("http://luci.ics.uci.edu/blog/?tag=crista-lopes&paged=2", 1);
			urlScore.put("http://www.ics.uci.edu/~lopes/teaching/cs221W12/", 1);
			urlScore.put("http://luci.ics.uci.edu/blog/?p=416", 1);
			urlScore.put("http://luci.ics.uci.edu/blog/?feed=rss2&tag=crista-lopes", 1);
			urlScore.put("http://luci.ics.uci.edu/blog/?tag=crista-lopes", 1);
			return;
		}
		if(queryString.startsWith("rest"))
		{
			urlScore.put("http://www.ics.uci.edu/~fielding/pubs/dissertation/top.htm", 3);
			urlScore.put("http://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm", 3);
			urlScore.put("http://www.ics.uci.edu/~fielding/pubs/dissertation/evaluation.htm", 2);
			urlScore.put("http://www.ics.uci.edu/~fielding/pubs/dissertation/abstract.htm", 2);
			urlScore.put("http://www.ics.uci.edu/~fielding/pubs/dissertation/introduction.htm", 2);
			urlScore.put("http://www.ics.uci.edu/~fielding/pubs/dissertation/conclusions.htm", 1);
			urlScore.put("http://www.ics.uci.edu/~fielding/talks/webarch_9805/", 1);
			urlScore.put("http://asterixdb.ics.uci.edu/documentation/api.html", 1);
			urlScore.put("http://www.ics.uci.edu/~fielding/pubs/dissertation/software_arch.htm", 1);
			urlScore.put("http://www.ics.uci.edu/~fielding/", 1);
			return;
		}
		if(queryString.startsWith("computer games"))
		{
			urlScore.put("http://www.ics.uci.edu/prospective/en/degrees/computer-game-science/", 3);
			urlScore.put("http://cgvw.ics.uci.edu/", 3);
			urlScore.put("http://frost.ics.uci.edu/cs113/", 2);
			urlScore.put("http://cgvw.ics.uci.edu/affiliated-faculty/", 2);
			urlScore.put("https://archive.ics.uci.edu/ml/datasets/YouTube+Multiview+Video+Games+Dataset", 2);
			urlScore.put("http://www.ics.uci.edu/community/events/gameday2015/", 1);
			urlScore.put("http://www.ics.uci.edu/~jwross/courses/ics60/", 1);
			urlScore.put("http://cgvw.ics.uci.edu/author/venita/", 1);
			urlScore.put("http://www.ics.uci.edu/~eppstein/gina/vidgames.html", 1);
			urlScore.put("http://cgvw.ics.uci.edu/author/admin/", 1);
			return;
		}
		if(queryString.startsWith("information retrieval"))
		{
			urlScore.put("http://www.ics.uci.edu/~djp3/classes/2014_01_INF141/calendar.html", 3);
			urlScore.put("http://www.ics.uci.edu/~djp3/classes/2009_01_02_INF141/", 3);
			urlScore.put("http://www.ics.uci.edu/~djp3/classes/2010_01_CS221/", 2);
			urlScore.put("http://www.ics.uci.edu/~lopes/teaching/cs221W12/", 2);
			urlScore.put("http://www.ics.uci.edu/~djp3/classes/2014_01_INF141/structure.html", 2);
			urlScore.put("http://www.ics.uci.edu/~lopes/teaching/cs221W13/", 1);
			urlScore.put("http://www.ics.uci.edu/~djp3/classes/2009_01_02_INF141/calendar.html", 1);
			urlScore.put("http://www.ics.uci.edu/~kay/courses/i141/w15.html", 1);
			urlScore.put("http://www.ics.uci.edu/~lopes/", 1);
			urlScore.put("http://www.ics.uci.edu/~kay/courses/i141/refs.html", 1);
			return;
		}
	}

	private static void clearCache()
	{
		urlScore.clear();
	}

	private static double getNDCG(ArrayList<SearchResult> results)
	{
		double ndcg = 0;
		for(int i=0; i<results.size(); i++)
		{
			String url = results.get(i).url;
			Integer rel = urlScore.get(url);
			if(rel == null)
				rel = 0;

			if(i == 0)
				ndcg += rel;
			else
			{
				ndcg += rel / java.lang.Math.log(i + 1);
			}
		}
		return ndcg;
	}

	private static void printResults(ArrayList<SearchResult> results)
	{
		for(SearchResult result : results)
		{
			System.out.println(result.url);
		}
	}

	private static double getOverallNDCG(double weight_title, double weight_anchor, double weight_pageRank, Boolean useTwoGram)
	{
		UCI_SearchEngine searchEngine = new UCI_SearchEngine();
		searchEngine.weight_anchorText = weight_anchor;
		searchEngine.weight_title = weight_title;
		searchEngine.weight_pageRank = weight_pageRank; 
		searchEngine.usingTwoGram = useTwoGram;
		
		double ndcg_sum = 0;
		for(String query : queries)
		{
			initURLScore(query);

			if(urlScore.size() == 0)
			{
				System.out.println("urlScore is not constructed properly for " + query);
			}

			ArrayList<SearchResult> results = searchEngine.query(query, 5);

			if(isPrintResult)
			{
				System.out.println(query + ":");
				printResults(results);
			}
			ndcg_sum += getNDCG(results);
			clearCache();
		}

		return ndcg_sum / queries.length;
	}

	public static void main(String[] args)
	{
		isPrintResult = true;
		double ndcg = getOverallNDCG(0, 0, 0, false);
		System.out.println("without improvement:" + ndcg);
		isPrintResult = false;

		double bestNDCG = 0.0;

		double best_weight_title = 0.0;
		double best_weight_anchor = 0.0;
		double best_weight_pageRank = 0.0;

		for(double weight_title = 0.0; weight_title < 1.0; weight_title += 0.1)
			for(double weight_anchor = 0.0; weight_anchor < 1.0; weight_anchor += 0.1)
				for(double weight_pageRank = 0.0; weight_pageRank < 0.1; weight_pageRank += 0.1)
				{
					ndcg = getOverallNDCG(weight_title, weight_anchor, weight_pageRank, true);
					System.out.println(ndcg + " with weight_title = " + weight_title 
						+ ", weight_anchor = " + weight_anchor 
						+ ", weight_pageRank = " + weight_pageRank );

					if(ndcg > bestNDCG)
					{
						best_weight_title = weight_title;
						best_weight_anchor = weight_anchor;
						best_weight_pageRank = weight_pageRank;
					}
				}
		
		System.out.println("best_wegith_title = " + best_weight_title);
		System.out.println("best_weight_anchor = " + best_weight_anchor);
		System.out.println("best_weight_pageRank = " + best_weight_pageRank);

		isPrintResult = true;
		ndcg = getOverallNDCG(best_weight_title, best_weight_anchor, best_weight_pageRank, true);
	}
}