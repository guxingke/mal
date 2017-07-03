package mal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;

public class types {

}

interface MalType {
}

class MalList implements MalType {
  protected List<MalType> malTypeList;
  protected String left = "(";
  protected String right = ")";

  MalList() {
    this.malTypeList = new ArrayList<>();
  }

  public final int size() {
    return this.malTypeList.size();
  }

  public MalType get(int index) {
    return this.malTypeList.get(index);
  }

  protected void add(MalType malType) {
    this.malTypeList.add(malType);
  }

  @Override
  public String toString() {
    return malTypeList.stream()
        .map(MalType::toString)
        .collect(Collectors.joining(" ", this.left, this.right));
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

  @Override
  public String toString() {
    return value.toString();
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

}

interface MalBool extends MalType {

}

class MalTrue implements MalBool {

}

class MalFalse implements MalBool {

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
}
