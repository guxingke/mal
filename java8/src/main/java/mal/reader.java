package mal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class reader {

  static MalType read_str(String val) {
    return read_form(new Reader(tokenizer(val)));
  }

  static List<String> tokenizer(String val) {
    List<String> tokens = new ArrayList<>();
    Pattern pattern = Pattern.compile("[\\s,]*(~@|[\\[\\]{}()'`~^@]|\"(?:\\\\.|[^\\\\\"])*\"|;.*|[^\\s\\[\\]{}('\"`,;)]*)");
    Matcher matcher = pattern.matcher(val);

    while (matcher.find()) {
      String token = matcher.group(1);
      if (token != null &&
          !token.equals("") &&
          !(token.charAt(0) == ';')) {
        tokens.add(token);
      }
    }

    return tokens;
  }

  static MalType read_form(Reader reader) {
    String token = reader.peek();
    if (token == null) {
      throw new EOFException("");
    }
    switch (token) {
      case "(":
      case "[":
      case "{":
        return read_list(reader);
      case "'":
        reader.next();
        return new MalQuote(read_form(reader));
      case "`":
        reader.next();
        return new MalQuasiQuote(read_form(reader));
      case "~":
        reader.next();
        return new MalUnQuote(read_form(reader));
      case "@":
        reader.next();
        return new MalDeref(read_form(reader));
      case "~@":
        reader.next();
        return new MalSpliceUnquote(read_form(reader));
      case "^":
        reader.next();
        MalType meta = read_form(reader);
        return new MalWithMeta(read_form(reader), meta);
      default:
        return read_atom(reader);
    }
  }

  static MalList read_list(Reader reader) {
    MalList malList = new MalList();
    String token = reader.next();
    if ("[".equals(token)) {
      malList = new MalMList();
    }

    if ("{".equals(token)) {
      malList = new MalLList();
    }

    while (!reader.peek().equals(malList.right)) {
      malList.add(read_form(reader));

      if (reader.peek() == null) {
        throw new EOFException(malList.right);
      }
    }

    reader.next();
    return malList;
  }

  static MalType read_atom(Reader reader) {
    String token = reader.next();
    Integer tryParse = null;
    try {
      tryParse = Integer.parseInt(token);
    } catch (NumberFormatException e) {
      // pass
    }

    if (tryParse != null) {
      return new MalInt(Integer.parseInt(token));
    }

    switch (token) {
      case "+":
        return new MalAddSymbol();
      case "-":
        return new MalSubSymbol();
      case "/":
        return new MalDivSymbol();
      case "*":
        return new MalMultiSymbol();
      case "true":
        return new MalTrue();
      case "false":
        return new MalFalse();
      case "nil":
        return new MalNil();
    }

    // "a"
    if (token.startsWith("\"")) {
      return new MalString(token.substring(1, token.length() - 1));
    }
    // ":a" , mal string for temporary
    if (token.startsWith(":")) {
      return new MalString(token);
    }

    return new MalSymbol(token);
  }

  static class Reader {
    List<String> tokens;
    int position;

    Reader(List<String> tokens) {
      this.tokens = tokens;
      this.position = 0;
    }

    String next() {
      if (position == tokens.size()) {
        return null;
      }
      String token = tokens.get(position);
      position++;
      return token;
    }

    String peek() {
      if (position == tokens.size()) {
        return null;
      }
      return tokens.get(position);
    }
  }

  static class EOFException extends RuntimeException {
    String msg;

    EOFException(String msg) {
      this.msg = msg;
    }
  }
}


