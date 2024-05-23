package crux.ast.types;

import crux.ast.SymbolTable.Symbol;
import crux.ast.*;
import crux.ast.traversal.NullNodeVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class will associate types with the AST nodes from Stage 2
 */
public final class TypeChecker {
  private final ArrayList<String> errors = new ArrayList<>();
  private Symbol currentFunctionSymbol;
  private boolean lastStatementReturns;
  private boolean hasBreak;


  public TypeChecker() {
    System.out.println("INIT");
    reset();
  }

  private void reset() {
    currentFunctionSymbol = null;
    lastStatementReturns = false;
    hasBreak = false;
  }

  public ArrayList<String> getErrors() {
    return errors;
  }

  public void check(DeclarationList ast) {
    var inferenceVisitor = new TypeInferenceVisitor();
    inferenceVisitor.visit(ast);
  }

  /**
   * Helper function, should be used to add error into the errors array
   */
  private void addTypeError(Node n, String message) {
    errors.add(String.format("TypeError%s[%s]", n.getPosition(), message));
  }

  /**
   * Helper function, should be used to record Types if the Type is an ErrorType then it will call
   * addTypeError
   */
  private void setNodeType(Node n, Type ty) {
    ((BaseNode) n).setType(ty);
    if (ty.getClass() == ErrorType.class) {
      var error = (ErrorType) ty;
      addTypeError(n, error.getMessage());
    }
  }

  /**
   * Helper to retrieve Type from the map
   */
  public Type getType(Node n) {
    return ((BaseNode) n).getType();
  }


  /**
   * This calls will visit each AST node and try to resolve it's type with the help of the
   * symbolTable.
   */
  private final class TypeInferenceVisitor extends NullNodeVisitor<Void> {

    /**
     * Helper function
     * @param node
     * @return node's base type
     */
    private Type visitAndSetType(Node node) {
      node.accept(this);
      Type type = ((BaseNode) node).getType();

      if(type != null) {
        setNodeType(node, type);
      }

      System.out.println("Set Node: " + node.toString() + " to Type: " + type);

      return type;
    }
    @Override
    public Void visit(VarAccess vaccess) {
      System.out.print("Visited VarAccess");
      Symbol symbol = vaccess.getSymbol();
      Type symbolType = symbol.getType();

      setNodeType(vaccess, symbolType);

      return null;
    }

    @Override
    public Void visit(ArrayDeclaration arrayDeclaration) {
      //need to verify
      System.out.print("Visited ArrayDeclaration");

      Symbol arraySymbol = arrayDeclaration.getSymbol();
      Type arrayType = arraySymbol.getType();
      if (!(arrayType instanceof ArrayType)) {
        setNodeType(arrayDeclaration, new ErrorType("Expected ArrayType, but got " + arrayType));
      } else {
        setNodeType(arrayDeclaration, arrayType);
      }

      lastStatementReturns = false;

      System.out.print("Exited ArrayDeclaration");
      return null;
    }

    @Override
    public Void visit(Assignment assignment) {
      System.out.println("Visited Assignment");

      Type locationType = visitAndSetType(assignment.getLocation());
      Type valueType = visitAndSetType(assignment.getValue());

      Type resultType = locationType.assign(valueType);

      setNodeType(assignment, resultType);

      lastStatementReturns = false;

      System.out.print("Exited Assignment");
      return null;
    }

    @Override
    public Void visit(Break brk) {
      System.out.println("Visited Break");
      hasBreak = true;
      System.out.println("Exit Break");

      return null;
    }

    @Override
    public Void visit(Call call) {
      System.out.println("Visited Call");
      TypeList argTypes = new TypeList();

      for (Node arg : call.getChildren()) {
        Type argType = visitAndSetType(arg);
        argTypes.append(argType);
      }

      Symbol calleeSymbol = call.getCallee();
      Type calleeType = calleeSymbol.getType();

      Type returnType = calleeType.call(argTypes);
      setNodeType(call, returnType);

      lastStatementReturns = false;

      System.out.println("Exit Call");
      return null;
    }

    @Override
    public Void visit(Continue cont) {
      System.out.println("Visited Cont");

      return null;
    }
    
    @Override
    public Void visit(DeclarationList declarationList) {
      System.out.println("Visited DeclarationList");

      lastStatementReturns = false;

      for(Node child : declarationList.getChildren()) {
        visitAndSetType(child);
      }

      System.out.println("Exit DeclarationList");
      return null;
    }

    @Override
    public Void visit(FunctionDefinition functionDefinition) {
      System.out.println("Visited FuncDef");

      currentFunctionSymbol = functionDefinition.getSymbol();

      FuncType functionType = (FuncType)currentFunctionSymbol.getType();
      Type currentFunctionReturnType = functionType.getRet();
      System.out.println("Func type: " + functionType);

      //if func is main, verify return type is void and no args.
      if ("main".equals(currentFunctionSymbol.getName())) {
        if (!(currentFunctionReturnType instanceof VoidType)) {
          setNodeType(functionDefinition, new ErrorType("Main function must return void."));
        }
        if (!functionDefinition.getParameters().isEmpty()) {
          setNodeType(functionDefinition, new ErrorType("Main function must not have parameters."));
        }
      }

      //only bool and int params allowed
      for (Symbol param : functionDefinition.getParameters()) {
        Type paramType = param.getType();
        System.out.println("paramType: " + paramType);

        if (!(paramType instanceof IntType) && !(paramType instanceof BoolType)) {
          setNodeType(functionDefinition, new ErrorType("Parameter must be of type int or bool."));
        }
      }

      //visit funcBody
      for(Node stmt : functionDefinition.getChildren()) {
        visitAndSetType(stmt);
      }

      //if not void, there must be a return statement
      if (!(currentFunctionReturnType instanceof VoidType) && !lastStatementReturns) {
        setNodeType(functionDefinition, new ErrorType("Function does not have return."));
      }

      lastStatementReturns = false;

      System.out.println("Exit FuncDef");
      return null;
    }

