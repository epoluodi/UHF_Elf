package kaiqiaole.com.uhf_elf.Common;

import android.app.Activity;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Message;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;



import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kaiqiaole.com.uhf_elf.R;

/**
 * 自定义 提示窗口
 * @author YXG
 */
public class CustomPopWindowPlugin {



    //popwindows方法
    static PopupWindow popupWindow = null;
    public static View popview;
    static  Thread thread;
    static Activity mainActivity;
    static int showtype=0;







    public static String CheckGetWebURL(String barcode)
    {
        Pattern pattern = Pattern.compile("((http|https)://)([A-Za-z0-9-]+.)+[A-Za-z]{2,}(:[0-9]+)?");

        Matcher m = pattern.matcher(barcode);
        if (!m.find())
        {

            return "";
        }
        return m.group();


    }


    public static void ShowPopWindow(View v, LayoutInflater inflater, String title,
                                     String info) {
//        if (mainActivity != null)
//            mainActivity.setShowBackView();
        showtype=1;
        popview = inflater.inflate(R.layout.popinfowindows, null);
        ((TextView) popview.findViewById(R.id.title)).setText(title);
        ((TextView) popview.findViewById(R.id.poptext)).setText(info);
        popupWindow = new PopupWindow();
        popupWindow.setContentView(popview);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);

        popupWindow.setAnimationStyle(R.style.Animationpopwindows);
        popupWindow.showAtLocation(v, Gravity.CENTER_VERTICAL, 0, 0);

    }






    //设置popwindows中的文本
    public static void Setpoptext(String text) {
        if (popupWindow !=null)
            ((TextView) popview.findViewById(R.id.poptext)).setText(text);
    }

    //关闭POPwindows
    public static void CLosePopwindow() {



        if (popupWindow !=null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }







}
