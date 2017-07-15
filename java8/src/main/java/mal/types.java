package mal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mal.env.Env;

public class types {

}

interface MalType {
}

class MalList implements MalType {
  List<MalType> malTypeList;
  String left = "(";
  String right = ")";

  MalList() {
    this.malTypeList = new ArrayList<>();
  }

  MalList(List malTypes) {
    this.malTypeList = malTypes;
    if (malTypeList == null) {
      this.malTypeList = new ArrayList<>();
    }
  }

  final int size() {
    return this.malTypeList.size();
  }

  MalType get(int index) {
    if (this.malTypeList.size() < index + 1) {
      return new MalNil();
    }
    return this.malTypeList.get(index);
  }

  void add(MalType malType) {
    this.malTypeList.add(malType);
  }

  MalList rest() {
    MalList ret = new MalList();
    switch (this.left) {
      case "[":
        ret = new MalVector();
    }

    if (this.size() == 1) {
      return ret;
    }

    ret.malTypeList = new ArrayList<>(malTypeList.subList(1, this.size()));
    return ret;
  }

  MalList append(MalList another) {
    this.malTypeList.addAll(another.malTypeList);
    return this;
  }

  @Override
  public String toString() {
    return malTypeList.stream()
        .map(MalType::toString)
        .collect(Collectors.joining(" ", this.left, this.right));
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MalList)) {
      return false;
    }

    MalList target = (MalList) obj;

    if (target.size() != this.size()) {
      return false;
    }

    for (int i = 0; i < target.size(); i++) {
      if (!Objects.equals(target.get(i), this.get(i))) {
        return false;
      }
    }
    return true;
  }
}

class MalQuote extends MalList {
  MalQuote(MalType type) {
    this.left = "(quote";
    this.right = ")";
    this.malTypeList = new ArrayList<>();
    this.malTypeList.add(new MalSymbol("quote"));

    this.malTypeList.add(type);
  }

  @Override
  public String toString() {
    return this.malTypeList.get(1).toString();
  }
}

class MalUnQuote extends MalList {
  MalUnQuote(MalType type) {
    super();
    this.left = "(unquote";
    this.right = ")";

    this.malTypeList.add(new MalSymbol("unquote"));
    this.malTypeList.add(type);
  }

  @Override
  public String toString() {
    return this.malTypeList.get(1).toString();
  }
}

class MalDeref extends MalList {

  MalDeref(MalType type) {
    super();
    this.left = "(deref";
    this.right = ")";
    this.malTypeList.add(new MalSymbol("deref"));
    if (type instanceof MalList) {
      this.malTypeList.addAll(((MalList) type).malTypeList);
    } else {
      this.malTypeList.add(type);
    }
  }

}

class MalSpliceUnquote extends MalList {
  MalSpliceUnquote(MalType type) {
    super();
    this.left = "(splice-unquote";
    this.right = ")";

    this.malTypeList.add(new MalSymbol("splice-unquote"));
    this.malTypeList.add(type);
  }

  @Override
  public String toString() {
    return this.malTypeList.get(1).toString();
  }
}

class MalWithMeta implements MalType {
  MalType meta;
  MalType mal;
  String key;

  MalWithMeta(MalType mal, MalType meta) {
    this.mal = mal;
    this.meta = meta;
    this.key = "with-meta";
  }

  @Override
  public String toString() {
    return "(" + key + " " + mal.toString() + " " + meta.toString() + ")";
  }

}

class MalQuasiQuote extends MalList {

  MalQuasiQuote(MalType type) {
    super();
    this.left = "(quasiquote";
    this.right = ")";

    this.malTypeList.add(new MalSymbol("quasiquote"));
    this.malTypeList.add(type);
  }

  @Override
  public String toString() {
    return this.malTypeList.get(1).toString();
  }
}

class MalVector extends MalList {

  MalVector() {
    this.malTypeList = new ArrayList<>();
    this.left = "[";
    this.right = "]";
  }
}

class MalHashMap implements MalType {
  MalList list;
  Map<String, MalType> map = new HashMap<>();

