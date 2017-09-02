package step8_macros;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

class core {
  static Map<String, mal> ns = new HashMap<>();

  static {
    ns.put(
        "+",
        (fun) args -> {
          Integer ret = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce(0, (left, right) -> left + right);
          return new number(ret);
        }
    );

    ns.put(
        "-",
        (fun) args -> {
          Optional<Integer> ret = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce((left, right) -> left - right);
          return new number(ret.get());
        }
    );

    ns.put(
        "/",
        (fun) args -> {
          Optional<Integer> ret = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce((left, right) -> left / right);
          return new number(ret.get());
        }
    );

    ns.put(
        "*",
        (fun) args -> {
          Integer ret = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce(1, (left, right) -> left * right);
          return new number(ret);
        }
    );

    ns.put(
        "prn",
        (fun) args -> {
          mal f1 = args.get(0);
          System.out.println(f1.toString(true));
          return new nil();
        }
    );

    ns.put(
        "list",
        (fun) args -> args
    );

    ns.put(
        "list?",
        (fun) args -> args.get(0) instanceof list && !(args.get(0) instanceof vector) && !(args.get(0) instanceof hash_map) ? new True() : new False()
    );

    ns.put(
        "empty?",
        (fun) args -> args.get(0) instanceof list && ((list) args.get(0)).size() == 0 ? new True() : new False()
    );

    ns.put(
        "count",
        (fun) args -> {
          if (!(args.get(0) instanceof list)) {
            return new number(0);
          }
          return new number(((list) args.get(0)).size());
        }
    );

    ns.put(
        "=",
        (fun) args -> args.get(0).equals(args.get(1)) ? new True() : new False()
    );

    ns.put(
        "<",
        (fun) args -> {
          Optional<Integer> reduce = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce((left, right) -> {
                if (left < right) {
                  return right;
                }
                return Integer.MAX_VALUE;
              });
          return reduce.get() == Integer.MAX_VALUE ? new False() : new True();
        }
    );

    ns.put(
        "<=",
        (fun) args -> {
          Optional<Integer> reduce = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce((left, right) -> {
                if (left <= right) {
                  return right;
                }
                return Integer.MAX_VALUE;
              });
          return reduce.get() == Integer.MAX_VALUE ? new False() : new True();
        }
    );

    ns.put(
        ">",
        (fun) args -> {
          Optional<Integer> reduce = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce((left, right) -> {
                if (left > right) {
                  return right;
                }
                return Integer.MIN_VALUE;
              });
          return reduce.get() == Integer.MIN_VALUE ? new False() : new True();
        }
    );

    ns.put(
        ">=",
        (fun) args -> {
          Optional<Integer> reduce = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce((left, right) -> {
                if (left >= right) {
                  return right;
                }
                return Integer.MIN_VALUE;
              });
          return reduce.get() == Integer.MIN_VALUE ? new False() : new True();
        }
    );

    ns.put(
        "pr-str",
        (fun) args -> new str(args.data
            .stream()
            .map(it -> printer.pr_str(it, true))
            .collect(Collectors.joining(" ")))
    );

    ns.put(
        "str",
        (fun) args -> new str(args.data
            .stream()
            .map(it -> printer.pr_str(it, false))
            .collect(Collectors.joining("")))
    );

    ns.put(
        "prn",
        (fun) args -> {
          System.out.println(args.data
              .stream()
              .map(it -> printer.pr_str(it, true))
              .collect(Collectors.joining(" ")));
          return new nil();
        }
    );

    ns.put(
        "println",
        (fun) args -> {
          System.out.println(args.data
              .stream()
              .map(it -> printer.pr_str(it, false))
              .collect(Collectors.joining(" ")));
          return new nil();
        }
    );

    ns.put(
        "read-string",
        (fun) args -> {
          return reader.read_str(((str) args.get(0)).val);
        }
    );

    ns.put(
        "slurp",
        (fun) args -> {
          String name = ((str) args.get(0)).val;
          try {
            return new str(new Scanner(new File(name)).useDelimiter("\\Z").next() + "\n");
          } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
          }
        }
    );

    ns.put(
        "atom",
        (fun) args -> {
          return new atom(args.get(0));
        }
    );

    ns.put(
        "atom?",
        (fun) args -> {
          return args.get(0) instanceof atom ? new True() : new False();
        }
    );

    ns.put(
        "deref",
        (fun) args -> {
          return ((atom) args.get(0)).val;
        }
    );

    ns.put(
        "reset!",
        (fun) args -> {
          return ((atom) args.get(0)).val = args.get(1);
        }
    );

    ns.put(
        "swap!",
        (fun) args -> {
          atom atom = (atom) args.get(0);
          list rest = args.rest().rest();
          rest.data.add(0, atom.val);

          if (args.get(1) instanceof fun) {
            fun fun = (fun) args.get(1);
            atom.val = fun.apply(rest);
            return atom.val;
          }
          fn fn = (fn) args.get(1);

          atom.val = fn.apply(rest);
          return atom.val;
        }
    );

    ns.put(
        "cons",
        (fun) args -> {
          mal f1 = args.get(0);
          list f2 = (list) args.get(1);

          List<mal> target = new ArrayList<>();
          target.add(f1);
          target.addAll(f2.data);
          return new list(target);
        }
    );

    ns.put(
        "concat",
        (fun) args -> {
          List<mal> targets = new ArrayList<>();
          for (mal malType : args.data) {
            targets.addAll(((list) malType).data);
          }
          return new list(targets);
        }
    );

    ns.put(
        "nth",
        (fun) args -> {
          list f1 = (list) args.get(0);
          number f2 = (number) args.get(1);
          if (f1.size() <= f2.val) {
            throw new core.OutOfIndexException();
          }
          return f1.get(f2.val);
        }
    );

    ns.put(
        "first",
        (fun) args -> {
          mal f1 = args.get(0);
          if (f1 instanceof nil) {
            return new nil();
          }
          list ml = ((list)f1);
          return ml.size() > 0 ? ml.get(0) : new nil();
        }
    );

    ns.put(
        "rest",
        (fun) args -> {
          mal f1 = args.get(0);
          if (!(f1 instanceof list)) {
            return new list();
          }

          list list = (list) f1;
          if (list.size() == 0) {
            return new list();
          }

          return ((list) args.get(0)).rest();
        }
    );
  }

  public static class OutOfIndexException extends RuntimeException {
  }
}
