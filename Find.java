package find;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import static java.nio.file.FileVisitOption.*;

public class Find {
  public static class Visitor
    extends SimpleFileVisitor<Path> {
    private final PathMatcher matcher;

    Visitor(String pattern) {
      matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
    }

    private void find(Path path) {
      Path name = path.getFileName();
      if (name != null && matcher.matches(name)) {
        System.out.println(path);
      }
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
      find(path);
      return CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) {
      find(path);
      return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path path, IOException except) {
      System.err.println(except);
      return CONTINUE;
    }
  }

  private static void printHelp() {
    System.err.println("java Find -dir \"<dirname>\" -pattern \"<glob_pattern>\"");
    System.exit(-1);
  }

  public static void main(String args[]) throws IOException {
    if (args.length < 4 || args.length % 2 != 0) {
      printHelp();
    }

    String pattern = null;
    Path[] dirs = new Path[args.length / 2 - 1];
    int number = 0;

    for (int i = 0; i < args.length; i += 2) {
      if (args[i].equals("-dir")) {
        dirs[number++] = Paths.get(args[i + 1]);
      } else if (args[i].equals("-pattern")) {
        pattern = args[i + 1];
      } else {
        printHelp();
      }
    }

    if (pattern == null) {
      printHelp();
    }

    Visitor visitor = new Visitor(pattern);

    for (Path p: dirs) {
      Files.walkFileTree(p, visitor);
    }
  }
}
