package com.protreino.luxandapp.async;

import android.os.AsyncTask;

import com.protreino.luxandapp.R;
import com.protreino.luxandapp.interfaces.TaskCallback;
import com.protreino.luxandapp.main.App;
import com.protreino.luxandapp.model.ResultWrapper;
import com.protreino.services.enumeration.TaskType;
import com.protreino.services.enumeration.TcpMessageType;
import com.protreino.services.enumeration.VerificationResult;
import com.protreino.services.to.TcpMessageTO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ProcessAccessRequestTask extends AsyncTask<Void, Integer, ResultWrapper<VerificationResult>> {

    private TaskCallback callback;
    private Socket socket;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private String codigo;
    private String deviceIdentifier;
    private int timeout = 10000;

    public ProcessAccessRequestTask(TaskCallback callback, String codigo, String deviceIdentifier){
        super();
        this.callback = callback;
        this.codigo = codigo;
        this.deviceIdentifier = deviceIdentifier;
    }

    @Override
    protected ResultWrapper<VerificationResult> doInBackground(Void... params) {
        try {
            String ip = App.getPreferenceAsString(R.string.pref_desktop_ip);
            int port = App.getPreferenceAsInteger(R.string.pref_desktop_port);

            if (ip.isEmpty() || port <= 0)
                return new ResultWrapper<>("Endereço do servidor não configurado", TaskType.ACCESS_REQUEST);

            String codigoEnvio = "";
            if(codigo != null && codigo.contains("(")){
                String [] splited = codigo.split("\\(");
                if(splited.length > 1)
                    codigoEnvio = splited[1].replace(")", "").replace(" ", "");
                else
                    codigoEnvio = codigo;
            }else{
                codigoEnvio = codigo;
            }


            TcpMessageTO messageToServer = new TcpMessageTO(TcpMessageType.PROCESS_ACCESS_REQUEST_FROM_APP);
            messageToServer.getParans().put("device_identifier", deviceIdentifier);
            messageToServer.getParans().put("codigo", codigoEnvio);
            messageToServer.getParans().put("location", "App");
            messageToServer.getParans().put("createNotification", true);

            System.out.println("doInBackground: Tentando conectar em " + ip + ":" + port);

            socket = new Socket();
            socket.setTcpNoDelay(true);
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.setSoTimeout(timeout);

            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            out.writeObject(messageToServer);
            out.flush();

            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            TcpMessageTO serverResponse = (TcpMessageTO) in.readObject();

            if (serverResponse == null)
                return new ResultWrapper<>("Resposta nula do servidor", TaskType.ACCESS_REQUEST);

            VerificationResult result = (VerificationResult) serverResponse.getParans().get("verification_result");

            return new ResultWrapper<>(result, TaskType.ACCESS_REQUEST);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            closeAll();
        }
        return new ResultWrapper<>("Falhar ao validar acesso", TaskType.ACCESS_REQUEST);
    }

    @Override
    protected void onPostExecute(ResultWrapper<VerificationResult> resultWrapper) {
        super.onPostExecute(resultWrapper);
        callback.onTaskCompleted(resultWrapper);
    }

    private void closeAll(){
        try {
            if (socket != null)
                socket.close();
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
