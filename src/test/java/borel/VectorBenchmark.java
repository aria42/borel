package borel;

import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.map.TLongDoubleMap;
import gnu.trove.map.hash.TLongDoubleHashMap;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Threads(Threads.MAX)
public class VectorBenchmark {

  private final static int NUM_DIMENSIONS = 10000;
  private final static int NUM_ACTIVE_VECTORS = 100;
  private final static int NUM_FEATURE_VECTORS = 10;
  private final double[] weights = new double[NUM_DIMENSIONS];
  private Vector weightVector;
  private List<TLongDoubleMap> troveFeatureVector = new ArrayList<>();
  private List<Vector> featureVectors = new ArrayList<>();

  @Benchmark
  public void sparseDenseDotProductThroughVector() throws Exception {
    double sum = featureVectors.stream().mapToDouble(weightVector::dotProduct).sum();
  }

  @Benchmark
  public void sparseDenseDotProductDirect() throws Exception {
    double sum = 0.0;
    for (TLongDoubleMap featureVector : troveFeatureVector) {
      double dotProduct = 0.0;
      for (TLongDoubleIterator it = featureVector.iterator(); it.hasNext(); ) {
        it.advance();
        long idx = it.key();
        double val = it.value();
        dotProduct += val * weights[(int)idx];
      }
      sum += dotProduct;
    }
  }

  @Setup
  public void up() {
    Random rand = new Random(0);
    for (int i = 0; i < NUM_DIMENSIONS; i++) {
      // in range [-1,1] uniformly
      weights[i] = 2.0 * (rand.nextDouble() - 0.5);
    }
    weightVector = DenseVector.of(weights);
    troveFeatureVector = IntStream.range(0, NUM_FEATURE_VECTORS)
        .mapToObj(ignored -> {
          TLongDoubleMap vec = new TLongDoubleHashMap(NUM_FEATURE_VECTORS);
          for (int j = 0; j < NUM_ACTIVE_VECTORS; j++) {
            int dimensionIdx = rand.nextInt(NUM_DIMENSIONS);
            vec.put(dimensionIdx, rand.nextDouble());
          }
          return vec;
        })
        .collect(toList());
    featureVectors = troveFeatureVector.stream()
        .map(fv -> SparseVector.make(fv, NUM_DIMENSIONS))
        .collect(toList());
  }

  public static void main(String[] args) throws Exception {
    Main.main(args);
  }
}
