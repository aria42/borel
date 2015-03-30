package borel.optimize;

import borel.DenseVector;
import lombok.val;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Test
public class NewtonMethodTest {
  public void testGradientDescent() throws Exception {
    val minimizer = new NewtonMethod(__ -> QuasiNewton.gradientDescent());
    testQuartic(minimizer);
  }

  public void testLBFGS() throws Exception {
    testQuartic(new NewtonMethod(__ -> QuasiNewton.lbfgs(1)));
    testQuartic(new NewtonMethod(__ -> QuasiNewton.lbfgs(3)));
  }

  private void testQuartic(NewtonMethod minimizer) {
    // Simple Function
    val res = minimizer.minimize(GradientFn.from(1, TestUtils.xSquared), DenseVector.of(1.0));
    assertEquals(res.xmin, DenseVector.of(0.0));
    assertEquals(res.fxmin, 0.0, 0.0);
    // Simple Function
    val res2 = minimizer.minimize(GradientFn.from(2, TestUtils.quartic));
    double l2Dist = res2.xmin.l2Distance(DenseVector.of(1.0, -2.0));
    assertTrue(l2Dist < 0.001);
    assertEquals(res2.fxmin, 0.0, 1.0e-5);
  }
}