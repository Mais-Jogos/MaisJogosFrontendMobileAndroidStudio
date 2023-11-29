package com.app.mais_jogos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AvatarLoja extends AppCompatActivity {
    private static final String URL = "http://10.0.2.2:8080/api/avatar";
    class ResponseAvatar {
        private Integer id;
        private String nome;
        private double valor;
    }

    EditText nome;
    EditText valor;

    Button editar;
    Button excluir;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabela_editar_skin);

        SharedPreferences sp = this.getSharedPreferences("cadastroAvatar", MODE_PRIVATE);
        int idReview = sp.getInt("id", 0);

        nome = (EditText) findViewById(R.id.editTextText23);
        valor = (EditText) findViewById(R.id.editTextText20);

        carregaDadosApi(idReview);

        editar = findViewById(R.id.button21);
        editar.setOnClickListener(
                e -> {
                    Avatar c = new Avatar();
                    c.setNome(nome.getText().toString());
                    editaInformacoesAdmin(idReview, c);
                    modalEditarInformacoes("Dados atualizados!");
                }
        );
        excluir = findViewById(R.id.button18);
        excluir.setOnClickListener(
                e -> {
                    carregarModalDelete(idReview);
                }
        );
    }

    private void modalEditarInformacoes(String texto){
        AlertDialog.Builder confirmaEdicao = new AlertDialog.Builder(AvatarLoja.this);
        confirmaEdicao.setTitle("Avatar");
        confirmaEdicao.setMessage(texto);
        confirmaEdicao.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("PerfilAdmin", "Dados atualizados!");
            }
        });
        confirmaEdicao.create().show();
    }
    private void carregaDadosApi(int id){
        ExecutorService executer = Executors.newSingleThreadExecutor();

        executer.execute(() -> {
            OkHttpClient client = new OkHttpClient();

            Gson gson = new Gson();

            Request request = new Request.Builder().get().url(URL + "/listarAvatar/" + id).build();
            Call call = client.newCall(request);

            try {
                Response response = call.execute();
                String responseString = response.body().string();
                Log.i("ReviewUser","Sucesso!:\n" + responseString);

                AvatarLoja.ResponseAvatar reviewData = gson.fromJson(responseString, AvatarLoja.ResponseAvatar.class);
                nome.setText(reviewData.nome);
                valor.setText(String.valueOf(reviewData.valor));



            } catch (IOException e) {
                Log.i("ReviewUser", "Erro :(:\n" + e);
                throw  new RuntimeException(e);
            }
        });



    }

    private void editaInformacoesAdmin(int id, Avatar adminObj){
        ExecutorService executer = Executors.newSingleThreadExecutor();

        executer.execute(() -> {
            OkHttpClient client = new OkHttpClient();

            Gson gson = new Gson();

            String adminEmJson = gson.toJson(adminObj);

            RequestBody body = RequestBody.create(adminEmJson, MediaType.get("application/json"));

            Request request = new Request.Builder().put(body).url(URL + "/alterarAvatar/" + id).build();

            Call call = client.newCall(request);

            try {
                Response response = call.execute();
                String responseString = response.body().string();
                Log.i("AdminCadastro","Sucesso!:\n" + responseString);




            } catch (IOException e) {
                Log.i("AdminCadastro", "Erro :(:\n" + e);
                throw  new RuntimeException(e);
            }
        });
    }

    private void carregarModalDelete(int id){
        // Modal
        androidx.appcompat.app.AlertDialog.Builder confirmaExlusao = new androidx.appcompat.app.AlertDialog.Builder(AvatarLoja.this);
        confirmaExlusao.setTitle("Atenção!!");
        confirmaExlusao.setMessage("Tem certeza que deseja excluir a sua Skin?\nEssa ação não pode ser desfeita!");
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

            Request request = new Request.Builder().delete().url(URL + "/deletarAvatar/" + id).build();
            Call call = client.newCall(request);

            try {
                Response response = call.execute();
                String responseString = response.body().string();
                Log.i("Avatar Deletada com sucesso","Sucesso!:\n" + responseString);

                Intent intent = new Intent(getApplicationContext(), PerfilAdmin.class);
                startActivity(intent);
            } catch (IOException e) {
                Log.i("PerfilAdmin", "Erro :(:\n" + e);
                throw  new RuntimeException(e);
            }
        });
    }
}
