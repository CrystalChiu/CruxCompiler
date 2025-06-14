package crux;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

final class CompilerStageTests {
  /**
   * TODO: Change variable TEST_TO_RUN to run tests for other stages. For example, to run tests for
   * all stages: private final String[] TEST_TO_RUN = {"stage1", "stage2", "stage3", "stage4",
   * "stage5"};
   */
  private final String[] TEST_TO_RUN = {"stage5"};

  private boolean skipStage(String stageName) {
    return List.of(TEST_TO_RUN).stream().noneMatch(s -> s.toLowerCase().equals(stageName));
  }

  static String sanitize(String str) {
    return str.replace("\r", "");
  }

  class StringPair {
    public StringPair(String a, String b) {
      this.a = a;
      this.b = b;
    }

    public String a;
    public String b;
  }

  public static final int TIMEOUT = 20;

  @TestFactory
  Stream<DynamicTest> parseTree() throws IOException {
    if (skipStage("stage1")) {
      return Stream.empty();
    }

    var tests = getTests("parse-tree");
    return tests.stream()
            .map(test -> dynamicTest(test.in, () -> {
      ExecutorService executor = Executors.newSingleThreadExecutor();
      Future<StringPair> future = executor.submit(new Callable<StringPair>() {
        public StringPair call() throws IOException {
          var loader = getClass().getClassLoader();
          var in = loader.getResourceAsStream(test.in);

          var outStream = new ByteArrayOutputStream();
          var errStream = new ByteArrayOutputStream();
          var driver = new Driver(new PrintStream(outStream), new PrintStream(errStream));

          driver.setInputStream(in);
          driver.enablePrintParseTree();

          var status = driver.run();
          var actualOutput = status == State.Finished ? outStream.toString() : errStream.toString();

          var expectedOutput = readResourceToString(test.out);
          return new StringPair(expectedOutput, actualOutput);
        }
      });
      StringPair sp = null;
      try {
        sp = future.get(TIMEOUT, TimeUnit.SECONDS);
      } catch (TimeoutException e) {
        future.cancel(true);
      }

      if (sp == null) {
        Assertions.fail(String.format("Timeout for parse tree for program %s.", test.in));
      } else {
        Assertions.assertEquals(sanitize(sp.a).trim(), sanitize(sp.b).trim(),
                String.format("Parse tree for program %s differs from expected output. Expected: %s. \nActual: %s", test.in, sp.a, sp.b));
      }
    }));
  }

  @TestFactory
  Stream<DynamicTest> ast() throws IOException {
    if (skipStage("stage2")) {
      return Stream.empty();
    }

    var tests = getTests("ast");
    return tests.stream()
            .map(test -> dynamicTest(test.in, () -> {
      ExecutorService executor = Executors.newSingleThreadExecutor();
      Future<StringPair> future = executor.submit(new Callable<StringPair>() {
        public StringPair call() throws IOException {
          var loader = getClass().getClassLoader();
          var in = loader.getResourceAsStream(test.in);

          var outStream = new ByteArrayOutputStream();
          var errStream = new ByteArrayOutputStream();
          var driver = new Driver(new PrintStream(outStream), new PrintStream(errStream));

          driver.setInputStream(in);
          driver.enablePrintAst();

          var status = driver.run();
          var actualOutput = status == State.Finished ? outStream.toString() : errStream.toString();

          var expectedOutput = readResourceToString(test.out);
          return new StringPair(expectedOutput, actualOutput);
        }
      });
      StringPair sp = null;
      try {
        sp = future.get(TIMEOUT, TimeUnit.SECONDS);
      } catch (TimeoutException e) {
        future.cancel(true);
      }
      if (sp == null) {
        Assertions.fail(String.format("Timeout for AST for program %s.", test.in));
      } else {
        Assertions.assertEquals(sanitize(sp.a).trim(), sanitize(sp.b).trim(),
            String.format("AST for program %s differs from expected output.", test.in));
      }
    }));
  }

  @TestFactory
  Stream<DynamicTest> typeCheck() throws IOException {
    if (skipStage("stage3")) {
      return Stream.empty();
    }

    var tests = getTests("type-check");
    return tests.stream()
            .limit(49)
            .map(test -> dynamicTest(test.in, () -> {
      ExecutorService executor = Executors.newSingleThreadExecutor();
      Future<StringPair> future = executor.submit(new Callable<StringPair>() {
        public StringPair call() throws IOException {
          var loader = getClass().getClassLoader();
          var in = loader.getResourceAsStream(test.in);

          var outStream = new ByteArrayOutputStream();
          var outPrintStream = new PrintStream(outStream);
          var driver = new Driver(outPrintStream, outPrintStream);
          driver.enableTypeCheck();

          if (!driver.hasSupportEndToEnd()) {
            driver.readAST(loader.getResourceAsStream(test.inputdeserial));

            driver.run();
          } else {
            driver.setInputStream(in);
            driver.run();
          }

          var actualOutput = outStream.toString();
          var expectedOutput = readResourceToString(test.out);
          return new StringPair(expectedOutput, actualOutput);
        }
      });
      StringPair sp = null;
      try {
        sp = future.get(TIMEOUT, TimeUnit.SECONDS);
      } catch (TimeoutException e) {
        future.cancel(true);
      }
      if (sp == null) {
        Assertions.fail(String.format("Timeout for type check for program %s.", test.in));
      } else {
        Assertions.assertEquals(sanitize(sp.a).trim(), sanitize(sp.b).trim(),
            String.format("Type check for program %s differs from expected output.", test.in));
      }
    }));
  }

