package mal;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
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

  MalList(List<MalType> malTypes) {
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
        ret = new MalMList();
      case "{":
        ret = new MalLList();
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

class MalQuote implements MalType {
  private String key;
  private MalType mal;

  MalQuote(MalType type) {
    this.mal = type;
    key = "quote";
  }

  @Override
  public String toString() {
    return "(" + key + " " + mal.toString() + ")";
  }
}

class MalUnQuote implements MalType {
  private String key;
  private MalType mal;

  MalUnQuote(MalType type) {
    this.mal = type;
    key = "unquote";
  }

  @Override
  public String toString() {
    return "(" + key + " " + mal.toString() + ")";
  }

}

class MalDeref implements MalType {
  private String key;
  private MalType mal;

  MalDeref(MalType type) {
    this.mal = type;
    key = "deref";
  }

  @Override
  public String toString() {
    return "(" + key + " " + mal.toString() + ")";
  }
}

class MalSpliceUnquote implements MalType {
  private String key;
  private MalType mal;

  MalSpliceUnquote(MalType type) {
    this.mal = type;
    key = "splice-unquote";
  }

  @Override
  public String toString() {
    return "(" + key + " " + mal.toString() + ")";
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

class MalQuasiQuote implements MalType {
  private String key;
  private MalType mal;

  MalQuasiQuote(MalType type) {
    this.mal = type;
    key = "quasiquote";
  }

  @Override
  public String toString() {
    return "(" + key + " " + mal.toString() + ")";
  }
}

class MalMList extends MalList {

  MalMList() {
    this.malTypeList = new ArrayList<>();
    this.left = "[";
    this.right = "]";
  }
}

class MalLList extends MalList {

  MalLList() {
    this.malTypeList = new ArrayList<>();
    this.left = "{";
    this.right = "}";
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

class MalSysSymbol extends MalSymbol {
  MalSysSymbol(String value) {
    super(value);
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

  @Override
  public String toString() {
    return "#<function>";
  }
}
