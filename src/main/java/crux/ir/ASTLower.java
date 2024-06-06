package crux.ir;

import com.sun.jdi.BooleanValue;
import crux.ast.SymbolTable.Symbol;
import crux.ast.*;
import crux.ast.OpExpr.Operation;
import crux.ast.traversal.NodeVisitor;
import crux.ast.types.*;
import crux.ir.insts.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class InstPair {
  private Instruction start;
  private Instruction end;
  private Value value;

  public InstPair(Instruction start, Instruction end, LocalVar value) {
    this.start = start;
    this.end = end;
    this.value = value;
  }

  public InstPair(Instruction start, Instruction end) {
    this.start = start;
    this.end = end;
  }

  public InstPair(LocalVar value) {
    this.value = value;
  }

  public Instruction getStart() {
    return start;
  }

  public Instruction getEnd() {
    return end;
  }

  public Value getValue() {
    return value;
  }

  public void setValue(Value value) {
    this.value = value;
  }
}


/**
 * Convert AST to IR and build the CFG
 */
public final class ASTLower implements NodeVisitor<InstPair> {
  private Program mCurrentProgram = null;
  private Function mCurrentFunction = null;
  private Map<Symbol, LocalVar> mCurrentLocalVarMap = null;
  private NopInst mCurLoopHead = null;
  private NopInst mCurLoopExit = null;

  /**
   * A constructor to initialize member variables
   */
  public ASTLower() {}

  public Program lower(DeclarationList ast) {
    visit(ast);
    return mCurrentProgram;
  }

  /**
   * Helper Function
   * @param from (end instruction)
   * @param to (start of next)
   */
  private void linkInstructions(Instruction from, Instruction to) {
    System.out.println("Called LinkInstructions(" + from + ", " + to + ")");

    if (from != null && to != null && (from != to)) {
      System.out.println("\tLinked " + from + " to " + to);
      from.setNext(0, to);
    }
  }

  @Override
  public InstPair visit(DeclarationList declarationList) {
    System.out.println("\n---------------------");
    System.out.println("Visited Declaration List");

    mCurrentProgram = new Program();

    //visit each declaration in the list
    for(Node node : declarationList.getChildren()) {
      InstPair curPair = node.accept(this);

      //visit other nodes
//      if (node instanceof FunctionDefinition) {
//        FunctionDefinition funcDef = (FunctionDefinition) node;
//        curPair = visit(funcDef);
//      } else if (node instanceof VariableDeclaration) {
//        VariableDeclaration varAssign = (VariableDeclaration) node;
//        curPair = visit(varAssign);
//      } else if (node instanceof ArrayDeclaration) {
//        ArrayDeclaration arrAssign = (ArrayDeclaration) node;
//        curPair = visit(arrAssign);
//      } else {
//        throw new IllegalArgumentException("Unexpected node type: " + node.getClass().getName());
//      }
    }

    System.out.println("Exited decl list");
    return null;
  }

  /**
   * This visitor should create a Function instance for the functionDefinition node, add parameters
   * to the localVarMap, add the function to the program, and init the function start Instruction.
   */
  @Override
  public InstPair visit(FunctionDefinition functionDefinition) {
    System.out.println("Visited FuncDef List");

    Symbol functionSymbol = functionDefinition.getSymbol();
    mCurrentLocalVarMap = new HashMap<>();
    mCurrentFunction = new Function(functionSymbol.getName(), (FuncType)functionSymbol.getType());;

    // set func arguments
    List<LocalVar> arguments = new ArrayList<>();
    for (Symbol param : functionDefinition.getParameters()) {
      LocalVar localVar = mCurrentFunction.getTempVar(param.getType());
      arguments.add(localVar);
      mCurrentLocalVarMap.put(param, localVar);
    }
    mCurrentFunction.setArguments(arguments);

    mCurrentProgram.addFunction(mCurrentFunction);

    // visit func body
    InstPair bodyPair = functionDefinition.getStatements().accept(this);

    // set the start node of mCurrentFunction
    mCurrentFunction.setStart(bodyPair.getStart());

    //dump mCurrentFunction and localVarMap?
    mCurrentFunction = null;
    mCurrentLocalVarMap = null;
    mCurLoopHead = null;
    mCurLoopExit = null;

    System.out.println("Exit FunctionDef");
    return null;
  }