  MalHashMap(MalList list) {
    list.left = "{";
    list.right = "}";
    this.list = list;

    for (int i = 0; i < list.size() / 2; i++) {
      map.put(((MalString) list.get(i)).value, list.get(i + 1));
    }
  }

  @Override
  public String toString() {
    return this.list.toString();
  }

  public int size() {
    return map.size();
  }

  public MalType get(MalSymbol key) {
    if (!map.containsKey(key.getValue())) {
      throw new env.NotFoundException("hash-map not contains this key, " + key.getValue());
    }

    return map.get(key.getValue());
  }
}

@Getter
class MalInt implements MalType {
  Integer value;

  MalInt(int value) {
    this.value = value;
  }

  public static MalInt add(MalInt a, MalInt b) {
    return new MalInt(a.value + b.value);
  }

  public static MalInt sub(MalInt a, MalInt b) {
    return new MalInt(a.value - b.value);
  }

  public static MalInt multi(MalInt a, MalInt b) {
    return new MalInt(a.value * b.value);
  }

  public static MalInt div(MalInt a, MalInt b) {
    return new MalInt(a.value / b.value);
  }

  public static MalBool lt(MalInt a, MalInt b) {
    return sub(a, b).getValue() < 0 ? new MalTrue() : new MalFalse();
  }

  public static MalBool lte(MalInt a, MalInt b) {
    return sub(a, b).getValue() <= 0 ? new MalTrue() : new MalFalse();
  }

  public static MalBool gt(MalInt a, MalInt b) {
    return sub(a, b).getValue() > 0 ? new MalTrue() : new MalFalse();
  }

  public static MalBool gte(MalInt a, MalInt b) {
    return sub(a, b).getValue() >= 0 ? new MalTrue() : new MalFalse();
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MalInt)) {
      return false;
    }

    return Objects.equals(((MalInt) obj).getValue(), this.getValue());
  }
}

@Data
@AllArgsConstructor
class MalSymbol implements MalType {
  String value;

  @Override
  public String toString() {
    return this.value;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MalSymbol)) {
      return false;
    }
    return ((MalSymbol) obj).getValue().equals(this.getValue());
  }
}

class MalAddSymbol extends MalSymbol {
  MalAddSymbol() {
    super("+");
  }
}

class MalSubSymbol extends MalSymbol {
  MalSubSymbol() {
    super("-");
  }
}

class MalMultiSymbol extends MalSymbol {
  MalMultiSymbol() {
    super("*");
  }
}

class MalDivSymbol extends MalSymbol {
  MalDivSymbol() {
    super("/");
  }
}

class MalNil implements MalType {
  @Override
  public String toString() {
    return "nil";
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof MalNil;
  }
}

abstract class MalBool implements MalType {
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MalBool)) {
      return false;
    }

    if (this instanceof MalTrue && obj instanceof MalTrue) {
      return true;
    }

    if (this instanceof MalFalse && obj instanceof MalFalse) {
      return true;
    }

    return false;
  }
}

class MalTrue extends MalBool {

  @Override
  public String toString() {
    return "true";
  }
}

class MalFalse extends MalBool {

  @Override
  public String toString() {
    return "false";
  }
}

class MalString implements MalType {
  String value;

  MalString(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MalString)) {
      return false;
    }

    return ((MalString) obj).value.equals(this.value);
  }

}

@Data
@AllArgsConstructor
class MalAtom implements MalType {
  MalType value;

  @Override
  public String toString() {
    return "(atom " + value.toString() + ")";
  }
}

@FunctionalInterface
interface ILambda {
  MalType apply(MalList args);
}

@AllArgsConstructor
@NoArgsConstructor
abstract class MalFun implements MalType, ILambda {
  public MalType ast;
  public Env env;
  public MalList params;
  public boolean isMacro;

  public MalFun(MalType ast, Env env, MalList params) {
    this.ast = ast;
    this.env = env;
    this.params = params;
  }

  @Override
  public String toString() {
    if (ast == null || ast instanceof MalNil) {
      return "#<function>";
    }
    return "#<function> " + ast.toString();
  }
}
