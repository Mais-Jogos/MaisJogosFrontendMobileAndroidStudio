package com.app.mais_jogos;

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

public class AvatarActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:8080";
    private static final String APP_CAFE = "APP_AVATAR";

    List<Avatar> lista = new ArrayList<>();
    class ResponseAvatar {
        private Integer id;
        private String nome;
        private double valor;
    }

    EditText txtNome;
    EditText txtValor;

    Button btnSalvar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastrar_skin);
        txtNome = findViewById(R.id.txtName);
        txtValor = findViewById(R.id.editTextNumber);
        btnSalvar = findViewById(R.id.button5);

        btnSalvar.setOnClickListener(
                e -> {
                    salvar();
                }
        );
    }

    private void salvar() {
        Avatar a = new Avatar();
       a.setNome(txtNome.getText().toString());
       a.setValor(Double.parseDouble(txtValor.getText().toString()));

        lista.add(a);
        salvarAPI(a);
    }

    public void salvarAPI(Avatar a) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Log.i(APP_CAFE, "Excutando request");
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            String cafeJson = gson.toJson(a);

            Log.i(APP_CAFE, "JSON Body: " + cafeJson);
            RequestBody body = RequestBody.create(cafeJson,
                    MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .post(body)
                    .url(BASE_URL + "/api/avatar/salvar")
                    .build();
            Call call = client.newCall(request);
            Log.i(APP_CAFE, "Resquest feito no servidor");
            try {
                Response response = call.execute();

                String responseString = response.body().string();
                Log.i("AdminCadastro","Sucesso!:\n" + responseString);

                AvatarActivity.ResponseAvatar responseAvatar = gson.fromJson(responseString, AvatarActivity.ResponseAvatar.class);

                SharedPreferences sp = this.getSharedPreferences("cadastroAvatar", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("id", responseAvatar.id);
                editor.apply();

                Intent intent = new Intent(this, AvatarLoja.class);
                startActivity(intent);
            } catch (IOException e) {
                Log.e(APP_CAFE, "Erro", e);
                throw new RuntimeException(e);
            }
        });

    }
}
