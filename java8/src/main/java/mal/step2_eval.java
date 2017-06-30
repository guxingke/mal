package mal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class step2_eval {

  public static MalType READ(String val) {
    return reader.read_str(val);
  }

  public static MalType EVAL(MalType val) {
    if (val instanceof MalList) {
      MalList list = (MalList) val;
      if (list.size() == 0) {
        return val;
      }

      MalList ast = ((MalList) eval_ast(val));
      MalIntFun fun = (MalIntFun) ast.get(0);
      return fun.apply(((MalInt) ast.get(1)), ((MalInt) ast.get(2)));
    }

    return eval_ast(val);
  }
  
  public static String PRINT(MalType val) {
    return printer.pr_str(val);
  }

  public static String rep(String val) {
    return PRINT(EVAL(READ(val)));
  }

  static Map<Class<? extends MalSymbol>, MalIntFun> env = new HashMap<>();
  static {
    env.put(MalAddSymbol.class, MalInt::add);
    env.put(MalSubSymbol.class, MalInt::sub);
    env.put(MalMultiSymbol.class, MalInt::mult);
    env.put(MalDivSymbol.class, MalInt::div);
  }

  public static MalType eval_ast(MalType ast) {
    if (ast instanceof MalSymbol) {
      return env.get(ast.getClass());
    }

    MalList rets = new MalLList();
    if (ast instanceof MalList) {
      MalList list = (MalList) ast;
      for (int i=0; i< list.size(); i++) {
        rets.add(EVAL(list.get(i)));
      }

      return rets;
    }

    return ast;
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

  @FunctionalInterface
  public interface MalIntFun extends MalType {
    MalInt apply(MalInt left, MalInt right);
  }
}

