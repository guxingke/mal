package step3_env;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
}

class keyword extends str {
  keyword(String val) {
    super(val);
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
}

class nil implements mal {

  @Override
  public String toString() {
    return "nil";
  }
}

class mbool implements mal {
}

class mture extends mbool {
  @Override
  public String toString() {
    return "true";
  }
}

class mfalse extends mbool {
  @Override
  public String toString(boolean print_readably) {
    return "false";
  }
}

@FunctionalInterface
interface fun extends mal {
  mal apply(list args);
}
