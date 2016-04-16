package kaiqiaole.com.uhf_elf.Common;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import java.text.SimpleDateFormat;

/**
 * Created by Stereo on 16/4/14.
 */
public class Common {
    public static final String WebUrl = "http://services.kaiqiaole.com/com.kaiqiaole.service/";
    public static final String Login ="uhf/login";
    public static final String bindTag ="uhf/bindTag";
    public static final String unbindTag ="uhf/unbindTag";
    public static final String scanBoxTags ="uhf/scanBoxTags";

    public static String worker_id = "";
    public static String worker_code = "";
    public static String worker_name = "";


    public static String GetSysTime()
    {
        SimpleDateFormat sDateFormat   =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date   =   sDateFormat.format(new java.util.Date());
        return date;
    }
    public static String GetSysTime2()
    {
        SimpleDateFormat sDateFormat   =   new SimpleDateFormat("yyyyMMddHHmmss");
        String date   =   sDateFormat.format(new   java.util.Date());
        return date;
    }

    public static void PlaysoundScan(Context context) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//系统自带提示音

        Ringtone rt = RingtoneManager.getRingtone(context, uri);
        rt.play();
    }



    public static void Vibrator(Context context, long time) {
        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(time);
    }


}
