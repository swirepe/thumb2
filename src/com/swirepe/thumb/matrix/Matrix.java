package com.swirepe.thumb.matrix;

import java.util.Iterator;


public class Matrix<T> implements Iterable<T>{
  protected final T[][] matrix;
  
  public Matrix(T[][] hostMatrix) {
    this.matrix = hostMatrix;
  }
  
  public AllRegions getAllRegions(int xWidth, int yWidth) {
    return new AllRegions(xWidth, yWidth, lenX(), lenY());
  }
  
  public T get(int x, int y) {
    return this.matrix[y][x];
  }
  
  
  public int lenY() {
    return this.matrix.length;
  }

  public int lenX() {
    return this.matrix[0].length;
  }


  @Override
  public Iterator<T> iterator() {
    return new RegionIterator<T>(this);
  }
}