  @Override
  public InstPair visit(StatementList statementList) {
    System.out.println("Visited Statement List");

    Instruction startInstruction = new NopInst();
    Instruction endInstruction = null;
    System.out.println("Start Instr STMTLIST: " + startInstruction);

    // visit each statement & add an edge in between each InstPair
    for (Node node : statementList.getChildren()) {
      Statement statement = (Statement) node;
      System.out.println("Cur Statement: " + statement);

      InstPair curPair = node.accept(this);
      System.out.println("STMT LIST CUR PAIR: " + curPair.getStart() + ", " + curPair.getEnd());

      if (endInstruction == null) {
        //the start of the statement list links
        startInstruction = curPair.getStart();
      } else {
        linkInstructions(endInstruction, curPair.getStart());
      }

      endInstruction = curPair.getEnd();
      System.out.println("Current stmtlist start + end: " + startInstruction + ", " + endInstruction);
    }

    System.out.println("Statement List Returned: " + startInstruction + ", " + endInstruction);
    return new InstPair(startInstruction, endInstruction);
  }

  /**
   * Declarations, could be either local or Global
   */
  @Override
  public InstPair visit(VariableDeclaration variableDeclaration) {
    System.out.println("Visited variableDeclaration");

    if (mCurrentFunction == null) {
      //global var
      GlobalDecl globalDecl = new GlobalDecl(variableDeclaration.getSymbol(), IntegerConstant.get(mCurrentProgram, 1));
      System.out.println("\nGLOBAL CREATED: " + System.identityHashCode(globalDecl.getSymbol()));
      System.out.println("\tGlobal var created: " + globalDecl);
      mCurrentProgram.addGlobalVar(globalDecl);
    } else {
      //local var
      LocalVar tempVar = mCurrentFunction.getTempVar(variableDeclaration.getSymbol().getType());
      System.out.println("\nLOCAL CREATED: " + System.identityHashCode(tempVar));
      mCurrentLocalVarMap.put(variableDeclaration.getSymbol(), tempVar);
    }

    System.out.println("Exit variableDeclaration");

    NopInst nop = new NopInst();
    return new InstPair(nop, nop);
  }

  /**
   * Create a declaration for array and connected it to the CFG
   */
  @Override
  public InstPair visit(ArrayDeclaration arrayDeclaration) {
    System.out.println("Visited arrayDeclaration");

    ArrayType arr = (ArrayType) arrayDeclaration.getSymbol().getType();
    long arrSize = arr.getExtent();

    //all array declarations are global
    GlobalDecl globalDecl = new GlobalDecl(arrayDeclaration.getSymbol(), IntegerConstant.get(mCurrentProgram, arrSize)); // You need to handle the array size appropriately
    mCurrentProgram.addGlobalVar(globalDecl);

    System.out.println("Exit arrayDeclaration");
    NopInst nop = new NopInst();
    return new InstPair(nop, nop);
  }

  /**
   * LookUp the name in the map(s). For globals, we should do a load to get the value to load into a
   * LocalVar.
   */
  @Override
  public InstPair visit(VarAccess name) {
    System.out.println("Visited VarAccess");

    Symbol varSymbol = name.getSymbol();

    if(mCurrentLocalVarMap.containsKey(varSymbol)) {
      //local var
      NopInst nop = new NopInst();
      System.out.println("VarAccess Returned (Nops): " + nop);
      return new InstPair(nop, nop, mCurrentLocalVarMap.get(varSymbol));
    } else {
      //global var
      AddressVar destAddressVar = new AddressVar(varSymbol.getType());
      AddressAt addressAt = new AddressAt(destAddressVar, findGlobalSymbol(varSymbol));

      LoadInst loadInst = new LoadInst(mCurrentFunction.getTempVar(varSymbol.getType()), destAddressVar);
      System.out.println("DESTINATION: " + loadInst.getDst());

      linkInstructions(addressAt, loadInst);

      System.out.println("VarAccess Returned: " + addressAt + ", " + loadInst);
      return new InstPair(addressAt, loadInst, loadInst.getDst());
    }
  }

