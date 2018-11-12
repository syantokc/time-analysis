import java.util.regex.Pattern;
import java.util.*;
public class ParseDocument {
	// Parses an unstructured (mostly web) string of text to array list of tokens
	public static ArrayList<String> parseDoc(String s){
		Scanner s_ = new Scanner(s);
		ArrayList<String> d = new ArrayList<String>();
		// Split input with the pattern
		while(s_.hasNext()){
			String l = s_.next();
			//System.out.println(l + " " + l.length());
			if(Pattern.matches("[a-z'A-Z]*", l)){
				d.add(l);
			}

			if( Pattern.matches("[a-z]*[.]{1}", l) ){
				char t[] = l.toCharArray();
				l = String.valueOf(t,0,l.length()-1);
				d.add(l);
				d.add("[period]");

			}
			if( Pattern.matches("[a-z]*[?]{1}", l) ){
				char t[] = l.toCharArray();
				l = String.valueOf(t,0,l.length()-1);
				d.add(l);
				d.add("[interogation]");

			}
			if( Pattern.matches("[a-z]*[:]{1}", l) ){
				char t[] = l.toCharArray();
				l = String.valueOf(t,0,l.length()-1);
				d.add(l);
				d.add("[colon]");

			}
			if( Pattern.matches("[a-z]*[!]+", l) ){
				char t[] = l.toCharArray();
				l = String.valueOf(t,0,l.indexOf('!'));
				d.add(l);
				d.add("[exclamation]");

			}
			if( Pattern.matches("[a-z()]*[.]+[!]+[a-z()]*", l) ){
				d.add("[elipsis]");
				d.add("[exclamation]");
			}
			if( Pattern.matches("[a-z]*[.][.]+", l) ){
				char t[] = l.toCharArray();
				l = String.valueOf(t,0,l.indexOf('.'));
				d.add(l);
				d.add("[elipsis]");

			}
			if( Pattern.matches("[a-z]*[:][-]?[)(DoOPp|][ ]*", l) ){
				char t[] = l.toCharArray();
				l = String.valueOf(t,0,l.indexOf(':'));
				d.add(l);
				d.add("[smiley]");

			}
			if( Pattern.matches("[a-z]*[;][-]?[)(DoOPp|][ ]*", l) ){
				char t[] = l.toCharArray();
				l = String.valueOf(t,0,l.indexOf(';'));
				d.add(l);
				d.add("[smiley]");

			}
			if( Pattern.matches("[a-z]*[,]{1}[a-z]*", l) ){
				char t[] = l.toCharArray();
				String m = String.valueOf(t,0,l.indexOf(','));
				d.add(m);
				d.add("[comma]");
				if(l.charAt(l.length()-1)!=','){
					String k = String.valueOf(t,l.indexOf(',')+1,l.length()-m.length()-1);
					d.add(k);
				}
			}
			if( Pattern.matches("[a-z]*[;]{1}[a-z]*", l) ){
				char t[] = l.toCharArray();
				String m = String.valueOf(t,0,l.indexOf(';'));
				d.add(m);
				d.add("[comma]");
				if(l.charAt(l.length()-1)!=';'){
					String k = String.valueOf(t,l.indexOf(';')+1,l.length()-m.length()-1);
					d.add(k);
				}
			}
			if( Pattern.matches(".*[\\p{Digit}].*", l) ){
				d.add("[number]");
			}
			if( Pattern.matches("[a-z]*[-]{1}[a-z]*", l) ){
				char t[] = l.toCharArray();
				String m = String.valueOf(t,0,l.indexOf('-'));
				d.add(m);
				d.add("[hyphen]");
				if(l.charAt(l.length()-1)!=','){
					String k = String.valueOf(t,l.indexOf('-')+1,l.length()-m.length()-1);
					d.add(k);
				}
			}
			if( Pattern.matches("[a-z]*[/]{1}[a-z]*", l) ){
				char t[] = l.toCharArray();
				String m = String.valueOf(t,0,l.indexOf('/'));
				d.add(m);
				d.add("[slash]");
				if(l.charAt(l.length()-1)!=','){
					String k = String.valueOf(t,l.indexOf('/')+1,l.length()-m.length()-1);
					d.add(k);
				}
			}
			if( Pattern.matches("[h][m][m]+[.]*", l) ){
				d.add("[hmm...]");
			}
			if( Pattern.matches("[(][a-z]*", l) ){
				char t[] = l.toCharArray();
				if(l.charAt(0)=='(' && l.charAt(l.length()-1)!=')'){
					String m = String.valueOf(t,1,l.length()-1);
					d.add("[bracket]");
					d.add(m);
				}
			}
			if( Pattern.matches("[a-z]*[)]", l) ){
				char t[] = l.toCharArray();
				if(l.charAt(0)!='(' && l.charAt(l.length()-1)==')'){
					String m = String.valueOf(t,0,l.length()-1);
					d.add("[bracket]");
					d.add(m);
				}
			}
			if( Pattern.matches("[(][a-z]*[)]", l) ){
				char t[] = l.toCharArray();
				if(l.charAt(0)=='(' && l.charAt(l.length()-1)==')'){
					String m = String.valueOf(t,1,l.length()-2);
					d.add("[bracket]");
					d.add(m);
				}
			}
			if( Pattern.matches("[{][a-z]*", l) ){
				char t[] = l.toCharArray();
				if(l.charAt(0)=='{' && l.charAt(l.length()-1)!='}'){
					String m = String.valueOf(t,1,l.length()-1);
					d.add("[bracket]");
					d.add(m);
				}
			}
			if( Pattern.matches("[a-z]*[}]", l) ){
				char t[] = l.toCharArray();
				if(l.charAt(0)!='{' && l.charAt(l.length()-1)=='}'){
					String m = String.valueOf(t,0,l.length()-1);
					d.add("[bracket]");
					d.add(m);
				}
			}
			if( Pattern.matches("[{][a-z]*[}]", l) ){
				char t[] = l.toCharArray();
				if(l.charAt(0)=='{' && l.charAt(l.length()-1)=='}'){
					String m = String.valueOf(t,1,l.length()-2);
					d.add("[bracket]");
					d.add(m);
				}
			}
			if( Pattern.matches("[\\[][a-z]*", l) ){
				char t[] = l.toCharArray();
				if(l.charAt(0)=='[' && l.charAt(l.length()-1)!=']'){
					String m = String.valueOf(t,1,l.length()-1);
					d.add("[bracket]");
					d.add(m);
				}
			}
			if( Pattern.matches("[a-z]*[\\]]", l) ){
				char t[] = l.toCharArray();
				if(l.charAt(0)!='[' && l.charAt(l.length()-1)==']'){
					String m = String.valueOf(t,0,l.length()-1);
					d.add("[bracket]");
					d.add(m);
				}
			}
			if( Pattern.matches("[\\[][a-z]*[\\]]", l) ){
				char t[] = l.toCharArray();
				if(l.charAt(0)=='[' && l.charAt(l.length()-1)==']'){
					String m = String.valueOf(t,1,l.length()-2);
					d.add("[bracket]");
					d.add(m);
				}
			}
			if( Pattern.matches(".*[l][o][l].*", l) ){
				d.add("[LOL]");
			}
			if( Pattern.matches("[a-z]*[n][\\?][t]", l) ){
				l = l.replace('?', '\'');
				d.add(l);

			}
			if( Pattern.matches("[a-z]*[\\?][d]", l) ){
				l = l.replace('?', '\'');
				d.add(l);

			}
			if( Pattern.matches("[a-z]*[\\?][l][l]", l) ){
				l = l.replace('?', '\'');
				d.add(l);

			}
			if( Pattern.matches("[a-z]*[\\?][d]", l) ){
				l = l.replace('?', '\'');
				d.add(l);

			}
			if( Pattern.matches("[a-z]*[\\?][s]", l) ){
				l = l.replace('?', '\'');
				d.add(l);

			}

			if( Pattern.matches("[a-z]*[\\?][v][e]", l) ){
				l = l.replace('?', '\'');
				d.add(l);

			}
			if( Pattern.matches("[a-z]*[\\?][r][e]", l) ){
				l = l.replace('?', '\'');
				d.add(l);

			}

		}
		return d;
	}
	static ArrayList<String>  parseSimple(String s){
		ArrayList<String> tokens = new ArrayList();		
		for(String st : s.toLowerCase().split("[\\p{Punct}]+")){
			//System.out.println(st);		
			tokens.addAll( Arrays.asList( st.trim().split("[\\p{Space}]+") ) );
		}
		return tokens;
		
	}
	static ArrayList<String>  parseBigram(String s){
		ArrayList<String> tokens = new ArrayList();		
		for(String st : s.toLowerCase().split("[\\p{Punct}]+")){
			//System.out.println(st);		
			tokens.addAll( Arrays.asList( st.trim().split("[\\p{Space}]+") ) );
		}
		return tokens;
	}
	public static void main(String [] a){
		//String s = 	"Great GReat GREat ;-) GREAT 		pasta. The staff is very nice and personable. :-( We     will be back!!" ;
		String s = "OK! I am a King Man! I have a number of Editions! their are 5 that REALLY stand out! I have no \"order\" each has it's own merits!!!! In fact! I shall allow this edition to stand on it's own! It is AMAZING! Buy it, Live It, Read...;-) It Love It!";		
				System.out.println(parseSimple(s));
				System.out.println(parseDoc(s));
		/*for(String i : s.split("[[\\p{Punct}]+[\\p{Space}]+]"))
			if(i.length()>0)
			System.out.println(i);*/
	}
}
