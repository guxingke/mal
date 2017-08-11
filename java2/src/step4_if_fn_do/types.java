package step4_if_fn_do;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
}

class form implements mal {
  final symbol key;
  final mal data;

  form(symbol key, mal data) {
    this.key = key;
    this.data = data;
  }

  @Override
  public String toString() {
    return new list(Arrays.asList(key, data)).toString();
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

  hash_map() {
    super();
  }

  hash_map(List<mal> data) {
    super(data);
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