  /**
   * Helper function for Assignment
   * @param program
   * @param symbolName
   * @return
   */
  private Symbol findGlobalSymbol(Symbol symbol) {
    String symbolName = symbol.getName();
    Iterator<GlobalDecl> globalsIterator = mCurrentProgram.getGlobals();

    while (globalsIterator.hasNext()) {
      GlobalDecl globalDecl = globalsIterator.next();
      Symbol currentSymbol = globalDecl.getSymbol();

      if (currentSymbol.getName().equals(symbolName)) {
        return currentSymbol;
      }
    }
    return null;
  }
  /**
   * If the location is a VarAccess to a LocalVar, copy the value to it. If the location is a
   * VarAccess to a global, store the value. If the location is ArrayAccess, store the value.
   */
  @Override
  public InstPair visit(Assignment assignment) {
    System.out.println("Visited Assignment");

    Expression locationExpr = assignment.getLocation();
    Expression valueExpr = assignment.getValue();
    //visit rhs only
    InstPair pair = valueExpr.accept(this);

    System.out.println("\tRHS Returned pair: " + pair.getStart() + ", " + pair.getEnd());

    if (locationExpr instanceof VarAccess) {
      VarAccess varAccess = (VarAccess) locationExpr;
      Symbol varSymbol = varAccess.getSymbol();
      System.out.println("\tCUR SYMBOL: " + varSymbol);

      if (mCurrentLocalVarMap.containsKey(varSymbol)) {
        //local var assignment
        System.out.println("LOCAL var: " + System.identityHashCode(varSymbol));
        LocalVar localVar = mCurrentLocalVarMap.get(varSymbol);
        CopyInst copyInst = new CopyInst(localVar, pair.getValue());
        System.out.println("\tCreated copy for assignment: " + copyInst + " for value: " + pair.getValue());

        linkInstructions(pair.getEnd(), copyInst);

        System.out.println("Assignment Returned (Local Var): " + pair.getStart() + ", " + copyInst);
        return new InstPair(pair.getStart(), copyInst);
      } else {
        //global var assignment
        System.out.println("GLOBAL var: " + System.identityHashCode(varSymbol));
        AddressVar destAddressVar = new AddressVar(varSymbol.getType());
        AddressAt addressAt = new AddressAt(destAddressVar, findGlobalSymbol(varSymbol));
        StoreInst storeInst = new StoreInst((LocalVar) pair.getValue(), destAddressVar);

        linkInstructions(addressAt, pair.getStart());
        linkInstructions(pair.getEnd(), storeInst);

        System.out.println("Assignment Returned (Global Var): " + pair.getStart() + ", " + storeInst);
        return new InstPair(addressAt, storeInst);
      }
    } else if (locationExpr instanceof ArrayAccess) {
      //array assignment
      ArrayAccess arrayAccess = (ArrayAccess) locationExpr;
      Symbol arrSymbol = arrayAccess.getBase();
      Expression indexExpr = arrayAccess.getIndex();

      InstPair indexPair = indexExpr.accept(this);

      AddressVar destAddressVar = new AddressVar(arrSymbol.getType());
      //AddressAt addressAt = new AddressAt(destAddressVar, arrSymbol, (LocalVar) indexPair.getValue());
      AddressAt addressAt = new AddressAt(destAddressVar, findGlobalSymbol(arrSymbol), (LocalVar) indexPair.getValue());
      StoreInst storeInst = new StoreInst((LocalVar) pair.getValue(), destAddressVar);

      linkInstructions(indexPair.getEnd(), addressAt);
      linkInstructions(addressAt, pair.getStart());
      linkInstructions(pair.getEnd(), storeInst);

      System.out.println("Assignment Returned (Array): " + indexPair.getStart() + ", " + storeInst);
      return new InstPair(indexPair.getStart(), storeInst);
    }

    throw new UnsupportedOperationException("Assignment type error");
  }

