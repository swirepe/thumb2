package com.swirepe.thumb.net;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import com.swirepe.thumb.ThumbnailerService;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class NetDispatcher implements Runnable {
  private final Logger logger = Logger.getLogger(this.getClass().getName());
  private final Socket socket;
  private final ThumbnailerService thumbService;
  private final int width;
  private final int height;
  private final boolean resize;
  
  public NetDispatcher(Socket socket, ThumbnailerService service, int width, int height, boolean resize) {
    this.socket = socket;
    this.thumbService = service;
    this.width = width;
    this.height = height;
    this.resize = resize;
  }
  
  @Override
  public void run() {
    final long startTime = System.currentTimeMillis();
    logger.info("Dispatch started.");
    BufferedImage image = read();
    if (image == null) {
      try {
        socket.close();
        logger.info("Dispatch read null image. Aborting.");
      } catch (IOException ignored) {}
      return;
    }
    ListenableFuture<BufferedImage> future = thumbService.submit(image, width, height, resize);
    Futures.addCallback(future, new FutureCallback<BufferedImage>(){

      @Override
      public void onFailure(Throwable throwable) {
        try {
          socket.close();
          logger.log(Level.WARNING, "Dispatch failed in " + + (System.currentTimeMillis() - startTime) + "ms", throwable);
        } catch (IOException ignored) {}
      }

      @Override
      public void onSuccess(BufferedImage thumb) {
        try {
          BufferedOutputStream buffer = new BufferedOutputStream(socket.getOutputStream());
          ImageIO.write(thumb, "png", buffer);
          logger.info("Dispatch completed in " + (System.currentTimeMillis() - startTime) + "ms");
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          try {
            socket.close();
          } catch (IOException ignored) {}
        }
      }
      
    });
  }
  
  private BufferedImage read() {
    try {
      InputStream buffer = new BufferedInputStream(socket.getInputStream());
      return ImageIO.read(buffer);
    } catch (IOException e) {
      return null;
    }
    // or, for a whole bufferedImage being sent
    /*
    ObjectInput input = new ObjectInputStream(buffer);
    BufferedImage image = (BufferedImage)input.readObject();
    return image;
    */
  }
  
  
}
