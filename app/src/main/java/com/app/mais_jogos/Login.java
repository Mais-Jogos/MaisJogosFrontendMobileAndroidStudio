package com.app.mais_jogos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.mais_jogos.dev.PerfilDev;
import com.app.mais_jogos.user.PerfilUser;
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

public class Login extends AppCompatActivity {

    TextView cadastro;
    TextView erroLogin;
    TextView sucessLogin;
    EditText emailLogin;
    EditText senhaLogin;
    Button btnLogin;
    private final String URL = "http://10.0.2.2:8080/login";
    Gson gson = new Gson();
    Storage storage = new Storage();
    private static final String LOGIN = "Login";

    class DadosLogin {
        public String login = "";
        public String password = "";

        public DadosLogin(String login, String password) {
            this.login = login;
            this.password = password;
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        cadastro = findViewById(R.id.txtLoginCadastro);
        erroLogin = findViewById(R.id.erroLogin);
        sucessLogin = findViewById(R.id.sucessLogin);
        emailLogin = findViewById(R.id.txtEmailLogin);
        senhaLogin = findViewById(R.id.txtSenhaLogin);
        btnLogin = findViewById(R.id.btnLogin);

        cadastro.setOnClickListener( e ->{
            Intent SelectPlayer = new Intent(this, SelectPlayer.class);

            startActivity(SelectPlayer);
        });

        btnLogin.setOnClickListener(e ->{
            salvar();
        });

    }
    private void salvar(){
        DadosLogin userLogin = new DadosLogin(
                emailLogin.getText().toString(),
                senhaLogin.getText().toString()
        );
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() ->{
            OkHttpClient client = new OkHttpClient();
            String loginJson = gson.toJson(userLogin);
            Log.i(LOGIN, "Login json "+ loginJson);
            RequestBody body = RequestBody.create(loginJson, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .post(body)
                    .url(URL)
                    .build();
            Call call = client.newCall(request);
            try(Response response = call.execute()){
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.i(LOGIN, "Response token " + responseBody);

                    SharedPreferences sp = this.getSharedPreferences("CADASTRO", MODE_PRIVATE);
                    String storagefromJson = sp.getString("storage", null);

                    JsonObject convertObject = gson.fromJson(storagefromJson, JsonObject.class);
                    storage = gson.fromJson(convertObject, Storage.class);
                    Log.i(LOGIN, "Storage Login Salvar: " + storagefromJson);

                    storage.setToken(responseBody);
                    String storageJson = gson.toJson(storage);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("storage", storageJson);
                    editor.commit();

                    Log.i(LOGIN, "Storage Login: " + storageJson);
                    sucessLogin.setText("Você foi logado!");
                    if(storage.getType().contains("dev")){
                        Intent perfilDev = new Intent(this, PerfilDev.class);
                        startActivity(perfilDev);
                    }else if(storage.getType().contains("user")){
                        Intent perfilUser = new Intent(this, PerfilUser.class);
                        startActivity(perfilUser);
                    }else{
                        Log.i(LOGIN, "TYPE ELSE: "+ storage.getType());
                    }
                } else {
                    Log.e(LOGIN, "Erro na requisição: " + response.code());
                    erroLogin.setText("Ocorreu um erro, tente novamente!");
                }

            }catch(IOException err){
                Log.e(LOGIN, "Erro", err);
                erroLogin.setText("Ocorreu um erro, tente novamente!");
                throw new RuntimeException(err);
            }
        });
    }
}