  /**
   * Lower a Call.
   */
  @Override
  public InstPair visit(Call call) {
    System.out.println("Visited Call");
    List<LocalVar> argVars = new ArrayList<>();
    Instruction startInst = null;
    Instruction prevEnd = null;

    // visit each argument and add to list as LocalVar
    for (Node arg : call.getChildren()) {
      InstPair argPair = arg.accept(this);

      if (argPair != null) {
        if (argPair.getStart() != null && prevEnd != null) {
          linkInstructions(prevEnd, argPair.getStart());
        }
        prevEnd = argPair.getEnd();
        if (startInst == null) {
          startInst = argPair.getStart();
        }
        argVars.add((LocalVar) argPair.getValue());
      }
    }

    // check if func return is void
    LocalVar returnVar = null;
    Type type = call.getCallee().getType();
    Type funcReturnType = ((FuncType)(type)).getRet();

    if (!(funcReturnType instanceof VoidType)) {
      returnVar = mCurrentFunction.getTempVar(funcReturnType); //temp return val
    }

    // create new CallInst
    CallInst callInst = new CallInst(returnVar, call.getCallee(), argVars);
    if(prevEnd != null)
      linkInstructions(prevEnd, callInst);
    if(startInst == null) {
      startInst = callInst;
    }

    System.out.println("Call Returned: " + startInst + ", " + callInst);
    return new InstPair(startInst, callInst, returnVar);
  }

  private BinaryOperator.Op convertBin(OpExpr.Operation operation) {
    switch (operation) {
      case ADD:
        return BinaryOperator.Op.Add;
      case SUB:
        return BinaryOperator.Op.Sub;
      case MULT:
        return BinaryOperator.Op.Mul;
      case DIV:
        return BinaryOperator.Op.Div;
      default:
        throw new IllegalArgumentException("Unsupported operation: " + operation);
    }
  }

  CompareInst.Predicate convertPredicate(OpExpr.Operation operation) {
    switch (operation) {
      case GE:
        return CompareInst.Predicate.GE;
      case LE:
        return CompareInst.Predicate.LE;
      case GT:
        return CompareInst.Predicate.GT;
      case LT:
        return CompareInst.Predicate.LT;
      case EQ:
        return CompareInst.Predicate.EQ;
      case NE:
        return CompareInst.Predicate.NE;
      default:
        throw new IllegalArgumentException("Unsupported operation: " + operation);
    }
  }

  /**
   * Handle operations like arithmetics and comparisons. Also handle logical operations (and,
   * or, not).
   */
  @Override
  public InstPair visit(OpExpr operation) {
    System.out.println("Visited OpExpr");

    System.out.println("\tVISITING LEFT!: " + operation.getLeft());
    InstPair leftPair = operation.getLeft().accept(this);
    LocalVar leftVar = (LocalVar) leftPair.getValue();
    Instruction startInst = leftPair.getStart();
    System.out.println("\t\tLEFT PAIR: " + leftPair.getStart() + ", " + leftPair.getEnd() + ", " + leftPair.getValue());

    System.out.println("\tVISITING RIGHT!: " + operation.getRight());
    InstPair rightPair = null;
    Instruction endInstr = null;
    LocalVar rightVar = null;
    if(operation.getRight() != null) {
      rightPair = operation.getRight().accept(this);
      endInstr = rightPair.getEnd();
      rightVar = (LocalVar) rightPair.getValue();
    }

    //double check this
    LocalVar resultVar = mCurrentFunction.getTempVar(new BoolType());

    System.out.println("\tOP: " + operation.getOp());
    switch (operation.getOp()) {
      case ADD:
      case SUB:
      case MULT:
      case DIV:
        BinaryOperator.Op binaryOp = convertBin(operation.getOp());
        BinaryOperator opInstArithmetic = new BinaryOperator(binaryOp, resultVar, leftVar, rightVar);

        linkInstructions(leftPair.getEnd(), rightPair.getStart());
        linkInstructions(rightPair.getEnd(), opInstArithmetic);

        endInstr = opInstArithmetic;
        break;
      case GE:
      case GT:
      case LE:
      case LT:
      case EQ:
      case NE:
          CompareInst.Predicate predicate = convertPredicate(operation.getOp());
          System.out.println("Making compareInst --> " + leftVar + ", " + rightVar);
          CompareInst cmpInst = new CompareInst(resultVar, predicate, leftVar, rightVar); //this is correct

          linkInstructions(leftPair.getEnd(), rightPair.getStart());
          linkInstructions(rightPair.getEnd(), cmpInst);

          endInstr = cmpInst;
        break;
      case LOGIC_NOT:
        UnaryNotInst notInst = new UnaryNotInst(resultVar, leftVar);

        linkInstructions(leftPair.getEnd(), notInst);
        endInstr = notInst;
        break;
      case LOGIC_AND:
        JumpInst andJump = new JumpInst((LocalVar) leftPair.getValue());
        linkInstructions(leftPair.getEnd(), andJump);

        CopyInst andLeftCopy = new CopyInst(resultVar, BooleanConstant.get(mCurrentProgram, false));
        CopyInst andRightCopy = new CopyInst(resultVar, rightPair.getValue());

        andJump.setNext(0, andLeftCopy); //true branch
        andJump.setNext(1, rightPair.getStart()); //false branch

        NopInst andMerge = new NopInst();
        linkInstructions(rightPair.getEnd(), andRightCopy);
        linkInstructions(andRightCopy, andMerge);
        linkInstructions(andLeftCopy, andMerge);

        endInstr = andMerge;

        break;
      case LOGIC_OR:
        JumpInst orJump = new JumpInst((LocalVar) leftPair.getValue());
        linkInstructions(leftPair.getEnd(), orJump);

        CopyInst orLeftCopy = new CopyInst(resultVar, BooleanConstant.get(mCurrentProgram, true));
        CopyInst orRightCopy = new CopyInst(resultVar, rightPair.getValue());

        orJump.setNext(0, rightPair.getStart()); //false branch
        orJump.setNext(1, orLeftCopy); //true branch

        NopInst orMerge = new NopInst();
        linkInstructions(rightPair.getEnd(), orRightCopy);
        linkInstructions(orRightCopy, orMerge);
        linkInstructions(orLeftCopy, orMerge);

        endInstr = orMerge;
        break;
    }

    System.out.println("OpExpr Returned: " + startInst + ", " + endInstr);
    return new InstPair(startInst, endInstr, resultVar);
  }

