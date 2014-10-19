package com.swirepe.thumb.processing;

import com.swirepe.thumb.matrix.Region;


public class InterestingRegion {
  private final Region region;
  private final double entropy;
  
  public InterestingRegion(Region region, double entropy) {
    this.region = region;
    this.entropy = entropy;
  }
  
  public double getEntropy() {
    return entropy;
  }
  
  public Region getRegion() {
    return region;
  }
}
