package com.swirepe.thumb.matrix;

import java.util.HashMap;

public class EntropyMatrix extends Matrix<Double> {

  private EntropyMatrix(Double[][] hostMatrix) {
    super(hostMatrix);
  }

  public static EntropyMatrix fromImageMatrix(ImageMatrix imageMatrix) {
    HashMap<Integer, Double> frequencies = calculateFrequencies(imageMatrix);
    int lenY = imageMatrix.lenY();
    int lenX = imageMatrix.lenX();
    Double[][] thisMatrix = new Double[lenY][lenX];
    double numElements = lenX * lenY;
    double pvalue;
    for (int y = 0; y < lenY; y++) {
      for (int x = 0; x < lenX; x++) {
        pvalue = frequencies.get(imageMatrix.get(x, y)) / numElements; // p(x)
        pvalue = pvalue * (Math.log( 1.0 / pvalue) / Math.log(2));
        thisMatrix[y][x] = pvalue;
      }
    }
    return new EntropyMatrix(thisMatrix);
  }
  
  private static HashMap<Integer, Double> calculateFrequencies(ImageMatrix imageMatrix) {
    HashMap<Integer, Double> frequencies = new HashMap<Integer, Double>();
    for (Integer pixel : imageMatrix) {
      double count = 1.0;
      if (frequencies.containsKey(pixel)) {
        count += frequencies.get(pixel);
      }
      frequencies.put(pixel, count);
    }
    return frequencies;
  }
}
