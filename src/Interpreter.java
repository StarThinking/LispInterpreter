
public class Interpreter {
	
	public static void main(String[] args) {
		try {
			Analyzer.getStringFromFile();
			Parser.ParseStart();
			Parser.checkInnerNodeIsList();
			Parser.checkRootIsList();
			Printer.print();
		} 
		catch (ListException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
	}

}
