package borel.optimize;

import borel.Vector;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.function.Function;


@Slf4j(topic = "NewtonMethodOptimize")
public class NewtonMethod implements GradientFnMinimizer {

  private final Function<GradientFn, QuasiNewton> quasiNewtonFn;
  private final Opts opts;

  public static class Opts {
    public int maxIters = 150;
    public double tolerance = 1.0e-10;
    public double alpha = 0.5;
    public double beta = 0.01;
    public double stepLenTolerance = 1.0e-10;

    public LineMinimizer lineMinimizer() {
      return new BacktrackingLineMinimizer(alpha, beta, stepLenTolerance);
    }
  }

  public NewtonMethod(Function<GradientFn, QuasiNewton> quasiNewtonFn) {
    this(quasiNewtonFn, new Opts());
  }

  public NewtonMethod(Function<GradientFn, QuasiNewton> quasiNewtonFn, Opts opts) {
    this.quasiNewtonFn = quasiNewtonFn;
    this.opts = opts;
  }
  private Vector step(GradientFn gradFn, Vector x, LineMinimizer ls, QuasiNewton qn) {
    Vector grad = gradFn.apply(x).grad;
    Vector dir = qn.implictMultiply(grad);
    dir.scaleInPlace(-1.0);
    val lsRes = ls.minimize(gradFn, x, dir);
    double stepLen = lsRes.stepLength;
    return x.add(stepLen, dir);
  }
  @Override
  public Result minimize(GradientFn gradFn, Vector initGuess) {
    QuasiNewton qn = this.quasiNewtonFn.apply(gradFn);
    val lm = this.opts.lineMinimizer();
    Vector x = initGuess;
    for (int i=0; i < opts.maxIters; ++i) {
      // iteration
      val curRes = gradFn.apply(x);
      val xnew = step(gradFn, x, lm, qn);
      val newRes = gradFn.apply(xnew);
      if (newRes.fx > curRes.fx) {
        throw new IllegalStateException("Step increased function value");
      }
      double larger = Math.min(Math.abs(curRes.fx), Math.abs(newRes.fx));
      double relDiff = larger > 0.0 ? Math.abs(newRes.fx-curRes.fx)/larger : 0.0;
      // update
      qn.update(xnew.add(-1.0,x), newRes.grad.add(-1.0, curRes.grad));
      x = xnew;
      log.info("[Iteration {}] Ended with value {} and relDiff {}\n", i, newRes.fx, relDiff);
      if (relDiff < opts.tolerance) {
        break;
      }
    }
    return Result.of(gradFn.apply(x).fx, x);
  }
}