package nave.com.test;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class HTTPRequests implements Runnable
{
    private Map<Object, Object> params;
    private HTTPSendToServer sendToServer;
    private Object serverResponse = null;

    public enum Services
    {
        CONNECT, LOADFILE, SUM, SUB, MULT, DIV, REGISTRY, LOGIN
    }

    public HTTPRequests()
    {
        params = new HashMap<>();
        sendToServer = new HTTPSendToServer();
    }

    public Object ServerResponse() { return serverResponse; }
    public void NullServerResponse() { serverResponse = null; }

    public void Connect()
    {
        params.clear();
        StartThread("Connect Thread");
    }

    public void Login(String email, String pass)
    {
        params.clear();
        params.put("sID", Services.LOGIN.ordinal());
        params.put("Nome", email);
        params.put("Pass", pass);

        StartThread("LoginThread");
    }

    public void RegisterLogin(String email, String pass)
    {
        params.clear();
        params.put("sID", Services.REGISTRY.ordinal());
        params.put("Nome", email);
        params.put("Pass", pass);
        Log.e("REGISTERRR PARAMNSSSSS", "OIjisodfjsjidofjiosdjiofjisdo");

        StartThread("LoginThread");
    }

    private void StartThread(String name)
    {
        Thread thread = new Thread(this);
        thread.setName(name);
        thread.start();
    }

    @Override
    public void run() { serverResponse = sendToServer.DoPost(params); }
}