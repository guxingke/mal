package mal;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class step3_env {

  static MalType READ(String val) {
    return reader.read_str(val);
  }

  static MalType EVAL(MalType val, env.Env env) {
    if (val instanceof MalList) {
      MalList list = (MalList) val;
      if (list.size() == 0) {
        return val;
      }

      MalList ast = ((MalList) eval_ast(val, env));

      if (!(ast.get(0) instanceof MalIntFun)) {
        return ast;
      }

      MalIntFun fun = (MalIntFun) ast.get(0);
      return fun.apply(((MalInt) ast.get(1)), ((MalInt) ast.get(2)));
    }

    return eval_ast(val, env);
  }
  
  static String PRINT(MalType val) {
    return printer.pr_str(val);
  }

  static String rep(String val, env.Env env) {
    return PRINT(EVAL(READ(val), env));
  }

  static MalType eval_ast(MalType ast, env.Env env) {
    if (ast instanceof MalSymbol) {
      MalSymbol symbol = (MalSymbol) ast;
      if (env.find(symbol) instanceof MalNil) {
        throw new reader.EOFException(" ");
      }

      return env.get(symbol);
    }

    MalList rets = new MalLList();
    if (ast instanceof MalList) {
      MalList list = (MalList) ast;

      if ("[".equals(list.left)) {
        rets = new MalMList();
      }

      if ("{".equals(list.left)) {
        rets = new MalLList();
      }

      for (int i=0; i< list.size(); i++) {
        rets.add(EVAL(list.get(i), env));
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

      env.Env env = new env.Env(null);
      env.set(new MalAddSymbol(), (MalIntFun) MalInt::add);
      env.set(new MalSubSymbol(), (MalIntFun) MalInt::sub);
      env.set(new MalMultiSymbol(), (MalIntFun) MalInt::multi);
      env.set(new MalDivSymbol(), (MalIntFun) MalInt::div);

      try {
        System.out.println(rep(line, env));
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

