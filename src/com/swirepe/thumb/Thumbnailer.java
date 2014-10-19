package com.swirepe.thumb;

import com.swirepe.thumb.matrix.ImageMatrix;
import com.swirepe.thumb.matrix.Region;
import com.swirepe.thumb.processing.InterestingRegionFinder;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class Thumbnailer implements Callable<BufferedImage> {
  private static final double RESIZE_RATIO = 2.0; // resizeRatio * thumbnail dimensions for preprocessing
  
  private final Logger logger = Logger.getLogger(this.getClass().getName());
  private final BufferedImage image;
  private final int width;
  private final int height;
  private final boolean resize;
  
  public Thumbnailer(BufferedImage image, int width, int height) {
    this.image = image;
    this.width = width;
    this.height = height;
    this.resize = true;
  }
 
  public Thumbnailer(BufferedImage image, int width, int height, boolean resize) {
    this.image = image;
    this.width = width;
    this.height = height;
    this.resize = resize;
  }
  
  public BufferedImage makeThumbnail(BufferedImage image, int width, int height) throws InterruptedException {
    return makeThumbnail(image, width, height, true);
  }
  
  public BufferedImage makeThumbnail(BufferedImage image, int width, int height, boolean resize) throws InterruptedException {
    logger.info("Thumbnailing image image: " + image.getWidth() + "x" + image.getHeight() + " -> " + width + "x" + height);
    if (resize) {
      image = resize(image, width, height);
    }
    ImageMatrix imageMatrix = ImageMatrix.fromBufferedImage(image);
    InterestingRegionFinder finder = new InterestingRegionFinder(imageMatrix);
    Region region = finder.process(width, height);
    return image.getSubimage(region.getX(), region.getY(), region.getXWidth(), region.getYWidth());
  }
  
  @Override
  public BufferedImage call() throws InterruptedException {
    return makeThumbnail(image, width, height, resize);
  }
  
  private BufferedImage resize(BufferedImage image, int thumbWidth, int thumbHeight) {
    double newWidth = thumbWidth * RESIZE_RATIO;
    double newHeight = thumbHeight * RESIZE_RATIO;
    if (image.getWidth() <= newWidth && image.getHeight() <= newHeight) {
      logger.info("Not base resizing image: too small to resize.");
      return image;
    }
    double xScale = newWidth / image.getWidth();
    double yScale = newHeight / image.getHeight();
    logger.info("Resizing base image by scale: " + xScale + "x" + yScale);
    AffineTransform at = new AffineTransform();
    at.scale(xScale, yScale);
    AffineTransformOp scaleOp = 
        new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    BufferedImage resized = scaleOp.createCompatibleDestImage(image, null);
    BufferedImage returnImage = scaleOp.filter(image, resized);
    logger.info("Resized base image: " + returnImage.getWidth() + "x" + returnImage.getHeight());
    return returnImage;
  }
}
