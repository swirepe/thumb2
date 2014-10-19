package com.swirepe.thumb.matrix;

import java.util.Iterator;


public class AllRegions implements Iterator<Region>, Iterable<Region>{
  private final int xWidth;
  private final int yWidth;
  private final int xMax;
  private final int yMax;

  private int x = 0;
  private int y = 0;
  
  public AllRegions(int xWidth, int yWidth, int xMax, int yMax) {
    this.xWidth = xWidth;
    this.yWidth = yWidth;
    this.xMax = xMax;
    this.yMax = yMax;
  }
  
  @Override
  public boolean hasNext() {
    return (x + xWidth < xMax) || (y + yWidth < yMax);
  }

  @Override
  public Region next() {
    Region returnRegion = new Region(x, y, xWidth, yWidth);
    x += 1;
    if (x + xWidth > xMax) {
      x = 0;
      y += 1;
    }
    return returnRegion;
  }

  @Override
  public void remove() {
    // do nothing
  }

  @Override
  public Iterator<Region> iterator() {
    return this;
  }

}
