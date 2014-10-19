package com.swirepe.thumb.processing;

import com.swirepe.thumb.matrix.Region;
import com.swirepe.thumb.matrix.RegionIterator;

import java.util.concurrent.Callable;


public class InterestingRegionWorker implements Callable<InterestingRegion> {
  
  protected final Region region;
  protected final RegionIterator<Double> iterator;
  
  public InterestingRegionWorker(RegionIterator<Double> iterator) {
    this.region = iterator.getRegion();
    this.iterator = iterator;
  }

  @Override
  public InterestingRegion call() {
    double entropy = 0.0;
    for (double prob : iterator) {
      entropy += prob;
    }
    entropy *= -1;
    return new InterestingRegion(region, entropy);
  }
}
