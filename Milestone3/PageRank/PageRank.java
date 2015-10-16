import java.io.*;
import java.util.*;


public class PageRank{

	private static ArrayList<Double> PR = null;
	private static final int uniqueWebNum = 124246;
	private static final double dampFactor = 0.85;
	private static String lineEnd = System.getProperty("line.separator");
	private static String rootPath = "/home/sidney/workspace/WebCrawler/crawlData/";
	private static String prPath = rootPath + "pagerank.txt";

	public PageRank(){

	}

	public static void initial(){
		Graph.Instance().initialGraph();
	}

	public static void calculate(){
		Graph.printGraph();
		HashMap<Integer, ArrayList<Integer>> out = Graph.Instance().getOutBoundMap();
		HashMap<Integer, ArrayList<Integer>> in = Graph.Instance().getInBoundMap();


		PR = new ArrayList<Double>(uniqueWebNum);
		ArrayList<Double> PR_new = new ArrayList<Double>(uniqueWebNum);

		for(int i=0; i<uniqueWebNum; i++){
			PR.add(1.0);
			// PR_new.add(1.0);
		}

		// double diff;
		int round=1000;

		for(int k=0; k<round; k++){
			for(int i=0; i<uniqueWebNum; i++){
				Integer targetID = i+1;
				Double newPR = 0.0;
				if(in.containsKey(targetID)){
					ArrayList<Integer> list = in.get(targetID);
					if(list==null || list.size()==0)
						continue;
					for(int j=0; j<list.size(); j++){
						Integer sourceID = list.get(j);

						if(out.containsKey(sourceID)){
							ArrayList<Integer> outBoundList = out.get(sourceID);
							if(outBoundList!=null && outBoundList.size()>0){
								newPR += dampFactor * PR.get(sourceID-1)/outBoundList.size();
							}
						}
					}
					newPR += (1-dampFactor);
				}
				else{
					newPR = (1-dampFactor);
				}
				PR.set(i, newPR);
			}
		}


		// do{
		// 	round++;
		// 	for(int i=0; i<uniqueWebNum; i++){
		// 		Integer targetID = i+1;
		// 		Double newPR = 1.0;	// Not sure!!!!!!

		// 		if(in.containsKey(targetID)){
		// 			ArrayList<Integer> list = in.get(targetID);
		// 			if(list==null || list.size()==0)
		// 				continue;
		// 			for(int j=0; j<list.size(); j++){
		// 				Integer sourceID = list.get(j);

		// 				if(out.containsKey(sourceID)){
		// 					ArrayList<Integer> outBoundList = out.get(sourceID);
		// 					if(outBoundList!=null && outBoundList.size()>0){
		// 						newPR += dampFactor * PR.get(sourceID-1)/outBoundList.size();
		// 					}
		// 				}
		// 			}
		// 			newPR += (1-dampFactor);
		// 		}

		// 		PR_new.set(i, newPR);
		// 	}

		// 	diff = calculateDiff(PR, PR_new);

		// 	for(int i=0; i<uniqueWebNum; i++){
		// 		PR.set(i, PR_new.get(i));
		// 		PR_new.set(i, 1.0);
		// 	}

		// }while(diff>0.001);

	}

	// private static double calculateDiff(ArrayList<Double> l1, ArrayList<Double> l2){
	//     double diff = 0;
    
	//     for(int i = 0; i < l1.size(); i++) {
	//       diff += Math.abs(l1.get(i) - l2.get(i));
	//     }//end: for(x)
	    
	//     return diff;
	// }

	private static void writeToDisk() throws IOException{
		//Initial output file
		File file_output = new File(prPath);
		if (!file_output.exists()){
            File parent = new File(file_output.getParent());
            if (!parent.exists()){
                parent.mkdirs();
            }
        }

        StringBuilder str = new StringBuilder();
        for(int i=0; i<uniqueWebNum; i++){
        	str.append((i+1)+" "+PR.get(i)+lineEnd);
        }
        try{
			FileWriter fWriter = new FileWriter(file_output);
			fWriter.write(str.toString());
			fWriter.close();       	
        }catch(IOException e){
        	e.printStackTrace();
        }
	}

	public static void main(String[] args) throws Exception {
		PageRank.initial();
		PageRank.calculate();
		PageRank.writeToDisk();
	}

}	