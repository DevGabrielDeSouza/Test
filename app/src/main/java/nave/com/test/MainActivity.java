package nave.com.test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ServiceConnection, Runnable
{

    private EditText email;
    private EditText password;
    private EditText confirm ;
    private EditText emailBack;
    private EditText passwordBack ;
    private EditText confirmBack ;
    private EditText nameBack;
    private TextView messager;
    private boolean sign ;
    static final String DB_URL = "http://10.10.11.9/login_server/Registro_Set.php";

    private HTTPService myService;
    private HTTPRequests.Services currService;
    private final ServiceConnection connection = this;
    private Intent i;
    private Handler handler;
    private Button buttonLogin;
    private int pingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetInterface();

        i = new Intent(this, HTTPService.class);

        pingTime = 0;

        handler = new Handler();
        startService(i);

        buttonLogin = (Button) findViewById(R.id.logButton);
        buttonLogin.setVisibility(View.INVISIBLE);
        ShowMessage("No Connection", false);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        bindService(i, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        handler.removeCallbacks(this);

        if(myService.GetRequests() != null)
        {
            unbindService(connection);
            stopService(i);
        }
    }

    private void Login(String login, String pass)
    {
        myService.GetRequests().Login(login, pass);
        currService = HTTPRequests.Services.LOGIN;
        pingTime = 0;
        handler.post(this);
    }

    private void RegisterLogin(String login, String pass)
    {
        Log.e("TENTEI FAZER LOGN!!", "OI");
        myService.GetRequests().RegisterLogin(login, pass);
        currService = HTTPRequests.Services.REGISTRY;
        pingTime = 0;
        handler.post(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service)
    {
        HTTPService.HTTPBinder binder = (HTTPService.HTTPBinder) service;
        myService = binder.GetService();
        myService.GetRequests().Connect();
        currService = HTTPRequests.Services.CONNECT;
        pingTime = 0;
        handler.post(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        myService = null;
        handler.removeCallbacks(this);
    }

    @Override
    public void run()
    {
        if(myService.GetRequests().ServerResponse() != null)
        {
            if(currService == HTTPRequests.Services.CONNECT) ConnectionResponse(myService.GetRequests().ServerResponse());
            else if (currService == HTTPRequests.Services.LOGIN) LoginResponse(myService.GetRequests().ServerResponse());
        }
        else
        {
            if(pingTime < 5)
            {
                pingTime++;
                handler.postDelayed(this, 3000);
                Toast.makeText(this, "Waiting server response - Attempt: " + pingTime, Toast.LENGTH_SHORT).show();
            }
            else
            {
                pingTime = 0;
                Toast.makeText(this, "Server doesn't response. Try again later.", Toast.LENGTH_SHORT).show();
                finish(); //backing to previous Activity
            }
        }

        myService.GetRequests().NullServerResponse();
    }

    private void ConnectionResponse(Object serverResponse)
    {
        if((Boolean) serverResponse)
        {
            Toast.makeText(this, "Server connected!!", Toast.LENGTH_SHORT).show();
            buttonLogin.setEnabled(true);
            buttonLogin.setVisibility(View.VISIBLE);
            ShowMessage("  ", false);
        } else
        {
            unbindService(connection);
            stopService(i);

            Toast.makeText(this, "Problems to connect with server", Toast.LENGTH_SHORT).show();
        }
    }

    private void LoginResponse(Object serverResponse) {
        if(serverResponse.toString().equals(email.getText().toString()+"|"+password.getText().toString()+"|")){
            Log.e("AMARINHOOOOOOOOOOOOOOOO", serverResponse.toString());

        }else {
            ShowMessage("User or password wrong", false);
            Log.e("CERTO:", email.getText().toString() + "|" + password.getText().toString()+"|");
            Log.e("ERRADO: ", serverResponse.toString());
        }
        /*if(!serverResponse.toString().equals(""))
            //ShowMessage(serverResponse.toString(), true);
            Log.e("AMARINHOOOOOOOOOOOOOOOO", serverResponse.toString());
        else
            ShowMessage("User not found", false);*/
    }

    private void SetInterface()
    {
        sign = false;
        emailBack = getEditText("editText");
        passwordBack = getEditText("editText2");
        confirmBack = getEditText("editText3");
        nameBack = getEditText("editText5");
        emailBack.setKeyListener(null);
        passwordBack.setKeyListener(null);
        confirmBack.setKeyListener(null);
        email = getEditText("emailText");
        password = getEditText("passText");
        confirm = getEditText("confirmText");
        messager = (TextView)findViewById(R.id.message);
        email.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
                if(isEmpty(email))
                {
                    emailBack.setText(R.string.Email);
                }
                else
                    emailBack.setText("  ");
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        password.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
                if(isEmpty(password))
                {
                    passwordBack.setText(R.string.Password);
                }
                else
                    passwordBack.setText("  ");
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        confirm.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
                if(isEmpty(confirm))
                {
                    confirmBack.setText(R.string.ConfirmPassword);
                }
                else
                    confirmBack.setText("  ");
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }
    private EditText getEditText(String id)
    {
        int resID = getResources().getIdentifier(id, "id", "nave.com.test");
        return  (EditText) findViewById(resID);
    }
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().equals("");
    }
    public void ChangeSceneSign(View v)
    {
        if(!sign)
        {
            EditText s = (EditText) findViewById(R.id.editText3);
            s.setVisibility(View.VISIBLE);
            s = (EditText) findViewById(R.id.confirmText);
            s.setVisibility(View.VISIBLE);
            Button b = (Button)  findViewById(R.id.signButton);
            b.setVisibility(View.VISIBLE);
            b = (Button)  findViewById(R.id.logButton);
            b.setVisibility(View.GONE);
            b = (Button)  findViewById(R.id.sign);
            b.setText(R.string.Login);
            sign = true;
        }
        else
        {
            EditText s = (EditText) findViewById(R.id.editText3);
            s.setVisibility(View.GONE);
            s = (EditText) findViewById(R.id.confirmText);
            s.setVisibility(View.GONE);
            Button b = (Button)  findViewById(R.id.signButton);
            b.setVisibility(View.GONE);
            b = (Button)  findViewById(R.id.logButton);
            b.setVisibility(View.VISIBLE);
            b = (Button)  findViewById(R.id.sign);
            b.setText(R.string.SignFace);
            sign = false;
        }
    }
    private void ShowMessage(String message, boolean done)
    {
        if (!done)
        {
            messager.setTextColor(getResources().getColor(R.color.Red));
            messager.setText(message);
        }
        else
        {
            messager.setTextColor(getResources().getColor(R.color.Green));
            messager.setText(message);
        }
    }
    private boolean isEmailValid(CharSequence email) {return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(); }

    public void SaveLogin(View v)
    {
        if(!isEmpty(email) && !isEmpty(password) && !isEmpty(confirm))
        {
            if(isEmailValid(email.getText().toString()))
            {
                if(confirm.getText().toString().equals(password.getText().toString()))
                {
                        try
                        {
                            Wrapper conn = null;
                            //String _email = email.getText().toString();
                            String _name =  email.getText().toString();
                            String _password =  password.getText().toString();
                            //String IQuery = "INSERT INTO `registro`(`Nome`,`Senha`) VALUES('"+_name+"', '"+_password+"')";
                            //System.out.println(IQuery);				//STEP 3: Open a connection
                            //conn = DriverManager.getConnection(DB_URL, "localhost", "");
                            //((Connection)conn).createStatement().execute(IQuery);
                            RegisterLogin(_name, _password);
                            ShowMessage("Registred", true);//print on console
                            //http://10.10.11.62/login_server/registro_set.php?Nome="Miiler"&Senha="123"&Email="aoisjdiao@zsdjioad.co
                        }
                        catch (Exception e)
                        {
                            ShowMessage("Error!", false);
                        }
                }
                else
                {
                    ShowMessage("Passwords are Different",false);
                }
            }
            else
            {
                ShowMessage("Insert a Valid Email", false);
            }
        } else
        {
            ShowMessage("Fill All", false);
        }
    }
    public void Log(View v)
    {
        if(!isEmpty(email) && !isEmpty(password))
        {
            if(isEmailValid(email.getText().toString()))
            {
                try
                {
                    Login(email.getText().toString(), password.getText().toString());
                    ShowMessage("Logged", true);
                }
                catch (Exception e){ShowMessage("Error!", false);}
            }
            else
            {
                ShowMessage("Insert a Valid Email", false);
            }
        } else
        {
            ShowMessage("Fill All", false);
        }
    }
    /*public boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }*/
    private static final String[] OPCOES = {"Carregar Imagens",	"Exibir Imagens"	};
    private static final String[] ACOES = {"TELA_IMAGEM", "TELA_EXI"};
}
