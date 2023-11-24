package com.app.mais_jogos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private static final String URL = "http://10.0.2.2:8080/auth/cadastro/dev";
    private static final String CADASTRO_DEV = "Cadastro Dev";

    @SuppressLint("SetTextI18n")
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
        if (b != null) {
            dev = (Dev) b.getSerializable("Dev");
        }

        emailDev = findViewById(R.id.txtEmailDev);
        senhaDev = findViewById(R.id.txtSenhaDev);
        confirmarSenhaDev = findViewById(R.id.txtConfirmarSenhaDev);
        btnCadastraDev = findViewById(R.id.btnSaveDev);
        errorLoginDev = findViewById(R.id.errorLoginDev);
        sucessLoginDev = findViewById(R.id.sucessLoginDev);

        btnCadastraDev.setOnClickListener(e ->{
            if (validaCampos()){
                if (validaSenha()){
                    errorLoginDev.setText("");
                    dev.setLogin(emailDev.getText().toString());
                    dev.setPassword(senhaDev.getText().toString());
                    dev.setConfirmarSenha(confirmarSenhaDev.getText().toString());
                    salvar(dev);
                    Intent perfilDev = new Intent(this, PerfilDevActivity.class);
                    startActivity(perfilDev);
                }else{
                    errorLoginDev.setText("As senhas são diferentes!");
                }
            }else{
                errorLoginDev.setText("Preencha todos os campos!");
            }
        });
    }
    private boolean validaCampos(){
        return senhaDev.getText().length() != 0 &&
                emailDev.getText().length() != 0 &&
                confirmarSenhaDev.getText().length() != 0;
    }
    private boolean validaSenha(){
        return senhaDev.getText().toString().equals(confirmarSenhaDev.getText().toString());
    }
    @SuppressLint("SetTextI18n")
    private void salvar(Dev d){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            OkHttpClient client = new OkHttpClient();
            String devJson = gson.toJson(d);
            RequestBody body = RequestBody.create(devJson, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .post(body)
                    .url(URL)
                    .build();
            Call call = client.newCall(request);
            Log.i(CADASTRO_DEV, "Request feita no servidor");
            Log.i(CADASTRO_DEV, devJson);
            try{
                Response response = call.execute();
                sucessLoginDev.setText("Cadastrado com sucesso!");
                Log.i(CADASTRO_DEV, "Response" + response);
            }catch (IOException e){
                Log.e(CADASTRO_DEV, "Erro: ", e);
                throw  new RuntimeException(e);
            }
        });
    }
}
