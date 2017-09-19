package stepA_mal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
          Long ret = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce(0L, (left, right) -> left + right);
          return new number(ret);
        }
    );

    ns.put(
        "-",
        (fun) args -> {
          Optional<Long> ret = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce((left, right) -> left - right);
          return new number(ret.get());
        }
    );

    ns.put(
        "/",
        (fun) args -> {
          Optional<Long> ret = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce((left, right) -> left / right);
          return new number(ret.get());
        }
    );

    ns.put(
        "*",
        (fun) args -> {
          Long ret = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce(1L, (left, right) -> left * right);
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
            return new number(0L);
          }
          return new number(((long) ((list) args.get(0)).size()));
        }
    );

    ns.put(
        "=",
        (fun) args -> args.get(0).equals(args.get(1)) ? new True() : new False()
    );

    ns.put(
        "<",
        (fun) args -> {
          Optional<Long> reduce = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce((left, right) -> {
                if (left < right) {
                  return right;
                }
                return Long.MAX_VALUE;
              });
          return reduce.get() == Long.MAX_VALUE ? new False() : new True();
        }
    );

    ns.put(
        "<=",
        (fun) args -> {
          Optional<Long> reduce = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce((left, right) -> {
                if (left <= right) {
                  return right;
                }
                return Long.MAX_VALUE;
              });
          return reduce.get() == Long.MAX_VALUE ? new False() : new True();
        }
    );

    ns.put(
        ">",
        (fun) args -> {
          Optional<Long> reduce = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce((left, right) -> {
                if (left > right) {
                  return right;
                }
                return Long.MIN_VALUE;
              });
          return reduce.get() == Long.MIN_VALUE ? new False() : new True();
        }
    );

    ns.put(
        ">=",
        (fun) args -> {
          Optional<Long> reduce = args.data.stream()
              .map(item -> ((number) item).val)
              .reduce((left, right) -> {
                if (left >= right) {
                  return right;
                }
                return Long.MIN_VALUE;
              });
          return reduce.get() == Long.MIN_VALUE ? new False() : new True();
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
          return f1.get(f2.val.intValue());
        }
    );

    ns.put(
        "first",
        (fun) args -> {
          mal f1 = args.get(0);
          if (f1 instanceof nil) {
            return new nil();
          }
          list ml = ((list) f1);
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

    ns.put(
        "throw",
        (fun) args -> {
          throw new mal_exception(args.get(0).toString(false));
        }
    );

    ns.put(
        "apply",
        (fun) args -> {
          mal func = args.get(0);

          list params = new list();
          for (mal item : args.rest().data) {
            if (item instanceof list) {
              for (mal innerItem : ((list) item).data) {
                params.add(innerItem);
              }
            } else {
              params.add(item);
            }
          }

          if (func instanceof fun) {
            return ((fun) func).apply(params);
          }

          return ((fn) func).apply(params);
        }
    );

    ns.put(
        "map",
        (fun) args -> {
          mal func = args.get(0);

          List<mal> rets = ((list) args.rest().get(0)).data.stream()
              .map(it -> {
                    list params = new list();
                    params.add(it);
                    if (func instanceof fun) {
                      return ((fun) func).apply(params);
                    }

                    return ((fn) func).apply(params);
                  }
              ).collect(Collectors.toList());
          return new list(rets);
        }
    );

    ns.put(
        "nil?",
        (fun) args -> {
          return args.get(0) instanceof nil ? new True() : new False();
        }
    );

    ns.put(
        "true?",
        (fun) args -> {
          return args.get(0) instanceof True ? new True() : new False();
        }
    );

    ns.put(
        "false?",
        (fun) args -> {
          return args.get(0) instanceof False ? new True() : new False();
        }
    );

    ns.put(
        "symbol?",
        (fun) args -> {
          return args.get(0) instanceof symbol ? new True() : new False();
        }
    );

    ns.put(
        "symbol",
        (fun) args -> {
          return new symbol(args.get(0).toString());
        }
    );

    ns.put(
        "keyword?",
        (fun) args -> {
          if (args.get(0) instanceof str && ((str) args.get(0)).val.startsWith(":")) {
            return new True();
          }
          return args.get(0) instanceof keyword ? new True() : new False();
        }
    );

    ns.put(
        "keyword",
        (fun) args -> {
          if (args.get(0) instanceof keyword) {
            return args.get(0);
          }

          return new keyword(":" + args.get(0).toString());
        }
    );

    ns.put(
        "vector",
        (fun) args -> {
          return new vector(args.data);
        }
    );

    ns.put(
        "vector?",
        (fun) args -> {
          return args.get(0) instanceof vector ? new True() : new False();
        }
    );

    ns.put(
        "hash-map",
        (fun) args -> {
          return new hash_map(args.data);
        }
    );

    ns.put(
        "map?",
        (fun) args -> {
          return args.get(0) instanceof hash_map ? new True() : new False();
        }
    );

    ns.put(
        "keys",
        (fun) args -> {
          hash_map map = (hash_map) args.get(0);
          return map.keys();
        }
    );

    ns.put(
        "vals",
        (fun) args -> {
          hash_map map = (hash_map) args.get(0);
          return map.vals();
        }
    );

    ns.put(
        "get",
        (fun) args -> {
          if (!(args.get(0) instanceof hash_map)) {
            return new nil();
          }
          return ((hash_map) args.get(0)).get(args.get(1).toString());
        }
    );

    ns.put(
        "contains?",
        (fun) args -> {
          return ((hash_map) args.get(0)).contains(args.get(1).toString());
        }

    );

    ns.put(
        "sequential?",
        (fun) args -> {
          String s = args.get(0).toString();
          return s.startsWith("(") || s.startsWith("[") ? new True() : new False();
        }
    );

    ns.put(
        "assoc",
        (fun) args -> {
          hash_map map = (hash_map) args.get(0);

          List<mal> data = new ArrayList<>(map.data);

          data.addAll(args.rest().data);
          return new hash_map(data);
        }
    );

    ns.put(
        "dissoc",
        (fun) args -> {
          hash_map map = (hash_map) args.get(0);
          Map<String, mal> data = new HashMap<String, mal>(map.map);

          for (mal key : args.rest().data) {
            if (data.containsKey(key.toString())) {
              data.remove(key.toString());
            }
          }
          List<mal> dd = new ArrayList<mal>();
          data.forEach((key, val)->{
            if (!key.startsWith(":")) {
              dd.add(new str(key));
            } else {
              dd.add(new keyword(key));
            }
            dd.add(val);
          });

          return new hash_map(dd);
        }
    );

    ns.put(
        "readline",
        (fun) args -> {
          String prompt = ((str) args.get(0)).val;

          System.out.print(prompt);
          BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
          String line= null;
          try {
            line = buffer.readLine();
          } catch (IOException e) {
            e.printStackTrace();
          }
          if (line == null) {
            return new nil();
          }
          return new str(line);
        }
    );

    ns.put(
        "meta",
        (fun) args -> {
          mal f1 = args.get(0);
          if (!(f1 instanceof mv)) {
            return new nil();
          }
          mv meta = ((mv) f1).meta;
          if (meta == null) {
            return new nil();
          }
          return meta;
        }
    );

    ns.put(
        "with-meta",
        (fun) args -> {
          mv copy = ((mv) args.get(0)).copy();
          copy.meta = ((mv) args.get(1));
          return copy;
        }
    );

    ns.put(
        "string?",
        (fun) args -> {
          mal f1 = args.get(0);
          return f1 instanceof str && !((str) f1).val.startsWith(":")? new True() : new False();
        }
    );

    ns.put(
        "conj",
        (fun) args -> {
          mv copy = ((mv) args.get(0)).copy();

          if (copy instanceof vector) {
            for (int i = 1; i < args.data.size(); i++) {
              ((vector)copy).data.add(args.get(i));
            }
            return copy;
          }

          for (int i = 1; i < args.data.size(); i++) {
            ((list)copy).data.add(0, args.get(i));
          }

          return copy;
        }
    );

    // 接受一个 列表, 向量, 字符串或者 nil。
    // 如果传入的是空列表，空向量，或空字符串("")，则返回 nil，
    // 否则，
    // 如果为列表，则原样返回，
    // 如果是向量，则转换为列表并返回；
    // 如果是字符串，则将字符串切分为单个字符的字符串列表并返回。
    ns.put(
        "seq",
        (fun) args -> {
          mal f1 = args.get(0);
          if (f1 instanceof nil) {
            return new nil();
          }

          if (f1 instanceof str) {
            String val = ((str) f1).val;
            if (val.isEmpty()) {
              return new nil();
            }
            List<mal> strs = new ArrayList<>();
            for (int i = 0; i < val.length(); i++) {
              strs.add(new str(String.valueOf(val.charAt(i))));
            }

            return new list(strs);
          }

          list list = (list) f1;
          if (list.size() == 0) {
            return new nil();
          }

          return new list(list.data);
        }
    );

    ns.put(
        "time-ms",
        (fun) args -> {
          return new number((System.currentTimeMillis()));
        }
    );
  }

  public static class OutOfIndexException extends RuntimeException {
  }
}