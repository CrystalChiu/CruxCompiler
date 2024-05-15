package crux.ast.types;

/**
 * Types for Booleans values This should implement the equivalent methods along with and,or, and not
 * equivalent will check if the param is instance of BoolType
 */
public final class BoolType extends Type implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  @Override
  public String toString() {
    return "bool";
  }

  @Override
  public Type and(Type that) {
    if (that instanceof BoolType) {
      return new BoolType();  // 'and' with another BoolType is valid
    }
    return super.and(that);  // Otherwise, it should return an error
  }

  @Override
  public Type or(Type that) {
    if (that instanceof BoolType) {
      return new BoolType();  // 'or' with another BoolType is valid
    }
    return super.or(that);  // Otherwise, it's an error
  }

  @Override
  public Type not() {
    return new BoolType();  // 'not' is valid for BoolType
  }

  @Override
  public Type assign(Type source) {
    if (source instanceof BoolType) {
      return new BoolType();  // Assignment of BoolType is valid
    }
    return super.assign(source);  // Otherwise, it's an error
  }

  @Override
  public boolean equivalent(Type that) {
    return that instanceof BoolType;  // Checks if the parameter is also BoolType
  }
}
