package milestone1;


public class index_weight {
private String word;
private double weight;
index_weight() {
	this.word = "";
	this.weight = 0;
}
index_weight(String word, double weight){
	this.word = word;
	this.weight = weight;
}
String getWord() {
	return this.word;
}
double getWeight() {
	return this.weight;
}
}
