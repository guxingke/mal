package mal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

import mal.env.Env;

public class step4_if_fn_do {

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
        MalSymbol symbol = (MalSymbol) ast.get(0);
        switch (symbol.getValue()) {
          case "def!":
            return env.set(((MalSymbol) ast.get(1)), EVAL(ast.get(2), env));
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
            MalList malList = (MalList) eval_ast(ast.rest(), env);
            return malList.get(malList.size() - 1);
          case "if":
            MalType bool = EVAL(list.get(1), env);
            if (!(bool instanceof MalBool)) {
              throw new reader.EOFException("un expect op");
            }

            if (bool instanceof MalTrue) {
              return EVAL(list.get(2), env);
            }

            if (bool instanceof MalFalse) {
              return EVAL(list.get(3), env);
            }

            return new MalNil();
          case "fn*":
            MalType f1 = ast.get(1);
            MalType f2 = ast.get(2);
            Env currentEnv = env;
            return new MalFun() {
              @Override
              public MalType apply(MalList args) {
                return EVAL(f2, new Env(currentEnv, ((MalList) f1), args));
              }
            };
        }
      }

      if (!(ast.get(0) instanceof MalFun)) {
        return ast;
      }
      return ((MalFun) ast.get(0)).apply(ast.rest());
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
    if (ast instanceof MalSysSymbol) {
      return ast;
    }

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
        rets = new MalMList();
      }

      if ("{".equals(list.left)) {
        rets = new MalLList();
      }

      for (int i=0; i< list.size(); i++) {
        if (i == 0) {
          MalType index0 = EVAL(list.get(i), env);
          rets.add(index0);

          if (index0 instanceof MalSymbol) {
            String value = ((MalSymbol) index0).getValue();
            switch (value) {
              case "def!":
                i++;
                rets.add(list.get(i));
                continue;
              case "let*":
                i++;
                rets.add(list.get(i));
                i++;
                rets.add(list.get(i));
                continue;
              case "fn*":
                i++;
                rets.add(list.get(i));
                i++;
                rets.add(list.get(i));
                continue;
              case "do":
                MalList rest = list.rest();
                rets.append(rest);
                i += rest.size();
                continue;
              case "if":
                MalList ifRest = list.rest();
                rets.append(ifRest);
                i += ifRest.size();
                continue;
            }
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
    core.ns.forEach(env::set);

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
}

