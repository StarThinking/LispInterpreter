import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Analyzer {

	static private List<Character> list = new ArrayList<Character>();
	static private int index = 0;
	
	public static void getStringFromFile() {
		try {
			InputStreamReader read = new InputStreamReader(System.in);
			BufferedReader bufferedReader = new BufferedReader(read);
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				for(int i=0; i<line.length(); i++){
					list.add(line.charAt(i));
				}
			}
			read.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean isEnd(){
		if (index >= list.size())
			return true;
		else
			return false;
	}
	
	public static Token getNextToken() throws ListException {
		while(index <= (list.size() - 1)) {
			Character c = list.get(index++);
			if(c.equals(' ')){
				continue;
			}
			else if(c.equals('('))
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
					if(isDigit && Character.isAlphabetic(list.get(atomIndex)))
						throw(new ListException("The atom's characters beginning with \"" + sb.toString() + list.get(atomIndex) + "\" are invalid!"));
					sb.append(list.get(atomIndex));
					atomIndex ++;
				}
				index = atomIndex;
				return (new Token(TokenType.Atom, sb.toString()));
			}	
		}
		// no more character
		return null;
	}

}
