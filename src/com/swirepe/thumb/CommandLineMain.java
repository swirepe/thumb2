package com.swirepe.thumb;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;


public class CommandLineMain {
  private static boolean silent = false;
  private static boolean quiet = false;
  
  public static void report(String report) {
    if (!silent) {
      System.err.println(report);
    }
  }
  
  public static void quietReport(String report) {
    if (quiet) {
      System.out.println(report);
    }
  }
  
  private static void usage() {
    System.err.println("Thumbnail generator - swirepe@swirepe.com");
    System.err.println("Usage:");
    System.err.println("\t--threads int\tNumber of threads to allocate to the thumbnailer service.");
    System.err.println("\t--width int\tWidth of the thumbnails to return.");
    System.err.println("\t--height int\tHeight of the thumbnails to return.");
    System.err.println("\t--no-resize\tDo not resize the image before finding a thumbnail.");
    System.err.println("\t--prefix\tPrefix to attach to generated thumbnails.");
    System.err.println("\t--quiet\tOnly report the output names, nothing else.");
    System.err.println("\t--silent\tDisable logging.");
  }

  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();
    
    int width = 100;
    int height = 100;
    int threads = 4;
    boolean resize = true;
    String prefix = "thumb-";
    ArrayList<String> filenames = new ArrayList<String>();
    for (int i = 0; i < args.length; i++) {
      switch(args[i]) {
        case "--help":
        case "-h":
          usage();
          System.exit(0);
        case "--width":
          i += 1;
          width = Integer.parseInt(args[i]);
          break;
        case "--height":
          i += 1;
          height = Integer.parseInt(args[i]);
          break;
        case "--threads":
          i += 1;
          threads = Integer.parseInt(args[i]);
          break;
        case "--prefix":
          i += 1;
          prefix = args[i];
          break;
        case "--no-resize":
          resize = false;
          break;
        case "--quiet":
          LogManager.getLogManager().reset();
          silent = true;
          quiet = true;
          break;
        case "--silent":
          LogManager.getLogManager().reset();
          silent = true;
          break;
        default:
          if (args[i].startsWith("--")) {
            System.err.println("Unrecognized argument: " + args[i]);
            usage();
            System.exit(1);
          }
          filenames.add(args[i]);
      }
    }
    
    if (filenames.size() == 0) {
      report("No files found. Exiting.");
      System.exit(1);
    }
    
    ThumbnailerService service = new ThumbnailerService(threads);
    ExecutorService dispatchService = Executors.newCachedThreadPool();
    ArrayList<File> files = expandFilenames(filenames);
    for (final File file : files) {
      dispatchService.submit(new CommandLineDispatcher(service, file, prefix, width, height, resize));
    }
    dispatchService.shutdown();
    try {
      dispatchService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    } catch (InterruptedException ignored) { }
    report("Thumb Service terminated in " + service.awaitTermination() + "ms");
    report("All files processed in " + (System.currentTimeMillis() - startTime) + "ms");
    System.exit(0);
  }

  private static ArrayList<File> expandFilenames(ArrayList<String> filenames) {
    ArrayList<File> files = new ArrayList<File>();
    for (String filename : filenames) {
      files.add(new File(filename));
    }
    
    for (int i = 0; i < files.size(); i++) {
      File f = files.get(i);
      File[] listed = f.listFiles();
      if (listed != null) {
        files.remove(i);
        i -= 1;
        for (File fChild : listed) {
          files.add(fChild);
        }
      }
    }
    return files;
  }
}
