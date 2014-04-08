package ScanPrint;

/*
 * Morena 7 - Image Acquisition Framework
 *
 * Copyright (c) 1999-2011 Gnome spol. s r.o. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Gnome spol. s r.o. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Gnome.
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import eu.gnome.morena.Device;
import eu.gnome.morena.DeviceBase;
import eu.gnome.morena.TransferDoneListener;

/**
 * Helper class that encapsulates a synchronization of the scanning process.
 * Provided methods returns either BufferedImage or File containing an image.
 * 
 * @see #scanFile(Device, boolean)
 * @see #scanFile(Device, int, boolean)
 * 
 * @see #scanImage(Device, boolean)
 * @see #scanImage(Device, int, boolean)
 */

public class SynchronousHelper {
  public static final int WIA_ERROR_PAPER_EMPTY = 417;

  /**
   * TransferDoneListener interface implementation that handles a scanned
   * document as BufferedImage.
   * 
   */
  static class ImageTransferHandler implements TransferDoneListener {
    BufferedImage image;
    int code;
    String error;
    boolean transferDone = false;

    public void transferDone(File file) {
      if (file != null)
        try {
          image = ImageIO.read(file);
        } catch (IOException e) {
          error = e.getLocalizedMessage();
        }
      notifyRequestor();
    }

    public void transferFailed(int code, String error) {
      this.code = code;
      this.error = error;
      notifyRequestor();
    }

    private synchronized void notifyRequestor() {
      transferDone = true;
      this.notify();
    }

  }

  /**
   * TransferDoneListener interface implementation that handles a scanned
   * document as a File.
   * 
   */
  static class FileTransferHandler implements TransferDoneListener {
    File imageFile;
    int code;
    String error;
    boolean transferDone = false;

    /**
     * Transferred image is handled in this callback method. File containing the
     * image is provided as an argument. The image type may vary depending on
     * the interface (Wia/ICA) and the device driver. Typical format includes
     * BMP for WIA scanners and JPEG for WIA camera and for ICA devices. Please
     * note that this method runs in different thread than that where the
     * device.startTransfer() has been called.
     * 
     * @param file
     *          - the file containing the acquired image
     * 
     * @see eu.gnome.morena.TransferDoneListener#transferDone(java.io.File)
     */

    public void transferDone(File file) {
      imageFile = file;
      notifyRequestor();
    }

    /**
     * This callback method is called when scanning process failed for any
     * reason. Description of the problem is provided.
     */

    public void transferFailed(int code, String error) {
      this.code = code;
      this.error = error;
      notifyRequestor();
    }

    private synchronized void notifyRequestor() {
      transferDone = true;
      this.notify();
    }

  }

  /**
   * Convenient method that starts scanning process on specified device from
   * default functional unit (0) and returns a scanned document as a
   * BufferedImage. Device driver UI is displayed according showUI parameter.
   * 
   * @param device
   * @param showUI
   * @return - BufferedImage of the scanned document
   * @throws Exception
   */
  public static BufferedImage scanImage(Device device) throws Exception {
    return scanImage(device, 0);
  }

  /**
   * Convenient method that starts scanning process on specified device from
   * specified functional unit (0, 1, ...) and returns a scanned document as a
   * BufferedImage. Device driver UI is displayed according showUI parameter.
   * 
   * @param device
   * @param showUI
   * @return - BufferedImage of the scanned document
   * @throws Exception
   */
  public static BufferedImage scanImage(Device device, int item) throws Exception {
    ImageTransferHandler th = new ImageTransferHandler();

    synchronized (th) {
      ((DeviceBase) device).startTransfer(th, item);
      while (!th.transferDone)
        th.wait();
    }
    if (th.image != null)
      return th.image;
    throw new Exception(th.error);
  }

  /**
   * Convenient method that starts scanning process on specified device from
   * default functional unit (0) and returns a scanned document as a File.
   * Device driver UI is displayed according showUI parameter.
   * 
   * @param device
   * @param showUI
   * @return - File containing an image of the scanned document
   * @throws Exception
   */
  public static File scanFile(Device device) throws Exception {
    return scanFile(device, 0);
  }

  /**
   * Convenient method that starts scanning process on specified device from
   * specified functional unit (0, 1, ...) and returns a scanned document as a
   * File. Device driver UI is displayed according showUI parameter.
   * 
   * @param device
   * @param item
   *          - scanner's functional unit (flatbed, document feeder, ...) number
   * @param showUI
   * @return - File containing an image of the scanned document
   * @throws Exception
   */
  public static File scanFile(Device device, int item) throws Exception {
    FileTransferHandler th = new FileTransferHandler();

    synchronized (th) {
      ((DeviceBase) device).startTransfer(th, item);
      while (!th.transferDone)
        th.wait();
    }
    if (th.imageFile != null)
      return th.imageFile;
    throw new Exception(th.error);
  }

}
