
enum ExprType {
	    INTEGER, SYMB_ATOM, NON_ATOM
	}

public class SExpr implements Comparable<SExpr> {
	
	private SExpr lS;
	private SExpr rS;
	private int value;
	private String name;
	private ExprType exprType;

	public SExpr(SExpr lS, SExpr rS) {
		super();
		this.lS = lS;
		this.rS = rS;
		this.exprType = ExprType.NON_ATOM;
	}

	public SExpr(int value) {
		super();
		this.value = value;
		this.exprType = ExprType.INTEGER;
	}

	public SExpr(String name) {
		super();
		this.name = name;
		this.exprType = ExprType.SYMB_ATOM;
	}

	@Override
	public int compareTo(SExpr o) {
		// TODO CHECK CASE
		if(o.name.equals(this.name))
			return 0;
		
		else return 1;
	}
	
	public boolean equals(SExpr o) {
		// TODO CHECK CASE
		return o.name.equals(this.name);
	}

	public SExpr getlS() {
		return lS;
	}

	public void setlS(SExpr lS) {
		this.lS = lS;
	}

	public SExpr getrS() {
		return rS;
	}

	public void setrS(SExpr rS) {
		this.rS = rS;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ExprType getExprType() {
		return exprType;
	}

	public void setExprType(ExprType exprType) {
		this.exprType = exprType;
	}
	
	public String toString(){
		return LISPDelegate.getInstance().outputString(this);
	}
	
	public Object clone() {
	       try {
	           return super.clone();
	       }
	       catch (CloneNotSupportedException e) {
	           System.out.println("This object can't be cloned");
	           return null;
	       }
	   } 

	

}
