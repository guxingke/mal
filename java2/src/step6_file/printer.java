package step6_file;

import java.util.Collection;
import java.util.stream.Collectors;

class printer {
  static String pr_str(mal val, boolean print_readably) {
    if (val instanceof number) {
      return ((number) val).val.toString();
    } else if (val instanceof keyword) {
      return ((keyword) val).val;
    } else if (val instanceof str) {
      if (print_readably) {
        return "\"" + ((str) val).val
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n") + "\"";
      }
      return ((str) val).val;
    } else if (val instanceof symbol) {
      return ((symbol) val).val;
    } else if (val instanceof fun) {
      return "#" + val;
    } else if (val instanceof vector) {
      return pr_str(((vector) val).data, "[", "]", print_readably);
    } else if (val instanceof hash_map) {
      return pr_str(((list) val).data, "{", "}", print_readably);
    } else if (val instanceof list) {
      return pr_str(((list) val).data, "(", ")", print_readably);
    }
    return val.toString();
  }

  private static String pr_str(Collection<mal> coll, String prefix, String suffix, boolean print_readably) {
    return coll.stream().map(it -> pr_str(it, print_readably)).collect(Collectors.joining(" ", prefix, suffix));
  }
}
