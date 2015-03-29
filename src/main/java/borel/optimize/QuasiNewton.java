package borel.optimize;

import borel.Vector;

@FunctionalInterface
public interface QuasiNewton {

  Vector implictMultiply(Vector dir);

  default void update(Vector xDelta, Vector gradDelta) {
  }

  static QuasiNewton gradientDescent() {
    return dir -> dir;
  }
}
