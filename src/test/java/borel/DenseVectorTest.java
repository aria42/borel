package borel;

import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.testng.Assert.*;

public class DenseVectorTest {

  @Test
  public void testDimension() throws Exception {
    DenseVector v = new DenseVector(10);
    assertEquals(v.dimension(), 10);
  }

  @Test
  public void testAt() throws Exception {
    double[] elems = {1.0,2.0,3.0};
    DenseVector v = new DenseVector(elems);
    assertEquals(v.at(1), 2.0);
  }

  @Test
  public void testSet() throws Exception {
    DenseVector v = new DenseVector(3);
    v.set(2, 1.0);
    assertEquals(v.at(2), 1.0);
  }

  @Test
  public void testNonZeroEntries() throws Exception {
    double[] elems = {1.0,2.0,3.0};
    DenseVector v = new DenseVector(elems);
    List<Vector.Entry> entries =  v.nonZeroEntries().collect(toList());
    assertEquals(entries.get(0), Vector.Entry.of(0, 1.0));
  }

  @Test
  public void testNumStoredEntries() throws Exception {
    DenseVector v = new DenseVector(5);
    assertEquals(v.numStoredEntries(), 5);
  }

  @Test
  public void testCopy() throws Exception {
    DenseVector v = new DenseVector(new double[]{1.0,2.0,3.0});
    VectorTest.testCopy(v);
  }

  @Test
  public void testDotProduct() throws Exception {
    Vector vec = new DenseVector(new double[]{1.0,2.0,3.0});
    assertEquals(vec.dotProduct(vec), 14.0);
  }
}