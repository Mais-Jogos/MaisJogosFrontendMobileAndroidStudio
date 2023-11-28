package com.app.mais_jogos.dev;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.app.mais_jogos.Login;
import com.app.mais_jogos.R;
import com.app.mais_jogos.Storage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CadastroDevSegundaEtapa extends AppCompatActivity {
    Dev dev;
    TextView titleGradient;
    TextView errorDev2;
    TextView sucessDev2;
    EditText emailDev;
    EditText senhaDev;
    EditText confirmarSenhaDev;
    Button btnCadastraDev;
    Gson gson = new Gson();
    private static final String URL = "http://10.0.2.2:8080/api/usuario/salvar";
    private static final String CADASTRO_DEV = "Cadastro Dev";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_dev_segunda_etapa);

        /* Title Gradient */
        titleGradient =  findViewById(R.id.titleSegundoDev);
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
        errorDev2 = findViewById(R.id.errorSegundoDev);
        sucessDev2 = findViewById(R.id.sucessSegundoDev);
        btnCadastraDev = findViewById(R.id.btnSaveDev);

        btnCadastraDev.setOnClickListener(e ->{
            if (validaCampos()){
                if (validaSenha()){
                    errorDev2.setText("");
                    dev.setLogin(emailDev.getText().toString());
                    dev.setPassword(senhaDev.getText().toString());
                    dev.setConfirmarSenha(confirmarSenhaDev.getText().toString());
                    salvar(dev);
                }else{
                    errorDev2.setText("As senhas são diferentes!");
                }
            }else{
                errorDev2.setText("Preencha todos os campos!");
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
    private void salvar(Dev d){
        SharedPreferences sp = this.getSharedPreferences("CADASTRO", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String devJson = gson.toJson(d);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(devJson, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .post(body)
                    .url(URL)
                    .build();
            Call call = client.newCall(request);
            Log.i(CADASTRO_DEV, "Request feita no servidor "+ devJson);

            try(Response response = call.execute()){
                if(response.isSuccessful()){
                    String strResposta = response.body().string();
                    JsonObject convertObject = gson.fromJson(strResposta, JsonObject.class);
                    Dev devData = gson.fromJson(convertObject, Dev.class);
                    Log.i(CADASTRO_DEV, "Dev resposta: " + strResposta);

                    Storage s = new Storage();
                    s.setId(String.valueOf(devData.getId()));
                    s.setType("dev");

                    String storage = gson.toJson(s);
                    editor.putString("storage", storage);
                    editor.apply();
                    Log.i(CADASTRO_DEV, "Storage Cadastro " + storage);

                    sucessDev2.setText("Cadastrado com sucesso!");
                    Intent login = new Intent(this, Login.class);
                    startActivity(login);
                }else{
                    Log.e(CADASTRO_DEV, "Erro na requisição: " + response.code());
                    errorDev2.setText("Ocorreu um erro, tente novamente!");
                }
            }catch (IOException e){
                Log.e(CADASTRO_DEV, "Erro: ", e);
                errorDev2.setText("Ocorreu um erro, tente novamente!");
                throw  new RuntimeException(e);
            }
        });
    }
}
