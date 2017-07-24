package step3_env;

import java.util.HashMap;
import java.util.Map;

class env {

  static class Env implements mal {
    Env outer;
    Map<String, mal> data;

    Env(Env outer) {
      this.data = new HashMap<String, mal>();
      this.outer = outer;
    }

    Env set(String key, mal val) {
      data.put(key, val);
      return this;
    }

    mal find(String key) {
      if (data.containsKey(key)) {
        return this;
      }

      if (outer != null) {
        return outer.find(key);
      }

      return new nil();
    }

    mal get(String key) {
      mal env = this.find(key);
      if (env instanceof nil) {
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
