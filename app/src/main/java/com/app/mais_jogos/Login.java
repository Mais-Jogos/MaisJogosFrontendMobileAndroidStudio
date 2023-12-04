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
    Storage storage = new Storage();
    Gson gson = new Gson();
    private final String URL = "https://backendmaisjogos-production.up.railway.app/login";
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
            if(validaCampos()){
                erroLogin.setText("");
                salvar();
            }else{
                erroLogin.setText("Preencha todos os campos");
            }
        });

    }
    private boolean validaCampos(){
        return senhaLogin.getText().length() != 0 &&
                emailLogin.getText().length() != 0;
    }
    private void salvar() {
        SharedPreferences sp = this.getSharedPreferences("CADASTRO", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        DadosLogin userLogin = new DadosLogin(
                emailLogin.getText().toString(),
                senhaLogin.getText().toString()
        );
        String loginJson = gson.toJson(userLogin);
        Log.i(LOGIN, "Login json " + loginJson);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(loginJson, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .post(body)
                    .url(URL)
                    .build();
            Call call = client.newCall(request);

            try (Response response = call.execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.i(LOGIN, "Response token " + responseBody);

                    String storagefromJson = sp.getString("storage", null);

                    JsonObject convertObject = gson.fromJson(storagefromJson, JsonObject.class);
                    storage = gson.fromJson(convertObject, Storage.class);
                    Log.i(LOGIN, "Storage Login: " + storagefromJson);

                    storage.setToken(responseBody);
                    String storageJson = gson.toJson(storage);
                    editor.putString("storage", storageJson);
                    editor.apply();

                    runOnUiThread(() -> {
                        sucessLogin.setText("Você foi logado!");
                        if (storage.getType().contains("dev")) {
                            Intent perfilDev = new Intent(Login.this, PerfilDev.class);
                            startActivity(perfilDev);
                        } else if (storage.getType().contains("user")) {
                            Intent perfilUser = new Intent(Login.this, PerfilUser.class);
                            startActivity(perfilUser);
                        } else {
                            Log.i(LOGIN, "Tipo não encontrado: " + storage.getType());
                            erroLogin.setText("Tipo não encontrado!");
                        }
                    });
                } else {
                    Log.e(LOGIN, "Erro na requisição: " + response.code());
                    runOnUiThread(() -> {
                        erroLogin.setText("Ocorreu um erro, tente novamente!");
                    });
                }

            } catch (IOException err) {
                Log.e(LOGIN, "Erro", err);
                runOnUiThread(() -> {
                    erroLogin.setText("Ocorreu um erro, tente novamente!");
                });
                throw new RuntimeException(err);
            }
        });
    }
}
