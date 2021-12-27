package meteor.util;

import static meteor.MeteorLiteClientLauncher.consoleStream;
import static meteor.MeteorLiteClientLauncher.verboseFileStream;
import java.io.OutputStream;
import java.io.PrintStream;
import org.jetbrains.annotations.NotNull;
import meteor.Logger;

public class LoggerStream extends PrintStream {

  public boolean error = false;

  public LoggerStream(@NotNull OutputStream out) {
    super(out);
  }

  @Override
  public void println(String s) {
    if (error) {
      verboseFileStream.println(Logger.generateError(s));
      super.print(s);
    } else {
      consoleStream.println(s);
      super.print(s);
    }
  }

  @Override
  public void print(String s) {
    if (error) {
      verboseFileStream.print(Logger.generateError(s));
      super.print(s);
    } else {
      consoleStream.print(s);
      super.print(s);
    }
  }
}
