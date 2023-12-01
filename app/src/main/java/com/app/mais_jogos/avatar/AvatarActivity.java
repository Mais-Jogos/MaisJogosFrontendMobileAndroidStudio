package com.app.mais_jogos.avatar;

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
import com.app.mais_jogos.admin.PerfilAdmin;
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

public class AvatarActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://backendmaisjogos-production.up.railway.app";
    private static final String APP_AVATAR = "App Avatar";

    List<Avatar> lista = new ArrayList<>();
    Gson gson = new Gson();
    class ResponseAvatar {
        private Integer id;
        private String nome;
        private double valor;
    }

    EditText txtNome;
    EditText txtValor;
    TextView errorSkin;
    TextView sucessSkin;
    Button btnSalvar;
    ImageView btnVoltar;
    Button btnVoltar2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastrar_skin);
        txtNome = findViewById(R.id.txtNomeSkin);
        txtValor = findViewById(R.id.txtValorSkin);
        btnSalvar = findViewById(R.id.btnConcluirSkin);
        errorSkin = findViewById(R.id.errorSkin);
        sucessSkin = findViewById(R.id.sucessSkin);
        btnVoltar = findViewById(R.id.imgVoltarSkin);
        btnVoltar2 = findViewById(R.id.btnVoltarSkin);

        btnSalvar.setOnClickListener(e -> {
            if(validaCampos()){
                salvar();
            }else{
                errorSkin.setText("Preencha todos os campos!");
            }
        });
        btnVoltar.setOnClickListener(e ->{
            Intent perfilAdmin = new Intent(this, PerfilAdmin.class);
            startActivity(perfilAdmin);
        });
        btnVoltar2.setOnClickListener(e ->{
            Intent perfilAdmin = new Intent(this, PerfilAdmin.class);
            startActivity(perfilAdmin);
        });

    }

    private boolean validaCampos(){
        return txtNome.getText().length() != 0 && txtValor.getText().length() != 0;
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
            Log.i(APP_AVATAR, "Excutando request");
            OkHttpClient client = new OkHttpClient();
            String avatarJson = gson.toJson(a);

            Log.i(APP_AVATAR, "JSON Body: " + avatarJson);
            RequestBody body = RequestBody.create(avatarJson,
                    MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(BASE_URL + "/api/avatar/salvar")
                    .post(body)
                    .build();
            Call call = client.newCall(request);
            Log.i(APP_AVATAR, "Resquest feito no servidor");
            try(Response response = call.execute()){
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    Log.i("AdminCadastro", "Sucesso!:\n" + responseString);

                    AvatarActivity.ResponseAvatar responseAvatar = gson.fromJson(responseString, AvatarActivity.ResponseAvatar.class);

                    SharedPreferences sp = this.getSharedPreferences("cadastroAvatar", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("id", responseAvatar.id);
                    editor.apply();

                    Intent intent = new Intent(this, AvatarLoja.class);
                    startActivity(intent);
                }else {
                    Log.e(APP_AVATAR, "Erro" + response.body().string() + " - " + response.code());
                }

            } catch (IOException e) {
                Log.e(APP_AVATAR, "Erro", e);
                throw new RuntimeException(e);
            }
        });

    }
}
