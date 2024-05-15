package crux.ast.types;

/**
 * Types for Integers values. This should implement the equivalent methods along with add, sub, mul,
 * div, and compare. The method equivalent will check if the param is an instance of IntType.
 */
public final class IntType extends Type implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  @Override
  public String toString() {
    return "int";
  }

  //------Added------

  @Override
  public Type add(Type that) {
    if (that instanceof IntType) {
      return new IntType();
    }
    return super.add(that);  // If not IntType, it should return an error
  }

  @Override
  public Type sub(Type that) {
    if (that instanceof IntType) {
      return new IntType();
    }
    return super.sub(that);
  }

  @Override
  public Type mul(Type that) {
    if (that instanceof IntType) {
      return new IntType();
    }
    return super.mul(that);
  }

  @Override
  public Type div(Type that) {
    if (that instanceof IntType) {
      return new IntType();
    }
    return super.div(that);
  }

  @Override
  public Type compare(Type that) {
    if (that instanceof IntType) {
      return new BoolType();  // Comparison returns a boolean
    }
    return super.compare(that);
  }

  @Override
  public Type assign(Type source) {
    if (source instanceof IntType) {
      return new IntType();
    }
    return super.assign(source);  // Assignment requires matching types
  }

  @Override
  public boolean equivalent(Type that) {
    return that instanceof IntType;
  }
}
