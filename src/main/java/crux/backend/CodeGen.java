package crux.backend;

import com.sun.jdi.BooleanType;
import crux.ast.SymbolTable.Symbol;
import crux.ast.types.BoolType;
import crux.ast.types.IntType;
import crux.ir.*;
import crux.ir.insts.*;
import crux.printing.IRValueFormatter;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Convert the CFG into Assembly Instructions
 */
public final class CodeGen extends InstVisitor {
  private final Program p;
  private final CodePrinter out;
  private HashMap<Instruction, String> functionLabels;
  private HashMap<Variable, Integer> varIndexMap;
  private int varIndex;

  private final IRValueFormatter irFormat = new IRValueFormatter();
  private void printInstructionInfo(Instruction i, String visitMethodName) {
    var info = String.format("/* %s */", i.format(irFormat));
    var info2 = String.format("/* %s */", visitMethodName);
    out.printCode(info);
    out.printCode(info2);
  }

  public CodeGen(Program p) {
    this.p = p;
    // Do not change the file name that is outputted or it will
    // break the grader!
    functionLabels = new HashMap<>();
    varIndexMap = new HashMap<>();
    varIndex = 0;

    out = new CodePrinter("a.s");
  }

  /**
   * It should allocate space for globals call genCode for each Function
   */
  public void genCode() {
    //TODO
    for(Iterator<GlobalDecl> glob_it = p.getGlobals(); glob_it.hasNext();) {
      GlobalDecl g = glob_it.next();
      String name = g.getSymbol().getName();
      long size = g.getNumElement().getValue() * 8; //not sure abt this
      out.printCode(".comm " + name + ", " + size + ", 8");
    }

    int count[] = new int[1];
    for(Iterator<Function> func_it = p.getFunctions(); func_it.hasNext();) {
      Function f = func_it.next();
      genCode(f, count);
    }

    out.close();
  }

  private void genCode(Function f, int count[]) {
    HashMap<Instruction, String> labels = f.assignLabels(count);
    functionLabels.putAll(labels);

    out.printCode(".globl " + f.getName());
    out.printLabel(f.getName() + ":");

    int numSlots = f.getNumTempVars() + f.getNumTempAddressVars();
    if (numSlots % 2 != 0) {
      numSlots++;
    }
    out.printCode("enter $(" + 8 * numSlots + "), $0");

    List<LocalVar> args = f.getArguments();
    String[] argRegisters = {"%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9"};
    for (int i = 0; i < args.size() && i < argRegisters.length; i++) {
      out.printCode("movq " + argRegisters[i] + ", -" + (8 * (i + 1)) + "(%rbp)");
      varIndexMap.put(args.get(i), i + 1);
    }

    generateBody(f, labels);

    out.printCode("leave");
    out.printCode("ret");
  }

  /**
   * Helper function to generate code for function body
   *  Linearize CFG using jumps and labels
   *  Use DFS traversal
   *  Refer to Function.assignLabels(int count[])
   * @param f
   * @param labels
   */
  private void generateBody(Function f, HashMap<Instruction, String> labels) {
    Stack<Instruction> toVisit = new Stack<>();
    HashSet<Instruction> visited = new HashSet<>();
    toVisit.push(f.getStart());

    while (!toVisit.isEmpty()) {
      Instruction inst = toVisit.pop();

      if (visited.contains(inst)) {
        if (labels.containsKey(inst)) {
          out.printCode("jmp " + labels.get(inst));
        }
        continue;
      }
      visited.add(inst);

      if (labels.containsKey(inst)) {
        out.printLabel(labels.get(inst) + ":");
      }

      inst.accept(this);

      if (inst.numNext() == 0) {
        // Print epilogue only once
        out.printCode("leave");
        out.printCode("ret");
      } else {
        // Push next instructions to the stack (0 first, then 1)
        for (int i = inst.numNext() - 1; i >= 0; i--) {
          toVisit.push(inst.getNext(i));
        }
      }
    }
  }

  //------------Local Var Helper Functions---------
  private int getVarIndex(Variable var) {
    if (!varIndexMap.containsKey(var)) {
      varIndexMap.put(var, ++varIndex);
    }
    return varIndexMap.get(var);
  }

  private void printVarToReg(String reg, Variable var) {
    int index = getVarIndex(var);
    out.printCode("movq -" + (8 * index) + "(%rbp), " + reg);
  }

  private void printRegToVar(Variable var, String reg) {
    int index = getVarIndex(var);
    out.printCode("movq " + reg + ", -" + (8 * index) + "(%rbp)");
  }

  private void printImmediateToReg(long imm, String reg) {
    out.printCode("movq $" + imm + ", " + reg);
  }