    @Override
    public Void visit(IfElseBranch ifElseBranch) {
      System.out.println("Visited IfElseBranch");

      // Visit the condition and check its type
      Type conditionType = visitAndSetType(ifElseBranch.getCondition());
      if (!(conditionType instanceof BoolType)) {
        setNodeType(ifElseBranch, new ErrorType("Condition in if-else branch must be of type bool."));
      }

      // Visit the thenBlock and elseBlock if not null
      if (ifElseBranch.getThenBlock() != null) {
        visitAndSetType(ifElseBranch.getThenBlock());
      }
      if (ifElseBranch.getElseBlock() != null) {
        visitAndSetType(ifElseBranch.getElseBlock());
      }

      System.out.println("Exit IfElseBranch");
      return null;
    }

    @Override
    public Void visit(ArrayAccess access) {
      System.out.println("Visited ArrayAccess");

      Expression indexExpr = access.getIndex();
      Type indexType = visitAndSetType(indexExpr);

      Symbol baseSymbol = access.getBase();
      Type baseType = baseSymbol.getType();

      if (!(baseType instanceof ArrayType)) {
        setNodeType(access, new ErrorType("Base type must be an array type."));
        return null;
      }

      ArrayType arrayType = (ArrayType)baseType;
      Type elementType = arrayType.index(indexType);

      setNodeType(access, elementType);

      return null;
    }

    @Override
    public Void visit(LiteralBool literalBool) {
      System.out.println("Visited LiteralBool");

      setNodeType(literalBool, new BoolType());

      return null;
    }

    @Override
    public Void visit(LiteralInt literalInt) {
      System.out.println("Visited LiteralInt");
      setNodeType(literalInt, new IntType());

      return null;
    }

    @Override
    public Void visit(Loop loop) {
      boolean prevHasBreak = hasBreak;
      hasBreak = false;

      boolean prevLastStatementReturns = lastStatementReturns;
      lastStatementReturns = false;
      for (Node stmt : loop.getBody().getChildren()) {
        visitAndSetType(stmt);
      }

      if (!hasBreak && !lastStatementReturns) {
        setNodeType(loop, new ErrorType("Infinite loop without break statement."));
      }

      hasBreak = prevHasBreak;
      lastStatementReturns = prevLastStatementReturns;

      System.out.println("Exit Loop");

      return null;
    }

    @Override
    public Void visit(OpExpr op) {
      System.out.println("Visited OpExpr");

      Expression left = op.getLeft();
      Expression right = op.getRight();

      Type leftType = visitAndSetType(left);
      Type rightType = visitAndSetType(right);
      Type resultType;

      switch (op.getOp()) {
        case ADD:
        case SUB:
        case MULT:
        case DIV:
          resultType = leftType.add(rightType);
          break;
        case LT:
        case GT:
        case LE:
        case GE:
          if (leftType instanceof IntType && rightType instanceof IntType) {
            resultType = new BoolType();
          } else {
            resultType = new ErrorType("Unsupported operand types for " + op.getOp() + ": " + leftType + " and " + rightType);
          }
          break;
        case EQ:
        case NE:
          if (leftType.equivalent(rightType)) {
            resultType = new BoolType();
          } else {
            resultType = new ErrorType("Unsupported operand types for " + op.getOp() + ": " + leftType + " and " + rightType);
          }
          break;
        case LOGIC_NOT:
          if (rightType instanceof BoolType) {
            resultType = rightType;
          } else {
            resultType = new ErrorType("Unsupported operand type for " + op.getOp() + ": " + rightType);
          }
          break;
        case LOGIC_AND:
        case LOGIC_OR:
          resultType = leftType.and(rightType);
          break;
        default:
          resultType = new ErrorType("Unsupported operator: " + op.getOp());
      }

      setNodeType(op, resultType);

      return null;
    }

    @Override
    public Void visit(Return ret) {
      System.out.println("Visited Return");

      Expression returnValue = ret.getValue();
      Type returnType = visitAndSetType(returnValue);

      FuncType functionType = (FuncType)currentFunctionSymbol.getType();
      Type currentFunctionReturnType = functionType.getRet();

      if (!returnType.equivalent(currentFunctionReturnType)) {
        setNodeType(ret, new ErrorType("Return type does not match function return type."));
      } else {
        setNodeType(ret, returnType);
      }

      lastStatementReturns = true;

      return null;
    }

    @Override
    public Void visit(StatementList statementList) {
      System.out.println("Visited StatementList");

      lastStatementReturns = false;

      boolean seenReturn = false;
      for (Node stmtNode : statementList.getChildren()) {
        System.out.println("smtlist: here");
        visitAndSetType(stmtNode);
        System.out.println("smtlist: here2");

        if(seenReturn) {
          setNodeType(stmtNode, new ErrorType("Unreachable statement found after return."));
        }

        if(stmtNode instanceof Return) {
          seenReturn = true;
        }
      }

      System.out.println("Exit StatementList");
      return null;
    }

    @Override
    public Void visit(VariableDeclaration variableDeclaration) {
      System.out.println("Visited VariableDeclaration");

      Symbol variableSymbol = variableDeclaration.getSymbol();
      Type variableType = variableSymbol.getType();

      if (!(variableType instanceof IntType) && !(variableType instanceof BoolType)) {
        setNodeType(variableDeclaration, new ErrorType("Variable type must be int or bool."));
      } else {
        setNodeType(variableDeclaration, variableType);
      }

      lastStatementReturns = false;

      return null;
    }
  }
}
