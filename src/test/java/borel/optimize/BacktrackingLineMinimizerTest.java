package borel.optimize;


import borel.DenseVector;
import lombok.val;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

@Test
public class BacktrackingLineMinimizerTest {

  public void testBacktrackingLineSearcher() throws Exception {
    val ls = new BacktrackingLineMinimizer(0.5, 0.01, 1.0e-10);
    val result = ls.minimize(TestUtils.xSquared, DenseVector.of(1.0), DenseVector.of(-1.0));
    assertEquals("", result.stepLength, 1.0,0.001);
    assertEquals("", result.fxmin, 0.0, 0.001);
    val result2 = ls.minimize(TestUtils.xSquared, DenseVector.of(0.0), DenseVector.of(1.0));
    assertEquals("",result2.stepLength,0.0, 0.001);
    assertEquals("",result2.fxmin,0.0, 0.001);
  }
}