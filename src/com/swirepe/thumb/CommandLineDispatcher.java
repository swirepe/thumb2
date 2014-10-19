package com.swirepe.thumb;

import static com.swirepe.thumb.CommandLineMain.report;
import static com.swirepe.thumb.CommandLineMain.quietReport;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import com.swirepe.thumb.matrix.ImageReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CommandLineDispatcher implements Runnable {
  private final ThumbnailerService service;
  private final File file;
  private final String prefix;
  private final int width;
  private final int height;
  private final boolean resize;
  public CommandLineDispatcher(ThumbnailerService service, 
      File file, 
      String prefix, 
      int width,
      int height,
      boolean resize) {
    this.service = service;
    this.file = file;
    this.prefix = prefix;
    this.width = width;
    this.height = height;
    this.resize = resize;
  }
  
  @Override
  public void run() {
    final String filename = file.getAbsolutePath();
    report(filename + "\tReading.");
    BufferedImage image = new ImageReader().read(file);
    report(filename + "\tSubmitting for processing.");
    ListenableFuture<BufferedImage> imageFuture = service.submit(image, width, height, resize);
    Futures.addCallback(imageFuture, new FutureCallback<BufferedImage>() {

      @Override
      public void onFailure(Throwable arg0) {
        report(filename + "\tFAILURE: could not process.");
      }

      @Override
      public void onSuccess(BufferedImage thumbnail) {
        String newFilename = prefix + new File(filename).getName();
        report(filename + "\tWriting thumbnail to " + newFilename);
        try {
          ImageIO.write(thumbnail, "png", new File(newFilename));
          report(filename + "\tFinished.");
          quietReport(newFilename);
        } catch (IOException e) {
          report(filename + "\tFAILURE: could not write " + newFilename);
        }
      }
    });
    
  }
}