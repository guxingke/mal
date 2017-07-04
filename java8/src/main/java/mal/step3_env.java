package mal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

import mal.env.Env;

public class step3_env {

  static MalType READ(String val) {
    return reader.read_str(val);
  }

  static MalType EVAL(MalType val, Env env) {
    if (val instanceof MalList) {
      MalList list = (MalList) val;
      if (list.size() == 0) {
        return val;
      }

      MalList ast = ((MalList) eval_ast(val, env));
      if (ast.get(0) instanceof MalSymbol) {
        if (((MalSymbol) ast.get(0)).getValue().equals("def!")) {
          MalType eval = EVAL(ast.get(2), env);
          env.set(((MalSymbol) ast.get(1)), eval);
          return eval;
        }

        if (Objects.equals(ast.get(0), new MalSymbol("let*"))) {
          Env innerEnv = new Env(env);
          MalType binding = list.get(1);
          if (!(binding instanceof MalList)) {
            throw new reader.EOFException("let* should be follow a list");
          }
          MalList bindingList = (MalList) binding;

          for (int j = 0; j <= bindingList.size() / 2; j = j + 2) {
            innerEnv.set(((MalSymbol) bindingList.get(j)), EVAL(bindingList.get(j + 1), innerEnv));
          }

          MalType ret = EVAL(list.get(2), innerEnv);
          return ret;
        }
      }

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

  static String rep(String val, Env env) {
    return PRINT(EVAL(READ(val), env));
  }

  static MalType eval_ast(MalType ast, Env env) {
    if (ast instanceof MalSymbol) {
      MalSymbol symbol = (MalSymbol) ast;

      if (env.find(symbol) instanceof MalNil) {
        throw new env.NotFoundException("not found symbol");
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
        if (i == 0) {
          MalType index0 = EVAL(list.get(i), env);
          rets.add(index0);
          if (Objects.equals(index0, new MalSymbol("def!"))) {
            i++;
            rets.add(list.get(i));
          }
          if (Objects.equals(index0, new MalSymbol("let*"))) {
            i++;
            rets.add(list.get(i));
            i++;
            rets.add(list.get(i));
          }
          continue;
        }
        rets.add(EVAL(list.get(i), env));
      }

      return rets;
    }

    return ast;
  }

  public static void main(String[] args) throws Exception {

    Env env = new Env(null);
    env.set(new MalAddSymbol(), (MalIntFun) MalInt::add);
    env.set(new MalSubSymbol(), (MalIntFun) MalInt::sub);
    env.set(new MalMultiSymbol(), (MalIntFun) MalInt::multi);
    env.set(new MalDivSymbol(), (MalIntFun) MalInt::div);
    env.set(new MalSymbol("def!"), new MalSymbol("def!"));
    env.set(new MalSymbol("let*"), new MalSymbol("let*"));

    while(true) {
      System.out.print("user> ");
      BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
      String line=buffer.readLine();
      if (line == null) {
        break;
      }

      try {
        System.out.println(rep(line, env));
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  @FunctionalInterface
  public interface MalIntFun extends MalType {
    MalInt apply(MalInt left, MalInt right);
  }
}

