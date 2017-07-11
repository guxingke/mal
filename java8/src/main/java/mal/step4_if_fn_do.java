package mal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import mal.env.Env;

public class step4_if_fn_do {

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
      MalSymbol symbol = (MalSymbol) list.get(0);
      switch (symbol.getValue()) {
        case "def!":
          return env.set(((MalSymbol) list.get(1)), EVAL(list.get(2), env));
        case "let*":
          Env innerEnv = new Env(env);
          MalType binding = list.get(1);
          if (!(binding instanceof MalList)) {
            throw new reader.EOFException("let* should be follow a list");
          }
          MalList bindingList = (MalList) binding;

          for (int j = 0; j <= bindingList.size() / 2; j = j + 2) {
            innerEnv.set(((MalSymbol) bindingList.get(j)), EVAL(bindingList.get(j + 1), innerEnv));
          }
          return EVAL(list.get(2), innerEnv);
        case "do":
          MalList malList = (MalList) eval_ast(list.rest(), env);
          return malList.get(malList.size() - 1);
        case "if":
          MalBool ret;
          MalType bool = EVAL(list.get(1), env);
          if (bool instanceof MalNil
              || bool instanceof MalFalse) {
            ret = new MalFalse();
          } else {
            ret = new MalTrue();
          }

          if (ret instanceof MalTrue) {
            return EVAL(list.get(2), env);
          }

          if (ret instanceof MalFalse) {
            return EVAL(list.get(3), env);
          }
        case "fn*":
          MalType f1 = list.get(1);
          MalType f2 = list.get(2);
          Env currentEnv = env;
          return new MalFun() {
            @Override
            public MalType apply(MalList args) {
              return EVAL(f2, new Env(currentEnv, ((MalList) f1), args));
            }
          };
      }
    }

    MalList ast = ((MalList) eval_ast(val, env));
    if (!(ast.get(0) instanceof MalFun)) {
      return ast;
    }
    return ((MalFun) ast.get(0)).apply(ast.rest());
  }


  static String PRINT(MalType val) {
    return printer.pr_str(val);
  }

  static String rep(String val, Env env) {
    return PRINT(EVAL(READ(val), env));
  }

  static MalType eval_ast(MalType ast, Env env) {
    if (ast instanceof MalSymbol) {
      return env.get((MalSymbol)ast);
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
    core.ns.forEach(env::set);

    List<String> coreCodes = new ArrayList<>();
    coreCodes.add("(def! not (fn* (a) (if a false true)))");

    for (String code : coreCodes) {
      rep(code, env);
    }

    while (true) {
      System.out.print("user> ");
      BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
      String line = buffer.readLine();
      if (line == null) {
        break;
      }

      try {
        System.out.println(rep(line, env));
      } catch (mal.env.NotFoundException e) {
        System.out.println(e.getMessage());
      } catch (reader.EOFException e) {
        System.out.println(e.msg);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}

