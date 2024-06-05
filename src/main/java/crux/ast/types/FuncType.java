package crux.ast.types;

/**
 * The field args is a TypeList with a type for each param. The type ret is the type of the function
 * return. The function return could be int, bool, or void. This class should implement the call
 * method.
 */
public final class FuncType extends Type implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  private TypeList args;
  private Type ret;

  public FuncType(TypeList args, Type returnType) {
    this.args = args;
    this.ret = returnType;
  }

  public Type getRet() {
    return ret;
  }

  public TypeList getArgs() {
    return args;
  }

  @Override
  public String toString() {
    return "func(" + args + "):" + ret;
  }

  @Override
  public Type call(Type args) {
    if (!(args instanceof TypeList)) {
      return new ErrorType("Expected a list of types for function arguments, but got " + args);
    }

    TypeList argList = (TypeList) args;

    // check if the provided argument list is equivalent to the expected argument list
    if (!this.args.equivalent(argList)) {
      return new ErrorType("Function argument types do not match expected signature.");
    }

    return this.ret;
  }

  @Override
  public boolean equivalent(Type that) {
    if (that == null || !(that instanceof FuncType)) {
      return false;
    }

    FuncType otherFuncType = (FuncType) that;
    if (!this.ret.equivalent(otherFuncType.getRet())) {
      return false;
    }

    if (!this.args.equivalent(otherFuncType.getArgs())) {
      return false;
    }

    return true;
  }
}
