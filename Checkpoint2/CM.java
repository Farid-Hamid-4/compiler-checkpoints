import java.io.*;
import absyn.*;
   
class CM {
  static public void main(String argv[]) {    
    boolean a_flag = false;
    boolean s_flag = false;
    
    for (String s: argv)
      if(s.equals("-a"))
        a_flag = true;
      else if (s.equals("-s"))
        s_flag = true;

    /* Start the parser */
    try {
      parser p = new parser(new Lexer(new FileReader(argv[0])));
      Absyn result = (Absyn)(p.parse().value);      

      if (a_flag && result != null) {
        System.out.println("The abstract syntax tree is:");
        ShowTreeVisitor visitor = new ShowTreeVisitor();
        result.accept(visitor, 0); 
      }
      if (s_flag && result != null) {
        System.out.println("The semantic analyzer is:");
      }

    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
    }
  }
}

