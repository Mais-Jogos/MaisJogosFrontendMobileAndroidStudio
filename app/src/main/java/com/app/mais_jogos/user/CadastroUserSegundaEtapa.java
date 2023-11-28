package com.app.mais_jogos.user;

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

public class CadastroUserSegundaEtapa extends AppCompatActivity {
    User user;
    TextView titleGradient;
    TextView errorUser2;
    TextView sucessUser2;
    EditText emailUser;
    EditText senhaUser;
    EditText confirmarSenhaUser;
    Button btnCadastraUser;
    Gson gson = new Gson();
    private static final String URL = "http://10.0.2.2:8080/api/usuario/salvar";
    private static final String CADASTRO_USER = "Cadastro User";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_user_segunda_etapa);

        /* Title Gradient */
        titleGradient =  findViewById(R.id.titleSegundoUser);
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
        errorUser2 = findViewById(R.id.errorSegundoUser);
        sucessUser2 = findViewById(R.id.sucessSegundoUser);
        btnCadastraUser = findViewById(R.id.btnSaveUser);

        btnCadastraUser.setOnClickListener(e ->{
            if (validaCampos()){
                if (validaSenha()){
                    errorUser2.setText("");
                    user.setLogin(emailUser.getText().toString());
                    user.setPassword(senhaUser.getText().toString());
                    user.setConfirmarSenha(confirmarSenhaUser.getText().toString());
                    salvar(user);
                }else{
                    errorUser2.setText("As senhas são diferentes!");
                }
            }else{
                errorUser2.setText("Preencha todos os campos!");
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
    private void salvar(User d){
        SharedPreferences sp = this.getSharedPreferences("CADASTRO", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String userJson = gson.toJson(d);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(userJson, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .post(body)
                    .url(URL)
                    .build();
            Call call = client.newCall(request);
            Log.i(CADASTRO_USER, "Request feita no servidor "+ userJson);

            try(Response response = call.execute()){
                if(response.isSuccessful()){
                    String strResposta = response.body().string();
                    JsonObject convertObject = gson.fromJson(strResposta, JsonObject.class);
                    User userData = gson.fromJson(convertObject, User.class);
                    Log.i(CADASTRO_USER, "User resposta: " + strResposta);

                    Storage s = new Storage();
                    s.setId(String.valueOf(userData.getId()));
                    s.setType("user");

                    String storage = gson.toJson(s);
                    editor.putString("storage", storage);
                    editor.apply();
                    Log.i(CADASTRO_USER, "Storage Cadastro " + storage);

                    sucessUser2.setText("Cadastrado com sucesso!");
                    Intent login = new Intent(this, Login.class);
                    startActivity(login);
                }else{
                    Log.e(CADASTRO_USER, "Erro na requisição: " + response.code());
                    errorUser2.setText("Ocorreu um erro, tente novamente!");
                }
            }catch (IOException e){
                Log.e(CADASTRO_USER, "Erro: ", e);
                errorUser2.setText("Ocorreu um erro, tente novamente!");
                throw  new RuntimeException(e);
            }
        });
    }
}