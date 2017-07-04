package mal;

import java.util.HashMap;
import java.util.Map;

class env {
  static class Env implements MalType {
    Env outer;
    Map<MalSymbol, MalType> data;

    Env(Env outer) {
      this.data = new HashMap<>();
      this.outer = outer;
    }

    MalType set(MalSymbol key, MalType value) {
      if (key == null || value == null) {
        throw new IllegalArgumentException();
      }

      data.put(key, value);
      return this;
    }

    MalType find(MalSymbol key) {
      if (data.containsKey(key)) {
        return this;
      }

      if (outer != null) {
        return outer.find(key);
      }

      return new MalNil();
    }

    MalType get(MalSymbol key) {
      MalType env = this.find(key);
      if (env instanceof MalNil) {
        throw new NotFoundException("symbol not found");
      }

      Env realEnv = (Env) env;

      return realEnv.data.get(key);
    }
  }

  static class NotFoundException extends RuntimeException {
    NotFoundException(String msg) {
      super(msg);
    }
  }
}
