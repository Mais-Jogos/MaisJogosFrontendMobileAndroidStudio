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

public class LoginUserActivity extends AppCompatActivity {
    TextView titleGradient;
    User user;
    TextView errorLoginUser;
    TextView sucessLoginUser;
    EditText emailUser;
    EditText senhaUser;
    EditText confirmarSenhaUser;
    Button btnCadastraUser;
    Gson gson = new Gson();
    private static final String URL = "http://10.0.2.2:8080/auth/cadastro/user";
    private static final String CADASTRO_USER = "Cadastro User";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user);

        /* Title Gradient */
        titleGradient =  findViewById(R.id.titleLoginUser);
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
            user = (User) b.getSerializable("User");
        }

        emailUser = findViewById(R.id.txtEmailUser);
        senhaUser = findViewById(R.id.txtSenhaUser);
        confirmarSenhaUser = findViewById(R.id.txtConfirmarSenhaUser);
        btnCadastraUser = findViewById(R.id.btnSaveUser);
        errorLoginUser = findViewById(R.id.errorLoginUser);
        sucessLoginUser = findViewById(R.id.sucessLoginUser);

        btnCadastraUser.setOnClickListener(e ->{
            if (validaCampos()){
                if (validaSenha()){
                    errorLoginUser.setText("");
                    user.setLogin(emailUser.getText().toString());
                    user.setPassword(senhaUser.getText().toString());
                    user.setConfirmarSenha(confirmarSenhaUser.getText().toString());
                    salvar(user);
                    Intent perfilUser = new Intent(this, PerfilUserActivity.class);
                    startActivity(perfilUser);
                }else{
                    errorLoginUser.setText("As senhas são diferentes!");
                }
            }else{
                errorLoginUser.setText("Preencha todos os campos!");
            }
        });
    }
    private boolean validaCampos(){
        return senhaUser.getText().length() != 0 &&
                emailUser.getText().length() != 0 &&
                confirmarSenhaUser.getText().length() != 0;
    }
    private boolean validaSenha(){
        return senhaUser.getText().toString().equals(confirmarSenhaUser.getText().toString());
    }
    @SuppressLint("SetTextI18n")
    private void salvar(User d){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            OkHttpClient client = new OkHttpClient();
            String userJson = gson.toJson(d);
            RequestBody body = RequestBody.create(userJson, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .post(body)
                    .url(URL)
                    .build();
            Call call = client.newCall(request);
            Log.i(CADASTRO_USER, "Request feita no servidor");
            Log.i(CADASTRO_USER, userJson);
            try{
                Response response = call.execute();
                sucessLoginUser.setText("Cadastrado com sucesso!");
                Log.i(CADASTRO_USER, "Response" + response);
            }catch (IOException e){
                Log.e(CADASTRO_USER, "Erro: ", e);
                throw  new RuntimeException(e);
            }
        });
    }
}