  //--------------Visitor Functions--------------
  public void visit(AddressAt i) {
    printInstructionInfo(i, "AddressAt");

    AddressVar destVar = i.getDst();
    Symbol baseSymbol = i.getBase();
    LocalVar offsetVar = i.getOffset();

    out.printCode("movq " + baseSymbol.getName() + "@GOTPCREL(%rip), %r11");

    if (offsetVar != null) {
      int offsetIndex = getVarIndex(offsetVar);

      out.printCode("movq -" + (8 * offsetIndex) + "(%rbp), %r10");
      out.printCode("imulq $8, %r10");
      out.printCode("addq %r10, %r11");
    }

    int destIndex = getVarIndex(destVar);
    out.printCode("movq %r11, -" + (8 * destIndex) + "(%rbp)");
  }

  public void visit(BinaryOperator i) {
    printInstructionInfo(i, "BinaryOperator");

    LocalVar destVar = i.getDst();
    LocalVar lhsVar = i.getLeftOperand();
    LocalVar rhsVar = i.getRightOperand();

    String dest = "-" + (8 * getVarIndex(destVar)) + "(%rbp)";
    String lhs = "-" + (8 * getVarIndex(lhsVar)) + "(%rbp)";
    String rhs = "-" + (8 * getVarIndex(rhsVar)) + "(%rbp)";

    switch (i.getOperator()) {
      case Add:
        out.printCode("movq " + lhs + ", %r10");
        out.printCode("addq " + rhs + ", %r10");
        out.printCode("movq %r10, " + dest);
        break;
      case Sub:
        out.printCode("movq " + lhs + ", %r10");
        out.printCode("subq " + rhs + ", %r10");
        out.printCode("movq %r10, " + dest);
        break;
      case Mul:
        out.printCode("movq " + lhs + ", %rax");
        out.printCode("imulq " + rhs + ", %rax");
        out.printCode("movq %rax, " + dest);
        break;
      case Div:
        out.printCode("movq " + lhs + ", %rax");
        out.printCode("cqto");
        out.printCode("idivq " + rhs);
        out.printCode("movq %rax, " + dest);
        break;
      default:
        throw new IllegalArgumentException("Unsupported binary operator: " + i.getOperator());
    }
  }

  public void visit(CompareInst i) {
    printInstructionInfo(i, "CompareInst");

    LocalVar destVar = i.getDst();
    LocalVar lhsVar = i.getLeftOperand();
    LocalVar rhsVar = i.getRightOperand();

    String dest = "-" + (8 * getVarIndex(destVar)) + "(%rbp)";
    String lhs = "-" + (8 * getVarIndex(lhsVar)) + "(%rbp)";
    String rhs = "-" + (8 * getVarIndex(rhsVar)) + "(%rbp)";

    out.printCode("movq $0, %rax");
    out.printCode("movq $1, %r10");
    out.printCode("movq " + lhs + ", %r11");
    out.printCode("cmp " + rhs + ", %r11");

    switch (i.getPredicate()) {
      case GE:
        out.printCode("cmovge %r10, %rax");
        break;
      case GT:
        out.printCode("cmovg %r10, %rax");
        break;
      case LE:
        out.printCode("cmovle %r10, %rax");
        break;
      case LT:
        out.printCode("cmovl %r10, %rax");
        break;
      case EQ:
        out.printCode("cmove %r10, %rax");
        break;
      case NE:
        out.printCode("cmovne %r10, %rax");
        break;
      default:
        throw new IllegalArgumentException("Unsupported compare predicate: " + i.getPredicate());
    }

    out.printCode("movq %rax, " + dest);
  }

  public void visit(CopyInst i) {
    printInstructionInfo(i, "CopyInst");

    LocalVar destVar = i.getDstVar();
    Value srcValue = i.getSrcValue();

    String src;
    if (srcValue instanceof LocalVar) {
      int srcIndex = getVarIndex((LocalVar) srcValue);
      src = "-" + (8 * srcIndex) + "(%rbp)";
    } else if (srcValue.getType() instanceof IntType) {
      src = "$" + (((IntegerConstant)i.getSrcValue()).getValue());
    } else if (srcValue.getType() instanceof BoolType) {
      src = "$" + ((((BooleanConstant)srcValue).getValue()) ? 1 : 0);
    } else {
      throw new IllegalArgumentException("Unsupported source value type");
    }

    int destIndex = getVarIndex(destVar);
    String dest = "-" + (8 * destIndex) + "(%rbp)";

    if (src.startsWith("-")) {
      out.printCode("movq " + src + ", %r10");
      out.printCode("movq %r10, " + dest);
    } else {
      out.printCode("movq " + src + ", " + dest);
    }
  }

  public void visit(JumpInst i) {
    printInstructionInfo(i, "JumpInst");

    LocalVar predicate = i.getPredicate();
    String thenLabel = functionLabels.get(i.getNext(1));

    // Load the predicate into %r10
    int predIndex = getVarIndex(predicate);
    out.printCode("movq -" + (8 * predIndex) + "(%rbp), %r10");

    // Compare the predicate with 1
    out.printCode("cmp $1, %r10");

    // Conditional jump to the then block label if predicate is true
    out.printCode("je " + thenLabel);
  }

