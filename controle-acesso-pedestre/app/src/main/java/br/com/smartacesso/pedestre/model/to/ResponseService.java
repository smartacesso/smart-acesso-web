package br.com.smartacesso.pedestre.model.to;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

import br.com.smartacesso.pedestre.model.network.NetworkCall;

/**
 * Created by gustavo on 06/06/17.
 */

public class ResponseService {

    private String status;
    private String message;
    private Object object;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getObjectSize(){
        if(object != null && object instanceof LinkedTreeMap)
            return ((LinkedTreeMap) object).size();
        return 0;
    }

    public Object getObject() {

        if(object instanceof LinkedTreeMap){
            String json = NetworkCall.getGson().toJson((LinkedTreeMap)object, LinkedTreeMap.class);
            return json;
        }

        if(object instanceof ArrayList){
            String json = NetworkCall.getGson().toJson((ArrayList)object, ArrayList.class);
            return json;
        }

        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
