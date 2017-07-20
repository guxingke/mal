package step1_read_print;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class step1_read_print {

  public static mal READ(String val) {
    return reader.read_str(val);
  }

  public static mal EVAL(mal val) {
    return val;
  }

  public static String PRINT(mal val) {
    return printer.pr_str(val);
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
      try {
        System.out.println(rep(line));
      } catch (reader.EOFException e) {
        System.out.println(e.msg);
      }
    }
  }
}
