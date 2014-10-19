package com.swirepe.thumb.matrix;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageReader {
  public BufferedImage read(String filename) {
    return read(new File(filename));
  }

  public BufferedImage read(File file) {
    BufferedImage bi = null;
    try {
      bi = ImageIO.read(file);
    } catch (IOException e) {
      System.err.println("Error reading file:" + file.getAbsolutePath());
    }
    return bi;
  }
  
  public Matrix<Integer> load(String filename) {
    return load(new File(filename));
  }
  
  public Matrix<Integer> load(File file) {
    BufferedImage bi = read(file);
    return load(bi);
  }
  
  public Matrix<Integer> load(BufferedImage image) {
    return ImageMatrix.fromBufferedImage(image);
  }
}
