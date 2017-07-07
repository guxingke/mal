package mal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class env {
  static class Env implements MalType {
    Env outer;
    Map<String, MalType> data;

    Env(Env outer) {
      this.data = new HashMap<>();
      this.outer = outer;
    }

    Env(Env outer, MalList binds, MalList exprs) {
      this.data = new HashMap<>();
      this.outer = outer;


      for (int i = 0; i < binds.size(); i++) {

        String token = ((MalSymbol) binds.get(i)).getValue();
        if (token.equals("&")) {
          MalSymbol key = (MalSymbol) binds.get(i + 1);

          if (exprs.size() < i) {
            break;
          }
          List<MalType> malTypes = new ArrayList<>(exprs.malTypeList.subList(i, exprs.malTypeList.size()));
          MalList value = new MalList(malTypes);
          this.data.put(key.getValue(), value);
          break;
        }
        this.data.put(token, exprs.get(i));
      }
    }

    MalType set(MalSymbol key, MalType value) {
      if (key == null || value == null) {
        throw new IllegalArgumentException();
      }

      data.put(key.getValue(), value);
      return value;
    }

    MalType find(MalSymbol key) {
      if (data.containsKey(key.getValue())) {
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

      return realEnv.data.get(key.getValue());
    }
  }

  static class NotFoundException extends RuntimeException {
    NotFoundException(String msg) {
      super(msg);
    }
  }
}
