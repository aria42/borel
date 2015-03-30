package borel.optimize;


import borel.Vector;
import lombok.Data;
import lombok.val;

import java.util.LinkedList;
import java.util.Optional;

public class CachingGradientFn implements GradientFn {

  private final int maxHistory;
  private final GradientFn gradFn;
  private final LinkedList<HistoryEntry> history;

  @Data
  public class HistoryEntry {
    final Vector input;
    final double output;
    final Vector grad;
  }

  public CachingGradientFn(int maxHistory, GradientFn gradFn) {
    this.maxHistory = maxHistory;
    this.gradFn = gradFn;
    this.history = new LinkedList<>();
  }

  @Override
  public GradientFn.Result apply(Vector x) {
    Optional<HistoryEntry> foundEntry = this.history
        .stream()
        .filter(e -> e.input.equals(x))
        .findFirst();
    if (foundEntry.isPresent()) {
      return GradientFn.Result.of(foundEntry.get().output, foundEntry.get().grad);
    } else {
      GradientFn.Result result = this.gradFn.apply(x);
      val entry = new HistoryEntry(x, result.fx, result.grad);
      this.history.addFirst(entry);
      if (this.history.size() > this.maxHistory) {
        this.history.removeLast();
      }
      return result;
    }
  }
  @Override
  public long dimension() {
    return gradFn.dimension();
  }
}