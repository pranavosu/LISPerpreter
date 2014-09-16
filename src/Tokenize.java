import java.util.ArrayList;
import java.util.List;


public class Tokenize {
	
	
	private List<Token> result = null;
	int idx = 0;
	
	private final static int ATOM_SIZE_LIMIT = 10;
	
	
	public static enum Type {
        // This Scheme-like language has three token types:
        // open parens, close parens, and an "atom" type
        LPAREN, RPAREN, INTEGER, SYMB , DOT, WHITESPACE, END;
    }
	
	
	/*
     * Given a String, and an index, get the atom starting at that index
     */
    private static String getSym(String s, int i) throws Exception {
        int j = i;
        for( ; j < s.length(); ) {
            if(Character.isLetterOrDigit(s.charAt(j))) {
                j++;
            } else {
            	
                return s.substring(i, j);
            }
        }
        return s.substring(i, j);
    }
    
    private static String getInt(String s, int i) throws Exception {
        int j = i;
        for( ; j < s.length(); ) {
            if(Character.isDigit(s.charAt(j))) {
                j++;
            } else {
            	char ch = s.charAt(j);
            	if(j<s.length()-1 && Character.isLetter(s.charAt(j))){
            		throw new Exception("Identifier can't start with letters.");
            	}
                return s.substring(i, j);
            }
        }
        return s.substring(i, j);
    }
    
    private static String getWhiteSpace(String s, int i) {
        int j = i;
        for( ; j < s.length(); ) {
            if(Character.isWhitespace(s.charAt(j))) {
                j++;
            } else {
                return s.substring(i, j);
            }
        }
        return s.substring(i, j);
    }

    public void lex(String input, boolean add) throws Exception {
    	

    	
    		List<Token> result = new ArrayList<Token>();
    	int locOffset = 0;
    	
    	if(add)
    		locOffset = input.length()-1;
    	
        for(int i = 0; i < input.length(); ) {
            switch(input.charAt(i)) {
            case '(':
                result.add(new Token(Type.LPAREN, "(",i+locOffset));
                i++;
                break;
            case ')':
//            	if(i!=0 && input.charAt(i-1) == ' '){
//            		result.remove(i+locOffset);
//            		
//            	}
                result.add(new Token(Type.RPAREN, ")",i+locOffset));
                i++;
                break;
            case '.':
                result.add(new Token(Type.DOT, ".",i+locOffset));
                i++;
                break;
            default:
                if(Character.isWhitespace(input.charAt(i))) {
                	
                	
                	boolean shouldAddWhiteSpace = true;
                	
                	if(i>0 && (input.charAt(i-1) == '.'))
                		shouldAddWhiteSpace = false;
                	
                	if(i>0 && (input.charAt(i-1) == '('))
                		shouldAddWhiteSpace = false;
                	
                	//int begin = i;
                	 String atom = getWhiteSpace(input, i);
                     i += atom.length();
                    
                     if(i<input.length()-1 && input.charAt(i) == '.')
                    	 shouldAddWhiteSpace = false;
                     
                     if(i<input.length()-1 && ( input.charAt(i) == ')')){
                    	 shouldAddWhiteSpace = false;
                     }
                     
                     
                     
                     
                    if(shouldAddWhiteSpace)
                    	 result.add(new Token(Type.WHITESPACE, " ",i+locOffset));
                    
                } else  if(Character.isLetter(input.charAt(i))){
                    String atom = getSym(input, i);
                    int start = i+1;
                    i += atom.length();
                   
                    if(atom.length() > ATOM_SIZE_LIMIT )
                   	 throw new Exception("Size of "+atom+" > "+ATOM_SIZE_LIMIT);
                    
                    result.add(new Token(Type.SYMB, atom,start+locOffset));
                }
                else  if(Character.isDigit(input.charAt(i))){
                    String atom = getInt(input, i);
                    int start = i+1;
                    i += atom.length();

                    if(atom.length() > ATOM_SIZE_LIMIT )
                   	 throw new Exception("Size of "+atom+" > "+ATOM_SIZE_LIMIT);
                    
                    result.add(new Token(Type.INTEGER, atom,start+locOffset));
                }
                else if(input.charAt(i)=='+'||input.charAt(i)=='-'){
                	
                	char sign = input.charAt(i);
                	i++;
                	
                	if(Character.isDigit(input.charAt(i))){
                		
                		int start = i+1;
                		String atom = getInt(input, i);
                		atom = sign+atom;
                        i += atom.length()-1;

                        if(atom.length()-1 > ATOM_SIZE_LIMIT )
                       	 throw new Exception("Size of "+atom+" > "+ATOM_SIZE_LIMIT);
                        
                        result.add(new Token(Type.INTEGER, atom,start+locOffset));
                	}
                	else{
                		throw new Exception("Invalid Character:"+input.charAt(i)+", at:"+(i+1)+locOffset);
                	}
                	
                }else{
                	throw new Exception("Invalid Character:"+input.charAt(i)+", at:"+(i+1)+locOffset);
                }
                break;
            }
        }
        
        if(add){
        	this.result.addAll(result);
        	int i = 0;
        	
        	ArrayList<Token> al = new ArrayList<Token>();
        	for(Token t: this.result){
        		
        		if(t.value.equals(")")){
        			
        			if(i!=0){
        				Token t2 = this.result.get(i-1);
        				if(t2.value == " ")
        					al.add(t2);
        				
        					//this.result.remove(i-1);
        			}
        		}
        		
        		i++;
        	}
        	this.result.removeAll(al);
        	
        	this.idx = 0;
        }
        else
        {
        	this.result = result;
        	this.idx = 0;
        }
       //} return result;
    }
    
	 public Token getNextToken(){
		if(idx < result.size())
			return result.get(idx);
		else 
			return new Token(Type.END, "EOI",idx);
	 }
	 
	 public void consumeToken(){
		 
		 if(idx<result.size())
			 idx++;
		 else
			 System.out.println("No more tokens.");
	 
	 }
	 
	 public List<Token> getTokenList(){
		 
		 return new ArrayList<Token>(result);
		 
	 }
	 
	 public void addToken(Token t, int index){
		 
		 result.add(index, t);
		 
		 
		 
	 }
	 
	 public int getIndex(){
		 return idx;
		 
	 }

	public void removeAddedTokens(List<Integer>added) {
		// TODO Auto-generated method stub
		int i = 0;
		for(Integer e: added){
			
			result.remove(e.intValue() - i++);
		}
		
	}
	
	public void removeAllTokens(){
		
		if(result!=null)
			result.removeAll(result);
	}
	
	 
	

}
