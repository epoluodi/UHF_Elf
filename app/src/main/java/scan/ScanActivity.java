package scan;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import kaiqiaole.com.uhf_elf.R;
import scan.camera.CameraManager;
import scan.decoding.CaptureActivityHandler;
import scan.decoding.InactivityTimer;
import scan.view.ViewfinderView;

/**
 * 扫描窗口
 * @author YXG
 */
public class ScanActivity extends Activity implements Callback {


    public static String TAG = "ScanActivity";
    public static int SCANRESULTREQUEST = 10;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    //	private TextView txtResult;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    ImageView imageViewline =null;
    FrameLayout mainview;
    ImageView returnview;
    TextView textView;

    ImageView light;
    Boolean islight=false;

    /**
     * 二维扫描窗口
     * @author YXG
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_activity);
        returnview = (ImageView)findViewById(R.id.title_left_menu_image);
        returnview.setOnClickListener(onClickListenerreturn);
        mainview = (FrameLayout)findViewById(R.id.mainview);
        System.gc();//释放内存
        CameraManager.init(getApplication());
        CameraManager.get().setScantype(1);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        viewfinderView.setOnScanLine(iScanLine);
        light = (ImageView)findViewById(R.id.light);
        light.setOnClickListener(onClickListenerloght);

    }


    View.OnClickListener onClickListenerloght =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!islight)
            {
                islight=true;
                light.setImageDrawable(getResources().getDrawable(R.mipmap.onlight));
                CameraManager.LightControl(islight);
            }
            else
            {
                islight=false;
                light.setImageDrawable(getResources().getDrawable(R.mipmap.offlight));
                CameraManager.LightControl(false);
            }
        }
    };


    //身份证
    View.OnClickListener onClickListenerbtn2 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (CameraManager.get().getScantype()==1) {
                imageViewline.clearAnimation();
                viewfinderView.setOnScanLine(null);
                mainview.removeView(imageViewline);
                mainview.removeView(textView);
                imageViewline = null;
                textView = null;
                CameraManager.get().setScantype(2);
            }
        }
    };

    //二维码
    View.OnClickListener onClickListenerbtn1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            CameraManager.get().setScantype(1);
            viewfinderView.setOnScanLine(iScanLine);
        }
    };

    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = "ISO-8859-1";

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    IScanLine iScanLine = new IScanLine() {
        @Override
        public void OnDrawLine(Rect rect) {
            if (imageViewline ==null) {
                imageViewline = new ImageView(ScanActivity.this);
//                imageViewline.setBackgroundColor(getResources().getColor(R.color.red));
                imageViewline.setBackground(getResources().getDrawable(R.mipmap.scanline));
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        rect.right-rect.left, 3);


                layoutParams.setMargins(rect.left, rect.top, rect.right, rect.bottom);
                mainview.addView(imageViewline, layoutParams);
                slideview(rect);

//
                textView = new TextView(ScanActivity.this);
                textView.setText("条码，二维码放入扫描框内");
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setTextSize(12);


                Rect rect1 = CameraManager.get().getFramingRect();
                FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(
                        rect1.right-rect1.left,
                        50);
                textView.setGravity(Gravity.CENTER);
                layoutParams2.setMargins(rect1.left, rect1.bottom, rect1.right, rect1.bottom+50);
                mainview.addView(textView, layoutParams2);


            }

        }
    };

    public void slideview(final Rect rect) {
        TranslateAnimation animation = new TranslateAnimation(
                0, 0, 0, rect.bottom);
//        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(2000);
        animation.setStartOffset(0);
        animation.setRepeatCount(999);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setFillAfter(true);

        imageViewline.startAnimation(animation);
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
        setResult(0);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {

            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
//		viewfinderView.drawResultBitmap(barcode);
        playBeepSoundAndVibrate();
//		txtResult.setText(obj.getBarcodeFormat().toString() + ":"
//				+ obj.getText());
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        System.gc();//释放内存
        bundle.putString("code", getbarcodeDecode(obj.getText()));
        intent.putExtras(bundle);
        setResult(1, intent);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }


    String getbarcodeDecode(String barcode) {
        String utf_8;
        String GB;
        Boolean is_cn;
        Boolean is_Specia;
        try {

            Log.i("原始", barcode);
            utf_8 = new String(barcode.getBytes("ISO-8859-1"),"UTF-8");
//            GB2312 = new String(barcode.getBytes("GB2312"));
//            Log.i("Utf-8",utf_8);
            //测试


//            String ISO88591  = new String(barcode.getBytes("GB18030"));
//            Log.i("ISO88591",ISO88591);
//            String gbk  = new String(ISO88591.getBytes("GBK"));
//            Log.i("gbk",gbk);
//            String gb2312  = new String(ISO88591.getBytes("UTF-8"));
//            Log.i("gb2312",gb2312);
//            String gb18030  = new String(ISO88591.getBytes("GB18030"));
//            Log.i("gb18030",gb18030);
//            String unicode  = new String(ISO88591.getBytes("ISO-8859-1"));
//            Log.i("unicode",unicode);




            is_cn = isChineseCharacter(utf_8);

            is_Specia = isSpecialCharacter(utf_8);
            if (is_Specia) {
                return utf_8;
            }

            if (!is_cn) {

                GB = new String(barcode.getBytes("ISO-8859-1"), "GB2312");
                             Log.i("GB", GB);
//                Log.i("处理111", new String(gbk2utf8(barcode), "UTF-8"));
//                gbk2Utf(new String(barcode.getBytes("UTF-8")));

//                GB = new String(utf_8.getBytes("UTF-8"), "GB2312");
                return GB;
            } else
                return utf_8;


        } catch (Exception e) {
            return barcode;
        }
    }




    boolean isChineseCharacter(String chineseStr) {
        char[] charArray = chineseStr.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            //是否是Unicode编码,除了" "这个字符.这个字符要另外处理
            if ((charArray[i] >= '\u0000' && charArray[i] < '\uFFFD')||((charArray[i] > '\uFFFD' && charArray[i] < '\uFFFF'))) {
                continue;
            }
            else{
//                char[] chars =new char[2];
//                chars[0] = charArray[i];
//                chars[1] = charArray[i+1];
//                gbk2Utf(new String(chars));
//                checkcharConvertoGBK(chars);
                return false;
            }
        }
        return true;
    }

    public static void gbk2Utf(String gbk) {
//        String gbk = "我来了";
        char[] c = gbk.toCharArray();
        byte[] fullByte = new byte[3*c.length];
        for (int i=0; i<c.length; i++) {
            String binary = Integer.toBinaryString(c[i]);
            StringBuffer sb = new StringBuffer();
            int len = 16 - binary.length();
            //前面补零
            for(int j=0; j<len; j++){
                sb.append("0");
            }
            sb.append(binary);
            //增加位，达到到24位3个字节
            sb.insert(0, "1110");
            sb.insert(8, "10");
            sb.insert(16, "10");
            fullByte[i*3] = Integer.valueOf(sb.substring(0, 8), 2).byteValue();//二进制字符串创建整型
            fullByte[i*3+1] = Integer.valueOf(sb.substring(8, 16), 2).byteValue();
            fullByte[i*3+2] = Integer.valueOf(sb.substring(16, 24), 2).byteValue();
        }
        //模拟UTF-8编码的网站显示
//        try {
//            Log.i("处理111", new String(fullByte, "UTF-8"));
//            Log.i("处理111", new String(fullByte, "GB2312"));
//            Log.i("处理111", new String(fullByte, "ISO-8859-1"));
//            Log.i("处理111", new String(fullByte, "GBK"));
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
    }


    public byte[] gbk2utf8(String chenese) {

        // Step 1: 得到GBK编码下的字符数组，一个中文字符对应这里的一个c[i]
        char c[] = chenese.toCharArray();

        // Step 2: UTF-8使用3个字节存放一个中文字符，所以长度必须为字符的3倍
        byte[] fullByte = new byte[3 * c.length];

        // Step 3: 循环将字符的GBK编码转换成UTF-8编码
        for (int i = 0; i < c.length; i++) {

            // Step 3-1：将字符的ASCII编码转换成2进制值
            int m = (int) c[i];
            String word = Integer.toBinaryString(m);
            System.out.println(word);

            // Step 3-2：将2进制值补足16位(2个字节的长度)
            StringBuffer sb = new StringBuffer();
            int len = 16 - word.length();
            for (int j = 0; j < len; j++) {
                sb.append("0");
            }
            // Step 3-3：得到该字符最终的2进制GBK编码
            // 形似：1000 0010 0111 1010
            sb.append(word);

            // Step 3-4：最关键的步骤，根据UTF-8的汉字编码规则，首字节
            // 以1110开头，次字节以10开头，第3字节以10开头。在原始的2进制
            // 字符串中插入标志位。最终的长度从16--->16+3+2+2=24。
            sb.insert(0, "1110");
            sb.insert(8, "10");
            sb.insert(16, "10");
            System.out.println(sb.toString());

            // Step 3-5：将新的字符串进行分段截取，截为3个字节
            String s1 = sb.substring(0, 8);
            String s2 = sb.substring(8, 16);
            String s3 = sb.substring(16);

            // Step 3-6：最后的步骤，把代表3个字节的字符串按2进制的方式
            // 进行转换，变成2进制的整数，再转换成16进制值
            byte b0 = Integer.valueOf(s1, 2).byteValue();
            byte b1 = Integer.valueOf(s2, 2).byteValue();
            byte b2 = Integer.valueOf(s3, 2).byteValue();

            // Step 3-7：把转换后的3个字节按顺序存放到字节数组的对应位置
            byte[] bf = new byte[3];
            bf[0] = b0;
            bf[1] = b1;
            bf[2] = b2;

            fullByte[i * 3] = bf[0];
            fullByte[i * 3 + 1] = bf[1];
            fullByte[i * 3 + 2] = bf[2];

            // Step 3-8：返回继续解析下一个中文字符
        }
        return fullByte;
    }



    char[] checkcharConvertoGBK(char[] chars)
    {
        String string = new String(chars);
        Log.i("乱码:", string);
        try {
            String s = new String(string.getBytes("GBK"),"ISO-8859-1");
            Log.i("处理1:", s);
            s = new String(string.getBytes("GB2312"));
            Log.i("处理2:", s);
            s = new String(string.getBytes("GB18030"));
            Log.i("处理3:", s);
            s = new String(string.getBytes("UTF-8"));
            Log.i("处理4:", s);
            return string.toCharArray();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    boolean isSpecialCharacter(String str){
        //是" "这个特殊字符的乱码情况
        if(str.contains("ï¿½")){
            return true;
        }
        return false;
    }



	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};





}