package borel;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.testng.Assert.assertEquals;

public class VectorTest {
  public static void testCopy(Vector v) {
    List<Vector.Entry> vectorEntries =
        v.nonZeroEntries().sorted().collect(toList());
    Vector copy = v.copy();
    List<Vector.Entry> copyEntries =
        copy.nonZeroEntries().sorted().collect(toList());
    assertEquals(vectorEntries, copyEntries);
  }
}
