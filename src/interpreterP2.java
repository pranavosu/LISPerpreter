import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;


public class interpreterP2 {

	/**
	 * @param args
	 */
	
	static boolean add = false;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("** LISP Interpreter: PART 2 **");

		System.out.println("");
		LISPDelegate iDelegate = LISPDelegate.getInstance();
		while(true){
			
			try{
				
				System.out.print("INPUT> ");
			    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			    String s = bufferRead.readLine();
			   // System.out.println(s);
			    
			    
			    if(s.equalsIgnoreCase("EXIT*")){
			    	System.out.println("EXITING...DONE.");
			    	break;
			    }
			    s = s.trim();
			    
			    s = s + " ";

			    s = s.toUpperCase();
			    
			    iDelegate.tokenize(s,add);
			    
			    
			    if(add)
			    	add = false;
			    
			   // iDelegate.printTokens();
			    
			 
			    
			    SExpr exp = iDelegate.input();
			    
			   SExpr eval = iDelegate.EVAL(exp, null);
			   System.out.print("OUTPUT> ");
			   iDelegate.output(eval);
			   iDelegate.removeAddedTokens();
			   System.out.println();
			    
			}
		    catch(Exception e){
		    	
		    	if(e.getMessage().equals("WAIT"))
		    	{	
		    		//System.out.println("WAIT");
		    			add = true;
		    			
		    		 iDelegate.removeAddedTokens();
		    			
		    			
		    			continue;
		    	}
		    	else{
		    		
			    	System.out.println("Error! "+e.getMessage());
			    	iDelegate.removeAllTokens();
			    	continue;
			    	
			    	//System.out.println("Interpreter Terminated.");
			    	//break;
		    	}
		    }
		}
		
		
	}

}
