
public class Interpreter {
	
	public static void main(String[] args) {
		try {
			Analyzer.getStringFromFile();
			Parser.ParseStart();
			//Printer.prints();
			Eval.evals(Parser.getRootList());
		} 
		catch (Exception e) {
			if(e instanceof ListException)
				System.out.println("ERROR: " + e.getMessage());
			else {
				System.out.println("ERROR: Function fails!");
				//System.out.println(e);
			}
		}
	}
}
