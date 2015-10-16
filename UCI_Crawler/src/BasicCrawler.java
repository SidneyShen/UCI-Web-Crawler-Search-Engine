/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.*;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Yasser Ganjisaffar [lastname at gmail dot com]
 */
public class BasicCrawler extends WebCrawler {

	private static Logger logger = LoggerFactory.getLogger(BasicCrawler.class);
	
	private final static Pattern BINARY_FILES_EXTENSIONS =
        Pattern.compile(".*\\.(bmp|gif|jpe?g|png|tiff?|pdf|ico|xaml|pict|rif|pptx?|ps" +
        "|mid|mp2|mp3|mp4|wav|wma|au|aiff|flac|ogg|3gp|aac|amr|au|vox" +
        "|avi|mov|mpe?g|ra?m|m4v|smil|wm?v|swf|aaf|asf|flv|mkv" +
        "|zip|rar|gz|7z|aac|ace|alz|apk|arc|arj|dmg|jar|lzip|lha|tgz|" +
        "|csv|bin|tar|css|rss|xml|dat|ppt|doc|docx|pfm)" +
        "(\\?.*)?$"); // For url Query parts ( URL?q=... )
  //private final static Pattern SKIPSITE = Pattern.compile("^http://(ftp|fano|kdd)\\.ics\\.uci\\.edu/.*");
 // private final static Pattern QUERRFILTERS = Pattern.compile(".*[\\?@=].*");

  private static String pathOfTextFile = null;
	private static String pathOfLinkFile = null;
	
	
	private boolean isBlockedURL(String href)
	{
		if (href.contains("calendar.ics.uci.edu") && href.contains("year=")) 
		{
			int pos = href.lastIndexOf("year=");
			String year = href.substring(pos+4, href.length());
			int numYear = Integer.parseInt(year);
			if (numYear > 2015 || numYear < 2006) 
			{
				return true;
			}
		}
	//	if (href.contains("drzaius.ics.uci.edu/cgi-bin/")) 
	//	{
	//		return true;
	//	}
		
	//	if (href.contains("fano.ics.uci.edu")) 
	//	{
	//		return true;
	//	}
		
	///	if (href.contains("djp3-pc2.ics.uci.edu")) 
	//	{
	//		return true;
	//	}
			
		// ignore machine learning dynamic pages
	//	if (href.contains("http://archive.ics.uci.edu/ml/datasets.html?")) {
	//		return true;
	//	}
		
	//	if(href.contains("http://www.ics.uci.edu/~xhx/project/"))
	//		return true;
		// ignore physics.ics.uci.edu
	//	if (href.contains("physics.uci.edu")) 
	//	{
	//		return true;
	//	}
		// ignore the any other dynamic page
	//	 if(!href.contains("calendar.ics.uci.edu") && href.contains("?")) {
	//	 return false;
	//	 }
		// ignore the machine learning dataset
	//	if (href.contains("machine-learning-databases")) 
	//	{
	//		return true;
	//	}
	//	if(href.contains("http://mlearn.ics.uci.edu/databases"))
//			return true;
		
	//	if(href.contains("http://archive.ics.uci.edu/datasets"))
	//		return true;
	//	if(href.contains("http://graphmod.ics.uci.edu/repos/"))
	//	return true;
		
		return false;
	}
  /**
   * You should implement this function to specify whether the given url
   * should be crawled or not (based on your crawling logic).
   */
  @Override
  public boolean shouldVisit(Page page, WebURL url) {
    String href = url.getURL().toLowerCase();

    if(isBlockedURL(href))
    	return false;
    
    if(CrawlerManager.Instance().IsUrlVisited(href))
    	return false;
    
    
    return !BINARY_FILES_EXTENSIONS.matcher(href).matches() 
     //   && !SKIPSITE.matcher(href).matches()
       // && !QUERRFILTERS.matcher(href).matches()
       && href.contains(".ics.uci.edu");
  }
  

  /**
   * This function is called when a page is fetched and ready to be processed
   * by your program.
   */
  @Override
  public void visit(Page page)  
  {
    WebURL webUrl = page.getWebURL();
    int docid = webUrl.getDocid();
    String url = webUrl.getURL().toLowerCase();
    System.out.println("URL of doc " + docid + ": " + url);
    
    
    CrawlerManager.Instance().MarkUrlAsVisited(url);
    
    String subDomain = page.getWebURL().getSubDomain();
    CrawlerManager.Instance().MarkSubdomainAsVisited(subDomain);
    
    if (page.getParseData() instanceof HtmlParseData) 
    {

        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        //String title = htmlParseData.getTitle();
        String text = htmlParseData.getText();
        //String html = htmlParseData.getHtml();
        Set<WebURL> links = htmlParseData.getOutgoingUrls();
        try {
			CrawlerManager.Instance().AddPageTextIntoStatistics(page, text, webUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

        if (pathOfTextFile != null) {
            //downloadText(webUrl, title, text);
        }
        if (pathOfLinkFile != null){
            //downloadLink(webUrl, links);
        }
    }

  }

  public static void setPathOfTextFile(String path){
      pathOfTextFile = path;
  }

  public static void setPathOfLinkFile(String path){
      pathOfLinkFile = path;
  }
}
  /*private void downloadText(WebURL url, String title, String text) {
      try {
        String path = url.getURL().toLowerCase().substring("http://".length());
        File file = new File(pathOfTextFile + path);
        if (file.isDirectory()){
            file = new File(pathOfTextFile + path + "index.txt");
        }else if (url.getPath().lastIndexOf('.') < url.getPath().lastIndexOf('/')){
            file = new File(pathOfTextFile + path + "/index.txt");
        }
        if (!file.exists()){
            File parent = new File(file.getParent());
            if (!parent.exists()){
                parent.mkdirs();
            }
        }
        FileWriter fWriter = new FileWriter(file);
        if (title!=null){
            fWriter.write(title);
        }
        if (text != null){
            fWriter.write(text);
        }
        fWriter.close();
      } catch (Exception e) {
          System.err.println("Error when writing " + url );
          e.printStackTrace();
      }
  }
  */

  /*private synchronized void downloadLink(WebURL url, Set<WebURL> links){
      File file = new File(pathOfLinkFile);
      if (!file.exists()){
        File parent = new File(file.getParent());
        if (!parent.exists()){
            parent.mkdirs();
        }
    }

    try {
        FileWriter fWriter = new FileWriter(file,true);
        StringBuilder builder = new StringBuilder(url.getURL());
        builder.append("\t");
        for ( WebURL link : links){
            builder.append(link.getURL());
            builder.append(" ");
        }
        builder.append("\n");
        fWriter.write(builder.toString());
        fWriter.close();
    } catch (IOException e) {
        System.err.println("Error when writing " + url.getURL());
        e.printStackTrace();
    }
  }
*/