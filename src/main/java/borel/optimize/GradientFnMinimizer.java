package borel.optimize;

import borel.DenseVector;
import borel.Vector;
import lombok.Data;

@FunctionalInterface
public interface GradientFnMinimizer {

  @Data(staticConstructor = "of")
  class Result {
    public final double fxmin;
    public final Vector xmin;
  }

  default Result minimize(GradientFn gradFn) {
    return this.minimize(gradFn, DenseVector.of(gradFn.dimension()));
  }

  Result minimize(GradientFn gradFn, Vector initGuess);
}
