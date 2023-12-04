package com.app.mais_jogos.pix;

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

import com.app.mais_jogos.R;
import com.app.mais_jogos.Storage;
import com.app.mais_jogos.dev.PerfilDev;
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

public class ExibirChavesDev extends AppCompatActivity {

    class ResponseCadastroPix{
        private Integer id;
        private String pix;
        private String tipoPix;
        private double valorPag;
        private Integer idDev;
    }


    EditText inputTipoPix;

    EditText inputChavePix;

    Button btnExcluir;
    Button btnEditar;
    Storage storage = new Storage();
    Gson gson = new Gson();
    private static final String URL = "https://backendmaisjogos-production.up.railway.app/api/pixDev";

    private static final String EXIBIR_CHAVES = "EXIBIR_CHAVES";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibir_chaves_pix);

        SharedPreferences sp = this.getSharedPreferences("cadastroPix", MODE_PRIVATE);
        int idPixDev = sp.getInt("id", 0);
        Log.i(EXIBIR_CHAVES, String.valueOf(idPixDev));
        inputChavePix = findViewById(R.id.txtChavePix);
        inputTipoPix = findViewById(R.id.txtTipoPix);
        btnEditar = findViewById(R.id.btnAlterar);
        btnExcluir = findViewById(R.id.btnExcluir);
        carregaDadosApi(idPixDev);

        btnEditar.setOnClickListener(
                e -> {
                    DevPIX cadastroPIX = new DevPIX();
                    cadastroPIX.setPix(inputChavePix.getText().toString());
                    cadastroPIX.setTipoPix(inputTipoPix.getText().toString());

                    editaInformacoesPix(idPixDev, cadastroPIX);
                    modalEditarInformacoes("Dados atualizados!");
                }
        );

        btnExcluir.setOnClickListener(
                e -> {
                    carregarModalDelete(idPixDev);
                }
        );
    }
    private void modalEditarInformacoes(String texto){
        AlertDialog.Builder confirmaEdicao = new AlertDialog.Builder(ExibirChavesDev.this);
        confirmaEdicao.setTitle("Chaves Pix");
        confirmaEdicao.setMessage(texto);
        confirmaEdicao.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(EXIBIR_CHAVES, "Dados atualizados!");
            }
        });
        confirmaEdicao.create().show();
    }

    private void carregaDadosApi(int id){

        SharedPreferences sp = this.getSharedPreferences("CADASTRO", MODE_PRIVATE);
        String storageJson = sp.getString("storage", null);

        JsonObject convertObject = gson.fromJson(storageJson, JsonObject.class);
        storage = gson.fromJson(convertObject, Storage.class);
        Log.i(EXIBIR_CHAVES, "Store perfil "+ convertObject);
        if(storage != null) {
            ExecutorService executer = Executors.newSingleThreadExecutor();

            executer.execute(() -> {
                OkHttpClient client = new OkHttpClient();

                Gson gson = new Gson();

                Request request = new Request.Builder()
                        .url(URL+"/listarPix/"+id)
                        .header("Authorization", "Bearer " + storage.getToken())
                        .get()
                        .build();
                Call call = client.newCall(request);

                try {
                    Response response = call.execute();
                    String responseString = response.body().string();
                    Log.i(EXIBIR_CHAVES, "Sucesso!:\n" + responseString);

                    ExibirChavesDev.ResponseCadastroPix pixData = gson.fromJson(responseString, ExibirChavesDev.ResponseCadastroPix.class);

                    runOnUiThread(() -> inputChavePix.setText(pixData.pix));
                    runOnUiThread(() -> inputTipoPix.setText(pixData.tipoPix));

                } catch (IOException e) {
                    Log.i("ExibirChavesDev", "Erro :(:\n" + e);
                    throw new RuntimeException(e);
                }
            });
        }else{
            Log.i("Login", "Storage é null");
        }
    }

    private void carregarModalDelete(int id){
        // Modal
        AlertDialog.Builder confirmaExlusao = new AlertDialog.Builder(ExibirChavesDev.this);
        confirmaExlusao.setTitle("Atenção!!");
        confirmaExlusao.setMessage("Tem certeza que deseja excluir a seu Pix?\nEssa ação não pode ser desfeita!");
        confirmaExlusao.setCancelable(false);

        confirmaExlusao.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletaPix(id);
            }
        });

        confirmaExlusao.setNegativeButton("Não", null);

        confirmaExlusao.create().show();
    }
    private void deletaPix(int id){
        ExecutorService executer = Executors.newSingleThreadExecutor();

        executer.execute(() -> {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().header("Authorization", "Bearer " + storage.getToken()).delete().url(URL + "/deletarPix/" + id).build();
            Call call = client.newCall(request);

            try {
                Response response = call.execute();
                String responseString = response.body().string();
                Log.i(EXIBIR_CHAVES,"Sucesso!:\n" + responseString);

                Intent intent = new Intent(getApplicationContext(),  PerfilDev.class);
                startActivity(intent);
            } catch (IOException e) {
                Log.i("PerfilAdmin", "Erro :(:\n" + e);
                throw  new RuntimeException(e);
            }
        });
    }

    private void editaInformacoesPix(int id, DevPIX devPIX){
        ExecutorService executer = Executors.newSingleThreadExecutor();

        executer.execute(() -> {
            OkHttpClient client = new OkHttpClient();

            Gson gson = new Gson();

            String pixEmJson = gson.toJson(devPIX);

            RequestBody body = RequestBody.create(pixEmJson, MediaType.get("application/json"));

            Request request = new Request.Builder().header("Authorization", "Bearer " + storage.getToken()).put(body).url(URL + "/alterarReview/" + id).build();

            Call call = client.newCall(request);

            try {
                Response response = call.execute();
                String responseString = response.body().string();
                Log.i(EXIBIR_CHAVES,"Sucesso!:\n" + responseString);



                carregaDadosApi(id);
            } catch (IOException e) {
                Log.i("AdminCadastro", "Erro :(:\n" + e);
                throw  new RuntimeException(e);
            }
        });
    }
}