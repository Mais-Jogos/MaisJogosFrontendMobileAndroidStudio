package com.app.mais_jogos.pix;

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
import com.app.mais_jogos.Storage;
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

public class CadastroPIX extends AppCompatActivity {

    private static final String BASE_URL = "https://backendmaisjogos-production.up.railway.app";

    private static final String APP_PIX = "App Pix";

    List<DevPIX> lista = new ArrayList<>();

    Button btnSalvar;

    EditText txtNomeChave;
    EditText txtChavePix;

    TextView erroCadastroChavePix;
    Gson gson = new Gson();

    Storage storage = new Storage();

    class ResponseCadastroPix{
        private Integer id;
        private String pix;
        private String tipoPix;
        private double valorPag;
        private Integer idDev;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_pix);
        btnSalvar = findViewById(R.id.btnCadastrarChave);
        txtNomeChave = findViewById(R.id.txtNomeChave);
        txtChavePix = findViewById(R.id.txtChavePix);
        erroCadastroChavePix = findViewById(R.id.erroCadastroChavePix);


        btnSalvar.setOnClickListener(
                e -> {
                    if(validarCampos()){
                        salvar();
                    }else{
                        erroCadastroChavePix.setText("Preencha todos os campos");
                    }
                }
        );

    }
    private boolean validarCampos(){
        return txtNomeChave.getText().length() != 0 &&
                txtChavePix.getText().length() != 0;
    }
    private void salvar(){
        SharedPreferences sp = this.getSharedPreferences("CADASTRO", MODE_PRIVATE);
        String storageString = sp.getString("storage", null);
        JsonObject jsonObject = gson.fromJson(storageString, JsonObject.class);
        storage = gson.fromJson(jsonObject, Storage.class);
        DevPIX d = new DevPIX();

        d.setPix(txtChavePix.getText().toString());
        d.setTipoPix(txtNomeChave.getText().toString());
        d.setValorPag(12);
        d.setIdDev(1);

        lista.add(d);
        salvarApi(d);
    }
    private void salvarApi(DevPIX d){
        Log.i(APP_PIX, "Token: "+storage.getToken());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Log.i(APP_PIX, "Excutando request");
            OkHttpClient client = new OkHttpClient();
            String pixJson = gson.toJson(d);

            Log.i(APP_PIX, "JSON Body: " + pixJson);
            RequestBody body = RequestBody.create(pixJson,
                    MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(BASE_URL + "/api/pixDev/salvar")
                    .post(body)
                    .header("Authorization", "Bearer " + storage.getToken())
                    .build();
            Call call = client.newCall(request);
            Log.i(APP_PIX, "Resquest feito no servidor");
            try(Response response = call.execute()){
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    Log.i(APP_PIX, "Sucesso!:\n" + responseString);

                    CadastroPIX.ResponseCadastroPix responseReview = gson.fromJson(responseString, CadastroPIX.ResponseCadastroPix.class);

                    SharedPreferences sp = CadastroPIX.this.getSharedPreferences("cadastroPix", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("id", responseReview.id);
                    editor.apply();

                    Intent intent = new Intent(CadastroPIX.this, ExibirChavesDev.class);
                    startActivity(intent);
                } else {
                    Log.e(APP_PIX, "Erro" + response.body().string() + " - " + response.code());
                }

            } catch (IOException e) {
                Log.e(APP_PIX, "Erro", e);
                throw new RuntimeException(e);
            }
        });

    }
}