  /**
   * It should compute the address into the array, do the load, and return the value in a LocalVar.
   */
  @Override
  public InstPair visit(ArrayAccess access) {
    System.out.println("Visited ArrayAccess");

    Symbol baseSymbol = access.getBase();
    Expression indexExpr = access.getIndex();

    InstPair indexPair = indexExpr.accept(this);

    //get element type
    ArrayType arrayType = (ArrayType) (baseSymbol.getType());
    Type elementType = arrayType.getBase();

    //create cfg nodes
    System.out.println("\tARRAY BASE TYPE: " + elementType.getClass());
    AddressVar arrayElementAddress = new AddressVar(elementType);
    //AddressAt addressAtInst = new AddressAt(arrayElementAddress, baseSymbol, (LocalVar) indexPair.getValue());
    AddressAt addressAtInst = new AddressAt(arrayElementAddress, findGlobalSymbol(baseSymbol), (LocalVar) indexPair.getValue());

    LocalVar tempVar = mCurrentFunction.getTempVar(elementType);
    LoadInst loadInst = new LoadInst(tempVar, arrayElementAddress);

    linkInstructions(indexPair.getEnd(), addressAtInst);
    linkInstructions(addressAtInst, loadInst);

    System.out.println("ArrayAccess Returned " + indexPair.getStart() + ", " + loadInst);
    return new InstPair(indexPair.getStart(), loadInst, loadInst.getDst());
  }

  /**
   * Copy the literal into a tempVar
   */
  @Override
  public InstPair visit(LiteralBool literalBool) {
    System.out.println("Visited LiteralBool");

    LocalVar destVar = mCurrentFunction.getTempVar(new BoolType());
    Value srcValue = BooleanConstant.get(mCurrentProgram, literalBool.getValue());

    CopyInst copyInst = new CopyInst(destVar, srcValue);
    System.out.println("CREATED LITERAL BOOL " + copyInst + " with value: " + literalBool.getValue());

    System.out.println("Exit LiteralBool");
    return new InstPair(copyInst, copyInst, destVar);
  }

