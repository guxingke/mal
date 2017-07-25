package step4_if_fn_do;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
  }
}
