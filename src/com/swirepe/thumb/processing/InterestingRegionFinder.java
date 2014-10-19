package com.swirepe.thumb.processing;

import com.swirepe.thumb.matrix.EntropyMatrix;
import com.swirepe.thumb.matrix.ImageMatrix;
import com.swirepe.thumb.matrix.Region;
import com.swirepe.thumb.matrix.RegionIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;


public class InterestingRegionFinder {
  private final Logger logger = Logger.getLogger(this.getClass().getName());
  private final EntropyMatrix matrix;
  private final CompletionService<InterestingRegion> threadpool;
  private boolean useLeastEntropy = false;
  
  public InterestingRegionFinder(ImageMatrix matrix) {
    this.matrix = matrix.toEntropyMatrix();
    this.threadpool =
        new ExecutorCompletionService<InterestingRegion>(Executors.newCachedThreadPool());
  }
  
  public InterestingRegionFinder(CompletionService<InterestingRegion> service, ImageMatrix matrix) {
    this.matrix = matrix.toEntropyMatrix();
    this.threadpool = service;
  }
  
  public Region process(int thumbnailWidth, int thumbnailHeight) throws InterruptedException {
    Collection<InterestingRegionWorker> workers = getWorkers(thumbnailWidth, thumbnailHeight);
    List<Future<InterestingRegion>> futures = scheduleWorkers(workers);
    return resolveFutures(futures);
  }
  
  public Region process(int thumbnailWidth, int thumbnailHeight, double minimumChangePercent) throws InterruptedException {
    Collection<InterestingRegionWorker> workers = getWorkers(thumbnailWidth, thumbnailHeight);
    List<Future<InterestingRegion>> futures = scheduleWorkers(workers);
    return resolveFutures(futures, minimumChangePercent);
  }
  
  private Collection<InterestingRegionWorker> getWorkers(int thumbnailWidth, int thumbnailHeight) {
    Collection<InterestingRegionWorker> workers = new ArrayList<InterestingRegionWorker>();
    for (Region region : matrix.getAllRegions(thumbnailWidth, thumbnailHeight)) {
      InterestingRegionWorker worker = new InterestingRegionWorker(new RegionIterator<Double>(matrix, region));
      workers.add(worker);
    }
    logger.info("Created " + workers.size() + " InterestingRegionWorker objects.");
    return workers;
  }
  
  private List<Future<InterestingRegion>> scheduleWorkers(Collection<InterestingRegionWorker> workers) {
    List<Future<InterestingRegion>> futures = new ArrayList<Future<InterestingRegion>>(workers.size());
    for (Callable<InterestingRegion> worker : workers) {
      futures.add(threadpool.submit(worker));
    }
    return futures;
  }
  
  private Region resolveFutures(List<Future<InterestingRegion>> futures) throws InterruptedException {
    InterestingRegion mostInteresting = null;
    InterestingRegion thisRegion = null;
    for (int i = 0; i < futures.size(); i++) {
      try {
        thisRegion = threadpool.take().get();
        if (mostInteresting == null) {
          mostInteresting = thisRegion;
        } else {
          mostInteresting = compare(mostInteresting, thisRegion);
        }
      } catch (ExecutionException ignored) {}
    }
    logger.info("Most interesting region found: entropy = " + mostInteresting.getEntropy() 
        + ", x = " + mostInteresting.getRegion().getX() 
        + ", y = " + mostInteresting.getRegion().getY());
    return mostInteresting.getRegion();
  }
  
  private Region resolveFutures(List<Future<InterestingRegion>> futures, double minimumChangePercent) throws InterruptedException {
    InterestingRegion mostInteresting = null;
    InterestingRegion thisRegion = null;
    try {
      for (int i = 0; i < futures.size(); i++) {
        try {
          thisRegion = threadpool.take().get();
          if (mostInteresting == null) {
            mostInteresting = thisRegion;
          } else {
            if (isSignificantEntropyChange(mostInteresting, thisRegion, minimumChangePercent)) {
              mostInteresting = compare(mostInteresting, thisRegion);
            } else {
              break; // or not.  not sure.
            }
          }
        } catch (ExecutionException ignored) { }
      }
    } finally {
      for (Future<InterestingRegion> future : futures) {
        future.cancel(true);
      }
    }
    return mostInteresting.getRegion();
  }
  
  private InterestingRegion compare(InterestingRegion a, InterestingRegion b) {
    if (useLeastEntropy) {
      if (a.getEntropy() < b.getEntropy()) {
        return a;
      }
      return b;
    } else {
      if (a.getEntropy() > b.getEntropy()) {
        return a;
      }
    }
    return b;
  }
  
  private boolean isSignificantEntropyChange(InterestingRegion a, InterestingRegion b, double minimumChangePercent) {
    double aEntropy = a.getEntropy();
    double bEntropy = b.getEntropy();
    double change = 1.0 - Math.abs(Math.min(aEntropy, bEntropy) / Math.max(aEntropy, bEntropy));
    return change >= minimumChangePercent;
  }
  
  public void setUseLeastEntropy(boolean useLeastEntropy) {
    this.useLeastEntropy = useLeastEntropy;
  }
}
