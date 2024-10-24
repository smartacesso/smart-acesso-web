package br.com.smartacesso.pedestre.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

import br.com.smartacesso.pedestre.activity.BaseActivity;
/**
 * Created by gustavo on 28/01/18.
 */

public class ShareUtil {

    public static void takeScreenshot(String id, BaseActivity activity, String text) {

        try {

            activity.createProcessDialog();

            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/pro-treino";

            File folder = new File(mPath);
            if(!folder.isDirectory())
                folder.mkdir();


            // create bitmap screen capture
            View v1 = activity.getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath + "/" + id + ".jpg");

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            activity.destroyProcessDialog();

            openScreenshot(imageFile, activity, text);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
            activity.destroyProcessDialog();
        }
    }

    public static void openScreenshot(File imageFile, BaseActivity activity, String text) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());
        Uri uri = Uri.fromFile(imageFile);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        // Launch sharing dialog for image
        activity.startActivity(Intent.createChooser(shareIntent, text));


    }
}
