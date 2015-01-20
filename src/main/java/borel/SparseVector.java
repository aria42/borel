package borel;

import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.map.TLongDoubleMap;
import gnu.trove.map.hash.TLongDoubleHashMap;

import java.util.Spliterator;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class SparseVector implements Vector {

  private final TLongDoubleMap vec;
  private final long dimension;

  private SparseVector(TLongDoubleMap vec, long dimension) {

    this.vec = vec;
    this.dimension = dimension;
  }

  public static SparseVector withCapacity(int capacity, long dimension) {
    return new SparseVector(new TLongDoubleHashMap(capacity), dimension);
  }

  public static SparseVector fromEntries(Stream<Vector.Entry> entries, long dimension) {
    boolean isSized = entries.spliterator().hasCharacteristics(Spliterator.SIZED);
    TLongDoubleMap vec = isSized  ?
        new TLongDoubleHashMap((int)entries.spliterator().estimateSize()) :
        new TLongDoubleHashMap();
    entries.forEach(e -> vec.put(e.index, e.value));
    return new SparseVector(vec, dimension);
  }

  public static SparseVector make(long dimension) {
    return new SparseVector(new TLongDoubleHashMap(), dimension);
  }

  public static SparseVector make() {
    return make(Long.MAX_VALUE);
  }

  @Override
  public long dimension() {
    return dimension;
  }

  @Override
  public double at(long dimensionIdx) {
    return vec.get(dimensionIdx);
  }

  @Override
  public void set(long dimensionIdx, double val) {
    vec.put(dimensionIdx, val);
  }

  @Override
  public long numStoredEntries() {
    return vec.size();
  }

  @Override
  public Vector copy() {
    return new SparseVector(new TLongDoubleHashMap(vec), dimension);
  }

  @Override
  public Stream<Entry> nonZeroEntries() {
    long[] indices = vec.keys();
    return LongStream.of(indices)
        .mapToObj(idx -> Vector.Entry.of(idx, vec.get(idx)));
  }

  @Override
  public double dotProduct(Vector other) {
    if (other.numStoredEntries() < this.numStoredEntries()) {
      return other.dotProduct(this);
    }
    double result = 0.0;
    for (TLongDoubleIterator it = vec.iterator(); it.hasNext(); ) {
      it.advance();
      long idx = it.key();
      double val = it.value();
      result += val * other.at(idx);
    }
    return result;
  }
}
