package mal;

import java.util.HashMap;
import java.util.Map;

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
        System.out.println(args.get(0));
        return new MalNil();
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

    ns.put(new MalSymbol("empty?"), new MalFun() {
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
