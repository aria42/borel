package borel;

import lombok.Data;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 */
public interface Vector {
  /**
   *
   * @return dimension of the space for the vector
   */
  public long dimension();

  public double at(long dimensionIdx);

  public void set(long dimensionIdx, double val);

  public long numStoredEntries();

  public Vector copy();

  default public double[] at(long...dimensionIndices) {
    double[] result = new double[dimensionIndices.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = at(dimensionIndices[i]);
    }
    return result;
  }

  default public void mapInPlace(EntryUpdateFunction updateFn) {
    nonZeroEntries().forEachOrdered(entry -> {
      double updatedValue = updateFn.update(entry.index, entry.value);
      set(entry.index, updatedValue);
    });
  }

  default public Vector map(EntryUpdateFunction updateFn) {
    Vector result = copy();
    result.mapInPlace(updateFn);
    return result;
  }

  default public void affineUpdateInPlace(double scale, double offset) {
    this.mapInPlace((idx, value) -> scale * value + offset);
  }

  default public Vector affine(double scale, double offset) {
    Vector result = copy();
    result.affineUpdateInPlace(scale, offset);
    return result;
  }

  default public Vector scale(double scale) {
    return affine(scale, 0.0);
  }

  default public double dotProduct(Vector other) {
    if (other.dimension() != this.dimension()) {
      throw new IllegalArgumentException("Dimensions don't match");
    }
    // Do dot-product from the pov of vector with fewer elements
    if (other.numStoredEntries() < this.numStoredEntries()) {
      return other.dotProduct(this);
    }
    return nonZeroEntries()
        .mapToDouble(entry -> entry.value * other.at(entry.index))
        .sum();
  }

  default public double l2NormSquared() {
    return this.dotProduct(this);
  }

  default public Stream<Entry> entries() {
    VectorSpliterator spliterator = new VectorSpliterator(this, 0, dimension());
    return StreamSupport.stream(spliterator, true);
  }

  default public Stream<Entry> nonZeroEntries() {
    return entries().filter(e -> e.value != 0.0);
  }

  @Data(staticConstructor = "of")
  public final static class Entry implements Comparable<Entry> {
    public final long index;
    public final double value;

    @Override
    public int compareTo(Entry o) {
      return Long.compare(this.index, o.index);
    }
  }

  final static class VectorSpliterator implements Spliterator<Entry> {
    private Vector vec;
    private long position;
    private long stop;

    VectorSpliterator(Vector vec, long start, long stop) {
      this.vec = vec;
      this.position = start;
      this.stop = stop;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Entry> action) {
      if (position < stop) {
        action.accept(Entry.of(position, vec.at(position)));
        position++;
        return true;
      }
      return false;
    }

    @Override
    public Spliterator<Entry> trySplit() {
      long mid = (stop-position)/2 + position;
      if (position < mid) {
        long low = position;
        this.position = mid;
        return new VectorSpliterator(vec,low, mid);
      }
      return null;
    }

    @Override
    public long estimateSize() {
      return stop - position;
    }

    @Override
    public int characteristics() {
      return ORDERED | SIZED | IMMUTABLE | SUBSIZED;
    }
  }

  @FunctionalInterface
  public interface EntryUpdateFunction {
    public double update(long dimensionIdx, double value);
  }

}
