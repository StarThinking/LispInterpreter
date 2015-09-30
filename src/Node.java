

public class Node {
	  boolean isList;  
	  Token atom;
	  Node leftChild;
	  Node rightChild; 
	  
	  public Node(){
		  this.isList = false;
		  atom = null;
		  leftChild = null;
		  rightChild = null;
	  }
}
