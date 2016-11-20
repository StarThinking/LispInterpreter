import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Eval {
	
	private static String[] internalFuncList = {"CAR", "CDR", "CONS", "ATOM", "EQ", 
			"NULL", "INT", "PLUS", "MINUS", "TIMES", "QUOTIENT", "REMAINDER", "LESS", 
			"GREATER", "COND", "QUOTE", "DEFUN"};
	private static Map<String, Node> dlist;
	static {
		dlist = new HashMap<String, Node>();
	}
	
	private static Node add2DList(Node node) throws ListException {
		String funcName = car(node).atom.getContent();
		
		// check if list size if correct
		/*if(!nul(node.rightChild.rightChild.rightChild))
			throw(new ListException("Function is wrongly defined!"));*/
		
		// check if funcName is valid
		for(String f : internalFuncList) {
			if(funcName.equals(f))
				throw(new ListException("Function name " + funcName + " is reserved as internal functions!"));
		}
		
		// check if E1 is a list
		Parser.checkRootIsList(car(cdr(node)));
		if(!car(cdr(node)).isList)
			throw(new ListException("Expression E1 must be a list!"));
		
		// check if E1 includes T or NIL
		Node arg = car(cdr(node));
		while(arg.rightChild != null) {
			if(nul(car(arg)) || isT(car(arg)) || isInt(car(arg)))
				throw(new ListException("Expression E1 cannot contain T or NIL or INT!"));
			arg = arg.rightChild;
		}
		
		dlist.put(funcName, cdr(node));
		System.out.println(funcName);
		return null;
	}

	/*private static void printMap(Map<String, Node> alist) {
		if(alist == null)
			return;
		for (String k : alist.keySet()) {  
			System.out.println("key  = " + k + ", value = " + alist.get(k).atom.getContent());;
		} 
	}*/
	
	private static boolean bound(String key, Map<String, Node> alist) {
		for (String k : alist.keySet()) {  
			if(k.equals(key))
				return true;
		} 
		return false;
	}
	
	private static Node getval(String key, Map<String, Node> alist) {
		return alist.get(key);
	}
	
	private static Node evcon(Node node, Map<String, Node> alist, Map<String, Node> dlist) throws ListException {
		if(!nul(cdr(cdr(car(node)))))
			throw(new ListException("COND with wrong args!"));
		if(nul(node))
			throw(new ListException("evcon fails!"));
		if(isT(eval(car(car(node)), alist, dlist)))
			return eval(car(cdr(car(node))), alist, dlist);
		else
			return evcon(cdr(node), alist, dlist);
	}
	
	private static Node evlist(Node node, Map<String, Node> alist, Map<String, Node> dlist) throws ListException {
		//System.out.println("evlist");
		if(nul(node)) {
			return node;
		}
		else {
			return cons(eval(car(node), alist, dlist), evlist(cdr(node), alist, dlist));
		}
	}
	
	public static void evals(List<Node> rootList) throws ListException {
		for(Node root : rootList) {
			Map<String, Node> alist = new HashMap<String, Node>();
			Node result = eval(root, alist, Eval.dlist);
			if(result != null) {
				Printer.print(result);
				System.out.println("");
			}
		}
	}
	
	private static Node eval(Node root, Map<String, Node> alist, Map<String, Node> dlist) throws ListException {
		if(root.leftChild == null && root.rightChild == null) { // Exp is Atom
			if(root.atom.getContent().equals("T")) {
				return root;
			} else if(root.atom.getContent().equals("NIL")) {
				return root;
			} else if(isInt(root)) {
				return root;
			} else if(bound(root.atom.getContent(), alist)) {
				return getval(root.atom.getContent(), alist);
			} else {
				throw(new ListException("No such atom!"));
			}
		} else { // Exp is List
			Node funcNameNode = car(root);	
			if(funcNameNode.atom.getContent().equals("QUOTE")) {
				if(!nul(cdr(cdr(root))))
					throw(new ListException("QUOTE with wrong args!"));
				return car(cdr(root));
			}	
			else if(funcNameNode.atom.getContent().equals("COND")) {
				//special = true;
				return evcon(cdr(root), alist, dlist);
			}
			else if (funcNameNode.atom.getContent().equals("DEFUN")) {
				return add2DList(cdr(root));
			} else {
				Node argNode = evlist(cdr(root), alist, dlist);
				Parser.checkRootIsList(argNode); // Check if the argNode is list
				if(!argNode.isList)
					throw(new ListException("Not a list!"));
				return apply(funcNameNode, argNode, alist, dlist);
			}
		}
	}

	private static Node apply(Node funcNameNode, Node paramNode, Map<String, Node> alist, Map<String, Node> dlist) throws ListException {
		String funcName = funcNameNode.atom.getContent();
		//System.out.println("apply " + funcName);
	
		switch(funcName) {

			case "NULL":
				Node nullNode1 = paramNode.leftChild;
				Node nullNode2 = paramNode.rightChild;
				if(!nul(nullNode2))
					throw(new ListException("NULL with wrong args!"));
				if(nul(nullNode1) && nul(nullNode2)) {
					Node n = new Node();
					Token t = new Token(TokenType.Atom, "T");
					n.atom = t;
					return n;
				} else {
					Node n = new Node();
					Token t = new Token(TokenType.Atom, "NIL");
					n.atom = t;
					return n;
				}
				
			case "ATOM":
				Node atomNode1 = car(paramNode);
				Node atomNode2 = cdr(paramNode);
				if(!nul(atomNode2))
					throw(new ListException("ATOM with wrong args!"));
				else {
					Node n = new Node();
					if(atomNode1.atom != null) {
						Token t = new Token(TokenType.Atom, "T");
						n.atom = t;
					} else {
						Token t = new Token(TokenType.Atom, "NIL");
						n.atom = t;
					}
					return n;
				}
			
				
			case "INT":
				Node intNode1 = car(paramNode);
				Node intNode2 = cdr(paramNode);
				if(!nul(intNode2))
					throw(new ListException("INT with wrong args!"));
				else {
					Node n = new Node();
					if(isInt(intNode1)) {
						Token t = new Token(TokenType.Atom, "T");
						n.atom = t;
					} else {
						Token t = new Token(TokenType.Atom, "NIL");
						n.atom = t;
					}
					return n;
				}
					
			case "CAR":
				return car(car(paramNode));
				
			case "CDR":
				return cdr(car(paramNode));
						
			case "CONS":
				if(!nul(cdr(cdr(paramNode))))
					throw(new ListException("CONS with wrong args!"));
				return cons(car(paramNode), car(cdr(paramNode)));	
				
			case "PLUS":
				Node plusNode1 = car(paramNode);
				Node plusNode2 = car(cdr(paramNode));
				Node plusNode3 = cdr(cdr(paramNode));
				if(!nul(plusNode3) || !isInt(plusNode1) || !isInt(plusNode2)) {
					throw(new ListException("PLUS with wrong args!"));
				} else {
					int res = Integer.parseInt(plusNode1.atom.getContent()) + Integer.parseInt(plusNode2.atom.getContent());
					Node n = new Node();
					Token t = new Token(TokenType.Atom, String.valueOf(res));
					n.atom = t;
					return n;
				}
				
			case "MINUS":
				Node minusNode1 = car(paramNode);
				Node minusNode2 = car(cdr(paramNode));
				Node minusNode3 = cdr(cdr(paramNode));
				if(!nul(minusNode3) || !isInt(minusNode1) || !isInt(minusNode2)) {
					throw(new ListException("MINUS with wrong args!"));
				} else {
					int res = Integer.parseInt(minusNode1.atom.getContent()) - Integer.parseInt(minusNode2.atom.getContent());
					Node n = new Node();
					Token t = new Token(TokenType.Atom, String.valueOf(res));
					n.atom = t;
					return n;
				}
				
			case "TIMES":
				Node timesNode1 = car(paramNode);
				Node timesNode2 = car(cdr(paramNode));
				Node timesNode3 = cdr(cdr(paramNode));
				if(!nul(timesNode3) || !isInt(timesNode1) || !isInt(timesNode2)) {
					throw(new ListException("TIMES with wrong args!"));
				} else {
					int res = Integer.parseInt(timesNode1.atom.getContent()) * Integer.parseInt(timesNode2.atom.getContent());
					Node n = new Node();
					Token t = new Token(TokenType.Atom, String.valueOf(res));
					n.atom = t;
					return n;
				}
				
			case "QUOTIENT":
				Node quotientNode1 = car(paramNode);
				Node quotientNode2 = car(cdr(paramNode));
				Node quotientNode3 = cdr(cdr(paramNode));
				if(!nul(quotientNode3) || !isInt(quotientNode1) || !isInt(quotientNode2)) {
					throw(new ListException("QUOTIENT with wrong args!"));
				} else {
					int res = Integer.parseInt(quotientNode1.atom.getContent()) / Integer.parseInt(quotientNode2.atom.getContent());
					Node n = new Node();
					Token t = new Token(TokenType.Atom, String.valueOf(res));
					n.atom = t;
					return n;
				}
				
			case "REMAINDER":
				Node remainderNode1 = car(paramNode);
				Node remainderNode2 = car(cdr(paramNode));
				Node remainderNode3 = cdr(cdr(paramNode));
				if(!nul(remainderNode3) || !isInt(remainderNode1) || !isInt(remainderNode2)) {
					throw(new ListException("REMAINDER with wrong args!"));
				} else {
					int res = Integer.parseInt(remainderNode1.atom.getContent()) % Integer.parseInt(remainderNode2.atom.getContent());
					Node n = new Node();
					Token t = new Token(TokenType.Atom, String.valueOf(res));
					n.atom = t;
					return n;
				}
				
			case "LESS":
				Node lessNode1 = car(paramNode);
				Node lessNode2 = car(cdr(paramNode));
				Node lessNode3 = cdr(cdr(paramNode));
				if(!nul(lessNode3) || !isInt(lessNode1) || !isInt(lessNode2)) {
					throw(new ListException("LESS with wrong args!"));
				} else {
					Node n = new Node();
					int res = Integer.parseInt(lessNode1.atom.getContent()) - Integer.parseInt(lessNode2.atom.getContent());
					if(res < 0) {
						Token t = new Token(TokenType.Atom, "T");
						n.atom = t;
					} else {
						Token t = new Token(TokenType.Atom, "NIL");
						n.atom = t;
					}
					return n;
				}
				
			case "GREATER":
				Node greaterNode1 = car(paramNode);
				Node greaterNode2 = car(cdr(paramNode));
				Node greaterNode3 = cdr(cdr(paramNode));
				if(!nul(greaterNode3) || !isInt(greaterNode1) || !isInt(greaterNode2)) {
					throw(new ListException("LESS with wrong args!"));
				} else {
					Node n = new Node();
					int res = Integer.parseInt(greaterNode1.atom.getContent()) - Integer.parseInt(greaterNode2.atom.getContent());
					if(res > 0) {
						Token t = new Token(TokenType.Atom, "T");
						n.atom = t;
					} else {
						Token t = new Token(TokenType.Atom, "NIL");
						n.atom = t;
					}
					return n;
				}
				
			case "EQ":
				Node eqNode1 = car(paramNode);
				Node eqNode2 = car(cdr(paramNode));
				Node eqNode3 = cdr(cdr(paramNode));
				if(!nul(eqNode3)) {
					throw(new ListException("EQ with wrong args!"));
				} else {
					Node n = new Node();
					if(eqNode1.atom.getContent().equals(eqNode2.atom.getContent())) {		
						Token t = new Token(TokenType.Atom, "T");
						n.atom = t;
						
					} else {
						Token t = new Token(TokenType.Atom, "NIL");
						n.atom = t;
					}	
					return n;
				}
				
			default:
				Node func; // E1 and E2
				if(bound(funcName, dlist)) {
				    func = getval(funcName, dlist);
				} else {
					throw(new ListException("Undefined function " + funcName + "!"));
				}
				Map<String, Node> updated = addpairs(car(func), paramNode, alist);
				return eval(car(cdr(func)), updated, dlist);
		}
	}
	
	private static Map<String, Node> addpairs(Node formals, Node actuals, Map<String, Node> alist) throws ListException {
		// check if the number of actuals is equivalent to that of formals
		int formalNum = 0, actualNum = 0;
		Node index = null;
		index = formals;	
		while(!nul(index)) {
			formalNum ++;
			index = cdr(index);
		}
		index = actuals;
		while(!nul(index)) {
			actualNum ++;
			index = cdr(index);
		}
		if(formalNum != actualNum)
			throw(new ListException("Different numbers of formals and actuals!"));
		
		// make a copy of alist
		Map<String, Node> newalist = new HashMap<String, Node>();
		newalist.putAll(alist);
	
		while(!nul(formals)) {
			Node formal = car(formals);
			Node actual = car(actuals);
			newalist.put(formal.atom.getContent(), actual);
			formals = cdr(formals);				
			actuals = cdr(actuals);
		}		
		return newalist;
	}
	
	private static boolean isT(Node node) {
		if(node.atom == null)
			return false;
		else {
			if(node.atom.getContent().equals("T"))
				return true;
			else 
				return false;
		}
	}
	
	private static Node car(Node node) throws ListException {
		if(node.leftChild == null) 
			throw(new ListException("car fails!"));
		return node.leftChild;
	}
	
	private static Node cdr(Node node) throws ListException {
		if(node.rightChild == null) 
			throw(new ListException("cdr fails!"));
		return node.rightChild;
	}
	
	private static Node cons(Node l, Node r) {
		Node n = new Node();
		n.leftChild = l;
		n.rightChild = r;
		return n;
	}
	
	private static boolean nul(Node node) {
		Token token = node.atom;
		if(token == null) {
			//System.out.println("token == null");
			return false;
		}
		else {
			if(token.getContent().equals("NIL"))
				return true;
			else
				return false;
		}
	}
	
	private static boolean isInt(Node node) throws ListException {
		if(node.atom == null)
			return false;
			//throw(new ListException("Not an atom!"));
		 String value = node.atom.getContent();
		 try {
			 Integer.parseInt(value);
			 return true;
		 } catch (NumberFormatException e) {
			 return false;
		 }
	}

}
