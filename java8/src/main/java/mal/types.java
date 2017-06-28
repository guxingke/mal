package mal;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gxk
 * @since 2017/6/28 ����7:17
 */
public class types {

}

class MalType {

}

class MalList extends MalType {
  protected List<MalType> malTypeList;
  protected String left = "(";
  protected String right = ")";

  MalList() {
    this.malTypeList = new ArrayList<>();
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

class MalQuote extends MalType {
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

class MalUnQuote extends MalType {
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

class MalDeref extends MalType {
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

class MalSpliceUnquote extends MalType {
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

class MalWithMeta extends MalType {
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


class MalQuasiQuote extends MalType {
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

class MalInt extends MalType {
  Integer value;

  MalInt(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value.toString();
  }
}

class MalSymbol extends MalType {
  String value;

  MalSymbol(String token) {
    this.value = token;
  }

  @Override
  public String toString() {
    return this.value;
  }
}

class MalAddSymbol extends MalType {
  @Override
  public String toString() {
    return "+";
  }
}

class MalSubSymbol extends MalType {
  @Override
  public String toString() {
    return "-";
  }
}

class MalMultiSymbol extends MalType {
  @Override
  public String toString() {
    return "*";
  }
}

class MalDivSymbol extends MalType {
  @Override
  public String toString() {
    return "/";
  }
}

class MalNil extends MalType {

}

class MalTrue extends MalType {

}

class MalFalse extends MalType {

}

class MalString extends MalType {
  String value;

  MalString(String value) {
    this.value = value;
  }
}
