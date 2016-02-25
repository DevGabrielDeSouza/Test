package nave.com.test;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText confirm ;
    private EditText emailBack;
    private EditText passwordBack ;
    private EditText confirmBack ;
    private TextView messager;
    private boolean sign ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sign = false;
        emailBack = getEditText("editText");
        passwordBack = getEditText("editText2");
        confirmBack = getEditText("editText3");
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
                        String save = email.getText().toString() + ";" + password.getText().toString() + ";";
                        FileOutputStream fileOut = openFileOutput("save.txt", MODE_PRIVATE);
                        OutputStreamWriter outputWriter = new OutputStreamWriter(fileOut);
                        outputWriter.write(save);
                        outputWriter.close();
                        ShowMessage("Done", true);
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
                    FileInputStream fileIn = openFileInput("save.txt");
                    InputStreamReader isr = new InputStreamReader(fileIn);
                    BufferedReader bufferedReader = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    List<String> mLines = new ArrayList<>();
                    String line;
                    String read = "";
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        mLines.add(line);
                    }
                    for (String string: mLines)
                    {
                        read += string;
                    }
                    String[] separated = read.split(";");
                    for (String getEmail : separated)
                    {
                        if(getEmail.equals(email.getText().toString()))
                        {
                            for (String getPass : separated)
                            {
                                if(getPass.equals(password.getText().toString()))
                                {
                                    ShowMessage("Logged", true);
                                    break;
                                }
                                else
                                {
                                    ShowMessage("Password incorrect", false);
                                }
                            }
                            break;
                        }
                        else
                        {
                            ShowMessage("Email not registered", false);
                        }
                    }
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
    private static final String[] OPCOES = {"Carregar Imagens",	"Exibir Imagens"	};
    private static final String[] ACOES = {"TELA_IMAGEM", "TELA_EXI"};
}
