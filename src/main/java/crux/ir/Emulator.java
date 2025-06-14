package crux.ir;

import java.util.*;
import java.io.*;
import crux.ir.insts.*;
import crux.ast.SymbolTable.Symbol;
import crux.ast.types.*;

public class Emulator {
  HashMap<String, Function> functions = new HashMap<>();
  Stack<CallContext> stack = new Stack<>();
  HashMap<Long, Long> globalMap = new HashMap<>();
  HashMap<Symbol, Long> offsetMap = new HashMap<>();

  BufferedReader br;
  PrintStream out;
  public static boolean DEBUG = false;

  public Emulator(Program p, InputStream emulatorInput, OutputStream emulatorOutput) {
    br = new BufferedReader(new InputStreamReader(emulatorInput));
    out = new PrintStream(emulatorOutput);

    for (Iterator<Function> func_it = p.getFunctions(); func_it.hasNext();) {
      Function f = func_it.next();
      functions.put(f.getName(), f);
    }
    long offset = 0;
    for (Iterator<GlobalDecl> glob_it = p.getGlobals(); glob_it.hasNext();) {
      GlobalDecl g = glob_it.next();
      offsetMap.put(g.getSymbol(), offset);
      offset += ((IntegerConstant) g.getNumElement()).getValue() * 8;
    }
  }

  public void run() {
    Function main = functions.get("main");
    CallContext mainc = new CallContext(main, null, null);
    stack.push(mainc);
    while (!stack.isEmpty()) {
      CallContext c = stack.peek();
      if (c.pc == null) {
        // Handle implicit return from void function
        stack.pop();
      } else {
        c.pc.accept(c);
      }
    }
  }

  void debug(String msg) {
    if (DEBUG)
      out.println(msg);
  }

  class CallContext extends InstVisitor {
    Function f;
    Instruction pc;
    HashMap<Variable, Object> localMap;
    LocalVar retval;

    CallContext(Function f, Object[] arguments, LocalVar retval) {
      this.f = f;
      pc = f.getStart();
      localMap = new HashMap<>();
      if (arguments != null) {
        int index = 0;
        for (LocalVar arg : f.getArguments()) {
          localMap.put(arg, arguments[index++]);
        }
      }
      this.retval = retval;
    }

    public void visit(AddressAt i) {
      //System.out.println("Entered AddressAt: " + i);
      Symbol base = i.getBase();
      long address = offsetMap.get(base);
      Value v = i.getOffset();

      if (v != null) {
        address += 8 * ((Long) localMap.get(v));
      }
      localMap.put(i.getDst(), address);

      pc = pc.getNext(0);
      //System.out.println("Exited AddressAt: " + i);
    }

    public void visit(BinaryOperator i) {
      //System.out.println("Entered BinaryOperator");

      Object left = localMap.get(i.getLeftOperand());
      Object right = localMap.get(i.getRightOperand());
      Object result = null;
      switch (i.getOperator()) {
        case Add:
          result = ((Long) left) + ((Long) right);
          break;
        case Sub:
          result = ((Long) left) - ((Long) right);
          break;
        case Mul:
          result = ((Long) left) * ((Long) right);
          break;
        case Div:
          result = ((Long) left) / ((Long) right);
          break;
      }
      localMap.put(i.getDst(), result);
      debug("BinaryOperator: " + i.getDst() + "=" + left + i.getOperator() + right);
      pc = pc.getNext(0);

      //System.out.println("Exited BinaryOperator: " + i);
    }

    public void visit(CompareInst i) {
      //System.out.println("Entered CompareInst");
      //System.out.println("\t" + localMap.get(i.getLeftOperand()) + ", " + localMap.get( localMap.get(i.getRightOperand())));

      Long left = (Long) localMap.get(i.getLeftOperand());
      Long right = (Long) localMap.get(i.getRightOperand());
      Boolean result = null;
      switch (i.getPredicate()) {
        case GE:
          result = left >= right;
          break;
        case GT:
          result = left > right;
          break;
        case LE:
          result = left <= right;
          break;
        case LT:
          result = left < right;
          break;
        case EQ:
          result = left.equals(right);
          break;
        case NE:
          result = !left.equals(right);
          break;
      }

      localMap.put(i.getDst(), result);
      debug("CompareInst: " + i.getDst() + "=" + left + i.getPredicate() + right);

      pc = pc.getNext(0);
      //System.out.println("Exited CompareInst: " + i);
    }

