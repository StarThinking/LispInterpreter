
public class Printer {
	public static void prints() {	
		for(Node root : Parser.getRootList()) {
			print(root);
			System.out.println("");
		}
	}
	
	public static void print(Node root) {	
		Parser.checkRootIsList(root); // Check root is list or not before print
		if(root.isList) 
			printList(root);
		else
			printNode(root);
	}
	
	public static void printList(Node node) {
		if(node == null)
			return;
		if(node.atom != null) {
			System.out.print("");
			System.out.print(node.atom.getContent());
	}
		else {
			System.out.print("(");
			printList(node.leftChild);
			while(node.rightChild != null && node.rightChild.atom == null) {
				node = node.rightChild;
				System.out.print(" ");
				printList(node.leftChild);
			}
			System.out.print(")");
		}
	}
	
	public static void printNode(Node node) {
		if(node == null)
			return;
		if(node.atom != null)
			System.out.print(node.atom.getContent());
		else {
			System.out.print("(");
			printNode(node.leftChild);
			System.out.print(" . ");
			printNode(node.rightChild);
			System.out.print(")");
		}
	}
}
