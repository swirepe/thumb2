package com.swirepe.thumb.matrix;

import java.util.Iterator;


public class RegionIterator<T> implements Iterator<T>, Iterable<T>{

  protected final Matrix<T> matrix;
  protected final Region region;
  protected final int xMax;
  protected final int yMax;
  protected final int xStart;
  protected int x;
  protected int y;  
  public RegionIterator(Matrix<T> matrix) {
    this.matrix = matrix;
    xStart = 0;
    x = 0;
    y = 0;
    xMax = matrix.lenX();
    yMax = matrix.lenY();
    this.region = new Region(0, 0, xMax, yMax);
  }
  
  public RegionIterator(Matrix<T> matrix, Region region) {
    this.matrix = matrix;
    xStart = region.getX();
    x = region.getX();
    y = region.getY();
    xMax = Math.min(region.getXWidth() + region.getX(), matrix.lenX());
    yMax = Math.min(region.getYWidth() + region.getY(), matrix.lenY());
    this.region = region;
  }
  
  
  @Override
  public boolean hasNext() {
    return this.x < this.xMax && this.y < this.yMax;
  }

  @Override
  public T next() {
    T retVal = matrix.get(x, y);
    this.x += 1;
    if (this.x >= this.xMax){
      this.x = this.xStart;
      this.y += 1;
    }
    
    return retVal;
  }

  @Override
  public void remove() {
    // Do nothing
  }
  
  public Region getRegion() {
    return this.region;
  }

  @Override
  public Iterator<T> iterator() {
    return this;
  }
}
