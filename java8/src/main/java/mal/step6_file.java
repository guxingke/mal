package mal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mal.env.Env;

public class step6_file {

  static MalType READ(String val) {
    return reader.read_str(val);
  }

  static MalType EVAL(MalType val, Env env) {
    while (true) {
      if (!((val instanceof MalList))) {
        return eval_ast(val, env);
      }

      MalList ast = (MalList) val;
      if (ast.size() == 0) {
        return val;
      }

      String token = "_FN_";
      if (ast.get(0) instanceof MalSymbol) {
        token = ((MalSymbol) ast.get(0)).getValue();
      }

      switch (token) {
        case "def!":
          return env.set(((MalSymbol) ast.get(1)), EVAL(ast.get(2), env));
        case "let*":
          Env innerEnv = new Env(env);
          MalType binding = ast.get(1);
          if (!(binding instanceof MalList)) {
            throw new reader.EOFException("let* should be follow a ast");
          }
          MalList bindingList = (MalList) binding;

          for (int j = 0; j <= bindingList.size() / 2; j = j + 2) {
            innerEnv.set(((MalSymbol) bindingList.get(j)), EVAL(bindingList.get(j + 1), innerEnv));
          }
          val = ast.get(2);
          env = innerEnv;
          break;
        case "do":
          MalList rest = ast.rest();
          eval_ast(new MalList(new ArrayList<>(rest.malTypeList.subList(0, rest.size() - 1))), env);
          val = rest.get(rest.size() - 1);
          break;
        case "if":
          MalType bool = EVAL(ast.get(1), env);
          if (bool instanceof MalNil
              || bool instanceof MalFalse) {
            val = ast.get(3);
          } else {
            val = ast.get(2);
          }
          break;
        case "fn*":
          final MalList f1 = ((MalList) ast.get(1));
          final MalType f2 = ast.get(2);
          final Env currentEnv = env;
          return new MalFun(f2, env, f1) {
            @Override
            public MalType apply(MalList args) {
              return EVAL(f2, new Env(currentEnv, f1, args));
            }
          };
        default:
          MalList el= ((MalList) eval_ast(val, env));
          if (!(el.get(0) instanceof MalFun)) {
            System.out.println("UN EXPECT RETURN");
            return el;
          }

          MalFun fun = (MalFun) el.get(0);
          if (fun.ast == null || fun.ast instanceof MalNil) {
            return fun.apply(el.rest());
          }
          val = fun.ast;
          env = new Env(env, fun.params, el.rest());
      }
    }
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

    if (ast instanceof MalList) {
      MalList rets = new MalList();
      MalList list = (MalList) ast;
      if ("[".equals(list.left)) {
        rets = new MalVector();
      }
      for (int i = 0; i < list.size(); i++) {
        rets.add(EVAL(list.get(i), env));
      }
      return rets;
    }

    if (ast instanceof MalHashMap) {
      MalList innerList = ((MalHashMap) ast).list;
      Map<String, MalType> newMap = new HashMap<>();
      for (int i = 0; i < innerList.size()/2; i++) {
        newMap.put(
            ((MalString) innerList.get(i)).value,
            EVAL(innerList.get(i + 1), env)
        );
      }
      ((MalHashMap) ast).map = newMap;
    }
    return ast;
  }

  public static void main(String[] args) throws Exception {

    Env env = new Env(null);
    core.ns.forEach(env::set);

    // set eval
    final Env evalEnv = env;
    env.set(new MalSymbol("eval"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        return EVAL(args.get(0), evalEnv);
      }
    });

    List<String> coreCodes = new ArrayList<>();
    coreCodes.add("(def! not (fn* (a) (if a false true)))");
    coreCodes.add("(def! sum2 (fn* (n acc) (if (= n 0) acc (sum2 (- n 1) (+ n acc)))))");
    coreCodes.add("(def! load-file (fn* (f) (eval (read-string (str \"(do \" (slurp f) \")\" )))))");

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

