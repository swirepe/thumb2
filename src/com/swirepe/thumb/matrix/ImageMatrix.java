package com.swirepe.thumb.matrix;

import java.awt.image.BufferedImage;

public class ImageMatrix extends Matrix<Integer> {

  private ImageMatrix(Integer[][] hostMatrix) {
    super(hostMatrix);
  }

  public static ImageMatrix fromBufferedImage(BufferedImage image) {
    Integer[][] thisMatrix = new Integer[image.getHeight()][image.getWidth()];
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        thisMatrix[y][x] = truncateRBG(image.getRGB(x, y));
      }
    }
    return new ImageMatrix(thisMatrix);
  }

  public EntropyMatrix toEntropyMatrix() {
    return EntropyMatrix.fromImageMatrix(this);
  }
  
  private static int truncateRBG(int rbg) {
    int red = (rbg >> 16) & 0x000000FF;
    int green = (rbg >> 8) & 0x000000FF;
    int blue = (rbg) & 0x000000FF;

    red /= 10;
    green /= 10;
    blue /= 10;

    return toRBG(red, green, blue);
  }

  private static int toRBG(int red, int green, int blue) {
    return ((red << 16) | (green << 8) | blue);
  }
}
