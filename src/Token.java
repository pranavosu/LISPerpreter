public class Token {
        public final Tokenize.Type t;
        public final String value;
        public final int loc;
        // contents mainly for atom tokens
        // could have column and line number fields too, for reporting errors later
        public Token(Tokenize.Type t, String c, int loc) {
            this.t = t;
            this.value = c;
            this.loc = loc;
        }
        
        public String toString() {
            if(t == Tokenize.Type.SYMB || t == Tokenize.Type.INTEGER) {
                return t.toString()+"<" + value + ">";
            }
            return t.toString();
        }
   
}