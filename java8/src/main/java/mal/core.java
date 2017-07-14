package mal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author gxk
 * @since 2017/7/6 下午4:34
 */
public class core {
  static final Map<MalSymbol, MalFun> ns = new HashMap<>();
  static {
    ns.put(new MalAddSymbol(), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        MalInt ret = (MalInt) args.get(0);
        for (int i = 1; i < args.size(); i++) {
          ret = MalInt.add(ret, ((MalInt) args.get(i)));
        }
        return ret;
      }
    });

    ns.put(new MalSubSymbol(), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        MalInt ret = (MalInt) args.get(0);
        for (int i = 1; i < args.size(); i++) {
          ret = MalInt.sub(ret, ((MalInt) args.get(i)));
        }
        return ret;
      }
    });

    ns.put(new MalMultiSymbol(), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        MalInt ret = (MalInt) args.get(0);
        for (int i = 1; i < args.size(); i++) {
          ret = MalInt.multi(ret, ((MalInt) args.get(i)));
        }
        return ret;
      }
    });

    ns.put(new MalDivSymbol(), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        MalInt ret = (MalInt) args.get(0);
        for (int i = 1; i < args.size(); i++) {
          ret = MalInt.div(ret, ((MalInt) args.get(i)));
        }
        return ret;
      }
    });

    ns.put(new MalSymbol("prn"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        if (args.size() == 0) {
          System.out.println();
          return new MalNil();
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
          sb.append(printer.pr_str(args.get(i)));
          if (i != args.size() - 1) {
            sb.append(" ");
          }
        }
        System.out.println(sb.toString());
        return new MalNil();
      }
    });

    ns.put(new MalSymbol("str"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        if (args.size() == 0) {
          return new MalNil();
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
          sb.append(printer.pr_str(args.get(i)));
          if (i != args.size() - 1) {
            sb.append(" ");
          }
        }
        return new MalString(sb.toString());
      }
    });

    ns.put(new MalSymbol("list"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        return args;
      }
    });

    ns.put(new MalSymbol("list?"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        return args.get(0) instanceof MalList ? new MalTrue() : new MalFalse();
      }
    });

    ns.put(new MalSymbol("empty?"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        return args.get(0) instanceof MalList && ((MalList) args.get(0)).size() == 0 ? new MalTrue() : new MalFalse();
      }
    });

    ns.put(new MalSymbol("count"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        return args.get(0) instanceof MalList ? new MalInt(((MalList) args.get(0)).size()) : new MalInt(0);
      }
    });

    ns.put(new MalSymbol("="), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        return args.get(0).equals(args.get(1)) ? new MalTrue() : new MalFalse();
      }
    });

    ns.put(new MalSymbol("<"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        MalBool ret = new MalTrue();
        for (int i = 0; i < args.size()-1; i++) {
          ret = and(ret, MalInt.lt(((MalInt) args.get(i)), ((MalInt) args.get(i + 1))));
        }
        return ret;
      }
    });

    ns.put(new MalSymbol("<="), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        MalBool ret = new MalTrue();
        for (int i = 0; i < args.size()-1; i++) {
          ret = and(ret, MalInt.lte(((MalInt) args.get(i)), ((MalInt) args.get(i + 1))));
        }
        return ret;
      }
    });

    ns.put(new MalSymbol(">"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        MalBool ret = new MalTrue();
        for (int i = 0; i < args.size()-1; i++) {
          ret = and(ret, MalInt.gt(((MalInt) args.get(i)), ((MalInt) args.get(i + 1))));
        }
        return ret;
      }
    });

    ns.put(new MalSymbol(">="), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        MalBool ret = new MalTrue();
        for (int i = 0; i < args.size() - 1; i++) {
          ret = and(ret, MalInt.gte(((MalInt) args.get(i)), ((MalInt) args.get(i + 1))));
        }
        return ret;
      }
    });

    ns.put(new MalSymbol("read-string"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        return reader.read_str(((MalString) args.get(0)).value);
      }
    });

    ns.put(new MalSymbol("slurp"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        String name = ((MalString) args.get(0)).value;
        try {
          return new MalString(new Scanner(new File(name)).useDelimiter("\\Z").next() + "\n");
        } catch (FileNotFoundException e) {
          throw new RuntimeException(e.getMessage());
        }
      }
    });

    ns.put(new MalSymbol("atom"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        return new MalAtom(args.get(0));
      }
    });

    ns.put(new MalSymbol("atom?"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        return args.get(0) instanceof MalAtom ? new MalTrue() : new MalFalse();
      }
    });

    ns.put(new MalSymbol("deref"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        return ((MalAtom) args.get(0)).value;
      }
    });

    ns.put(new MalSymbol("reset!"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        return ((MalAtom) args.get(0)).value = args.get(1);
      }
    });

    ns.put(new MalSymbol("swap!"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        MalAtom atm = (MalAtom) args.get(0);
        MalFun f = (MalFun) args.get(1);
        MalList rest = args.rest().rest();
        rest.malTypeList.add(0, atm.getValue());

        atm.value = f.apply(rest);
        return atm.value;
      }
    });

    ns.put(new MalSymbol("cons"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        MalType f1 = args.get(0);
        MalList f2 = (MalList) args.get(1);

        List<MalType> target = new ArrayList<>();
        target.add(f1);
        target.addAll(f2.malTypeList);

        return new MalList(target);
      }
    });

    ns.put(new MalSymbol("concat"), new MalFun() {
      @Override
      public MalType apply(MalList args) {
        List<MalType> targets = new ArrayList<>();
        for (MalType malType : args.malTypeList) {
          targets.addAll(((MalList) malType).malTypeList);
        }
        return new MalList(targets);
      }
    });
  }

  static MalBool and(MalBool a, MalBool b) {
    if (a instanceof MalTrue && b instanceof MalTrue) {
      return new MalTrue();
    }
    return new MalFalse();
  }

  static MalBool or(MalBool a, MalBool b) {
    if (a instanceof MalFalse && b instanceof MalFalse) {
      return new MalFalse();
    }
    return new MalTrue();
  }
}
