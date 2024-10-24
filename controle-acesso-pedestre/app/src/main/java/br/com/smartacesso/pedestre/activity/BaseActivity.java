package br.com.smartacesso.pedestre.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.smartacesso.pedestre.R;
import br.com.smartacesso.pedestre.service.StartTasksService;
import br.com.smartacesso.pedestre.utils.AppConstants;

@EActivity
public class BaseActivity  extends AppCompatActivity {

    protected ProgressDialog p = null;
    protected SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    protected static int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    /*
     * atributos para gestos.
     */
    private static final int ACTION_TYPE_DEFAULT = 0;
    private static final int ACTION_TYPE_UP = 1;
    private static final int ACTION_TYPE_RIGHT = 2;
    private static final int ACTION_TYPE_DOWN = 3;
    private static final int ACTION_TYPE_LEFT = 4;
    private static final int SLIDE_RANGE = 350;
    private static final int SLIDE_RANGE_DOWN = 1200;
    private float mTouchStartPointX;
    private float mTouchStartPointY;
    private int mActionType = ACTION_TYPE_DEFAULT;

    protected Calendar cIni = Calendar.getInstance();
    protected Calendar cFim = Calendar.getInstance();
    protected Double valorTotal;

    @UiThread
    public void createProcessDialog() {
        p = new ProgressDialog(this);
        p.setMessage("Aguarde...");
        p.setCancelable(false);
        p.setCanceledOnTouchOutside(false);
        p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        p.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background);
        p.show();
    }

    @UiThread
    public void createProcessDialog(String msg) {
        p = new ProgressDialog(this);
        p.setCancelable(false);
        p.setCanceledOnTouchOutside(false);
        p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        p.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background);
        p.setMessage(msg);

        p.show();
    }

    @UiThread
    public void createCancelProcessDialog(String msg, DialogInterface.OnCancelListener listener) {
        p = new ProgressDialog(this);
        p.setMessage(msg);
        p.setCancelable(true);
        p.setOnCancelListener(listener);
        p.setCanceledOnTouchOutside(false);
        p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        p.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background);
        p.show();
    }

    @UiThread
    public void destroyProcessDialog() {
        if (p != null) {
            p.dismiss();
            p = null;
        }
    }

    public void openNeutroDialog(String msg, String title, Activity activity) {

        AlertDialog.Builder neutro = new AlertDialog.Builder(activity);
        neutro.setTitle(title);
        neutro.setMessage(msg);
        neutro.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        neutro.show();
    }

    public void openConfirmDialog(String msg, Activity activity,
                                  DialogInterface.OnClickListener actionOk,
                                  DialogInterface.OnClickListener actionCancel) {


        AlertDialog.Builder confirmar = new AlertDialog.Builder(activity);

        confirmar.setTitle("Confirmar");
        confirmar.setMessage(msg);
        confirmar.setPositiveButton("OK", actionOk);
        confirmar.setNegativeButton("Cancelar", actionCancel);

        confirmar.show();
    }

    public void realizaLogoff() {

        SharedPreferences prefs = this.getSharedPreferences(
                AppConstants.APP_SHARED_PROPS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();

        StartTasksService.para(this);

        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
            try {
                ((ActivityManager)getSystemService(ACTIVITY_SERVICE))
                        .clearApplicationUserData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                // clearing app data
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear "+packageName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //volta tela login
        Intent intent = new Intent(this, LoginActivity_.class);
        startActivity(intent);
        finish();


    }

    protected void openDatePicker(String dateStr, DatePickerDialog.OnDateSetListener datePickerListener) {

        try {
            Date data = new Date();
            if (dateStr != null && !"".equals(dateStr) && !dateStr.contains("Selecione"))
                data = sdf.parse(dateStr);
            Calendar c = Calendar.getInstance();
            c.setTime(data);
            DatePickerDialog datePicker = new DatePickerDialog(this, datePickerListener,
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            datePicker.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void openSelectImage(){
        //permissão para leitura
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }else {
            openCameraPicker();
        }
    }

    public void openCameraPicker(){

        ImagePicker.cameraOnly().start(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCameraPicker();
                } else {
                    Toast.makeText(this, "Não é possível capturar fotos sem sua permissão.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mTouchStartPointX = event.getRawX();
                mTouchStartPointY = event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (mTouchStartPointX - x > SLIDE_RANGE) {
                    mActionType = ACTION_TYPE_LEFT;
                } else if (x - mTouchStartPointX > SLIDE_RANGE) {
                    mActionType = ACTION_TYPE_RIGHT;
                } else if (mTouchStartPointY - y > SLIDE_RANGE) {
                    mActionType = ACTION_TYPE_UP;
                } else if (y - mTouchStartPointY > SLIDE_RANGE_DOWN) {
                    mActionType = ACTION_TYPE_DOWN;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mActionType == ACTION_TYPE_UP) {
                    slideUp();
                    mActionType = ACTION_TYPE_DEFAULT;
                } else if (mActionType == ACTION_TYPE_RIGHT) {
                    slideToRight();
                    mActionType = ACTION_TYPE_DEFAULT;
                } else if (mActionType == ACTION_TYPE_DOWN) {
                    slideDown();
                    mActionType = ACTION_TYPE_DEFAULT;
                } else if (mActionType == ACTION_TYPE_LEFT) {
                    slideToLeft();
                    mActionType = ACTION_TYPE_DEFAULT;
                }
                break;

            default:
                break;
        }
        return true;
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        view.clearFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    protected void slideToLeft() {
    }

    protected void slideToRight() {
    }

    protected void slideUp() {
    }

    protected void slideDown() {
    }
}
