package com.app.mais_jogos.dev;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.mais_jogos.pix.CadastroPIX;
import com.app.mais_jogos.R;
import com.app.mais_jogos.SelectPlayer;
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

public class PerfilDev extends AppCompatActivity {
    TextView ValorVendas;
    EditText SobreDev;
    EditText NomeDev;
    ImageButton btnDelete;
    ImageButton btnSave;

    Button btnRequerimento;
    Storage storage = new Storage();
    Dev desenvolvedor = new Dev();
    Gson gson = new Gson();
    private static final String URL = "https://backendmaisjogos-production.up.railway.app/api/usuario/listarCliente/";
    private static final String URL_DELETE = "https://backendmaisjogos-production.up.railway.app/api/usuario/deletarUser/";
    private static final String URL_EDIT = "https://backendmaisjogos-production.up.railway.app/api/usuario/alterarusuario/";
    private static final String PERFIL_DEV = "Perfil Dev";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_dev);
        SobreDev = findViewById(R.id.txtPerfilSobreDev);
        NomeDev = findViewById(R.id.txtPerfilNomeDev);
        ValorVendas = findViewById(R.id.txtValorVendas);
        btnSave = findViewById(R.id.btnPerfilUserSave);
        btnDelete = findViewById(R.id.btnPerfilUserDelete);
        btnRequerimento = findViewById(R.id.btnRequerimento);
        carregarPerfil();

        btnDelete.setOnClickListener(e ->{
            carregarModalDelete();
        });

        btnSave.setOnClickListener(e ->{
            editDev();
            modalEditarInformacoes("Dados atualizados!");
        });

        btnRequerimento.setOnClickListener(
                e-> {
                    Intent intent = new Intent(PerfilDev.this, CadastroPIX.class);
                    startActivity(intent);
                }
        );

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
                        String resposta = response.body().string();
                        JsonObject ObjectJson = gson.fromJson(resposta, JsonObject.class);
                        desenvolvedor = gson.fromJson(ObjectJson, Dev.class);

                        Log.i(PERFIL_DEV, "Dev resposta: " + ObjectJson);
                        Log.i(PERFIL_DEV, "sucesso na requisição: " + response.code());
                        runOnUiThread(() ->  SobreDev.setText(desenvolvedor.getSobre()));
                        runOnUiThread(() -> NomeDev.setText(desenvolvedor.getNome()));
                        runOnUiThread(() -> ValorVendas.setText(String.valueOf(desenvolvedor.getValorVendas())));

                    }else{
                        Log.i(PERFIL_DEV, "url: "+ URL + storage.getId());
                        Log.i(PERFIL_DEV, "Response: "+ response.body().string());
                        Log.e(PERFIL_DEV, "Erro na requisição: " + response.code());
                        Log.e(PERFIL_DEV, response.message());
                    }
                }catch (IOException e) {
                    Log.e(PERFIL_DEV, "Erro: ", e);
                    Log.i(PERFIL_DEV, "url: "+ URL + storage.getId());
                    throw new RuntimeException(e);
                }
            });
        }else{
            Log.i("Login", "Storage é null");
        }
    }
    private void carregarModalDelete(){
        // Modal
        AlertDialog.Builder confirmaExlusao = new AlertDialog.Builder(PerfilDev.this);
        confirmaExlusao.setTitle("Atenção!!");
        confirmaExlusao.setMessage("Tem certeza que deseja excluir a sua conta?\nEssa ação não pode ser desfeita!");
        confirmaExlusao.setCancelable(false);

        confirmaExlusao.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteDev();
            }
        });

        confirmaExlusao.setNegativeButton("Não", null);

        confirmaExlusao.create().show();
    }
    private void modalEditarInformacoes(String texto){
        AlertDialog.Builder confirmaEdicao = new AlertDialog.Builder(PerfilDev.this);
        confirmaEdicao.setTitle("Perfil Dev");
        confirmaEdicao.setMessage(texto);
        confirmaEdicao.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(PERFIL_DEV, "Dados atualizados!");
            }
        });
        confirmaEdicao.create().show();
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
                    Log.i(PERFIL_DEV, "Usuário deletado "
                            + response.message() + " - "
                            + response.body().string());
                    Intent cadastrar = new Intent(this, SelectPlayer.class);
                    startActivity(cadastrar);
                }
            }catch (IOException err) {
                Log.e(PERFIL_DEV, "Erro " + err);
            }
        });
    }

    public void editDev(){
        desenvolvedor.setNome(NomeDev.getText().toString());
        desenvolvedor.setSobre(SobreDev.getText().toString());
        String devJson = gson.toJson(desenvolvedor);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() ->{
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(devJson, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(URL_EDIT+storage.getId())
                    .header("Authorization", "Bearer " + storage.getToken())
                    .put(body)
                    .build();
            Call call = client.newCall(request);
            try(Response response = call.execute()){
                String strResposta = response.body().string();
                Log.i(PERFIL_DEV, "Dev Alterado: " + strResposta);
            }catch (IOException err){
                Log.e(PERFIL_DEV, "Erro ao alterar "+ err);
            }
        });
    }
}
