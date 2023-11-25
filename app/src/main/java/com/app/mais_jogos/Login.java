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

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private static final String LOGIN = "Login";

    class DadosLogin {
        public String login = "";
        public String password = "";

        public DadosLogin(String login, String password) {
            this.login = login;
            this.password = password;
        }
    }
    class Token{
        public String data;
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
            SharedPreferences sp = getApplicationContext().getSharedPreferences("CADASTRO", MODE_PRIVATE);
            String type = sp.getString("type", null);
            salvar();
            if(type == "dev"){
                Intent perfilDev = new Intent(this, PerfilDev.class);
                startActivity(perfilDev);
            }else if(type == "user"){
                Intent perfilUser = new Intent(this, PerfilUser.class);
                startActivity(perfilUser);
            }
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
            RequestBody body = RequestBody.create(userLogin.toString(), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .post(body)
                    .url(URL)
                    .build();
            Call call = client.newCall(request);
            try{
                Response response = call.execute();

                byte[] data = response.body().bytes();
                String json = new String(data, StandardCharsets.UTF_8);
                Log.i(LOGIN, String.valueOf(response));
                if (!json.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String d = jsonObject.getString("data");
                        Log.i(LOGIN, "Response data: " + d);
                        Log.i(LOGIN, "Response data: " + data);
                    } catch (JSONException e) {
                        Log.e(LOGIN, "Error parsing JSON: ", e);
                    }
                } else {
                    Log.i(LOGIN, "Response data is empty");
                }
                SharedPreferences sp = getApplicationContext().getSharedPreferences("CADASTRO", MODE_PRIVATE);
                String type = sp.getString("type", null);
                int id = sp.getInt("id", 0);
                sp.edit().putString("token", data.toString());
                sp.edit().commit();
                String token = sp.getString("type", null);
                Log.i(LOGIN, "Type "+ type);
                Log.i(LOGIN, "Id "+ id);
                Log.i(LOGIN, "Token "+ token);
                sucessLogin.setText("Você foi logado!");

            }catch(IOException err){
                Log.e(LOGIN, "Erro", err);
                erroLogin.setText("Ocorreu um erro, tente novamente!");
                throw new RuntimeException(err);
            }
        });
    }
}