  public void visit(LoadInst i) {
    printInstructionInfo(i, "LoadInst");

    LocalVar destVar = i.getDst();
    AddressVar srcAddress = i.getSrcAddress();

    int srcIndex = getVarIndex(srcAddress);
    String src = "-" + (8 * srcIndex) + "(%rbp)";

    int destIndex = getVarIndex(destVar);
    String dest = "-" + (8 * destIndex) + "(%rbp)";

    out.printCode("movq " + src + ", %r10");
    out.printCode("movq 0(%r10), %r11");
    out.printCode("movq %r11, " + dest);
  }

  public void visit(NopInst i) {
    //do nothing
  }

  public void visit(StoreInst i) {
    printInstructionInfo(i, "StoreInst");

    Value srcValue = i.getSrcValue();
    AddressVar destAddr = i.getDestAddress();

    String src;
    if (srcValue instanceof LocalVar) {
      int srcIndex = getVarIndex((LocalVar) srcValue);
      src = "-" + (8 * srcIndex) + "(%rbp)";
    } else if (srcValue instanceof IntegerConstant) {
      src = "$" + ((IntegerConstant) srcValue).getValue();
    } else if (srcValue instanceof BooleanConstant) {
      src = "$" + (((BooleanConstant) srcValue).getValue() ? 1 : 0);
    } else {
      throw new IllegalArgumentException("Unsupported source value type");
    }

    int destIndex = getVarIndex(destAddr);
    String destAddress = "-" + (8 * destIndex) + "(%rbp)";

    out.printCode("movq " + destAddress + ", %r10");

    if (src.startsWith("$")) {
      out.printCode("movq " + src + ", " + destAddress);
    } else {
      out.printCode("movq " + src + ", %r11");
      out.printCode("movq %r11, 0(%r10)");
    }
  }

  public void visit(ReturnInst i) {
    printInstructionInfo(i, "ReturnInst");

    Value returnValue = i.getReturnValue();

    if (returnValue != null) {
      String src;
      if (returnValue instanceof LocalVar) {
        int srcIndex = getVarIndex((LocalVar) returnValue);
        src = "-" + (8 * srcIndex) + "(%rbp)";
      } else if (returnValue.getType() instanceof IntType) {
        src = "$" + (((IntegerConstant)returnValue).getValue());
      } else if (returnValue.getType() instanceof BoolType) {
        src = "$" + ((((BooleanConstant)returnValue).getValue()) ? 1 : 0);
      } else {
        throw new IllegalArgumentException("Unsupported return value type");
      }

      if (src.startsWith("-")) {
        out.printCode("movq " + src + ", %r10");
        out.printCode("movq %r10, %rax");
      } else {
        out.printCode("movq " + src + ", %rax");
      }
    }

    out.printCode("leave");
    out.printCode("ret");
  }

  public void visit(CallInst i) {
    printInstructionInfo(i, "CallInst");

    String[] argRegisters = {"%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9"};
    List<LocalVar> args = i.getParams();
    String calleeName = i.getCallee().getName();

    for (int j = 0; j < args.size(); j++) {
      LocalVar arg = args.get(j);
      if (j < argRegisters.length) {
        //first 6 args moved to register
        printVarToReg(argRegisters[j], arg);
      } else {
        //otherwise the stack
        int offset = (j - argRegisters.length + 2) * 8; // 16(%rbp) is the first stack argument slot
        int varIndex = getVarIndex(arg);
        out.printCode("movq -" + (8 * varIndex) + "(%rbp), %r10");
        out.printCode("movq %r10, -" + offset + "(%rbp)");
      }
    }

    out.printCode("call " + calleeName);

    LocalVar destVar = i.getDst();
    if (destVar != null) {
      printRegToVar(destVar, "%rax");
    }
  }

  public void visit(UnaryNotInst i) {
    LocalVar destVar = i.getDst();
    LocalVar operand = i.getInner();

    out.printCode("movq $1, %r11");

    String src;
    if (operand instanceof LocalVar) {
      int srcIndex = getVarIndex(operand);
      src = "-" + (8 * srcIndex) + "(%rbp)";
    } else if (operand.getType() instanceof BoolType) {
      src = "$" + operand.getName(); //not 100% sure
    } else {
      throw new IllegalArgumentException("Unsupported operand type for unary not");
    }

    out.printCode("movq " + src + ", %r10");
    out.printCode("subq %r10, %r11");

    int destIndex = getVarIndex(destVar);
    out.printCode("movq %r11, -" + (8 * destIndex) + "(%rbp)");
  }
}
