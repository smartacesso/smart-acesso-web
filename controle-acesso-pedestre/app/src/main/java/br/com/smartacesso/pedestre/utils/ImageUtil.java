package br.com.smartacesso.pedestre.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by gustavo on 28/01/18.
 */

public class ImageUtil {

    /**
     * Retorna imagem de um array de bytes.
     * @param bytes
     * @return
     */
    public static Bitmap getImageBitmap(byte [] bytes) {

        if(bytes == null)
            return null;

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Retorna imagem de um array de bytes.
     * @param image
     * @return
     */
    public static byte [] getImageBytes(ImageView image) {

        if(image == null)
            return null;

        image.buildDrawingCache();
        Bitmap bit = image.getDrawingCache();

        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        return stream.toByteArray();
    }

    /**
     * Retorna imagem de um array de bytes.
     * @param image
     * @return
     */
    public static byte [] getImageBytes(Bitmap image) {

        if(image == null)
            return null;

        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        return stream.toByteArray();
    }

    /**
     * Retorna imagem de um array de bytes.
     * @param image
     * @return
     */
    public static byte [] getImageBytes(Bitmap image, int quality) {

        if(image == null)
            return null;

        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, quality, stream);

        return stream.toByteArray();
    }

    public static byte[] getImagemRedimensionada(Bitmap bitmapSource) {

        if(bitmapSource != null){

            byte [] tamanho = getImageBytes(bitmapSource);
            if(tamanho.length > 124000){

                //redimensiona imagem para ficar com menos de 100 KB
                Log.e("BaseActivity", "Redimensiona...");
                Bitmap resize = Bitmap.
                        createScaledBitmap(bitmapSource,
                                480,
                                640, true);
                return getImageBytes(resize, 50);

            }else{
                //deixa 100 KB
                return tamanho;
            }
        }
        return null;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());

        return output;
    }
}
