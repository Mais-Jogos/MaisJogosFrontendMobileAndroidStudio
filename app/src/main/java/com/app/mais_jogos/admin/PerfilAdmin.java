package com.app.mais_jogos.admin;
import com.app.mais_jogos.R;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.mais_jogos.SelectPlayer;
import com.app.mais_jogos.avatar.AvatarActivity;
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

public class PerfilAdmin extends AppCompatActivity {

    class ResponseAdmin{
        private int id;
        private String nome;
        private String email;
        private String password;
    }
    EditText inputNome;
    EditText inputEmail;
    EditText inputSenha;
    Button excluirAdmin;
    Button salvaAdmin;
    Button cadastraAvatar;

    private static final String URL = "http://10.0.2.2:8080/api/adm";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_admin);

        SharedPreferences sp = this.getSharedPreferences("cadastroAdmin", MODE_PRIVATE);
        int idAdmin = sp.getInt("id", 0);

        inputNome = findViewById(R.id.editTextNomeAdmin);
        inputEmail = findViewById(R.id.editTextEmailAdmin);
        inputSenha = findViewById(R.id.editTextSenhaAdmin);
        excluirAdmin = findViewById(R.id.btnExcluirAdmin);
        salvaAdmin = findViewById(R.id.btnSalvarAdmin);
        cadastraAvatar = findViewById(R.id.btnCadastroAvatar);

        carregaDadosApi(idAdmin);

        excluirAdmin.setOnClickListener( e ->{
            carregarModalDelete(idAdmin);
        });

        salvaAdmin.setOnClickListener(e ->{
            Admin adm = new Admin();
            adm.setNome(inputNome.getText().toString());
            adm.setEmail(inputEmail.getText().toString());
            adm.setPassword(inputSenha.getText().toString());
            editaInformacoesAdmin(idAdmin, adm);
            modalEditarInformacoes("Dados atualizados!");
        });

        cadastraAvatar.setOnClickListener(
                e -> {
                    Intent intent = new Intent(getApplicationContext(), AvatarActivity.class);
                    startActivity(intent);
                }
        );
    }

    private void carregaDadosApi(int id){
        ExecutorService executer = Executors.newSingleThreadExecutor();

        executer.execute(() -> {
            OkHttpClient client = new OkHttpClient();

            Gson gson = new Gson();

            Request request = new Request.Builder().get().url(URL + "/listarAdm/" + id).build();
            Call call = client.newCall(request);

            try {
                Response response = call.execute();
                String responseString = response.body().string();
                Log.i("PerfilAdmin","Sucesso!:\n" + responseString);

                PerfilAdmin.ResponseAdmin adminData = gson.fromJson(responseString, PerfilAdmin.ResponseAdmin.class);
                inputNome.setText(adminData.nome);
                inputEmail.setText(adminData.email);
                inputSenha.setText(adminData.password);

            } catch (IOException e) {
                Log.i("PerfilAdmin", "Erro :(:\n" + e);
                throw  new RuntimeException(e);
            }
        });
    }

    private void carregarModalDelete(int id){
        // Modal
        AlertDialog.Builder confirmaExlusao = new AlertDialog.Builder(PerfilAdmin.this);
        confirmaExlusao.setTitle("Atenção!!");
        confirmaExlusao.setMessage("Tem certeza que deseja excluir a sua conta?\nEssa ação não pode ser desfeita!");
        confirmaExlusao.setCancelable(false);

        confirmaExlusao.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletaAdmin(id);
            }
        });

        confirmaExlusao.setNegativeButton("Não", null);

        confirmaExlusao.create().show();
    }

    private void deletaAdmin(int id){
        ExecutorService executer = Executors.newSingleThreadExecutor();

        executer.execute(() -> {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().delete().url(URL + "/deletarAdm/" + id).build();
            Call call = client.newCall(request);

            try {
                Response response = call.execute();
                String responseString = response.body().string();
                Log.i("PerfilAdmin","Sucesso!:\n" + responseString);

                Intent intent = new Intent(getApplicationContext(), SelectPlayer.class);
                startActivity(intent);
            } catch (IOException e) {
                Log.i("PerfilAdmin", "Erro :(:\n" + e);
                throw  new RuntimeException(e);
            }
        });
    }

    private void modalEditarInformacoes(String texto){
        AlertDialog.Builder confirmaEdicao = new AlertDialog.Builder(PerfilAdmin.this);
        confirmaEdicao.setTitle("Perfil Adminstrador");
        confirmaEdicao.setMessage(texto);
        confirmaEdicao.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("PerfilAdmin", "Dados atualizados!");
            }
        });
        confirmaEdicao.create().show();
    }

    private void editaInformacoesAdmin(int id, Admin adminObj){
        ExecutorService executer = Executors.newSingleThreadExecutor();

        executer.execute(() -> {
            OkHttpClient client = new OkHttpClient();

            Gson gson = new Gson();

            String adminEmJson = gson.toJson(adminObj);

            RequestBody body = RequestBody.create(adminEmJson, MediaType.get("application/json"));

            Request request = new Request.Builder().put(body).url(URL + "/alterarAdmin/" + id).build();

            Call call = client.newCall(request);

            try {
                Response response = call.execute();
                String responseString = response.body().string();
                Log.i("AdminCadastro","Sucesso!:\n" + responseString);



                carregaDadosApi(id);
            } catch (IOException e) {
                Log.i("AdminCadastro", "Erro :(:\n" + e);
                throw  new RuntimeException(e);
            }
        });
    }

}
