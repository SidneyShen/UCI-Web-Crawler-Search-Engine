package milestone1;


public class index_element {
private String word;
private int documentID;
private int position;
	index_element() {
	word = "";
	documentID = 0;
	position = 0;
	}
	index_element(String word, int documentID, int position) {
		this.word = word;
		this.documentID = documentID;
		this.position = position;
	}
public void set(String word, int documentID, int position)
{
	this.word = word;
	this.documentID = documentID;
	this.position = position;
}
public String getWord(){
	return this.word;
}
public int getDocumentID(){
	return this.documentID;
}
public int getPosition(){
	return this.position;
}
}
