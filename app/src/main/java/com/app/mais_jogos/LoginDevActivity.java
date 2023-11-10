package com.app.mais_jogos;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class LoginDevActivity extends AppCompatActivity {
    TextView titleGradient;
    Dev dev;
    TextView errorLoginDev;
    TextView sucessLoginDev;
    EditText emailDev;
    EditText senhaDev;
    EditText confirmarSenhaDev;
    Button btnCadastraDev;
    Gson gson = new Gson();
    private static final String URL = "http://localhost:8080/auth/cadastro/dev";
    private static final String CADASTRO_DEV = "Cadastro Dev";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dev);

        /* Title Gradient */
        titleGradient =  findViewById(R.id.titleLoginDev);
        TextPaint paint = titleGradient.getPaint();
        float width = paint.measureText("Cadastre-se");

        Shader textShader = new LinearGradient(0, 0, width, titleGradient.getTextSize(),
                new int[]{
                        Color.parseColor("#7900FB"),
                        Color.parseColor("#4B3068"),
                }, null, Shader.TileMode.CLAMP);
        titleGradient.getPaint().setShader(textShader);
        /* Title Gradient */

        /* Pegando as informações da outra activity*/
        Bundle b = getIntent().getExtras();
        dev = (Dev) b.getSerializable("Dev");

        emailDev = findViewById(R.id.txtEmailDev);
        senhaDev = findViewById(R.id.txtSenhaDev);
        confirmarSenhaDev = findViewById(R.id.txtConfirmarSenhaDev);
        btnCadastraDev = findViewById(R.id.btnSaveDev);
        errorLoginDev = findViewById(R.id.errorLoginDev);
        sucessLoginDev = findViewById(R.id.sucessLoginDev);

        btnCadastraDev.setOnClickListener(e ->{
            if (validaCampos() == true){
                if (validaSenha() == true){
                    errorLoginDev.setText("");
                    dev.setLogin(emailDev.getText().toString());
                    dev.setPassword(senhaDev.getText().toString());
                    dev.setConfirmarSenha(confirmarSenhaDev.getText().toString());
                    salvar(dev);
                }else{
                    errorLoginDev.setText("As senhas são diferentes!");
                }
            }else{
                errorLoginDev.setText("Preencha todos os campos!");
            }
        });
    }
    private boolean validaCampos(){
        if (senhaDev.getText().length() != 0 &&
                emailDev.getText().length() != 0 &&
                confirmarSenhaDev.getText().length() != 0){
            return true;
        }else{
            return false;
        }
    }
    private boolean validaSenha(){
        if (senhaDev.getText().toString().equals(confirmarSenhaDev.getText().toString())){
            return true;
        }else{
            return false;
        }
    }
    private void salvar(Dev d){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            OkHttpClient client = new OkHttpClient();
            String devJson = gson.toJson(d);
            RequestBody body = RequestBody.create(devJson, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .post(body)
                    .header("Bearer", "")
                    .url(URL)
                    .build();
            Call call = client.newCall(request);
            Log.i(CADASTRO_DEV, "Request feita no servidor");
            try{
                Response response = call.execute();
                sucessLoginDev.setText("Cadastrado com sucesso!");
            }catch (IOException e){
                Log.e(CADASTRO_DEV, "Erro: ", e);
                throw  new RuntimeException(e);
            }
        });
    }
}
