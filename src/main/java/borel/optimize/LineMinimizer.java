package borel.optimize;

import borel.Vector;
import lombok.Data;

@FunctionalInterface
public interface LineMinimizer {

  @Data(staticConstructor = "of")
  class Result {
    public final double stepLength;
    public final double fxmin;
  }

  Result minimize(GradientFn gradFn, Vector x, Vector dir);
}