package com.app.mais_jogos;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PerfilUserActivity extends AppCompatActivity {
    TextView SobreUser;
    TextView nomeUser;
    User usuario = new User();
    Gson gson = new Gson();
    private static final String URL = "http://10.0.2.2:8080/auth/user/1";
    private static final String PERFIL_USER = "Perfil User";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_user);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() ->{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL)
                    .get()
                    .build();
            try(Response response = client.newCall(request).execute()) {
                String resposta = response.body().string();
                JsonObject convertObject = gson.fromJson(resposta, JsonObject.class);
                Log.i(PERFIL_USER, "User resposta: " + resposta);
                usuario = gson.fromJson(resposta, User.class);
            }catch (IOException e) {
                Log.e(PERFIL_USER, "Erro: ", e);
                throw new RuntimeException(e);
            }
        });

        SobreUser = findViewById(R.id.txtPerfilNomeUser);
        nomeUser = findViewById(R.id.txtPerfilNomeUser);

        SobreUser.setText(usuario.getSobre());
        nomeUser.setText(usuario.getNome());

    }
}
