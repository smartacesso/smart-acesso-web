package br.com.smartacesso.pedestre.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.theartofdev.edmodo.cropper.CropImage;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.smartacesso.pedestre.R;
import br.com.smartacesso.pedestre.model.bo.PedestreBO;
import br.com.smartacesso.pedestre.model.entity.CadastroExterno;
import br.com.smartacesso.pedestre.model.entity.Pedestre;
import br.com.smartacesso.pedestre.model.to.FotoTO;
import br.com.smartacesso.pedestre.model.to.ResponseService;
import br.com.smartacesso.pedestre.service.StartTasksService;
import br.com.smartacesso.pedestre.service.SyncronizeService;
import br.com.smartacesso.pedestre.utils.AppConstants;
import br.com.smartacesso.pedestre.utils.ImageUtil;
import br.com.smartacesso.pedestre.utils.MaskEditUtil;
import br.com.smartacesso.pedestre.utils.QRCodeUtils;
import br.com.smartacesso.pedestre.utils.SharedPreferencesUtil;
import br.com.smartacesso.pedestre.views.adapters.FotoListViewAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

@EActivity
public class MainActivity extends BaseActivity {

    public static final String QR_CODE = "qrcode";
    public static final String FACIAL  = "facial";
    public String aba = QR_CODE;

    @ViewById LinearLayout container;
    @ViewById LinearLayout qrcodeContainer;
    @ViewById LinearLayout facialContainer;
    @ViewById TextView pedestre;
    @ViewById TextView organizacao;
    @ViewById LinearLayout perfil;
    @ViewById TextView cpf;
    @ViewById CircleImageView imagemProfile;

    //QRCode
    @ViewById ImageView qrCodeEmpty;
    @ViewById ImageView qrCode;
    @ViewById TextView qrCodeText;

    //Facial
    @ViewById CardView dadosFacialCard;
    @ViewById LinearLayout facialEmptyContainer;
    @ViewById TextView status;
    @ViewById TextView dataCadastro;
    @ViewById ListView listaImagens;
    @ViewById TextView labelListaImagens;
    @ViewById Button enviar;

    private AHBottomNavigation bottomNavigation;
    private boolean doubleBackToExitPressedOnce = false;
    Timer qrCodeDinamicoTempoTimer = null;

    private List<FotoTO> fotos;
    private Integer posicaoFoto;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("MainActiv", "Recebe: " + intent.getAction());
            if (SyncronizeService.BROADCAST_ACTION.equals(intent.getAction())){
                Pedestre u = SharedPreferencesUtil.getLoggedUser(MainActivity.this);
                montaDadosBasicosUiThread(u, true);
            }
            destroyProcessDialog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configuraNavigationBar();

