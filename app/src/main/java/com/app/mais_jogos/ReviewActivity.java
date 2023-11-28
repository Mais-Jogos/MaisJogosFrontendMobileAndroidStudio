package com.app.mais_jogos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

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


    private static final String BASE_URL = "http://10.0.2.2:8080";
    private static final String APP_CAFE = "APP_REVIEW";
    List<Review> lista = new ArrayList<>();

    ImageButton btnSalvar;

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
        setContentView(R.layout.review_user);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener( e -> salvar());
    }

    private void salvar() {
        Review c = new Review();
        c.setDataReview("31-10-2003");
        c.setDescricaoReview("AAAAAAA");
        c.setNotaReview(20.20);
        c.setIdJogo(2);
        c.setTituloReview("Um titulo");
        c.setIdUser(1);

        lista.add(c);
        salvarAPI(c);
    }



    public void salvarAPI(Review c) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Log.i(APP_CAFE, "Excutando request");
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            String cafeJson = gson.toJson(c);

            Log.i(APP_CAFE, "JSON Body: " + cafeJson);
            RequestBody body = RequestBody.create(cafeJson,
                    MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .post(body)
                    .url(BASE_URL + "/api/review/salvar")
                    .build();
            Call call = client.newCall(request);
            Log.i(APP_CAFE, "Resquest feito no servidor");
            try {
                Response response = call.execute();

                String responseString = response.body().string();
                Log.i("AdminCadastro","Sucesso!:\n" + responseString);

                ResponseReview responseReview = gson.fromJson(responseString, ResponseReview.class);

                SharedPreferences sp = this.getSharedPreferences("cadastroReview", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("id", responseReview.id);
                editor.apply();

                Intent intent = new Intent(this, ReviewUser.class);
                startActivity(intent);
            } catch (IOException e) {
                Log.e(APP_CAFE, "Erro", e);
                throw new RuntimeException(e);
            }
        });

    }


}
