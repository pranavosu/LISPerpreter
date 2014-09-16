import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LISPDelegate {
	
	private static HashMap<String, SExpr> symTable = new HashMap<String, SExpr>();
	private static LISPDelegate instance = null;
	Tokenize tokenizer = new Tokenize();
	static boolean isInList = false;
	private static List<Integer> addedTokenLoc = new ArrayList<Integer>();
	private static SExpr dList = new SExpr("NIL");
	   protected LISPDelegate() {
	      // Exists only to defeat instantiation.
	   }
	   public static LISPDelegate getInstance() {
	      if(instance == null) {
	         instance = new LISPDelegate();
	         addPrimitives();
	         
	      }
	      return instance;
	   }
	   
	   private static void addPrimitives(){
		   
		  String[] primList = {"T","NIL","CAR","CDR","CONS","ATOM","EQ","NULL","INT","PLUS"
				  ,"MINUS","TIMES","QUOTIENT","REMAINDER","LESS","GREATER","COND","QUOTE","DEFUN"};
		  SExpr expr = null;
		  for(String str: primList){
			  
			  expr = new SExpr(str);
			  symTable.put(str,expr);
			  
		  }
		  
		   
	   }
	 
	   public void tokenize(String s, boolean add) throws Exception{
		   
		   tokenizer.lex(s,add);
		  
	   }
	   
	   public void printTokens(){
		   
		   Token t = tokenizer.getNextToken();
		   
		   while(t.t != Tokenize.Type.END){
			   
			   System.out.println(t);
			   tokenizer.consumeToken();
			   t = tokenizer.getNextToken();
		   }
		   
		   
	   }
	   public SExpr input() throws Exception{
		   
		
		   if(!isListNotation()){
				 Token nxtToken = tokenizer.getNextToken();
				// System.out.println("Current Ip:"+nxtToken.value);
				 switch (nxtToken.t)
				 {
				 case RPAREN:
					 throw new Exception("Invalid Exp:"+nxtToken.value+",at:"+nxtToken.loc);
				 case DOT:
					 throw new Exception("Invalid Exp:"+nxtToken.value+",at:"+nxtToken.loc);
				 case LPAREN:
				 {
					 tokenizer.consumeToken();
					 
					 nxtToken = tokenizer.getNextToken();
					 
					 if(nxtToken.t == Tokenize.Type.RPAREN){
						 tokenizer.consumeToken();
						 return getSymExpression("NIL");
					 }
					 
					 SExpr ls = this.input();
					 nxtToken = tokenizer.getNextToken();
					 
					 if(nxtToken.t != Tokenize.Type.DOT)
					 {
						 
						 if(nxtToken.t == Tokenize.Type.RPAREN){
							 return new SExpr(ls,getSymExpression("NIL"));
						 }
						 
						 if(nxtToken.t == Tokenize.Type.END){
							 throw new Exception("WAIT");
						 }
						 else if(nxtToken.t != Tokenize.Type.WHITESPACE)
							 throw new Exception("Invalid Exp:"+nxtToken.value+","
									 	+nxtToken.t.toString()+",at:"+nxtToken.loc);
					 }
		
					 
					 tokenizer.consumeToken();
		
					 SExpr rs = this.input();
					 
					 nxtToken = tokenizer.getNextToken();
		
					 if(nxtToken.t != Tokenize.Type.RPAREN ){
						 
						 if(nxtToken.t == Tokenize.Type.END){
							 throw new Exception("WAIT");
						 }  else
							 throw new Exception("Invalid Exp:"+nxtToken.value+","+nxtToken.t.toString()
									 +",at:"+nxtToken.loc);
					 }
						 tokenizer.consumeToken();
									 
					 return new SExpr(ls,rs);
					 
				 }
		
				 case INTEGER:
				 {
					 
					 SExpr res = new SExpr(Integer.parseInt(nxtToken.value));
					 Token intoken =nxtToken;
					 
					 tokenizer.consumeToken();
					 
					 nxtToken = tokenizer.getNextToken();
		
					 if(intoken.loc == 1 && nxtToken.t != Tokenize.Type.END){
						 	throw new Exception("Invalid Exp:"+nxtToken.value+","+nxtToken.t.toString()
									 +",at:"+nxtToken.loc);
						}
					// System.out.println("Current Ip:"+nxtToken.value);
					 return res;
				 }
				 case SYMB:
				 {
					
					 SExpr expr = getSymExpression(nxtToken.value);
					 
					 Token symToken = nxtToken;
					 
					 tokenizer.consumeToken();
					 
					 nxtToken = tokenizer.getNextToken();
						
					 if(symToken.loc == 1 && nxtToken.t != Tokenize.Type.END){
						 	throw new Exception("Invalid Exp:"+nxtToken.value+","+nxtToken.t.toString()
									 +",at:"+nxtToken.loc);
						}
					 
					 return expr;
					 
				 }
				 case END:
					 throw new Exception("WAIT");
				 default:
					 System.out.println("Should not reach here.");
					break;
				 }
		
		   }
		   else{
			   
			   
			   
			   Token nxtToken = tokenizer.getNextToken();
				// System.out.println("Current Ip:"+nxtToken.value);
				 switch (nxtToken.t)
				 {
				 
				 case LPAREN:
				 {
					 boolean isInlist = false;
					 tokenizer.consumeToken();
					 boolean isAtom = false;
					 nxtToken = tokenizer.getNextToken();
					 if(nxtToken.t == Tokenize.Type.INTEGER || nxtToken.t == Tokenize.Type.SYMB){
						 isAtom = true; 
					 }
					 if(nxtToken.t == Tokenize.Type.RPAREN){
						 tokenizer.consumeToken();
						 return getSymExpression("NIL");
					 }
					 
					 SExpr ls = this.input();
					 nxtToken = tokenizer.getNextToken();
					 
					if(nxtToken.t == Tokenize.Type.WHITESPACE){
						
						tokenizer.consumeToken();
						nxtToken = tokenizer.getNextToken();
						
						if(nxtToken.t != Tokenize.Type.LPAREN && nxtToken.t != Tokenize.Type.INTEGER
								&& nxtToken.t != Tokenize.Type.SYMB &&nxtToken.t != Tokenize.Type.END){
							throw new Exception("Invalid Exp:"+nxtToken.value+","+nxtToken.t.toString()
									 +",at:"+nxtToken.loc);
						}
						
						if(isValidList(tokenizer.getIndex())){
							
							int idx = tokenizer.getIndex();
							tokenizer.addToken(new Token(Tokenize.Type.LPAREN,"(",idx), idx);
							addedTokenLoc.add(new Integer(idx));
							nxtToken = tokenizer.getNextToken();
							isInList = true;
							SExpr rs = this.input();
							nxtToken = tokenizer.getNextToken();
							
								return new SExpr(ls,rs);
						}
						else
							throw new Exception("Invalid List:"+nxtToken.value+",at:"+nxtToken.loc);
						
					}
					
					
					
					if(isInList || isAtom){
						if(nxtToken.t == Tokenize.Type.RPAREN){
							tokenizer.consumeToken();
							return new SExpr(ls,getSymExpression("NIL"));
						}
						if(nxtToken.t == Tokenize.Type.END){
							throw new Exception("WAIT");
						}
					}
					 tokenizer.consumeToken();
						
					 SExpr rs = this.input();
					 
					 nxtToken = tokenizer.getNextToken();
		
					 if(nxtToken.t != Tokenize.Type.RPAREN ){
						 
						 if(nxtToken.t == Tokenize.Type.END){
							 throw new Exception("WAIT");
						 }  else
							 throw new Exception("Invalid Exp:"+nxtToken.value+","+nxtToken.t.toString()
									 +",at:"+nxtToken.loc);
					 }
						 tokenizer.consumeToken();
									 
					 return new SExpr(ls,rs);
				 }
					 
				 case INTEGER:
				 {
					 SExpr res = new SExpr(Integer.parseInt(nxtToken.value));
					
					 Token intToken = nxtToken;
					 
					 tokenizer.consumeToken();
					 
					 nxtToken = tokenizer.getNextToken();
						
					 if(intToken.loc == 1 && nxtToken.t != Tokenize.Type.END){
						 	throw new Exception("Invalid Exp:"+nxtToken.value+","+nxtToken.t.toString()
									 +",at:"+nxtToken.loc);
						}
					 return res;
				 
				 }
				 case SYMB:
				 {
					
					 SExpr expr = getSymExpression(nxtToken.value);
					 
					 Token symtoken = nxtToken;
							 
					 tokenizer.consumeToken();
					 
					 nxtToken = tokenizer.getNextToken();
						
					 if(symtoken.loc == 1 && nxtToken.t != Tokenize.Type.END){
						 	throw new Exception("Invalid Exp:"+nxtToken.value+","+nxtToken.t.toString()
									 +",at:"+nxtToken.loc);
						}
					
					 return expr;
					 
				 }
				 case WHITESPACE:
				 {
					 throw new Exception("Invalid Exp:"+nxtToken.value+","+nxtToken.t.toString()
									 +",at:"+nxtToken.loc);
				 }
				 case END:
					 throw new Exception("WAIT");
				 default:
					 System.out.println("Should not reach here.");
					break;
				 }
			   
			   
		   }
		return new SExpr("erm");
		   
	   }
	   
	   
	   public void output(SExpr exp){
		   
		   switch(exp.getExprType()){
		   
		   case NON_ATOM:
		   {
			   System.out.print("(");
			   output(exp.getlS());
			   System.out.print(".");
			   output(exp.getrS());
			   System.out.print(")");
		   }
		   break;
		   case INTEGER:
		   {
			 System.out.print(exp.getValue());
		   }
		   break;
		   case SYMB_ATOM:
		   {
			   System.out.print(exp.getName());
		   }
		   break;
		   default:
			   System.out.println("Erm...wtf");
			break;
		   
		   }
		   
	   }
	   
	   public String outputString(SExpr exp){
		   
		   String s = "";
		   switch(exp.getExprType()){
		   
		   case NON_ATOM:
		   {
			   
			   s+="(";
			   s+=outputString(exp.getlS());
			   s+=".";
			   s+=outputString(exp.getrS());
			   s+=")";
		   }
		   break;
		   case INTEGER:
		   {
			return exp.getValue()+"";
		   }
		   case SYMB_ATOM:
		   {
			   return exp.getName();
		   }
		   default:
			   System.out.println("Erm...wtf");
			break;
		   
		   }
		return s;
		   
	   }
	   
	   public SExpr CAR(SExpr expr) throws Exception{
		   
		   if(expr.getExprType() == ExprType.INTEGER ||expr.getExprType() == ExprType.SYMB_ATOM)
			   throw new Exception("CAR requires non-atomic argument.");
		   
		   return expr.getlS();
		   
	   }
	   
	   public SExpr CDR(SExpr expr) throws Exception{
		   
		   if(expr.getExprType() == ExprType.INTEGER ||expr.getExprType() == ExprType.SYMB_ATOM)
			   throw new Exception("CDR requires non-atomic argument.");
		   
		   return expr.getrS();
		   
	   }
	   
	   public SExpr CONS(SExpr exprLs, SExpr exprRs) {
		   
//		   if(exprLs == null || exprRs == null){
//			   throw new Exception("Can't have null SExpr");
//		   }
		   
		   return new SExpr(exprLs,exprRs);
	   }
	   
	   public SExpr ATOM(SExpr expr) throws Exception{
		   
		   if(expr.getExprType() == ExprType.INTEGER ||expr.getExprType() == ExprType.SYMB_ATOM)
			   return getSymExpression("T");
		   
		   return getSymExpression("NIL");
	   }
	   
	   public SExpr NULL(SExpr expr) throws Exception{
		   
		   if(expr.getExprType() == ExprType.SYMB_ATOM)
			   if(expr.getName().equals("NIL"))
				   return getSymExpression("T");
		   
		   return getSymExpression("NIL");
	   }
	   
	   public SExpr EQ(SExpr exprLs, SExpr exprRs ) throws Exception{
		   
		   if(exprLs.getExprType() == ExprType.NON_ATOM || exprRs.getExprType() == ExprType.NON_ATOM)
			   throw new Exception("EQ applies only to ATOM's"); 
		   
		   if(exprLs.getExprType() == ExprType.INTEGER && exprRs.getExprType() == ExprType.INTEGER)
			   if(exprLs.getValue() == exprRs.getValue())
				   return getSymExpression("T");
		   
		   if(exprLs.getExprType() == ExprType.SYMB_ATOM && exprRs.getExprType() == ExprType.SYMB_ATOM)
			   if(exprLs.getName().equals(exprRs.getName()))
				   return getSymExpression("T");
		   
		   return getSymExpression("NIL");
	   }
	   
	   public SExpr INT(SExpr expr){
		   
		   if(expr.getExprType() == ExprType.INTEGER)
			   return getSymExpression("T");
		   
		   return getSymExpression("NIL");
	   }
	   
	   public SExpr PLUS(SExpr exprLs, SExpr exprRs ) throws Exception{
		   
		   if(exprLs.getExprType() == ExprType.NON_ATOM || exprRs.getExprType() == ExprType.NON_ATOM
			|| exprLs.getExprType() == ExprType.SYMB_ATOM || exprRs.getExprType() == ExprType.SYMB_ATOM)
			   throw new Exception("PLUS applies only to INTEGERS"); 
		   		   
		   return new SExpr(exprLs.getValue()+exprRs.getValue());
	   }
	   
	   public SExpr MINUS(SExpr exprLs, SExpr exprRs ) throws Exception{
		   
		   if(exprLs.getExprType() == ExprType.NON_ATOM || exprRs.getExprType() == ExprType.NON_ATOM
			|| exprLs.getExprType() == ExprType.SYMB_ATOM || exprRs.getExprType() == ExprType.SYMB_ATOM)
			   throw new Exception("MINUS applies only to INTEGERS"); 
		   		   
		   return new SExpr(exprLs.getValue()-exprRs.getValue());
	   }
	   
	   public SExpr TIMES(SExpr exprLs, SExpr exprRs ) throws Exception{
		   
		   if(exprLs.getExprType() == ExprType.NON_ATOM || exprRs.getExprType() == ExprType.NON_ATOM
			|| exprLs.getExprType() == ExprType.SYMB_ATOM || exprRs.getExprType() == ExprType.SYMB_ATOM)
			   throw new Exception("TIMES applies only to INTEGERS"); 
		   		   
		   return new SExpr(exprLs.getValue()*exprRs.getValue());
	   }
	   
	   public SExpr QUOTIENT(SExpr exprLs, SExpr exprRs ) throws Exception{
		   
		   if(exprLs.getExprType() == ExprType.NON_ATOM || exprRs.getExprType() == ExprType.NON_ATOM
			|| exprLs.getExprType() == ExprType.SYMB_ATOM || exprRs.getExprType() == ExprType.SYMB_ATOM)
			   throw new Exception("QUOTIENT applies only to INTEGERS"); 
		   		   
		   return new SExpr(exprLs.getValue()/exprRs.getValue());
	   }
	   
	   public SExpr REMAINDER(SExpr exprLs, SExpr exprRs ) throws Exception{
		   
		   if(exprLs.getExprType() == ExprType.NON_ATOM || exprRs.getExprType() == ExprType.NON_ATOM
			|| exprLs.getExprType() == ExprType.SYMB_ATOM || exprRs.getExprType() == ExprType.SYMB_ATOM)
			   throw new Exception("REMAINDER applies only to INTEGERS"); 
		   		   
		   return new SExpr(exprLs.getValue()%exprRs.getValue());
	   }
	   
	   public SExpr LESS(SExpr exprLs, SExpr exprRs ) throws Exception{
		   
		   if(exprLs.getExprType() == ExprType.NON_ATOM || exprRs.getExprType() == ExprType.NON_ATOM
			|| exprLs.getExprType() == ExprType.SYMB_ATOM || exprRs.getExprType() == ExprType.SYMB_ATOM)
			   throw new Exception("LESS applies only to INTEGERS"); 
		
		   if(exprLs.getValue()<exprRs.getValue()){
			   return getSymExpression("T");
		   }
		   
		   return getSymExpression("NIL");
	   }
	   
	   public SExpr GREATER(SExpr exprLs, SExpr exprRs ) throws Exception{
		   
		   if(exprLs.getExprType() == ExprType.NON_ATOM || exprRs.getExprType() == ExprType.NON_ATOM
			|| exprLs.getExprType() == ExprType.SYMB_ATOM || exprRs.getExprType() == ExprType.SYMB_ATOM)
			   throw new Exception("GREATER applies only to INTEGERS"); 
		
		   if(exprLs.getValue()>exprRs.getValue()){
			   return getSymExpression("T");
		   }
		   
		   return getSymExpression("NIL");
	   }
	   
	   public SExpr QUOTE(SExpr expr) throws Exception{
		   return expr;
	   }
	   
	   
	   public SExpr EVLIS(SExpr list, SExpr aList) throws Exception{
		   
		   
		   if(NULL(list).getName().equals("T"))
			   return getSymExpression("NIL");
		   else
			   return CONS(EVAL(CAR(list),aList),EVLIS(CDR(list),aList));
		   
	   }
	   
	   
	   public SExpr EVCON(SExpr be, SExpr aList) throws Exception{
		   
		   int count = countArguments(CAR(be));
		   
		   if(count == 1){
			   throw new Exception("Incorrect number of parameters for boolean exp.");
		   }
		   if(count > 2){
			   System.out.println("*Warning: Extra parameters in boolean exp not used*");
			 
		   }
		   if(NULL(be).getName().equals("T"))
			   throw new Exception("EVCON Error!");
		  
		   SExpr ev = EVAL(CAR(CAR(be)),aList);
		   
		   if(ev.getExprType()==ExprType.SYMB_ATOM){
		   if(ev.getName().equals("T")){
			   
			   SExpr evaled = CDR(CAR(be));
			   
			   return EVAL(evaled,aList);
		   }
		   else if(ev.getName().equals("NIL")){
			   return EVCON(CDR(be),aList);
		   }
		   }
		   else{
			   throw new Exception("EVCON Error. Not T/NIL");
		   }
		return ev;
		   
	   }
	   
	   public SExpr EVAL(SExpr expr, SExpr aList) throws Exception{
		   
		   if(aList == null)
			   aList = getSymExpression("NIL");
		   
		   if(ATOM(expr).getName().equals("T")){
			   
			   if(INT(expr).getName().equals("T")){
				   return expr;
			   }
			   else if(EQ(expr,getSymExpression("T")).getName().equals("T")){
				   return getSymExpression("T");
			   }
			   else if(EQ(expr,getSymExpression("NIL")).getName().equals("T")){
				   return getSymExpression("NIL");
			   }//TODO Implement the IN function;
			   else if(IN(expr, aList).getName().equals("T")){
				   return getVal(expr, aList);
			   }
			   else 
				   throw new Exception("Unbound Variable:"+expr.getName());
		   }
		   else if(ATOM(CAR(expr)).getName().equals("T")){
			   
			   if(EQ(CAR(expr),getSymExpression("QUOTE")).getName().equals("T")){
				   
				   if(!enforceArgLimit(CDR(expr), 1))
					   throw new Exception("QUOTE: Incorrect number of arguments.");
				   
				   return QUOTE(CAR(CDR(expr))); 
			   }
			   else if(EQ(CAR(expr),getSymExpression("COND")).getName().equals("T")){
				   
				   return EVCON(CDR(expr),aList); 
			   }
			   if(EQ(CAR(expr),getSymExpression("DEFUN")).getName().equals("T")){
				   
				   if(aList.getExprType() == ExprType.SYMB_ATOM && aList.getName().equals("NIL")){
				   
				   if(!enforceArgLimit(CDR(expr), 3))
					   throw new Exception("DEFUN: Incorrect number of arguments.");
				   
				   
				   		addToDlist(CDR(expr));
				   		return CAR(CDR(expr)); 
				   }
				   else
					   throw new Exception("DEFUN can't be called at this level.");
			   }
			   else{
				   return APPLY(CAR(expr),EVLIS(CDR(expr),aList),aList);
			   }
			   
			   
		   }
		   else{
			   throw new Exception("Error!");
		   }
	   }
	   
	   public void addToDlist(SExpr expr) throws Exception{
		   
		   SExpr fnName = CAR(expr);
		   SExpr fnParams = CAR(CDR(expr));
		  // SExpr fnBody = CAR(CDR(CDR(expr)));
		   
		   if(fnName.getExprType() != ExprType.SYMB_ATOM)
			   throw new Exception("DEFUN: Invalid function identifier.");
		   
		   if(fnParams.getExprType() == ExprType.SYMB_ATOM 
				   && !fnParams.getName().equals("NIL")) 
			   throw new Exception("DEFUN: Invalid function param list.");
		   
		   dList = new SExpr(expr,dList);
		   
	   }
	   
	   public SExpr addPairs(SExpr params, SExpr args, SExpr aList) throws Exception{
		   
		   if(countArguments(params)!=countArguments(args)){
			   throw new Exception("Invalid number of arguments.");
		   }
		   
		   while(!(params.getName() != null && params.getName().equals("NIL"))){
			   
			   SExpr pair = new SExpr(getSymExpression(CAR(params).getName()),CAR(args));
			   
			   aList = new SExpr(pair,aList);
			   //TODO Handle Parameter count mismatch
			   params = CDR(params);
			   args = CDR(args);
			   
		   }
		   
		   return aList;
	   }
	   
	   public SExpr IN(SExpr expr, SExpr list) throws Exception{
		   
		   SExpr listItem = null;
		   SExpr currentList = list;
//		   SExpr cdrItem = CDR(list); 
		   while(!(currentList.getName() != null && currentList.getName().equals("NIL"))){
			 
			   listItem = CAR(CAR(currentList));
			   
			   if(listItem.getName().equals(expr.getName())){
				   return getSymExpression("T");
			   }
			   
			   currentList = CDR(currentList);
			 
		   }
		   
		   return getSymExpression("NIL");
		   
	   }
	   
	   public SExpr getVal(SExpr expr, SExpr list) throws Exception{
		   
		   SExpr listItem = null;
		  // SExpr cdrItem = CDR(list); 
		   while(!(list.getName() != null && list.getName().equals("NIL"))){
			 
			   listItem = CAR(CAR(list));
			   
			   if(listItem.getName().equals(expr.getName())){
				   
				   SExpr retVal = CDR(CAR(list));
				   return retVal;
			   }
			   
			   list = CDR(list);
			 
		   }
		   
		   return getSymExpression("NIL");
		   
	   }
	   
	  boolean enforceArgLimit(SExpr expr, int n) throws Exception{
		  
		  return n == countArguments(expr);
	  }
	  
	  int countArguments(SExpr expr) throws Exception{
		  
		  int count = 0 ;
		  while(!(expr.getName() != null && expr.getName()=="NIL")){
			  
			  count++;
			  
			  expr = CDR(expr);
			  
		  }
		  return count;
	  }
	   
	   public SExpr APPLY(SExpr f, SExpr x, SExpr aList ) throws Exception{
		   
		   if(ATOM(f).getName().equals("T")){
			   
			   if(EQ(f,getSymExpression("CAR")).getName().equals("T")){
				   if(!enforceArgLimit(x, 1))
					   throw new Exception("CAR:Incorrect number of arguments.");
				   
				   return CAR(CAR(x));
			   }
			   else if(EQ(f,getSymExpression("CDR")).getName().equals("T")){
				   if(!enforceArgLimit(x, 1))
					   throw new Exception("CDR: Incorrect number of arguments.");
				   
				   return CDR(CAR(x));
			   }
			   else if(EQ(f,getSymExpression("CONS")).getName().equals("T")){
				   if(!enforceArgLimit(x, 2))
					   throw new Exception("CONS: Incorrect number of arguments.");
				   
				   return CONS(CAR(x),CAR(CDR(x)));
			   }
			   else if(EQ(f,getSymExpression("PLUS")).getName().equals("T")){
				   if(!enforceArgLimit(x, 2))
					   throw new Exception("PLUS: Incorrect number of arguments.");
				   
				   return PLUS(CAR(x),CAR(CDR(x)));
			   }
			   else if(EQ(f,getSymExpression("ATOM")).getName().equals("T")){
				   if(!enforceArgLimit(x, 1))
					   throw new Exception("ATOM: Incorrect number of arguments.");
				   
				   return ATOM(CAR(x));
			   }
			   else if(EQ(f,getSymExpression("NULL")).getName().equals("T")){
				   
				   if(!enforceArgLimit(x, 1))
					   throw new Exception("NULL: Incorrect number of arguments.");
				   
				   return NULL(CAR(x));
			   }
			   else if(EQ(f,getSymExpression("EQ")).getName().equals("T")){
				
				   if(!enforceArgLimit(x, 2))
					   throw new Exception("EQ: Incorrect number of arguments.");
				   
				   return EQ(CAR(x),CAR(CDR(x)));
			   }
			   else if(EQ(f,getSymExpression("INT")).getName().equals("T")){
				   if(!enforceArgLimit(x, 1))
					   throw new Exception("INT: Incorrect number of arguments.");
				   
				   return INT(CAR(x));
			   }
			   else if(EQ(f,getSymExpression("MINUS")).getName().equals("T")){
				  
				   if(!enforceArgLimit(x, 2))
					   throw new Exception("MINUS: Incorrect number of arguments.");
				   
				   return MINUS(CAR(x),CAR(CDR(x)));
			   }
			   else if(EQ(f,getSymExpression("TIMES")).getName().equals("T")){
				   if(!enforceArgLimit(x, 2))
					   throw new Exception("TIMES: Incorrect number of arguments.");
				   
				   return TIMES(CAR(x),CAR(CDR(x)));
			   }
			   else if(EQ(f,getSymExpression("QUOTIENT")).getName().equals("T")){
				   if(!enforceArgLimit(x, 2))
					   throw new Exception("QUOTIENT: Incorrect number of arguments.");
				   
				   return QUOTIENT(CAR(x),CAR(CDR(x)));
			   }
			   else if(EQ(f,getSymExpression("REMAINDER")).getName().equals("T")){
				   if(!enforceArgLimit(x, 2))
					   throw new Exception("REMAINDER: Incorrect number of arguments.");
				   
				   return REMAINDER(CAR(x),CAR(CDR(x)));
			   }
			   else if(EQ(f,getSymExpression("LESS")).getName().equals("T")){
				   if(!enforceArgLimit(x, 2))
					   throw new Exception("LESS :Incorrect number of arguments.");
				   return LESS(CAR(x),CAR(CDR(x)));
			   }
			   else if(EQ(f,getSymExpression("GREATER")).getName().equals("T")){
				   if(!enforceArgLimit(x, 2))
					   throw new Exception("GREATER: Incorrect number of arguments.");
				   return GREATER(CAR(x),CAR(CDR(x)));
			   }			 
			   else
				   return EVAL(CAR(CDR(getVal(f, dList))),addPairs(CAR(getVal(f, dList)),x,aList));
			   }
		   else
			   throw new Exception("Evaluation Error!");
	   }
	   
	   
	   
	   
	   
	   
	   
	   public boolean isListNotation(){
		   
		   for(Token t : tokenizer.getTokenList()){
			   if(t.t == Tokenize.Type.WHITESPACE){
				   return true;
			   }
			}
		   
		   return false;
	   }
	   
	   public boolean isValidList(int index){
		   
		   List<Token> list = tokenizer.getTokenList();
		   
		   for(int i = index-1;i<list.size()-2;i++){
			   if(list.get(i).t == Tokenize.Type.WHITESPACE){
				   
				   Token t2 = list.get(i+2);
				   	
				   if(t2.t == Tokenize.Type.DOT)
					   return false;
			   		}
		   }
		   
		   return true;
		   
	   }
	   
	   //Gets SExpr from symbol table if already exists, else returns a new SExpr.
		 private SExpr getSymExpression(String name){
			 SExpr expr = symTable.get(name);
			 
			 if(expr == null){
				 expr = new SExpr(name);
				 symTable.put(name, expr);
			 }
			 
			 return expr;
		 }
		public void removeAddedTokens() {
			// TODO Auto-generated method stub
			
			tokenizer.removeAddedTokens(addedTokenLoc);
			addedTokenLoc.clear();
			
		}
		
		public void removeAllTokens(){
			
			tokenizer.removeAllTokens();
			addedTokenLoc.clear();
			
		}
		
		
		
		
	   
}
