package com.app.mais_jogos.review;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.mais_jogos.R;
import com.app.mais_jogos.SelectPlayer;
import com.app.mais_jogos.user.PerfilUser;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReviewUser extends AppCompatActivity {
    private static final String URL = "http://10.0.2.2:8080/api/review";

    class ResponseReview{

        private Integer id;
        private double notaReview;

        private String dataReview;

        private String descricaoReview;

        private String tituloReview;

        private Integer idJogo;

        private Integer idUser;
    }
        TextView txtTitulo;

        TextView txtDescricao;

        TextView txtData;

        TextView txtAvaliacao;

        ImageButton buttonUpd;

        ImageButton buttonExclu;
        ImageView btnVoltar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_user);
        SharedPreferences sp = this.getSharedPreferences("cadastroReview", MODE_PRIVATE);
        int idReview = sp.getInt("id", 0);

        txtTitulo = findViewById(R.id.tituloReviewEditar);
        txtDescricao = findViewById(R.id.txtDescricaoReviewEditar);
        txtData = findViewById(R.id.dataReviewEditar);
        txtAvaliacao = findViewById(R.id.notaReviewEditar);
        btnVoltar = findViewById(R.id.imgVoltarReviewEditar);

        carregaDadosApi(idReview);

        buttonUpd = findViewById(R.id.btnEditarReview);

        buttonUpd.setOnClickListener(
              e  -> {
                  Intent intent = new Intent(this, EditReview.class);
                  startActivity(intent);
              });
        buttonExclu = findViewById(R.id.imgDeleteReview);

        buttonExclu.setOnClickListener(
                e -> {
                    carregarModalDelete(idReview);
                }
        );
        btnVoltar.setOnClickListener(e ->{
            Intent perfilUser = new Intent(this, PerfilUser.class);
            startActivity(perfilUser);
        });
    }

    private void carregaDadosApi(int id){
        ExecutorService executer = Executors.newSingleThreadExecutor();

        executer.execute(() -> {
            OkHttpClient client = new OkHttpClient();

            Gson gson = new Gson();

            Request request = new Request.Builder().get().url(URL + "/listarReview/" + id).build();
            Call call = client.newCall(request);

            try(Response response = call.execute()){
                if(response.isSuccessful()){
                    String responseString = response.body().string();
                    Log.i("ReviewUser","Sucesso!:\n" + responseString);

                    ReviewUser.ResponseReview reviewData = gson.fromJson(responseString, ReviewUser.ResponseReview.class);
                    txtTitulo.setText(reviewData.tituloReview);
                    txtData.setText(reviewData.dataReview);
                    txtDescricao.setText(reviewData.descricaoReview);
                    SharedPreferences sp = this.getSharedPreferences("cadastroReview", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("id", reviewData.id);
                    editor.apply();
                }

            } catch (IOException e) {
                Log.i("ReviewUser", "Erro :(:\n" + e);
                throw  new RuntimeException(e);
            }
        });



    }
    private void carregarModalDelete(int id){
        // Modal
        AlertDialog.Builder confirmaExlusao = new AlertDialog.Builder(ReviewUser.this);
        confirmaExlusao.setTitle("Atenção!!");
        confirmaExlusao.setMessage("Tem certeza que deseja excluir a sua review?\nEssa ação não pode ser desfeita!");
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

            Request request = new Request.Builder().delete().url(URL + "/deletarReview/" + id).build();
            Call call = client.newCall(request);

            try(Response response = call.execute()){
                if(response.isSuccessful()){
                    String responseString = response.body().string();
                    Log.i("Review Deletada com sucesso","Sucesso!:\n" + responseString);

                    Intent intent = new Intent(getApplicationContext(), SelectPlayer.class);
                    startActivity(intent);
                }
            } catch (IOException e) {
                Log.i("PerfilAdmin", "Erro :(:\n" + e);
                throw  new RuntimeException(e);
            }
        });
    }
}