    public void visit(CopyInst i) {
      //System.out.println("Enter CopyInst with " + i);

      Value srcval = i.getSrcValue();
      Object val;
      if (srcval instanceof IntegerConstant) {
        val = Long.valueOf(((IntegerConstant) srcval).getValue());
      } else if (srcval instanceof BooleanConstant) {
        val = ((BooleanConstant) srcval).getValue();
      } else {
        val = localMap.get(srcval);
      }

      debug("CopyInst: " + i.getDstVar() + "=" + val);
      localMap.put(i.getDstVar(), val);
      pc = pc.getNext(0);
      //System.out.println("Exited CopyInst: " + i);
    }

    public void visit(JumpInst i) {
      //System.out.println("Enter JumpInst");

      Boolean pred = (Boolean) localMap.get(i.getPredicate());
      debug("Jump: " + i.getPredicate() + "=" + pred);
      pc = pred ? pc.getNext(1) : pc.getNext(0);

      //System.out.println("Exited JumpInst: " + i);
    }

    public void visit(LoadInst i) {
      //System.out.println("Enter LoadInst");
      AddressVar var = i.getSrcAddress();
      Long address = (Long) localMap.get(var);
      Long value = globalMap.get(address);
      if (value == null) {
        out.println("Reading from uninitialized memory");
        value = Long.valueOf(0);
      }

      Object val;
      if (var.getType() instanceof IntType) {
        val = value;
      } else {
        val = Boolean.valueOf(value != 0);
      }

      debug("LoadInst: " + i.getDst() + "=" + val);
      localMap.put(i.getDst(), val);
      pc = pc.getNext(0);

      //System.out.println("Exited LoadInst: " + i);
    }

    public void visit(NopInst i) {
      // Do nothing
      debug("Nop:");
      pc = pc.getNext(0);
    }

    public void visit(StoreInst i) {
      //System.out.println("Enter StoreInst");

      Value srcval = i.getSrcValue();
      Object val = localMap.get(srcval);
      AddressVar dst = i.getDestAddress();
      Long address = (Long) localMap.get(dst);

      debug("StoreInst: *" + address + "=" + val);

      if (val instanceof Long) {
        globalMap.put(address, (Long) val);
      } else if (val instanceof Boolean) {
        globalMap.put(address, ((Boolean) val) ? Long.valueOf(1) : Long.valueOf(0));
      }
      pc = pc.getNext(0);

      //System.out.println("Exit StoreInst with: " + i);
    }

    public void visit(ReturnInst i) {
      //System.out.println("Enter ReturnInst");
      Object val = i.getReturnValue() != null ? localMap.get(i.getReturnValue()) : null;
      debug("ReturnInst: " + val);
      // Remove ourselves from the stack
      stack.pop();
      // Return value to caller
      if (!stack.isEmpty()) {
        CallContext caller = stack.peek();
        if (retval != null)
          caller.localMap.put(retval, val);
      }

      //System.out.println("Exit ReturnInst with: " + i);
    }

    public void visit(CallInst i) {
      //System.out.println("Enter CallInst");

      List<LocalVar> params = i.getParams();
      Object[] args = new Object[params.size()];
      for (int j = 0; j < args.length; j++) {
        args[j] = localMap.get(params.get(j));
      }

      Symbol varCallee = i.getCallee();
      String fName = varCallee.getName();
      debug("Calling " + fName + " with " + Arrays.toString(args));

      if (fName.equals("readInt")) {
        try {
          out.print("int?");
          String line = br.readLine();
          localMap.put(i.mDestVar, Long.valueOf(line));
        } catch (IOException e) {
          throw new Error("Error in inputting Integer.");
        }
      } else if (fName.equals("readChar")) {
        try {
          int val = br.read();
          if (val == -1)
            throw new Error("Reading past end of stream.");
          localMap.put(i.mDestVar, new Long(val));
        } catch (IOException e) {
          throw new Error("Error in inputting Integer.");
        }
      } else if (fName.equals("printBool")) {
        out.print(args[0]);
      } else if (fName.equals("printInt")) {
        out.print(args[0]);
      } else if (fName.equals("printChar")) {
        out.print((char) ((Long) args[0]).longValue());
      } else if (fName.equals("println")) {
        out.println("");
      } else {
        Function f = functions.get(fName);
        CallContext callee = new CallContext(f, args, (LocalVar) i.getDst());
        stack.push(callee);
      }
      pc = pc.getNext(0);

      //System.out.println("Exit CallInst with: " + i);
    }

    public void visit(UnaryNotInst i) {
      //System.out.println("Enter UnaryNotInst");

      Object left = localMap.get(i.getInner());
      Object result = !((Boolean) left);
      localMap.put(i.getDst(), result);
      debug("UnaryNotInst: " + result);
      pc = pc.getNext(0);

      //System.out.println("Exit UnaryNotInst with: " + i);
    }
  }
}
