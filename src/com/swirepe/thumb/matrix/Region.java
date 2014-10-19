package com.swirepe.thumb.matrix;


public class Region {
  private final int x;
  private final int y;
  private final int xWidth;
  private final int yWidth;
  
  public Region(int x, int y, int width) {
    this.x = x;
    this.y = y;
    this.xWidth = width;
    this.yWidth = width;
  }
  
  public Region(int x, int y, int xWidth, int yWidth) {
    this.x = x;
    this.y = y;
    this.xWidth = xWidth;
    this.yWidth = yWidth;
  }
  
  public int getX() {
    return x;
  }
  
  public int getY() {
    return y;
  }
  
  public int getXWidth() {
    return xWidth;
  }
  
  public int getYWidth() {
    return yWidth;
  }
}