  @TestFactory
  Stream<DynamicTest> emulateIR() throws IOException {
    if (skipStage("stage4")) {
      return Stream.empty();
    }

    var tests = getTests("ir");
    return tests.stream()
            .limit(50)
            .map(test -> dynamicTest(test.in, () -> {
      ExecutorService executor = Executors.newSingleThreadExecutor();
      Future<StringPair> future = executor.submit(new Callable<StringPair>() {
        public StringPair call() throws IOException {
          var loader = getClass().getClassLoader();
          var in = loader.getResourceAsStream(test.in);
          var input = loader.getResourceAsStream(test.input);

          var outStream = new ByteArrayOutputStream();
          var outPrintStream = new PrintStream(outStream);
          var driver = new Driver(outPrintStream, outPrintStream);
          driver.enableEmulator();
          //driver.enableDebugEmulator();
          driver.setEmulatorInput(input);

          if (!driver.hasSupportEndToEnd()) {
            driver.readASTTYPE(loader.getResourceAsStream(test.inputdeserial));
            driver.run();
          } else {
            driver.setInputStream(in);
            driver.run();
          }

          var actualOutput = outStream.toString();
          var expectedOutput = readResourceToString(test.out);

          return new StringPair(expectedOutput, actualOutput);
        }
      });
      StringPair sp = null;
      try {
        sp = future.get(TIMEOUT, TimeUnit.SECONDS);
      } catch (TimeoutException e) {
        future.cancel(true);
      }
      if (sp == null) {
        Assertions.fail(String.format("Timeout for IR for program %s.", test.in));
      } else {
        Assertions.assertEquals(sanitize(sp.a).trim(), sanitize(sp.b).trim(),
            String.format("IR for program %s differs from expected output.", test.in));
      }
    }));
  }

  @TestFactory
  Stream<DynamicTest> codegen() throws IOException {
    if (skipStage("stage5")) {
      return Stream.empty();
    }

    var tests = getTests("codegen");
    Runtime runtime = Runtime.getRuntime();

    return tests.stream()
            .limit(45)
            .map(test -> dynamicTest(test.in, () -> {
      ExecutorService executor = Executors.newSingleThreadExecutor();
      Future<StringPair> future = executor.submit(new Callable<StringPair>() {
        public StringPair call() throws IOException {
          var loader = getClass().getClassLoader();
          var in = loader.getResourceAsStream(test.in);
          var input = loader.getResourceAsStream(test.input);

          var driver = new Driver();

          if (!driver.hasSupportEndToEnd()) {
            driver.readIR(loader.getResourceAsStream(test.inputdeserial));
            driver.run();
          } else {
            driver.setInputStream(in);
            driver.run();
          }

          Process build = runtime.exec("gcc a.s src/runtime/runtime.c -o autotest.bin");
          try {
            if (build.waitFor() != 0) {
              throw new Error("Assembling and linking failed");
            }
          } catch (Exception e) {
            throw new Error("Assembling and linking failed");
          }
          Process run = runtime.exec("./autotest.bin");
          OutputStream runinput = run.getOutputStream();

          int val;
          while ((val = input.read()) != -1)
            runinput.write(val);
          runinput.close();

          InputStream inputStream = run.getInputStream();
          StringBuffer sb = new StringBuffer();
          while ((val = inputStream.read()) != -1)
            sb.append((char) val);

          var actualOutput = sb.toString();
          var expectedOutput = readResourceToString(test.out);
          return new StringPair(expectedOutput, actualOutput);
        }
      });
      StringPair sp = null;
      try {
        sp = future.get(TIMEOUT, TimeUnit.SECONDS);
      } catch (TimeoutException e) {
        future.cancel(true);
      }
      if (sp == null) {
        Assertions.fail(String.format("Timeout for CodeGen for program %s.", test.in));
      } else {
        Assertions.assertEquals(sanitize(sp.a).trim(), sanitize(sp.b).trim(),
            String.format("CodeGen for program %s differs from expected output.", test.in));
      }
    }));
  }

  private List<InOut> getTests(String stageName) throws IOException {
    var loader = getClass().getClassLoader();
    var folder = String.format("crux/stages/%s", stageName);
    try (var programs = loader.getResourceAsStream(folder);
        BufferedReader br =
            new BufferedReader(new InputStreamReader(Objects.requireNonNull(programs)))) {

      return br.lines().filter(resourceName -> resourceName.endsWith(".crx")).map(resourceName -> {
        var testName = resourceName.substring(0, resourceName.length() - 4);
        var input = String.format("%s/%s.in", folder, testName);
        var inputdeserial = String.format("%s/%s.ser", folder, testName);
        var out = String.format("%s/%s.out", folder, testName);
        return new InOut(folder + "/" + resourceName, input, inputdeserial, out);
      }).collect(Collectors.toList());
    }
  }

  private String readResourceToString(String resourceName) throws IOException {
    var loader = getClass().getClassLoader();
    try (var inputStream = Objects.requireNonNull(loader.getResourceAsStream(resourceName))) {
      var result = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int length;
      while ((length = inputStream.read(buffer)) != -1) {
        result.write(buffer, 0, length);
      }
      return result.toString(StandardCharsets.UTF_8);
    }
  }

  private static final class InOut {
    final String in;
    final String input;
    final String out;
    final String inputdeserial;

    private InOut(String in, String input, String inputdeserial, String out) {
      this.in = in;
      this.input = input;
      this.inputdeserial = inputdeserial;
      this.out = out;
    }
  }
}
