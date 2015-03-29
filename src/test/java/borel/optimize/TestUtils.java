package borel.optimize;

import borel.DenseVector;
import borel.Vector;
import lombok.val;

public class TestUtils {
  public static final GradientFn xSquared = GradientFn.from(1, x -> GradientFn.Result.of(
      x.at(0) * x.at(0),
      DenseVector.of(2.0 * x.at(0))));

  public static GradientFn quartic = GradientFn.from(2, x -> {
    double val = Math.pow(x.at(0) - 1.0, 4.0) + Math.pow(x.at(1) + 2.0, 4.0);
    Vector grad = DenseVector.of(4 * Math.pow(x.at(0) - 1.0, 3.0), 4 * Math.pow(x.at(1)+2.0, 3.0));
    return GradientFn.Result.of(val, grad);
  });
}
