package borel.optimize;

import borel.Vector;
import lombok.Data;
import lombok.val;

@Data(staticConstructor = "of")
public class BacktrackingLineMinimizer implements LineMinimizer {

  private final double alpha, beta, minStepLen;

  public BacktrackingLineMinimizer(double alpha, double beta, double minStepLen) {
    this.alpha = alpha;
    this.beta = beta;
    this.minStepLen = minStepLen;
  }

  @Override
  public Result minimize(GradientFn gradFn, Vector x, Vector dir) {
    val valGradPair = gradFn.apply(x);
    double f0 = valGradPair.fx;
    val grad = valGradPair.grad;
    if (grad.l2NormSquared() < minStepLen) {
      return Result.of(0.0, f0);
    }
    final double delta = beta * grad.dotProduct(dir);
    double stepLen = 1.0;
    while (stepLen >= minStepLen) {
      val stepX = x.add(stepLen, dir);
      final double fx = gradFn.apply(stepX).fx;
      if (fx <= f0 + stepLen * delta) {
        return Result.of(stepLen, fx);
      }
      stepLen *= alpha;
    }
    throw new RuntimeException("Step-size underflow");
  }
}
