package borel.optimize;

import borel.Vector;
import lombok.val;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface QuasiNewton {

  Vector implictMultiply(Vector dir);

  default void update(Vector xDelta, Vector gradDelta) {
  }

  static QuasiNewton gradientDescent() {
    return dir -> dir;
  }

  static QuasiNewton lbfgs(final int maxHistorySize) {

    return new QuasiNewton() {

      // Store (xDelta, gradDelta) pairs
      private final List<Tuple2<Vector, Vector>> history = new ArrayList<>();

      private double initialScale() {
        if (history.isEmpty()) {
          return 1.0;
        }
        Vector lastInputDiff = history.get(0).v1;
        Vector lastGradDiff = history.get(0).v2;
        double numer = lastGradDiff.dotProduct(lastInputDiff);
        double denom = lastGradDiff.l2NormSquared();
        if (denom == 0.0) {
          throw new RuntimeException("Can't have zero curvature");
        }
        return numer / denom;
      }

      @Override
      public Vector implictMultiply(Vector dir) {
        double[] rho = new double[history.size()];
        double[] alpha = new double[history.size()];
        Vector right = dir.copy();
        for (int i = history.size() - 1; i >= 0; i--) {
          val inputDifference = history.get(i).v1;
          val derivativeDifference = history.get(i).v2;
          rho[i] = inputDifference.dotProduct(derivativeDifference);
          if (rho[i] == 0.0) {
            throw new RuntimeException("LBFGSMinimizer.implicitMultiply: Curvature problem.");
          }
          alpha[i] = inputDifference.dotProduct(right) / rho[i];
          right = right.add(-alpha[i], derivativeDifference);
        }
        right.scaleInPlace(initialScale());
        Vector left = right;
        for (int i = 0; i < history.size(); i++) {
          Vector inputDifference = history.get(i).v1;
          Vector derivativeDifference = history.get(i).v2;
          double beta = derivativeDifference.dotProduct(left) / rho[i];
          left = left.add(alpha[i] - beta, inputDifference);
        }
        return left;
      }

      @Override
      public void update(Vector xDelta, Vector gradDelta) {
        this.history.add(0, Tuple.tuple(xDelta, gradDelta));
        while (this.history.size() > maxHistorySize) {
          this.history.remove(this.history.size()-1);
        }
      }
    };
  }
}