        Pedestre u = SharedPreferencesUtil.getLoggedUser(this);
        if(u != null){
            montaDadosBasicos(u);
            montaQRCodeArea(u, false);
        }

    }

    private void configuraNavigationBar() {

        findViewById(R.id.navigation).setVisibility(View.GONE);

        int[] tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.navigation);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);

        bottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.colorPrimary));
        bottomNavigation.setAccentColor(getResources().getColor(R.color.white));
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.background_navigator));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setTitleTextSizeInSp(13.f,13.f);

        Pedestre u = SharedPreferencesUtil.getLoggedUser(this);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        //limpaTela();
                        aba = QR_CODE;
                        facialContainer.setVisibility(View.GONE);
                        qrcodeContainer.setVisibility(View.VISIBLE);
                        montaQRCodeArea(u, false);
                        return true;
                    case 1:
                        aba = FACIAL;
                        facialContainer.setVisibility(View.VISIBLE);
                        qrcodeContainer.setVisibility(View.GONE);
                        montaQRFacialArea(u, false);
                        return true;
                }
                return false;
            }
        });

    }

    private void montaQRFacialArea(Pedestre u, boolean b) {

        if(u.getFacialAtual() != null){
            //monta tela com os dados do cadastro facial
            facialEmptyContainer.setVisibility(View.GONE);
            dadosFacialCard.setVisibility(View.VISIBLE);

            status.setText(u.getFacialAtual().getStatusCadastroExternoStr());
            if(u.getFacialAtual().getDataCadastroDaFace() != null)
                dataCadastro.setText(new SimpleDateFormat("dd/MM/yyyy").format(u.getFacialAtual().getDataCadastroDaFace()));

            String textoFoto = "Capturar";
            if(!u.getFacialAtual().getStatusCadastroExterno().equals("AGUARDANDO_CADASTRO")){
                labelListaImagens.setText(R.string.label_foto_coletadas);
                enviar.setVisibility(View.GONE);
                textoFoto = "Visualizar";
                enviar.setVisibility(View.GONE);
            }else{
                CadastroExterno cFacial = u.getFacialAtual();
                enviar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //valida se todas fotos foram tiradas
                        if(fotos.get(0).getFoto() == null
                                || fotos.get(1).getFoto() == null
                                || fotos.get(2).getFoto() == null) {
                            openNeutroDialog("Faça a captura de todas as fotos antes de enviar!", "Ops!", MainActivity.this);
                            return;
                        }
                        //envia para servidor e aguarda resposta
                        cFacial.setPrimeiraFoto(fotos.get(0).getFoto());
                        cFacial.setSegundaFoto(fotos.get(1).getFoto());
                        cFacial.setTerceiraFoto(fotos.get(2).getFoto());

                        cFacial.setImageHeight(fotos.get(0).getImageHeight());
                        cFacial.setImageWidth(fotos.get(0).getImageWidth());

                        envia(cFacial);

                    }
                });
            }
            labelListaImagens.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //exibir janela com texto de ajuda
                    String msg = "";
                    if(u.getFacialAtual().getStatusCadastroExterno().equals("AGUARDANDO_CADASTRO")) {
                        msg = "Suas fotos serão utilizadas somente para fins de identificação e reconhecidmento facial.\n\n" +
                              "- Procure um local bem iluminado\n" +
                              "- Aproxime-se da câmera e à foque em sua face\n" +
                              "- Certifique-se que seu rosto está enquadrado na câmera";
                    }else{
                        msg = "Suas fotos serão utilizadas somente para fins de identificação e reconhecidmento facial.\n\n" +
                                "- Todas as fotos foram coletadas e estão sendo analisadas.";
                    }
                    openNeutroDialog(msg, "Ajuda", MainActivity.this);
                }
            });

            fotos = new ArrayList<>();
            fotos.add(new FotoTO(textoFoto +" foto 1/3 ", u.getFacialAtual().getPrimeiraFoto()));
            fotos.add(new FotoTO(textoFoto +" foto 2/3", u.getFacialAtual().getSegundaFoto()));
            fotos.add(new FotoTO(textoFoto +" foto 3/3", u.getFacialAtual().getTerceiraFoto()));

            FotoListViewAdapter adapter = new FotoListViewAdapter(MainActivity.this,
                    R.layout.item_foto, fotos);
            listaImagens.setAdapter(adapter);
            listaImagens.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    acaoFoto(position, u);
                }
            });

        }else{
            facialEmptyContainer.setVisibility(View.VISIBLE);
            dadosFacialCard.setVisibility(View.GONE);
        }

    }

    @Background
    public void envia(CadastroExterno cFacial) {
        createProcessDialog(getString(R.string.msg_aguarde_envio_facial));

        try{

            PedestreBO bo = new PedestreBO(MainActivity.this);
            ResponseService resp =  bo.registraFacial(cFacial);

            if(resp != null && resp.getStatus().equals("OK"))
                envioComSucesso(resp);
            else
                envioSemSucesso(resp.getMessage());

        }catch (Exception e){
            e.printStackTrace();
            envioSemSucesso(e.getMessage());
        }

    }

    @UiThread
    public void envioComSucesso(ResponseService response){
        sincronizarDados();
    }

    @UiThread
    public void envioSemSucesso(String msg){
        destroyProcessDialog();
        //prepara tela
        openNeutroDialog("Dados não enviada!\n"
                + (msg != null ? "Motivo: " + msg : "" )
                + "\nTente novamente!", "Opss!", this);

    }

    private void acaoFoto(int position, Pedestre u) {
        if(u.getFacialAtual() != null){
            posicaoFoto = position;
            if(u.getFacialAtual().getStatusCadastroExterno().equals("AGUARDANDO_CADASTRO"))
                openSelectImage();
            else
                openImage(fotos.get(position));
        }
    }

    private void openImage(FotoTO fotoTO) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)){
            Image image = ImagePicker.getFirstImageOrNull(data);
            if(image != null && image.getPath() != null){
                try {

                    Uri imageUri = Uri.fromFile(new File(image.getPath()));
                    Bitmap imgBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, null);

                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);

                    Bitmap rotated = Bitmap.createBitmap(imgBitmap, 0, 0,
                            imgBitmap.getWidth(), imgBitmap.getHeight(),
                            matrix, true);

                    FotoTO to = fotos.get(posicaoFoto);
                    to.setFoto(ImageUtil.getImagemRedimensionada(imgBitmap));
                    to.setImageHeight(imgBitmap.getWidth()/2);
                    to.setImageWidth(imgBitmap.getHeight()/2);

                    ArrayAdapter adapter = ((ArrayAdapter)listaImagens.getAdapter());
                    if(adapter != null)
                        adapter.notifyDataSetChanged();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }

    }

    private void montaQRCodeArea(Pedestre u, boolean sincronizado) {

        if(u.getQrCodeParaAcesso() != null
                && !"".equals(u.getQrCodeParaAcesso())){
            //verifica tipo do QRCode
            String qrCodeValue      = u.getQrCodeParaAcesso();
            String qrCodeValueText  = "";

            if("DINAMICO_TEMPO".equals(u.getTipoQRCode())){

                if(sincronizado && qrCodeText.getText().toString().contains("por tempo"))
                    return;

                if(u.getTempoRenovacaoQRCode() != null
                        && !"".equals(u.getTempoRenovacaoQRCode())){
                    Integer tempoRenovacao = Integer.parseInt(u.getTempoRenovacaoQRCode());
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.MINUTE, tempoRenovacao);

                    qrCodeValue = qrCodeValue + "_" + new SimpleDateFormat("HHmmss").format(c.getTime());
                    qrCodeValueText = "QRCode dinâmico por tempo\n(válido até "
                            + new SimpleDateFormat("HH:mm:ss").format(c.getTime())  + ") ";

                    QRCodeDinamicoTimerTask task = new QRCodeDinamicoTimerTask();
                    qrCodeDinamicoTempoTimer = new Timer();
                    qrCodeDinamicoTempoTimer.schedule(task,
                            tempoRenovacao * 1000 * 60,
                            tempoRenovacao * 1000 * 60);
                }else{
                    qrCodeDinamicoTempoTimer = null;
                }

            }else if("DINAMICO_USO".equals(u.getTipoQRCode()))
                qrCodeValueText = "QRCode dinâmico por uso\n(Após o uso, aguardar sincronização automática)";
            else
                qrCodeValueText = "QRCode estático";

            alteraQRCode(qrCodeValue, qrCodeValueText);
        }else{
            qrCodeEmpty.setVisibility(View.VISIBLE);
            qrCode.setVisibility(View.GONE);
            qrCodeText.setText(R.string.mensagem_nenhum_item_encontrado);
        }

    }

    @UiThread
    public void alteraQRCode(String qrCodeValue, String qrCodeValueText) {
        Bitmap bmp = QRCodeUtils.getQRCodeBitmap(qrCodeValue);
        if(bmp != null) {
            qrCodeEmpty.setVisibility(View.GONE);
            qrCode.setVisibility(View.VISIBLE);
            qrCode.setImageBitmap(bmp);
            qrCodeText.setText(qrCodeValueText);
        }else{
            qrCodeEmpty.setVisibility(View.VISIBLE);
            qrCode.setVisibility(View.GONE);
            qrCodeText.setText(R.string.mensagem_nenhum_item_encontrado);
        }
    }

    @UiThread
    public void montaDadosBasicosUiThread(Pedestre u, boolean sincronizado){
        this.montaDadosBasicos(u);
        if(aba.equals(QR_CODE)) {
            facialContainer.setVisibility(View.GONE);
            qrcodeContainer.setVisibility(View.VISIBLE);
            this.montaQRCodeArea(u, sincronizado);
        }else{
            facialContainer.setVisibility(View.VISIBLE);
            qrcodeContainer.setVisibility(View.GONE);
            this.montaQRFacialArea(u, sincronizado);
        }
    }

    public void montaDadosBasicos(Pedestre u) {

        pedestre. setText(u.getNome()+"");
        if(u.getCpf() != null && !"".equals(u.getCpf())){
            cpf.setText(MaskEditUtil.mask(u.getCpf(), MaskEditUtil.FORMAT_CPF));
        }else{
            cpf.setText("Não informado");
        }
        if(u.getFoto() != null) {
            imagemProfile.setImageBitmap(
                    BitmapFactory.decodeByteArray(
                            u.getFoto(), 0, u.getFoto().length));
        }
        organizacao.setText(u.getUnidade());


        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sincronizarDados();
            }
        });

    }


    @UiThread
    public void sincronizarDados() {
        createProcessDialog(getString(R.string.msg_aguarde_procuro_dados));
        Pedestre user = SharedPreferencesUtil.getLoggedUser(this);
        try {
            if (user != null) {
                //sincronizacão inicial
                Intent service = new Intent(this, SyncronizeService.class);
                service.putExtra("sync", true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    this.startForegroundService(service);
                else
                    this.startService(service);
            }
        } catch (Exception e) {
            loginErro(e.getMessage());
        }
    }

    @UiThread
    public void loginErro(String msg) {
        openNeutroDialog(msg, getString(R.string.titulo_ops), this);
        destroyProcessDialog();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        final MenuItem sair = menu.findItem(R.id.sair);
        sair.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openConfirmDialog("Tem certeza que deseja sair e apagar todos os dados deste telefone?", MainActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realizaLogoff();
                    }
                }, null);
                return true;
            }
        });

        final MenuItem atualizar = menu.findItem(R.id.atualizar);
        atualizar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sincronizarDados();
                return true;
            }
        });

        return true;

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Snackbar snack = Snackbar.make(container, R.string.msg_duas_vezes, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        snack.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


    public void realizaLogoff() {

        SharedPreferences prefs = this.getSharedPreferences(
                AppConstants.APP_SHARED_PROPS, Context.MODE_PRIVATE);

        for (String syncService : SyncronizeService.SYNC_SERVICES)
            SharedPreferencesUtil.removeSharePreference(syncService, MainActivity.this);

        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();

        StartTasksService.para(this);

        try {
            // clearing app data
            String packageName = getApplicationContext().getPackageName();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear " + packageName);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,
                new IntentFilter(SyncronizeService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        try {
            if (broadcastReceiver != null)
                unregisterReceiver(broadcastReceiver);
        } catch (Throwable e) {
            //caso não estaja registrado
        }
        super.onPause();
    }


    public class QRCodeDinamicoTimerTask  extends TimerTask {
        @Override
        public void run() {
            Pedestre pedestre = SharedPreferencesUtil.getLoggedUser(MainActivity.this);
            if(pedestre != null
                    && pedestre.getTempoRenovacaoQRCode() != null
                    && !"".equals(pedestre.getTempoRenovacaoQRCode())){

                Integer tempoRenovacao = Integer.parseInt(pedestre.getTempoRenovacaoQRCode());
                Calendar c = Calendar.getInstance();
                c.add(Calendar.MINUTE, tempoRenovacao);

                alteraQRCode(pedestre.getQrCodeParaAcesso() + "_" + new SimpleDateFormat("HHmmss").format(c.getTime()),
                        "QRCode dinâmico por tempo\n(válido até "
                                + new SimpleDateFormat("HH:mm").format(c.getTime())  + ")");
            }

        }
    }



}
