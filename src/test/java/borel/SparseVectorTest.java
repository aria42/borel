package borel;

import org.testng.annotations.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.testng.Assert.*;

public class SparseVectorTest {

  @Test
  public void testDotProduct() throws Exception {
    Vector sparse = SparseVector.make(3);
    Vector dense = new DenseVector(new double[]{1.0,2.0,3.0});
    sparse.set(0, 1.0);
    sparse.set(1, 2.0);
    assertEquals(sparse.dotProduct(dense), 5.0);
  }

  @Test
  public void testCopy() throws Exception {
    Vector sparse = SparseVector.make(3);
    sparse.set(0, 1.0);
    VectorTest.testCopy(sparse);
  }
}