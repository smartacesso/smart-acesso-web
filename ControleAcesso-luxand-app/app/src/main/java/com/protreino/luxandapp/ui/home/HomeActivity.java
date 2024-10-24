package com.protreino.luxandapp.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.protreino.luxandapp.R;
import com.protreino.luxandapp.async.GetTicketGatesTask;
import com.protreino.luxandapp.main.App;
import com.protreino.luxandapp.service.StartTasksService;
import com.protreino.luxandapp.service.SyncronizeService;
import com.protreino.luxandapp.ui.BaseActivity;
import com.protreino.luxandapp.ui.components.Preview;
import com.protreino.luxandapp.ui.components.ProcessImageAndDrawResults;
import com.protreino.luxandapp.util.AppConstants;
import com.protreino.luxandapp.util.SharedPreferencesUtil;
import com.protreino.services.to.SimpleDevice;
import com.protreino.services.to.SimpleUser;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    public static final int REQUEST_WRITE_LOGS = 2;

    private HomeViewModel homeViewModel;
    private FrameLayout frameLayout;
    private FrameLayout progressBarHolder;
    private TextView selectedDevice;
    private TextView resultLabel;
    private Preview preview;
    private ProcessImageAndDrawResults draw;
    public static float sDensity = 1.0f;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("MainActiv", "Recebe: " + intent.getAction());
            if (SyncronizeService.BROADCAST_ACTION.equals(intent.getAction())){
                homeViewModel.finalizeSDK();
                homeViewModel.initializeSDK();
            }
            destroyProcessDialog();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sDensity = getResources().getDisplayMetrics().scaledDensity;

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.setHomeActivity(this);

        if (homeViewModel.hasError) {
            showAlertAndClose(homeViewModel.errorMessage);
        }
        else {
            setContentView(R.layout.activity_home);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            frameLayout = findViewById(R.id.preview_holder);
            progressBarHolder = findViewById(R.id.progress_bar_holder);
            selectedDevice = findViewById(R.id.selected_device);
            resultLabel = findViewById(R.id.result_label);

            selectedDevice.setText(homeViewModel.getSelectedDeviceName());

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setDisplayShowCustomEnabled(true);
                actionBar.setCustomView(R.layout.actionbar);
            }

            if (!allPermissionsGranted())
                requestPermission();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(broadcastReceiver,
                new IntentFilter(SyncronizeService.BROADCAST_ACTION));

        if (homeViewModel.hasError) {
            showAlertAndClose(homeViewModel.errorMessage);
        }
        else {
            if (allPermissionsGranted()) {
                createCameraLayerAndDrawLayer();
            }
        }
    }

    @Override
    public void onPause() {

        try {
            if (broadcastReceiver != null)
                unregisterReceiver(broadcastReceiver);
        } catch (Throwable e) {
            //caso não estaja registrado
        }

        if (draw != null || preview != null) {
            draw.stopping = 1;
            preview.releaseCallbacks();
            preview.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            preview = null;
            draw = null;
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showAlert("Acessar a câmera é essencial para o aplicativo", new Runnable() {
                    @Override
                    public void run() {
                        ActivityCompat.requestPermissions(HomeActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_PERMISSION_REQUEST_CODE);
                    }
                });
            } else {
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE);
            }
        }

        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_LOGS);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (allPermissionsGranted()) {
                createCameraLayerAndDrawLayer();
            }
            else {
                showAlertAndClose("Sem permissão para acessar a câmera");
            }
        }
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void createCameraLayerAndDrawLayer() {
        // Camera layer and drawing layer
        draw = new ProcessImageAndDrawResults(this);
        preview = new Preview(this, draw);

        frameLayout.addView(preview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(draw, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void flipCamera(View view) {
        preview.flipCamera();
    }

    public void showTicketGateMenu(View view) {
        pausePreview();
        showLoading();
        GetTicketGatesTask ticketGateListTask = new GetTicketGatesTask(homeViewModel);
        ticketGateListTask.execute();
    }

    public void showAlert(String message, final Runnable callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false); // cancel with button only
        if (callback != null) {
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    callback.run();
                }
            });
        }
        builder.show();
    }

    public void showAlertAndClose(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder.show();
    }

    public void showTicketGateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione uma catraca");

        List<String> deviceNames = new ArrayList<>();
        int checkedItem = -1;
        int index = 0;
        SimpleDevice actualSelectedDevice = homeViewModel.getSelectedDevice();
        for (SimpleDevice device : homeViewModel.deviceList) {
            deviceNames.add(device.getName());
            if (actualSelectedDevice != null && actualSelectedDevice.getIdentifier().equals(device.getIdentifier()))
                checkedItem = index;
            index++;
        }

        builder.setSingleChoiceItems(deviceNames.toArray(new String[0]), checkedItem, null);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                ListView listView = ((AlertDialog) dialog).getListView();
                homeViewModel.setSelectedDevice(homeViewModel.deviceList.get(listView.getCheckedItemPosition()));
                selectedDevice.setText(homeViewModel.getSelectedDeviceName());
                preview.resume();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int whichButton) {
                preview.resume();
            }
        });

        builder.show();
    }

    public void showLoading() {
        runOnUiThread(() -> {
            progressBarHolder.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        });
    }

    public void hideLoading() {
        runOnUiThread(() -> {
            progressBarHolder.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        });
    }

    public void resumePreview(){
        preview.resume();
    }

    public void pausePreview(){
        preview.pause();
    }

    public void setResultLabel(String text, int color) {
        runOnUiThread(() -> {
            resultLabel.setText(text);
            resultLabel.setTextColor(color);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        if("Aplicativo".equals(App.getPreferenceAsString(R.string.pref_tipo_verificacao))) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_home, menu);

            final MenuItem sair = menu.findItem(R.id.menu_sair);
            sair.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    openConfirmDialog("Tem certeza que deseja sair e apagar todos os dados deste telefone?", HomeActivity.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            realizaLogoff();
                        }
                    }, null);
                    return true;
                }
            });

            final MenuItem atualizar = menu.findItem(R.id.menu_atualizar);
            atualizar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    new SincronizaDadosTask().execute();
                    return true;
                }
            });
        }

        return true;

    }

    public class SincronizaDadosTask extends AsyncTask<Object, Integer, Object> {

        public SincronizaDadosTask(){
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            createProcessDialog(getString(R.string.msg_aguarde_procuro_dados));
        }

        @Override
        protected Object doInBackground(Object... objects) {
            SimpleUser user = SharedPreferencesUtil.getLoggedUser(HomeActivity.this);
            try {
                if (user != null) {
                    //sincronizacão inicial
                    //SyncronizeService.sincroniza(HomeActivity.this, false);
                    Intent service = new Intent(HomeActivity.this, SyncronizeService.class);
                    service.putExtra("sync", true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        HomeActivity.this.startForegroundService(service);
                    else
                        HomeActivity.this.startService(service);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
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

    }

}
