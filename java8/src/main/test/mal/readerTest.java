package mal;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class readerTest {
  @Test
  public void read_str() throws Exception {
  }

  @Test
  public void tokenizer() throws Exception {
    List<String> tokenizer = reader.tokenizer("  (   +  1    2   )   ");
    assertTrue(!tokenizer.isEmpty());
  }

  @Test
  public void read_form() throws Exception {
  }

}