  /**
   * Copy the literal into a tempVar
   */
  @Override
  public InstPair visit(LiteralInt literalInt) {
    System.out.println("Visited LiteralInt");

    LocalVar destVar = mCurrentFunction.getTempVar(new IntType());
    System.out.println("New temp var: " + destVar);
    Value source = IntegerConstant.get(mCurrentProgram, literalInt.getValue());

    CopyInst copyInst = new CopyInst(destVar, source);
    System.out.println("\tLiteral int value: " + literalInt.getValue());

    //make end null or ?
    System.out.println("Exit LiteralInt");
    return new InstPair(copyInst, copyInst, destVar);
  }

  /**
   * Lower a Return.
   */
  @Override
  public InstPair visit(Return ret) {
    System.out.println("Visited Return");

    InstPair valuePair = ret.getValue().accept(this);

    ReturnInst returnInst = new ReturnInst((LocalVar) valuePair.getValue());

    linkInstructions(valuePair.getEnd(), returnInst);
    return new InstPair(valuePair.getStart(), returnInst);
  }

  /**
   * Break Node
   */
  @Override
  public InstPair visit(Break brk) {
    System.out.println("Visited Break");
    NopInst nop = new NopInst();
    System.out.println("\n!!!BREAK NOP NODE CREATED: " + nop);
    return new InstPair(mCurLoopExit, nop);
  }

  /**
   * Continue Node
   */
  @Override
  public InstPair visit(Continue cont) {
    System.out.println("Visited Continue");
    return new InstPair(mCurLoopHead, new NopInst());
  }

  /**
   * Implement If Then Else statements.
   */
  @Override
  public InstPair visit(IfElseBranch ifElseBranch) {
    System.out.println("Visited IfElseBranch");

    InstPair condPair = ifElseBranch.getCondition().accept(this);
    System.out.println("\tCONDPAIR: " + condPair.getStart() + ", " + condPair.getEnd() + ", " + condPair.getValue());

    JumpInst jumpInst = new JumpInst((LocalVar) condPair.getValue());
    System.out.println("\n!!!!JUMP INSTR CREATED!!!!: " + jumpInst);

    //visit then and else body
    InstPair thenPair = ifElseBranch.getThenBlock().accept(this);
    System.out.println("\tTHEN PAIR: " + thenPair.getStart() + ", " + thenPair.getEnd() + ", " + thenPair.getValue());
    InstPair elsePair = ifElseBranch.getElseBlock().accept(this);;
    System.out.println("\tELSE PAIR: " + elsePair.getStart() + ", " + elsePair.getEnd() + ", " + elsePair.getValue());

    linkInstructions(condPair.getEnd(), jumpInst);

    jumpInst.setNext(0, elsePair.getStart()); //false branch
    jumpInst.setNext(1, thenPair.getStart()); //true branch

    NopInst mergeInst = new NopInst();
    System.out.print("\n!!!!MERGE INSTR CREATED!!!!: ");
    System.out.println(mergeInst + "\n");

    System.out.println("THEN PAIR END: " + thenPair.getEnd());
    linkInstructions(thenPair.getEnd(), mergeInst);

    if(elsePair.getEnd() != null)
      linkInstructions(elsePair.getEnd(), mergeInst);
    else
      linkInstructions(elsePair.getStart(), mergeInst);

    System.out.println("Returned from IfElseBranch: " + condPair.getStart() + ", " + mergeInst);
    return new InstPair(condPair.getStart(), mergeInst);
  }

  /**
   * Implement loops.
   */
  @Override
  public InstPair visit(Loop loop) {
    System.out.println("Visited Loop");

    NopInst loopHead = new NopInst();
    NopInst loopExit = new NopInst();

    NopInst prevLoopHead = mCurLoopHead;
    NopInst prevLoopExit = mCurLoopExit;

    mCurLoopHead = loopHead;
    mCurLoopExit = loopExit;

    //visit loop body
    InstPair bodyPair = loop.getBody().accept(this);
    System.out.println("\n!!!Loop Body Pair!!!: " + bodyPair.getStart() + ", " + bodyPair.getEnd());

    linkInstructions(bodyPair.getEnd(), bodyPair.getStart());

    mCurLoopHead = prevLoopHead;
    mCurLoopExit = prevLoopExit;

    linkInstructions(loopHead, bodyPair.getStart());

    System.out.println("Loop returned: " + loopHead + ", " + loopExit);
    return new InstPair(loopHead, loopExit);
  }
}
