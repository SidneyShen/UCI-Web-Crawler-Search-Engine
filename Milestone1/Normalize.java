package milestone1;

import java.util.*;
import java.io.*;
import java.lang.Math;
import java.lang.Double;
import java.lang.Integer;


public class Normalize{

	private static Normalize instance = null;
	private String orginalPath = "E:/crawlDataProcessed/index_weight/index_tfidf.txt";
	private String newPath = "E:/crawlDataProcessed/index_weight/index_tfidf_normalized.txt";
	private String lineEnd = System.getProperty("line.separator");
	private HashMap<Integer, Double> tfidfSum = new HashMap<Integer, Double>();

	public static void main(String[] args) throws IOException {
		Normalize.Instance().calculateSum();
		Normalize.Instance().writeFile();
	}

	public static Normalize Instance(){
		if(instance == null)
			instance = new Normalize();
		return instance;
	}

	public void calculateSum(){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(orginalPath));
			String line;
			while ((line = reader.readLine())!= null) 
			{
				String term = line.substring(0, line.indexOf(' '));
				line = line.substring(line.indexOf(' ') + 1);
				for(String doc_score : line.split(","))
				{
					doc_score = doc_score.trim();
					Integer docid = Integer.valueOf(doc_score.substring(0, doc_score.indexOf(':')));
					
					doc_score = doc_score.substring(doc_score.indexOf(':')+1);
					doc_score = doc_score.trim();
					Double tfidf = Double.parseDouble(doc_score);
					
					// Store the sum
					if(tfidfSum.containsKey(docid)){
						Double sum = Double.valueOf(tfidfSum.get(docid).doubleValue()+Math.pow(tfidf.doubleValue(),2));
						tfidfSum.put(docid,sum);
					}
					else
						tfidfSum.put(docid,Double.valueOf(Math.pow(tfidf.doubleValue(),2)));
				}
			}
			reader.close();
		}
		catch(Exception ex)
		{	
			System.out.println(ex);
			// reader.close();
		}
		squareRoot();
	}

	public void squareRoot(){
		Iterator<Map.Entry<Integer,Double>> iter = tfidfSum.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<Integer,Double> next = iter.next();
			next.setValue(Double.valueOf(Math.sqrt(next.getValue().doubleValue())));
		}
	}

	public void writeFile() throws IOException {

		//Initial output file
		File file_output = new File(newPath);
		if (!file_output.exists()){
            File parent = new File(file_output.getParent());
            if (!parent.exists()){
                parent.mkdirs();
            }
        }

        StringBuilder str = new StringBuilder();

        // Read original file and write to output
		try{
			BufferedReader reader = new BufferedReader(new FileReader(orginalPath));
			String line;
			while ((line = reader.readLine())!= null){
				// Term
				String term = line.substring(0, line.indexOf(' '));
				str.append(term+" ");

				//DocID and Normalized tfidf
				line = line.substring(line.indexOf(' ') + 1);
				for(String doc_score : line.split(",")){
					//DocID
					doc_score = doc_score.trim();
					Integer docid = Integer.valueOf(doc_score.substring(0, doc_score.indexOf(':')));
					// Normalized tfidf
					doc_score = doc_score.substring(doc_score.indexOf(':')+1).trim();
					Double tfidf = Double.parseDouble(doc_score);
					Double sum = tfidfSum.get(docid);
					Double new_tfidf = tfidf.doubleValue()/sum.doubleValue();

					str.append(docid.toString()+":"+new_tfidf.toString()+",");
				}
				str.deleteCharAt(str.length()-1);
				str.append(lineEnd);
			}
			reader.close();
		}
		catch(Exception ex)
		{	
			System.out.println(ex);
			// reader.close();
		}

		FileWriter fWriter = new FileWriter(file_output);
		fWriter.write(str.toString());
		fWriter.close();
	}
}