package br.com.smartacesso.pedestre.utils;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gustavo on 03/07/17.
 */

public class AppLogUtils {

    private static AppLogUtils instancia;
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdfLog;

    private AppLogUtils(){
        sdf = new SimpleDateFormat("yyyyMMdd");
        sdfLog = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    public static AppLogUtils getInstancia(){
        if(instancia == null)
            instancia = new AppLogUtils();
        return instancia;
    }


    public void gravaLog(String log){


        File externalStorageDir = Environment.getExternalStorageDirectory();
        File dir = new File(externalStorageDir , "pro-treino");
        if(!dir.exists())
            dir.mkdir();

        File myFile = new File(dir , "app-"+sdf.format(new Date())+".log");

        try{

            FileWriter fw = new FileWriter(myFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.print(sdfLog.format(new Date()) + ": " + log);
            out.close();

        }
        catch (IOException e){
            e.printStackTrace();
        }

    }



}
