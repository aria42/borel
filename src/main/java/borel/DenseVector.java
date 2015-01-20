package borel;

import java.util.Arrays;

public class DenseVector implements Vector {
  private final double[] elems;

  public DenseVector(double[] elems) {
    this.elems = elems;
  }

  public DenseVector(int numDimensions) {
    this(new double[numDimensions]);
  }

  @Override
  public long dimension() {
    return elems.length;
  }

  private void ensureIndexIsInteger(long dimensionIdx) {
    if (dimensionIdx > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Index overflows integer max");
    }
  }

  @Override
  public double at(long dimensionIdx) {
    if (dimensionIdx >= dimension()) {
      String errMsg = String.format("Illegal index %d > dimension %d",dimensionIdx,dimension());
      throw new IllegalArgumentException(errMsg);
    }
    ensureIndexIsInteger(dimensionIdx);
    return elems[(int) dimensionIdx];
  }

  @Override
  public void set(long dimensionIdx, double val) {
    ensureIndexIsInteger(dimensionIdx);
    elems[(int) dimensionIdx] = val;
  }

  @Override
  public long numStoredEntries() {
    return elems.length;
  }

  @Override
  public Vector copy() {
    return new DenseVector(Arrays.copyOf(elems, elems.length));
  }
}
