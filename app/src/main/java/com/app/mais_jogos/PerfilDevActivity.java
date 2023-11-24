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

public class PerfilDevActivity extends AppCompatActivity {
    TextView SobreDev;
    TextView NomeDev;
    Dev desenvolvedor = new Dev();
    Gson gson = new Gson();
    private static final String URL = "http://10.0.2.2:8080/auth/dev/1";
    private static final String PERFIL_DEV = "Perfil Dev";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_dev);
        SobreDev = findViewById(R.id.txtPerfilNomeDev);
        NomeDev = findViewById(R.id.txtPerfilNomeDev);

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
                Log.i(PERFIL_DEV, "Dev resposta: " + resposta);
                desenvolvedor = gson.fromJson(resposta, Dev.class);

                SobreDev.setText(desenvolvedor.getSobre().toString());
                NomeDev.setText(desenvolvedor.getNome().toString());
            }catch (IOException e) {
                Log.e(PERFIL_DEV, "Erro: ", e);
                throw new RuntimeException(e);
            }
        });

        SobreDev = findViewById(R.id.txtPerfilNomeDev);
        NomeDev = findViewById(R.id.txtPerfilNomeDev);

        SobreDev.setText(desenvolvedor.getSobre());
        NomeDev.setText(desenvolvedor.getNome());
    }
}
