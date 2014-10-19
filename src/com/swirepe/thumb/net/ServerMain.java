package com.swirepe.thumb.net;

import com.swirepe.thumb.ThumbnailerService;

import java.util.logging.LogManager;


public class ServerMain {
  private static void usage() {
    System.err.println("Thumbnail server - swirepe@swirepe.com");
    System.err.println("Usage:");
    System.err.println("\t--help\tThis message.");
    System.err.println("\t--port int\tPort for the server to listen on.");
    System.err.println("\t--threads int\tNumber of threads to allocate to the thumbnailer service.");
    System.err.println("\t--width int\tWidth of the thumbnails to return.");
    System.err.println("\t--height int\tHeight of the thumbnails to return.");
    System.err.println("\t--no-resize\tResize the image before finding a thumbnail.");
    System.err.println("\t--silent\tDisable logging.");
  }
  
  public static void main(String[] args) {
    int port = ThumbnailServer.PORT;
    int width = ThumbnailServer.WIDTH;
    int height = ThumbnailServer.HEIGHT;
    int threads = ThumbnailerService.THREADS;
    boolean resize = true;
    for (int i = 0; i < args.length; i++) {
      switch(args[i]) {
        case "--help":
        case "-h":
          usage();
          System.exit(0);
        case "--port":
          i += 1;
          port = Integer.parseInt(args[i]);
          break;
        case "--width":
          i += 1;
          width = Integer.parseInt(args[i]);
          break;
        case "--height":
          i += 1;
          height = Integer.parseInt(args[i]);
          break;
        case "--no-resize":
          resize = false;
          break;
        case "--threads":
          i += 1;
          threads = Integer.parseInt(args[i]);
          break;
        case "--silent":
          LogManager.getLogManager().reset();
        default:
          System.err.println("Unrecognized argument: " + args[i]);
          usage();
      }
    }    
    ThumbnailServer server = new ThumbnailServer(port, threads, width, height, resize);
    server.run();
  }
}
