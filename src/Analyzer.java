import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Analyzer {

	static private List<Character> list;
	static private int index;

	static{
		index = 0;
		list = new ArrayList<Character>();
	}
	
	public static void getStringFromFile() {
		try {
			InputStreamReader read = new InputStreamReader(System.in);
			BufferedReader bufferedReader = new BufferedReader(read);
			int charAsInt;
			while((charAsInt = bufferedReader.read()) != -1) {
				char c = (char)charAsInt;
				if(c == '\n') {
					list.add(' ');
					continue;
				}	
				list.add(c);
			}
			if(list.get(list.size()-1) == ' ')
				list.remove(list.size()-1);
			read.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Token getNextToken() throws ListException {
		//System.out.println("list.size() = " + list.size() + " index = " + index);
		while(index <= (list.size() - 1)) {
			//System.out.println("index = " + index);
			Character c = list.get(index++);
			if(c.equals(' ')){
				continue;
			}
			if(c.equals('('))
				return (new Token(TokenType.OpenParenthesis, "("));	
			else if(c.equals(')')) 
				return (new Token(TokenType.ClosingParenthesis, ")"));
			else if(c.equals('.'))
				return (new Token(TokenType.Dot, "."));
			else {
				StringBuffer sb = new StringBuffer();
				sb.append(c);
				boolean isDigit;
				if(Character.isDigit(c))
					isDigit = true;
				else
					isDigit = false;
				int atomIndex = index;
				while((atomIndex <= (list.size() - 1)) && (Character.isAlphabetic(list.get(atomIndex)) || Character.isDigit(list.get(atomIndex)))) {
					//System.out.println("here");
					if(isDigit && Character.isAlphabetic(list.get(atomIndex)))
						throw(new ListException("The atom's characters beginning with \"" + sb.toString() + list.get(atomIndex) + "\" are invalid!"));
					sb.append(list.get(atomIndex));
					atomIndex ++;
				}
				index = atomIndex;
				//System.out.println("sb.toString() = " + sb.toString() + ",  sb.length() = " + sb.length());
				return (new Token(TokenType.Atom, sb.toString()));
			}	
		}
		// no more character
		return null;
	}
	
	public static int getIndex() {
		return index;
	}

	public static void setIndex(int index) {
		Analyzer.index = index;
	}
	
	
	public static boolean isEnd(){
		if (index >= list.size())
			return true;
		else
			return false;
	}
	

}
