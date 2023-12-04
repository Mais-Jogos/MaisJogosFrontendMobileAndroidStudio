package com.app.mais_jogos.user;

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

import com.app.mais_jogos.R;
import com.app.mais_jogos.SelectPlayer;
import com.app.mais_jogos.Storage;
import com.app.mais_jogos.dev.PerfilDev;
import com.app.mais_jogos.review.ReviewActivity;
import com.app.mais_jogos.user.User;
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

public class PerfilUser extends AppCompatActivity {
    EditText SobreNomeUser;
    EditText NomeUser;
    ImageButton btnDelete;
    ImageButton btnSave;

    Button btnReview;
    Storage storage = new Storage();
    User usuario = new User();
    Gson gson = new Gson();
    private static final String URL = "https://backendmaisjogos-production.up.railway.app/api/usuario/listarCliente/";
    private static final String URL_DELETE = "https://backendmaisjogos-production.up.railway.app/api/usuario/deletarUser/";
    private static final String URL_EDIT = "https://backendmaisjogos-production.up.railway.app/api/usuario/alterarusuario/";
    private static final String PERFIL_USER = "Perfil User";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_user);
        SobreNomeUser = findViewById(R.id.txtPerfilSobrenomeUser);
        NomeUser = findViewById(R.id.txtPerfilNomeUser);
        btnSave = findViewById(R.id.btnPerfilUserSave);
        btnDelete = findViewById(R.id.btnPerfilUserDelete);
        btnReview = findViewById(R.id.btnReview);
        carregarPerfil();

        btnDelete.setOnClickListener(e ->{
            carregarModalDelete();
        });

        btnSave.setOnClickListener(e ->{
            editUser();
            modalEditarInformacoes("Dados atualizados!");
        });
        btnReview.setOnClickListener(
                e-> {

                    Intent review = new Intent(this, ReviewActivity.class);
                    startActivity(review);
                }
        );
    }
    private void carregarModalDelete(){
        // Modal
        AlertDialog.Builder confirmaExlusao = new AlertDialog.Builder(PerfilUser.this);
        confirmaExlusao.setTitle("Atenção!!");
        confirmaExlusao.setMessage("Tem certeza que deseja excluir a sua conta?\nEssa ação não pode ser desfeita!");
        confirmaExlusao.setCancelable(false);

        confirmaExlusao.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteUser();
            }
        });

        confirmaExlusao.setNegativeButton("Não", null);

        confirmaExlusao.create().show();
    }
    private void modalEditarInformacoes(String texto){
        AlertDialog.Builder confirmaEdicao = new AlertDialog.Builder(PerfilUser.this);
        confirmaEdicao.setTitle("Perfil Dev");
        confirmaEdicao.setMessage(texto);
        confirmaEdicao.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(PERFIL_USER, "Dados atualizados!");
            }
        });
        confirmaEdicao.create().show();
    }
    public void carregarPerfil(){
        SharedPreferences sp = this.getSharedPreferences("CADASTRO", MODE_PRIVATE);
        String storageJson = sp.getString("storage", null);

        JsonObject convertObject = gson.fromJson(storageJson, JsonObject.class);
        storage = gson.fromJson(convertObject, Storage.class);

        Log.i(PERFIL_USER, "Store perfil "+ convertObject);

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
                        usuario = gson.fromJson(ObjectJson, User.class);

                        Log.i(PERFIL_USER, "User resposta: " + ObjectJson);
                        Log.i(PERFIL_USER, "sucesso na requisição: " + response.code());
                        runOnUiThread(() -> SobreNomeUser.setText(usuario.getSobrenome()));
                        runOnUiThread(() -> NomeUser.setText(usuario.getNome()));

                    }else{
                        Log.i(PERFIL_USER, "url: "+ URL + storage.getId());
                        Log.i(PERFIL_USER, "Response: "+ response.body().string());
                        Log.e(PERFIL_USER, "Erro na requisição: " + response.code());
                        Log.e(PERFIL_USER, response.message());
                    }
                }catch (IOException e) {
                    Log.e(PERFIL_USER, "Erro: ", e);
                    Log.i(PERFIL_USER, "url: "+ URL + storage.getId());
                    throw new RuntimeException(e);
                }
            });
        }else{
            Log.i("Login", "Storage é null");
        }
    }
    public void deleteUser(){
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
                    Log.i(PERFIL_USER, "Usuário deletado "
                            + response.message() + " - "
                            + response.body().string());
                    Intent cadastrar = new Intent(this, SelectPlayer.class);
                    startActivity(cadastrar);
                }
            }catch (IOException err) {
                Log.e(PERFIL_USER, "Erro " + err);
            }
        });
    }

    public void editUser(){
        usuario.setNome(NomeUser.getText().toString());
        usuario.setSobrenome(SobreNomeUser.getText().toString());
        String userJson = gson.toJson(usuario);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() ->{
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(userJson, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(URL_EDIT+storage.getId())
                    .header("Authorization", "Bearer " + storage.getToken())
                    .put(body)
                    .build();
            Call call = client.newCall(request);
            try(Response response = call.execute()){
                String strResposta = response.body().string();
                Log.i(PERFIL_USER, "User Alterado: " + strResposta);
            }catch (IOException err){
                Log.e(PERFIL_USER, "Erro ao alterar "+ err);
            }
        });
    }
}
