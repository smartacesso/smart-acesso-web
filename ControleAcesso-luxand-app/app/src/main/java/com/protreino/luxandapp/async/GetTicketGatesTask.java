package com.protreino.luxandapp.async;

import android.os.AsyncTask;

import com.protreino.luxandapp.R;
import com.protreino.luxandapp.interfaces.TaskCallback;
import com.protreino.luxandapp.main.App;
import com.protreino.luxandapp.model.ResultWrapper;
import com.protreino.services.enumeration.TaskType;
import com.protreino.services.enumeration.TcpMessageType;
import com.protreino.services.to.SimpleDevice;
import com.protreino.services.to.TcpMessageTO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class GetTicketGatesTask extends AsyncTask<Void, Integer, ResultWrapper<List<SimpleDevice>>> {

    private TaskCallback callback;
    private Socket socket;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private int timeout = 4000;

    public GetTicketGatesTask(TaskCallback callback){
        super();
        this.callback = callback;
    }

    @Override
    protected ResultWrapper<List<SimpleDevice>> doInBackground(Void... params) {
        try {
            String ip = App.getPreferenceAsString(R.string.pref_desktop_ip);
            int port = App.getPreferenceAsInteger(R.string.pref_desktop_port);

            if (ip.isEmpty() || port <= 0)
                return new ResultWrapper<>("Endereço do servidor não configurado", TaskType.GET_TICKET_GATE);

            TcpMessageTO messageToServer = new TcpMessageTO(TcpMessageType.GET_TICKET_GATE_LIST);

            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.setSoTimeout(timeout);

            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            out.writeObject(messageToServer);
            out.flush();

            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            TcpMessageTO serverResponse = (TcpMessageTO) in.readObject();

            if (serverResponse == null)
                return new ResultWrapper<>("Resposta nula do servidor", TaskType.GET_TICKET_GATE);

            List<SimpleDevice> response = (List<SimpleDevice>) serverResponse.getParans().get("list");

            return new ResultWrapper<>(response, TaskType.GET_TICKET_GATE);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            closeAll();
        }
        return new ResultWrapper<>("Não foi possível obter lista de catracas.", TaskType.GET_TICKET_GATE);
    }

    @Override
    protected void onPostExecute(ResultWrapper<List<SimpleDevice>> resultWrapper) {
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
