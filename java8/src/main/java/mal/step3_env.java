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
    if (!(val instanceof MalList)) {
      return eval_ast(val, env);
    }

    MalList list = (MalList) val;
    if (list.size() == 0) {
      return val;
    }

    if (list.get(0) instanceof MalSymbol) {
      if (((MalSymbol) list.get(0)).getValue().equals("def!")) {
        MalType eval = EVAL(list.get(2), env);
        env.set(((MalSymbol) list.get(1)), eval);
        return eval;
      }

      if (Objects.equals(list.get(0), new MalSymbol("let*"))) {
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

    MalList ast = ((MalList) eval_ast(val, env));
    if (!(ast.get(0) instanceof MalIntFun)) {
      return ast;
    }

    MalIntFun fun = (MalIntFun) ast.get(0);
    return fun.apply(((MalInt) ast.get(1)), ((MalInt) ast.get(2)));
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

    MalList rets = new MalList();
    if (ast instanceof MalList) {
      MalList list = (MalList) ast;

      if ("[".equals(list.left)) {
        rets = new MalVector();
      }

      for (int i = 0; i < list.size(); i++) {
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

    while (true) {
      System.out.print("user> ");
      BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
      String line = buffer.readLine();
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

