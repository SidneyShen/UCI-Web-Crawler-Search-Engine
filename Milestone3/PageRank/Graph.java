import java.io.*;
import java.util.*;

public class Graph{

	private static Graph instance = null;

	private String rootPath = "/home/sidney/workspace/WebCrawler/crawlData/";
	private String linkPath = rootPath + "links.txt";

	
	private static HashMap<Integer, ArrayList<Integer>> outbound = new HashMap<Integer, ArrayList<Integer>>();
	private static HashMap<Integer, ArrayList<Integer>> inbound = new HashMap<Integer, ArrayList<Integer>>();

	public static Graph Instance(){
		if(instance == null)
			instance = new Graph();
		return instance;
	}

	public static void main(String[] args) {
		Graph.Instance().initialGraph();
		Graph.Instance().printGraph();
	}

	public void initialGraph(){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(linkPath));
			String line;
			int rowNumCount = 0;
			Integer sourceID = null;
			Integer targetID = null;

			while ((line = reader.readLine())!= null) 
			{
				rowNumCount++;

				if(rowNumCount==3){
					if(!sourceID.equals(targetID))
						addToMap(sourceID, targetID);
					sourceID = null;
					targetID = null;
					rowNumCount = 0;
					continue;
				}
				else if(rowNumCount==1){
					sourceID = Integer.valueOf(line.substring(0, line.indexOf(' ')));
					continue;
				}
				else{
					targetID = Integer.valueOf(line.substring(0, line.indexOf(' ')));
					continue;
				}
			}
			reader.close();
		}
		catch(Exception ex){	
			System.out.println(ex);
		}
	}

	private void addToMap(Integer source, Integer target){

		// if(source.equals(1))
		// 	System.out.println("1's target: "+target);
		// if(target.equals(1))
		// 	System.out.println("1's source: "+source);

		// Add to outbound map
		if(outbound.containsKey(source)){
			ArrayList<Integer> list = outbound.get(source);
			if(list==null)
				list = new ArrayList<Integer>();
			list.add(target);
			if(source.equals(1))
				System.out.println("1's target: "+target);
			outbound.put(source, list);
		}
		else{
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(target);
			if(source.equals(1))
				System.out.println("1's target: "+target);
			outbound.put(source, list);
		}

		// Add to inbound map
		if(inbound.containsKey(target)){
			ArrayList<Integer> list = inbound.get(target);
			if(list==null)
				list = new ArrayList<Integer>();
			list.add(source);
			inbound.put(target, list);
		}
		else{
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(source);
			inbound.put(target, list);
		}
	}

	public HashMap<Integer, ArrayList<Integer>> getOutBoundMap(){
		return outbound;
	}

	public HashMap<Integer, ArrayList<Integer>> getInBoundMap(){
		return inbound;
	}

	public static void printGraph(){
		System.out.println("-------------Outbound---------------");

		System.out.print("1: ");
		Integer ID = 1;
		ArrayList<Integer> outlist = outbound.get(ID);
		for(Integer target : outlist){
			System.out.print(target+" ");
		}
		System.out.println();

		System.out.println("-------------Inbound---------------");

		System.out.print("1: ");
		ArrayList<Integer> inlist = inbound.get(ID);
		for(Integer source : inlist){
			System.out.print(source+" ");
		}
		System.out.println();

		// for(Map.Entry<Integer, ArrayList<Integer>> entry: outbound.entrySet()){

		// 	Integer source = entry.getKey();
		// 	System.out.println("Source: "+ source);

		// 	ArrayList<Integer> list = entry.getValue();
		// 	System.out.print("Target: ");
		// 	for(Integer target: list){
		// 		System.out.print(target+" ");
		// 	}
		// 	System.out.println();
		// }
	}

}