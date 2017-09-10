package stepA_mal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class types {
}

/**
 * the god, all mal type be impl this.
 */
interface mal{
  default String toString(boolean print_readably) {
    return this.toString();
  }
}

class list implements mal {
  final List<mal> data;

  list() {
    this.data = new ArrayList<>();
  }

  list(List<mal> data) {
    this.data = data;
  }

  mal add(mal item) {
    this.data.add(item);
    return item;
  }

  @Override
  public String toString() {
    if (data.isEmpty()) {
      return "()";
    }

    return data.stream()
        .map(mal -> mal.toString(true))
        .collect(Collectors.joining(" ", "(", ")"));
  }

  list rest() {
    if (this.data.size() <= 1) {
      return new list();
    }

    return new list(new ArrayList<>(this.data.subList(1, this.data.size())));
  }

  int size() {
    return this.data.size();
  }

  mal get(int index) {
    if (index >= this.data.size()) {
      return new nil();
    }
    return this.data.get(index);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof list)) {
      return false;
    }
    list target = (list) obj;


    if (target.size() != this.size()) {
      return false;
    }

    for (int i = 0; i < this.size(); i++) {
      if (!this.get(i).equals(target.get(i))) {
        return false;
      }
    }

    if ((target.data.isEmpty() && target.data.isEmpty())
        && !target.toString().equals(this.toString())) {
      return false;
    }

    return true;
  }
}

class vector extends list {
  vector() {
    super();
  }

  vector(List<mal> data) {
    super(data);
  }

  @Override
  public String toString() {
    if (data.isEmpty()) {
      return "[]";
    }

    return data.stream()
        .map(mal -> mal.toString(true))
        .collect(Collectors.joining(" ", "[", "]"));
  }
}

class symbol implements mal {
  final String val;

  symbol(String val) {
    this.val = val;
  }

  @Override
  public String toString() {
    return val;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof symbol && Objects.equals(((symbol) obj).val, this.val);
  }
}

class form extends list {
  final symbol key;
  final mal data;

  form(symbol key, mal data) {
    this.key = key;
    this.data = data;
    super.data.add(this.key);
    super.data.add(this.data);
  }

  @Override
  public String toString() {
    return new list(Arrays.asList(key, data)).toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof form)) {
      return false;
    }

    form form = (form) obj;
    return Objects.equals(form.key, this.key) && Objects.equals(form.data, this.data);
  }
}

class meta_form extends form {
  final mal meta;

  meta_form(mal data, mal meta) {
    super(new symbol("with-meta"), data);
    this.meta = meta;
  }

  @Override
  public String toString() {
    return new list(Arrays.asList(key, data, meta)).toString();
  }
}

class hash_map extends list {
  Map<String, mal> map = new HashMap<>();

  hash_map() {
    super();
  }

  hash_map(List<mal> data) {
    super(data);
    this.init();
  }

  private void init() {
    for (int i = 0; i < this.data.size(); i += 2) {
      mal mal = this.data.get(i);
      String key = null;
      if (mal instanceof keyword) {
        key = ((keyword) mal).val;
      } else if (mal instanceof str) {
        key = ((str) mal).val;
      }
      if (key == null) {
        continue;
      }
      map.put(key, this.data.get(i + 1));
    }
  }

  mal get(String keyword) {
    if (!this.map.containsKey(keyword)) {
      return new nil();
    }
    return this.map.get(keyword);
  }

  list keys() {
    List<mal> data = this.map.keySet().stream()
        .map(str::new).collect(Collectors.toList());
    return new list(data);
  }

  mal vals() {
    return new list(new ArrayList<>(this.map.values()));
  }

  bool contains(String keyword) {
    return this.map.containsKey(keyword) ? new True() : new False();
  }

  @Override
  public String toString() {
    if (data.isEmpty()) {
      return "{}";
    }

    return data.stream()
        .map(mal -> mal.toString(true))
        .collect(Collectors.joining(" ", "{", "}"));
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof hash_map)) {
      return false;
    }

    hash_map map = (hash_map) obj;

    for (String key : this.map.keySet()) {
      if (!map.map.containsKey(key)) {
        return false;
      }

      if (!this.map.get(key).equals(map.map.get(key))) {
        return false;
      }
    }

    return true;
  }
}

class str implements mal {
  final String val;

  str(String val) {
    this.val = val;
  }

  @Override
  public String toString() {
    return this.val;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof keyword) {
      return false;
    }
    return obj instanceof str && this.val.equals(((str) obj).val);
  }
}

class keyword extends str {
  keyword(String val) {
    super(val);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof keyword && this.val.equals(((keyword) obj).val);
  }

  @Override
  public int hashCode() {
    return this.val.hashCode();
  }
}

class number implements mal {
  final Integer val;

  number(Integer val) {
    this.val = val;
  }

  @Override
  public String toString() {
    return val.toString();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof number && Objects.equals(this.val, ((number) obj).val);
  }
}

class nil implements mal {

  @Override
  public String toString() {
    return "nil";
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof nil;
  }
}

class bool implements mal {
}

class True extends bool {

  @Override
  public String toString(boolean print_readably) {
    return this.toString();
  }

  @Override
  public String toString() {
    return "true";
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof True;
  }
}

class False extends bool {
  @Override
  public String toString(boolean print_readably) {
    return this.toString();
  }

  @Override
  public String toString() {
    return "false";
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof False;
  }
}

class atom implements mal {
  mal val;

  atom(mal val) {
    this.val = val;
  }

  @Override
  public String toString() {
    return "(atom " + val.toString() + ")";
  }
}

class mal_exception extends RuntimeException implements mal {
  mal_exception(String message) {
    super(message);
  }

  @Override
  public String toString() {
    return this.getMessage();
  }
}

@FunctionalInterface
interface ILambda {
  mal apply(list args);
}

@FunctionalInterface
interface fun extends ILambda, mal {
  @Override
  default String toString(boolean print_readably) {
    return "#<function>";
  }
}

abstract class fn implements mal, ILambda {
  mal ast;
  env.Env env;
  list args;
  boolean is_macro;

  fn() {
  }

  fn(mal ast, env.Env env, list args) {
    this.ast = ast;
    this.env = env;
    this.args = args;
  }

  @Override
  public String toString(boolean print_readably) {
    return toString();
  }

  @Override
  public String toString() {
    return "#<function>";
  }
}
