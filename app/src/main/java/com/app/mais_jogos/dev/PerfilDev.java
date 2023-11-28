package com.app.mais_jogos.dev;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.mais_jogos.R;
import com.app.mais_jogos.SelectPlayer;
import com.app.mais_jogos.Storage;
import com.app.mais_jogos.dev.Dev;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.OutputKeys;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PerfilDev extends AppCompatActivity {
    EditText SobreDev;
    EditText NomeDev;
    Button BtnDelete;
    Button btnSave;
    Dev desenvolvedor = new Dev();
    Storage storage = new Storage();
    Gson gson = new Gson();
    private static final String URL = "http://10.0.2.2:8080/api/usuario/listarCliente/";
    private static final String URL_DELETE = "http://10.0.2.2:8080/api/usuario/deletarUser/";
    private static final String PERFIL_DEV = "Perfil Dev";
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_dev);
        SobreDev = findViewById(R.id.txtPerfilNomeDev);
        NomeDev = findViewById(R.id.txtPerfilNomeDev);
        BtnDelete = findViewById(R.id.btnPerfilDevDelete);

        carregarPerfil();

        BtnDelete.setOnClickListener(e ->{
            deleteDev();
        });


        SobreDev = findViewById(R.id.txtPerfilSobreDev);
        NomeDev = findViewById(R.id.txtPerfilNomeDev);

    }

    public void carregarPerfil(){
        SharedPreferences sp = this.getSharedPreferences("CADASTRO", MODE_PRIVATE);
        String storageJson = sp.getString("storage", null);

        JsonObject convertObject = gson.fromJson(storageJson, JsonObject.class);
        storage = gson.fromJson(convertObject, Storage.class);

        Log.i(PERFIL_DEV, "Store perfil "+ convertObject);

        if(storage != null){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() ->{
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(URL+storage.getId())
                        .header("Authorization", "Bearer " + storage.getToken())
                        .get()
                        .build();
                try(Response response = client.newCall(request).execute()) {
                    if(response.isSuccessful()){
                        Log.i(PERFIL_DEV, "url: "+URL+storage.getId());
                        String resposta = response.body().string();
                        JsonObject ObjectJson = gson.fromJson(resposta, JsonObject.class);
                        desenvolvedor = gson.fromJson(ObjectJson, Dev.class);

                        Log.i(PERFIL_DEV, "Dev resposta: " + ObjectJson);
                        Log.i(PERFIL_DEV, "DEV id: "+storage.getId()+ " idDev: "+storage.getId());
                        Log.i(PERFIL_DEV, "sucesso na requisição: " + response.code());
                        SobreDev.setText(desenvolvedor.getSobre());
                        NomeDev.setText(desenvolvedor.getNome());

                    }else{
                        Log.i(PERFIL_DEV, "url: "+ URL + storage.getId());
                        Log.i(PERFIL_DEV, "Response: "+ response.body().string());
                        Log.e(PERFIL_DEV, "Erro na requisição: " + response.code());
                        Log.e(PERFIL_DEV, "Erro na requisição: " + response.message());
                    }
                }catch (IOException e) {
                    Log.e(PERFIL_DEV, "Erro: ", e);
                    throw new RuntimeException(e);
                }
            });
        }else{
            Log.i("Login", "Storage é null");
        }
    }
    public void deleteDev(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        OkHttpClient client = new OkHttpClient();
        executor.execute(() ->{
            Request request = new Request.Builder()
                    .url(URL_DELETE+storage.getId())
                    .header("Authorization", "Bearer " + storage.getToken())
                    .delete()
                    .build();
            try(Response response = client.newCall(request).execute()){
                if(response.isSuccessful()){
                    Intent cadastrar = new Intent(this, SelectPlayer.class);
                    startActivity(cadastrar);
                }
            }catch (IOException err) {
                Log.e(PERFIL_DEV, "Erro " + err);
            }
        });
    }
}
