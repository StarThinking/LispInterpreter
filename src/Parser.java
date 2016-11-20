import java.util.ArrayList;
import java.util.List;

public class Parser {
	
	private static List<Node> rootList = new ArrayList<Node>(); 
	private static int currentRoot = 0;
	private static boolean nodeFound;
	
	public static List<Node> getRootList() {
		return rootList;
	}

	public static void ParseStart() throws ListException {
		Node root = new Node();
		ParseSexp(root);
		rootList.add(root);
		if(!Analyzer.isEnd())
			ParseStart();
	}
	
	private static void ParseSexp(Node node) throws ListException{
		Token token1 = Analyzer.getNextToken();
		if(token1 == null)
			throw(new ListException("More tokens are expected!"));
		if(token1.getType() != TokenType.OpenParenthesis && token1.getType() != TokenType.Atom) {
			throw(new ListException("Not Atom or OpenParenthesis!"));
		}
		if(token1.getType() == TokenType.Atom) {
			node.atom = token1;
			return;
		}
		if(token1.getType() == TokenType.OpenParenthesis){
			node.leftChild = new Node();
			ParseSexp(node.leftChild);
		}
		Token token2 = Analyzer.getNextToken();
		if(token2.getType() != TokenType.Dot) 
			throw(new ListException("Dot is expected!"));
		node.rightChild = new Node();
		ParseSexp(node.rightChild);
		Token token3 = Analyzer.getNextToken();
		if(token3.getType() != TokenType.ClosingParenthesis) 
			throw(new ListException("ClosingParenthesis is expected!"));
	}
	
	public static void checkRootIsList(Node root) {
		if(root.leftChild == null && root.rightChild == null) {
			if(root.atom.getContent().equals("NIL")) 
				root.isList = true;
			else 
				root.isList = false;
			return;
		}
		travelForInnerNode(root);
		nodeFound = false;
		travelForRoot(root);
		if(!nodeFound)
			root.isList = true;
	}

	private static void checkInnerNode(Node node) {
		  if (node == null) 
			  return;   
		  if(node.atom != null) {
			  return;
		  }
		  else {
			  // check if the inner node is list
			  Node tmp = node;
			  while(tmp.rightChild != null) {
				  if(tmp.rightChild.atom != null) {
					  String atomStr = tmp.rightChild.atom.getContent();
					  if(atomStr.equals("NIL")) 
						  node.isList = true;
					  else 
						  node.isList = false;
						  break;		 
				  }
				  else 
					  tmp = tmp.rightChild;
			  }	
		  }
	}
	
	private static void travelForInnerNode(Node node) {
		 if (node == null) {  
	            return;  
	     } else {  
	    	 checkInnerNode(node);  
	    	 travelForInnerNode(node.leftChild);  
	    	 travelForInnerNode(node.rightChild);  
	     }  
	}
	
	private static void checkRootNode(Node node) {
		  if (node == null) 
			  return;   
		  if(node.atom == null) {
			  if(!node.isList) {
				  rootList.get(currentRoot).isList = false;
				  nodeFound = true;
			  }
		  }   
	}
	
	private static void travelForRoot(Node node) {
		 if (node == null) {  
	            return;  
	     } else {  
	    	 checkRootNode(node);  
	    	 travelForRoot(node.leftChild);  
	    	 travelForRoot(node.rightChild);  
	     }  
	}
	
}
