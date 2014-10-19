package com.swirepe.thumb.net;

import com.swirepe.thumb.ThumbnailerService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ThumbnailServer implements Runnable {
  public static final int PORT = 9200;
  public static final int WIDTH = 100;
  public static final int HEIGHT = 100;
  
  private final Logger logger = Logger.getLogger(this.getClass().getName());
  private final int port;
  private final int width;
  private final int height;
  private final boolean resize;
  private final ExecutorService threadpool = Executors.newCachedThreadPool();
  private final ThumbnailerService thumbnailService;
  
  public ThumbnailServer() {
    this.port = PORT;
    this.width = WIDTH;
    this.height = HEIGHT;
    this.resize = false;
    thumbnailService = new ThumbnailerService();
  }
  
  public ThumbnailServer(int port, int threads, int width, int height, boolean resize) {
    this.port = port;
    this.width = width;
    this.height = height;
    this.resize = resize;
    thumbnailService = new ThumbnailerService(threads);
  }

  @Override
  public void run() {
    logger.info("Server started with port = " + port + ", width = " + width + ", height = " + height);
    ServerSocket server = null;
    try {
      server = new ServerSocket(port);
      logger.info("Listening on port " + port);
      while (true) {
        Socket client = server.accept();
        logger.info("Connection accepted from " + client.getInetAddress());
        threadpool.submit(new NetDispatcher(client, thumbnailService, width, height, resize));
      }
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to start server.", e);
    } finally {
      try {
        server.close();
      } catch (Exception ignored) {}
    }
  }
  
}
