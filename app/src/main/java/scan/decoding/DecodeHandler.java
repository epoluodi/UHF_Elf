/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scan.decoding;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;

import kaiqiaole.com.uhf_elf.R;
import scan.ScanActivity;
import scan.camera.CameraManager;
import scan.camera.PlanarYUVLuminanceSource;


final class DecodeHandler extends Handler {

  private static final String TAG = DecodeHandler.class.getSimpleName();

  private final ScanActivity activity;
  private final MultiFormatReader multiFormatReader;

  DecodeHandler(ScanActivity activity, Hashtable<DecodeHintType, Object> hints) {
    multiFormatReader = new MultiFormatReader();
    multiFormatReader.setHints(hints);
    this.activity = activity;
  }

  @Override
  public void handleMessage(Message message) {
    switch (message.what) {
      case R.id.decode:
        //Log.d(TAG, "Got decode message");
        if (CameraManager.get().getScantype() ==1)
          decode((byte[]) message.obj, message.arg1, message.arg2);
        if (CameraManager.get().getScantype() ==2) {
//          decodeOCR((byte[]) message.obj, message.arg1, message.arg2);
          Message message1 = Message.obtain(activity.getHandler(), R.id.decode_failed);
          message1.sendToTarget();
        }
        break;
      case R.id.quit:
        Looper.myLooper().quit();
        break;
    }
  }

  private void decodeOCR(byte[] data, int width, int height) {

    long start = System.currentTimeMillis();
    Result rawResult = null;
    byte[] rotatedData = new byte[data.length];

    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
    File f = new File(Environment.getExternalStorageDirectory() + "/ocr.jpg");

    if (f.exists()) {
      f.delete();
    }
    try {
      FileOutputStream out = new FileOutputStream(f);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
      out.flush();
      out.close();
      Log.i(TAG, "已经保存");
    } catch (Exception e) {

      e.printStackTrace();
    }


//    int value=0;
//    for (int y = 0; y < height; y++) {
//      for (int x = 0; x < width; x++)
////        rotatedData[x * height + height - y - 1] = data[x + y * width];
//        value += data[x + y * width];
//    }
//    Log.i("图片计算值:",String.valueOf(Math.abs( value)) );


    if (rawResult != null) {
      long end = System.currentTimeMillis();


      Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
      Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
      Bundle bundle = new Bundle();
      message.setData(bundle);
      //Log.d(TAG, "Sending decode succeeded message...");
      message.sendToTarget();
    } else {
      Message message = Message.obtain(activity.getHandler(), R.id.decode_failed);
      message.sendToTarget();
    }
  }

  /**
   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
   * reuse the same reader objects from one decode to the next.
   *
   * @param data   The YUV preview frame.
   * @param width  The width of the preview frame.
   * @param height The height of the preview frame.
   */
  private void decode(byte[] data, int width, int height) {
    long start = System.currentTimeMillis();
    Result rawResult = null;
      byte[] rotatedData = new byte[data.length];
      for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++)
              rotatedData[x * height + height - y - 1] = data[x + y * width];
      }
      int tmp = width;
      width = height;
      height = tmp;
      data = rotatedData;
      PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(data, width, height);
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

    try {
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
//        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");//字符编码
//        hints.put(DecodeHintType.CHARACTER_SET, "GB2312");//字符编码
        hints.put(DecodeHintType.CHARACTER_SET, "ISO-8859-1");//字符编码
      rawResult = multiFormatReader.decode(bitmap,hints);


    } catch (ReaderException re) {
      // continue
    } finally {
      multiFormatReader.reset();
    }

    if (rawResult != null) {
      long end = System.currentTimeMillis();


      Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
      Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
      Bundle bundle = new Bundle();
      bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
      message.setData(bundle);
      //Log.d(TAG, "Sending decode succeeded message...");
      message.sendToTarget();
    } else {
      Message message = Message.obtain(activity.getHandler(), R.id.decode_failed);
      message.sendToTarget();
    }
  }



}
