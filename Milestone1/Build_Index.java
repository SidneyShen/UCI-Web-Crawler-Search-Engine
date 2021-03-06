//package milestone1;

import java.awt.List;
import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Build_Index 
{
	private ArrayList<index_element> indexList = new ArrayList<index_element>();
	private int blockNum = 0;
	private int unique_Weburl = 0;
	private int unique_words = 0;

	private String lineEnd = System.getProperty("line.separator");
	
	// Input file paths
<<<<<<< .mine
	private String url_filePath = "/home/sidney/workspace/WebCrawler/crawlData/URL.txt";
	private String token_path = "/home/sidney/workspace/WebCrawler/crawlData/tokens/";
	
=======

	private String rootPath = "E:/crawlDataProcessed/";
	private String url_filePath = rootPath + "URL.txt";
	private String token_path = rootPath + "tokens/";
	private String gram_path = rootPath + "grams/";
>>>>>>> .r38
	// Output file path
	// TODO : figure out what they are
<<<<<<< .mine
	private String indexFolderPath = "/home/sidney/workspace//WebCrawler/crawlData/index/";
	private String index_sorted_filePath ="/home/sidney/workspace/WebCrawler/crawlData/index_weight/index_sorted.txt";
	private String index_tfidf_filePath = "/home/sidney/workspace/WebCrawler/crawlData/index_weight/index_tfidf.txt";
	private String url_sorted_filePath = "/home/sidney/workspace/WebCrawler/crawlData/URL_sorted.txt";
=======
	private String indexFolderPath = rootPath + "index/";
	private String gramsFolderPath = rootPath + "grams_index/";
	private String index_sorted_filePath =rootPath + "index_weight/index_sorted.txt";
	private String index_tfidf_filePath = rootPath + "index_weight/index_tfidf.txt";
	private String grams_sorted_filePath =rootPath + "grams_weight/grams_sorted.txt";
	private String grams_tfidf_filePath = rootPath + "grams_weight/grams_tfidf.txt";
	private String url_sorted_filePath = rootPath + "URL_sorted.txt";
>>>>>>> .r38
	
	private String removeFileExtension(String fileName)
	{
		if(fileName.contains(".")) 
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		return fileName;
	}

	private void readTextFile(File input, int Flag)
	{ 
		try 
		{					
			if (!input.isFile()) 
				return;

			//the file name format is: docid.txt
			int docid = Integer.parseInt(removeFileExtension(input.getName())); 
		
			BufferedReader reader = new BufferedReader(new FileReader(input));

			String line;
			while ((line = reader.readLine())!= null) 
			{
				String[] buffer = line.trim().toLowerCase().split(" ");
				if (Flag == 1)
				{
				for (int i = 0; i < buffer.length; i += 2)
				{
					String word = buffer[i];
					int position = Integer.parseInt(buffer[i+1]);
					indexList.add(new index_element(word, docid, position));
				}
				}
				else if (Flag == 2)
				{
				for (int i = 0; i < buffer.length; i += 3)
				{
					String word = buffer[i] +' '+ buffer[i+1];
					int position = Integer.parseInt(buffer[i+2]);
					indexList.add(new index_element(word,docid,position));
				}	
				}
			}
			reader.close();
		}
		catch (Exception e)
		{
			System.out.println("File Reading Error");
		}
	}

	
	//compare alphanumeric order
	static class elementComparator implements Comparator<index_element>
	{   
		public int compare(index_element index1, index_element index2)
		{
			int flag = index1.getWord().compareTo(index2.getWord());
			if (flag == 0) 
				flag = index1.getDocumentID() - index2.getDocumentID();
			if (flag == 0) 
				flag = index1.getPosition() - index2.getPosition();
			return flag;
		}
	}
	
	//the indexes are stored in arrayList indexList
	//we now need to write into the disk
	//the format is-- word1 documentID1: position1 position2, documentID2: position1 position2
	private void outputIndex(String index_path) throws IOException
	{  
		StringBuilder builder = new StringBuilder();	
		String pre_word = "";				
		int pre_docid = 0;				
		for (index_element element:indexList) 
		{
			if (!element.getWord().equals(pre_word) || element.getDocumentID()!= pre_docid)
			{
				if(!pre_word.equals(""))
					builder.append(lineEnd);
				builder.append(element.getWord() + " " + element.getDocumentID() + ": ");
			}
			builder.append( element.getPosition() + " ");
	
			pre_docid = element.getDocumentID();
			pre_word = element.getWord();
		}
		builder.append(lineEnd);
		
		//String index_path = indexFolderPath + (++blockNum)+".txt";
		
		File file = new File(index_path);
		if (!file.exists())
		{
            File parent = new File(file.getParent());
            if (!parent.exists())
            {
                parent.mkdirs();
            }
        }
		FileWriter fWriter = new FileWriter(file);
		fWriter.write(builder.toString());
		fWriter.close();
	}
	
	//copy File operator
	private void copyFile(File source, File dest) throws IOException
	{ 
		// TODO: the dest.toPath() should not exist before, take care
		Files.copy(source.toPath(), dest.toPath());							 
	}
	
	private synchronized void mergeTwoFile(File file_helper1, File file_helper2, String fileName, String path, int Switch_Flag) throws IOException
	{
		File file_object = new File(path + fileName);
		FileWriter fWriter = new FileWriter(file_object);
		BufferedReader reader1 = new BufferedReader(new FileReader(file_helper1)); //create a copy of the merged files
		BufferedReader reader2 = new BufferedReader(new FileReader(file_helper2)); // for operation convenience
		StringBuilder builder = new StringBuilder();

		String prev_word = "";
		String line1 = reader1.readLine();
		String line2 = reader2.readLine();
		if (Switch_Flag == 1){
		while (!line1.isEmpty() || !line2.isEmpty())
		{
			int flag;
			
			// TODO : extract this part into a method
			String[] buffer1 = line1.trim().toLowerCase().split("[\\s|:]+"); //\\s means a whitespace, : is the character after document ID
			String[] buffer2 = line2.trim().toLowerCase().split("[\\s|:]+"); //this operation aims to separate the word and its locations
			if (line1.isEmpty()) 	//when line1 is empty, we regard line1.word bigger than line2.word, then we will add line2 and neglect the empty line1
				flag = 1;
			else 
				if (line2.isEmpty()) //similar consideration, remember, when line1 < line2, the flag should be negative values, the smaller one will be added into builder
					flag = -1;
			else 
				flag = buffer1[0].compareTo(buffer2[0]);  // buffer[0] is the word, when combining, frist consider alphanumeric order
			
			if (flag == 0) 
				flag = Integer.parseInt(buffer1[1]) - Integer.parseInt(buffer2[1]); // if same alphanumeric order
			
			String word = flag < 0 ? buffer1[0] : buffer2[0];
			String docidPos = flag < 0 ? line1.substring(buffer1[0].length()) : line2.substring(buffer2[0].length()); 
			String line = flag < 0 ? line1 : line2;

			if (prev_word.equals("")) 
			{				   //the below condition sentence is mainly for maintaining the document format 
				builder.append(line);			   //so that we can continue merge two files
			}
			else
			{
				if (!prev_word.equals(word))
				{
					builder.append(lineEnd);		   
					fWriter.write(builder.toString());
					fWriter.flush();
					builder.setLength(0); 
					builder.append(line);
				}
				else 
				{
					builder.append(",");		//because prev_word is the same as current word, so that we do not need to begin a new line
					builder.append(docidPos);  //instead, we just need a comma to separate the documentID1:position1, documentID2:postion2
				}	
			} 
			prev_word = word;

			if(flag < 0)
			{
				line1 = reader1.readLine();
				line1 = (line1 == null) ? "" : line1;
			}
			else
			{
				line2 = reader2.readLine();
				line2 = (line2 == null) ? "" : line2;
			}
		}
		}
		
		
		else if (Switch_Flag == 2) {
			while (!line1.isEmpty() || !line2.isEmpty())
			{
				int flag;
				
				// TODO : extract this part into a method
				String[] buffer1 = line1.trim().toLowerCase().split(":"); //\\s means a whitespace, : is the character after document ID
				String[] buffer2 = line2.trim().toLowerCase().split(":"); //this operation aims to separate the word and its locations
				String grams1 = "", grams2 = "";
				int Doc1 = 0, Doc2 = 0;
				String[] buffer_helper = buffer1[0].trim().split("[\\s]+");
				if (line1!="") 
				{
				grams1 = buffer_helper[0] + ' ' + buffer_helper[1];
				Doc1 = Integer.parseInt(buffer_helper[2]);
				}
				buffer_helper = buffer2[0].trim().split("[\\s]+");
				if (line2!="")
				{
				grams2 = buffer_helper[0] + ' ' + buffer_helper[1];
				Doc2 = Integer.parseInt(buffer_helper[2]);
				}
				if (line1.isEmpty()) 	//when line1 is empty, we regard line1.word bigger than line2.word, then we will add line2 and neglect the empty line1
					flag = 1;
				else 
					if (line2.isEmpty()) //similar consideration, remember, when line1 < line2, the flag should be negative values, the smaller one will be added into builder
						flag = -1;
				else 
					flag = grams1.compareTo(grams2);  // buffer[0] is the word, when combining, frist consider alphanumeric order
				
				if (flag == 0) 
					flag = Doc1 - Doc2; // if same alphanumeric order
				
				String word = flag < 0 ? grams1 : grams2;
				String docidPos = flag < 0 ? line1.substring(grams1.length()) : line2.substring(grams2.length()); 
				String line = flag < 0 ? line1 : line2;

				if (prev_word.equals("")) 
				{				   //the below condition sentence is mainly for maintaining the document format 
					builder.append(line);			   //so that we can continue merge two files
				}
				else
				{
					if (!prev_word.equals(word))
					{
						builder.append(lineEnd);		   
						fWriter.write(builder.toString()); 				
						fWriter.flush();
						builder.setLength(0); 
						builder.append(line);
					}
					else 
					{
						builder.append(",");		//because prev_word is the same as current word, so that we do not need to begin a new line
						builder.append(docidPos);  //instead, we just need a comma to separate the documentID1:position1, documentID2:postion2
					}	
				} 
				prev_word = word;
				
				if(flag < 0)
				{
					line1 = reader1.readLine();
					line1 = (line1 == null) ? "" : line1;
				}
				else
				{
					line2 = reader2.readLine();
					line2 = (line2 == null) ? "" : line2;
				}
			}	
		}

		fWriter.write(builder.toString());
		fWriter.close();
		
		reader1.close();
		reader2.close();

		file_helper1.delete();
		file_helper2.delete();
	}						

	// this is the main program for merge two index files
	private void index_merge(String path, int switch_flag) throws IOException
	{ 
		
		File file_index = new File(path);
		File file_helper1 = new File(path + "helper1.txt");
		File file_helper2 = new File(path + "helper2.txt");

		file_helper1.delete();
		file_helper2.delete();

		File[] list = file_index.listFiles(); //remember to delete the used file, helper file, otherwise it will affect 
		while (list.length>1)
		{						// the number of documents in a folder, which will make the 							
			System.out.println("Merge " + list.length + " files");
			for (int i = 0; i < list.length; i=i+2)			//merge process into trouble, because we do not know when we should stop											
			{
				File file1 = list[i];
				copyFile(file1, file_helper1);
				file1.delete();
				if (i+1<list.length) 
				{
					File file2 = list[i+1];
					String fileName = file2.getName();
					copyFile(file2, file_helper2);
					file2.delete();
					mergeTwoFile(file_helper1, file_helper2, fileName, path, switch_flag);	
				}
				else 
				{
					File file_helper = new File(path + file1.getName());
					copyFile(file_helper1, file_helper);
					file_helper1.delete();
				}
			}
			file_helper1.delete();
			file_helper2.delete();

			list = file_index.listFiles();
		}

		System.out.println("index merge finished");
	}
	
	private class indexComparator implements Comparator<String>
	{
		public int compare(String index_document_position1, String index_document_position2 )
		{
			String[] buffer_helper1 = index_document_position1.trim().split("[:|\\s]+");
			String[] buffer_helper2 = index_document_position2.trim().split("[:|\\s]+");
			return (Integer.parseInt(buffer_helper1[0]) - Integer.parseInt(buffer_helper2[0]));
		}
	}
	private class gramsComparator implements Comparator<String>
	{
		public int compare(String index_document_position1, String index_document_position2)
		{
			String[] buffer_helper1 = index_document_position1.trim().split("[:|\\s]+");
			String[] buffer_helper2 = index_document_position2.trim().split("[:|\\s]+");
			return (Integer.parseInt(buffer_helper1[0]) - Integer.parseInt(buffer_helper2[0]));
		}
	}
	private void output_tfidf2(File file_input, File file_output, File file_output2, int switch_flag) throws IOException
	{	
		BufferedWriter fWriter = new BufferedWriter(new FileWriter(file_output));
		BufferedWriter fWriter2 = new BufferedWriter(new FileWriter(file_output2));
		
		
		int i = 0;

		BufferedReader reader = new BufferedReader(new FileReader(file_input));
		String line; 
		while ((line = reader.readLine())!=null) 
		{
			unique_words++;

			StringBuilder builder = new StringBuilder();
			StringBuilder builder_tfidf = new StringBuilder();		
			ArrayList<String> buffer_array = new ArrayList<String>();
			String[] buffer = line.trim().split(",");
			String[] buffer_helper = buffer[0].split("[\\s|:]+");
			String word;
			if (switch_flag == 1)
				word = buffer_helper[0];
			else word = buffer_helper[0] + ' ' + buffer_helper[1];
			builder.append(word);
			buffer[0] = buffer[0].substring(word.length());
			int dF = buffer.length;
			for (int j = 0; j < dF; j++)
			{
				buffer_array.add(buffer[j]);	
			}
			Collections.sort(buffer_array, new indexComparator());
			builder_tfidf.append(word + " ");
			
			for (int j=0; j < dF;j++)
			{  
				//calculate the tf-idf, buffer will chop off each document with word's positions
				builder.append(buffer_array.get(j));   // buffer_helper will deal with a specific doumentID with word's positions
				buffer_helper = buffer_array.get(j).trim().split("[\\s|:]+");
				int tF = buffer_helper.length - 1;
				double TF_IDF = Math.log10(1+tF) * Math.log10(unique_Weburl/dF);
				builder_tfidf.append(buffer_helper[0] + ":" + String.format("%.4f",TF_IDF));
				
				if (j < dF - 1) 
				{
					builder.append(", ");
					builder_tfidf.append(", ");
				}
				else 
				{
					builder.append(lineEnd);
					builder_tfidf.append(lineEnd);
				}
			}
		
			fWriter.write(builder.toString());
			fWriter2.write(builder_tfidf.toString());
		}
		reader.close();
		fWriter.close();
		fWriter2.close();
	}
	
	//compare alphanumeric order
	private class urlComparator implements Comparator<String>
	{   
		public int compare(String URL1, String URL2)
		{
			String[] helper1 = URL1.split("\\s");
			String[] helper2 = URL2.split("\\s");
			return Integer.parseInt(helper1[1]) - Integer.parseInt(helper2[1]);
		}
	}
	public void buildGrams() throws IOException {
		File file = new File(token_path);
		
		File[] list = file.listFiles();
		String line;
		String lastword = "";
		String lastPosition = "";
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < list.length; i++){
		BufferedReader reader = new BufferedReader(new FileReader(list[i]));
		File file_output = new File(gram_path+list[i].getName());
		if (!file_output.exists()){
            File parent = new File(file_output.getParent());
            if (!parent.exists()){
                parent.mkdirs();
            }
        }
		BufferedWriter writer = new BufferedWriter(new FileWriter(file_output));
		while ((line = reader.readLine())!=null){
			String[] buffer = line.trim().toLowerCase().split(" ");
			
			for (int j = 0; j < buffer.length; j+=2)
			{
				if ( i!=0 && j == 0) 
					builder.append(lastword+' '+buffer[j]+ ' '+ lastPosition+' ');
				if (j+2 < buffer.length) builder.append(buffer[j]+' ' + buffer[j+2] + ' ' + buffer[j+1]+ ' ');
				
			}
			builder.append(lineEnd);
			lastword = buffer[buffer.length-2];
			lastPosition = buffer[buffer.length-1];
		}
		writer.write(builder.toString());
		builder.delete(0, builder.length());
		writer.close();
		reader.close();
		}
		//////
		file = new File(gram_path);
		list = file.listFiles();
		blockNum = 0;
		for (int i = 0; i < list.length; i++){
			readTextFile(list[i],2);
			if (((i%100==0 && i!=0)|| i == list.length-1) && !indexList.isEmpty()){
				Collections.sort(indexList, new elementComparator());
				String grams_path = gramsFolderPath + (++blockNum)+".txt";
				outputIndex(grams_path);
				indexList.clear();
			}
		}
		index_merge(gramsFolderPath,2);
		File file_grams = new File(gramsFolderPath).listFiles()[0];
		File file_grams_tfidf = new File(grams_tfidf_filePath);
		File file_grams_sorted = new File(grams_sorted_filePath);
		if (!file_grams_sorted.exists())
		{
	        File parent = new File(file_grams_sorted.getParent());
	        if (!parent.exists())
	        {
	            parent.mkdirs();
	        }
		}
		output_tfidf2(file_grams, file_grams_sorted, file_grams_tfidf,2);
	}

	public void buildIndex() throws IOException 
	{	
		File file = new File(token_path);
		File[] list = file.listFiles();
		unique_Weburl = list.length;
		blockNum = 0;
		for (int i = 0; i < list.length; i ++)
		{
			readTextFile(list[i],1);
			if (((i%100 == 0 && i!=0) || i == list.length-1) && !indexList.isEmpty()) {
				Collections.sort(indexList, new elementComparator());
				String index_path = indexFolderPath + (++blockNum)+".txt";
				outputIndex(index_path);
				indexList.clear();
			}
		}
		

		index_merge(indexFolderPath,1);

		File file_index = new File(indexFolderPath).listFiles()[0];
		File file_index_tfidf = new File(index_tfidf_filePath);
		File file_index_sorted = new File(index_sorted_filePath);
		if (!file_index_sorted.exists())
		{
	        File parent = new File(file_index_sorted.getParent());
	        if (!parent.exists())
	        {
	            parent.mkdirs();
	        }
		}
		output_tfidf2(file_index, file_index_sorted, file_index_tfidf,1);
		System.out.println("the unique_Weburl number is " + unique_Weburl);
		System.out.println("the unique words number is " + unique_words);
	}
}
