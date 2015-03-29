package borel.optimize;

import borel.Vector;
import lombok.Data;

import java.util.function.Function;

public interface GradientFn extends Function<Vector, GradientFn.Result> {

  @Data(staticConstructor = "of")
  class Result {
    public final double fx;
    public final Vector grad;
  }

  @Override
  Result apply(Vector vec);

  long dimension();

  static GradientFn from(long dimension, Function<Vector, Result> fn) {
    return new GradientFn() {
      @Override
      public Result apply(Vector vec) {
        if (vec.dimension() != this.dimension()) {
          throw new IllegalArgumentException("Argument doesn't match dimesnion(): " +
              vec.dimension() + " != " + this.dimension());
        }
        return fn.apply(vec);
      }

      @Override
      public long dimension() {
        return dimension;
      }
    };
  }
}
