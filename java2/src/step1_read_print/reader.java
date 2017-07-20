package step1_read_print;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class reader {

  static mal read_str(String val) {
    return read_form(new Reader(tokenizer(val)));
  }

  static mal read_form(Reader reader) {
    String token = reader.peek();
    if (token == null) {
      throw new EOFException();
    }

    switch (token) {
      case "(":
      case "[":
      case "{":
        return read_list(reader);
      case "'":
        reader.next();
        return new form(new symbol("quote"), read_form(reader));
      case "`":
        reader.next();
        return new form(new symbol("quasiquote"), read_form(reader));
      case "~":
        reader.next();
        return new form(new symbol("unquote"), read_form(reader));
      case "@":
        reader.next();
        return new form(new symbol("deref"), read_form(reader));
      case "~@":
        reader.next();
        return new form(new symbol("splice-unquote"), read_form(reader));
      case "^":
        reader.next();
        mal meta = read_form(reader);
        reader.next();
        return new meta_form(read_form(reader), meta);
      default:
        return read_atom(reader);
    }
  }

  static list read_list(Reader reader) {
    String token = reader.next();
    switch (token) {
      case "[":
        vector vector = new vector();

        while (!reader.peek().equals("]")) {
          vector.add(read_form(reader));

          if (reader.peek() == null) {
            throw new EOFException("]");
          }
        }
        return vector;
      case "{":
        hash_map map = new hash_map();
        while (!reader.peek().equals("}")) {
          map.add(read_form(reader));

          if (reader.peek() == null) {
            throw new EOFException("}");
          }
        }
        return map;
      default:
        list list = new list();
        while (!reader.peek().equals(")")) {
          list.add(read_form(reader));

          if (reader.peek() == null) {
            throw new EOFException(")");
          }
        }
        return list;
    }
  }

  static mal read_atom(Reader reader) {
    String token = reader.next();

    try {
      return new number(Integer.parseInt(token));
    } catch (NumberFormatException e) {
      // do nothing
    }

    return new symbol(token);
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
    final String msg;

    EOFException() {
      this("");
    }

    EOFException(String msg) {
      this.msg = msg;
    }
  }
}
