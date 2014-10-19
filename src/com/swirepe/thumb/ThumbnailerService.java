package com.swirepe.thumb;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThumbnailerService {
  public static final int THREADS = 4;
  private final ListeningExecutorService threadpool;
  
  public ThumbnailerService() {
    threadpool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(THREADS));
  }
  
  public ThumbnailerService(int numThreads) {
    threadpool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(numThreads));
  }
  
  public ListenableFuture<BufferedImage> submit(BufferedImage image, int width, int height, boolean resize) {
    return threadpool.submit(new Thumbnailer(image, width, height, resize));
  }
  
  /**
   * @return the milliseconds it took to shut this down.
   */
  public long awaitTermination() {
    long startTime = System.currentTimeMillis();
    try {
      threadpool.shutdown();
      threadpool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    } catch (InterruptedException ignored) { } 
    return System.currentTimeMillis() - startTime;
  }
  
}
