package com.app.mais_jogos.review;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.mais_jogos.R;
import com.app.mais_jogos.Storage;
import com.app.mais_jogos.user.PerfilUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReviewActivity extends AppCompatActivity {


    private static final String BASE_URL = "https://backendmaisjogos-production.up.railway.app";
    private static final String APP_REVIEW = "App Review";
    List<Review> lista = new ArrayList<>();
    Button btnSalvar;

    EditText txtDescricao;
    EditText txtDataPostagem;

    EditText txtAvaliacao;

    EditText txtTituloReview;
    TextView errorReview;
    TextView sucessReview;
    ImageView btnVoltar;
    Button btnVoltar2;
    Gson gson = new Gson();
    Storage storage = new Storage();
    class ResponseReview{

        private Integer id;
        private double notaReview;

        private String dataReview;

        private String descricaoReview;

        private String tituloReview;

        private Integer idJogo;

        private Integer idUser;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_review);
        btnSalvar = findViewById(R.id.btnSalvarReviewPFV);
        txtAvaliacao = findViewById(R.id.txtAvaliacao);
        txtDataPostagem = findViewById(R.id.txtDataPostagem);
        txtDescricao = findViewById(R.id.txtDescricao);
        txtTituloReview = findViewById(R.id.txtTituloReview);
        errorReview = findViewById(R.id.errorReview);
        sucessReview = findViewById(R.id.sucessReview);
        btnVoltar = findViewById(R.id.imgBtnVoltarReview);
        btnVoltar2 = findViewById(R.id.btnVoltarReview);

        btnSalvar.setOnClickListener( e -> {
            if(validaCampos()){
                salvar();
            }else{
                errorReview.setText("Preencha todos os campos!");
            }
        });
        btnVoltar.setOnClickListener(e ->{
            Intent perfilUser = new Intent(this, PerfilUser.class);
            startActivity(perfilUser);
        });
        btnVoltar2.setOnClickListener(e ->{
            Intent perfilUser = new Intent(this, PerfilUser.class);
            startActivity(perfilUser);
        });
    }
    private boolean validaCampos(){
        return txtDataPostagem.getText().length() != 0 &&
                txtDescricao.getText().length() != 0 &&
                txtAvaliacao.getText().length() != 0 &&
                txtTituloReview.getText().length() != 0;
    }
    private void salvar() {
        SharedPreferences sp = this.getSharedPreferences("CADASTRO", MODE_PRIVATE);
        String storageString = sp.getString("storage", null);
        JsonObject jsonObject = gson.fromJson(storageString, JsonObject.class);
        storage = gson.fromJson(jsonObject, Storage.class);
        Review r = new Review();
        r.setDataReview(txtDataPostagem.getText().toString());
        r.setDescricaoReview(txtDescricao.getText().toString());
        r.setNotaReview(Double.parseDouble(txtAvaliacao.getText().toString()));
        r.setIdJogo(1);
        r.setTituloReview(txtTituloReview.getText().toString());
        r.setIdUser(Integer.valueOf(storage.getId()));

        lista.add(r);
        salvarAPI(r);
    }



    public void salvarAPI(Review r) {
        Log.i(APP_REVIEW, "Token: "+storage.getToken());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        // Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Log.i(APP_REVIEW, "Excutando request");
            OkHttpClient client = new OkHttpClient();
            String reviewJson = gson.toJson(r);

            Log.i(APP_REVIEW, "JSON Body: " + reviewJson);
            RequestBody body = RequestBody.create(reviewJson,
                    MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(BASE_URL + "/api/review/salvar")
                    .post(body)
                    .build();
            Call call = client.newCall(request);
            Log.i(APP_REVIEW, "Resquest feito no servidor");
            try(Response response = call.execute()){
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    Log.i(APP_REVIEW, "Sucesso!:\n" + responseString);

                    ResponseReview responseReview = gson.fromJson(responseString, ResponseReview.class);

                    SharedPreferences sp = ReviewActivity.this.getSharedPreferences("cadastroReview", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("id", responseReview.id);
                    editor.apply();

                    Intent intent = new Intent(ReviewActivity.this, ReviewUser.class);
                    startActivity(intent);
                } else {
                    Log.e(APP_REVIEW, "Erro" + response.body().string() + " - " + response.code());
                }

            } catch (IOException e) {
                Log.e(APP_REVIEW, "Erro", e);
                throw new RuntimeException(e);
            }
        });

    }


}
