package mal;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class step0_repl {
  
  public static String READ(String val) {
    return val;
  }

  public static String EVAL(String val) {
    return val;
  }
  
  public static String PRINT(String val) {
    return val;
  }

  public static String rep(String val) {
    return PRINT(EVAL(READ(val)));
  }

  public static void main(String[] args) throws Exception {
    while(true) {
      System.out.print("user> ");
      BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
      String line=buffer.readLine();
      if (line == null) {
        break;
      }
      System.out.println(rep(line));
    }
  }
}
