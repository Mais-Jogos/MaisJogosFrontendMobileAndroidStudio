package com.app.mais_jogos.review;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.mais_jogos.R;
import com.app.mais_jogos.user.PerfilUser;
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

public class ReviewUser extends AppCompatActivity {
    private static final String URL = "https://backendmaisjogos-production.up.railway.app/api/review";

    class ResponseReview{

        private Integer id;
        private double notaReview;

        private String dataReview;

        private String descricaoReview;

        private String tituloReview;

        private Integer idJogo;

        private Integer idUser;
    }

        EditText txtTitulo;


        EditText txtDescricao;

        EditText txtData;

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

        txtTitulo = findViewById(R.id.tituloInput);
        txtDescricao = findViewById(R.id.descInput);
        txtData = findViewById(R.id.dataInput);
        txtAvaliacao = findViewById(R.id.notaReviewEditar);
        btnVoltar = findViewById(R.id.imgVoltarReviewEditar);

        carregaDadosApi(idReview);

        buttonUpd = findViewById(R.id.btnEditarReview);

        buttonUpd.setOnClickListener(
              e  -> {
                  Review review = new Review();
                  review.setTituloReview(txtTitulo.getText().toString());
                  review.setDescricaoReview(txtDescricao.getText().toString());
                  review.setDataReview(txtData.getText().toString());


                  editaInformacoesReview(idReview, review);
                  modalEditarInformacoes("Dados atualizados!");

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
                    runOnUiThread(() ->  txtTitulo.setText(reviewData.tituloReview));
                    runOnUiThread(() ->  txtData.setText(reviewData.dataReview));
                    runOnUiThread(() -> txtDescricao.setText(reviewData.descricaoReview));
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

                    Intent intent = new Intent(getApplicationContext(), PerfilUser.class);
                    startActivity(intent);
                }
            } catch (IOException e) {
                Log.i("PerfilAdmin", "Erro :(:\n" + e);
                throw  new RuntimeException(e);
            }
        });
    }
    private void modalEditarInformacoes(String texto){
        AlertDialog.Builder confirmaEdicao = new AlertDialog.Builder(ReviewUser.this);
        confirmaEdicao.setTitle("Review");
        confirmaEdicao.setMessage(texto);
        confirmaEdicao.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("Review", "Dados atualizados!");
            }
        });
        confirmaEdicao.create().show();
    }
    private void editaInformacoesReview(int id, Review review){
        ExecutorService executer = Executors.newSingleThreadExecutor();

        executer.execute(() -> {
            OkHttpClient client = new OkHttpClient();

            Gson gson = new Gson();

            String pixEmJson = gson.toJson(review);

            RequestBody body = RequestBody.create(pixEmJson, MediaType.get("application/json"));

            Request request = new Request.Builder().put(body).url(URL + "/alterarReview/" + id).build();

            Call call = client.newCall(request);

            try {
                Response response = call.execute();
                String responseString = response.body().string();
                Log.i("Review","Sucesso!:\n" + responseString);




            } catch (IOException e) {
                Log.i("Review", "Erro :(:\n" + e);
                throw  new RuntimeException(e);
            }
        });
    }
}
