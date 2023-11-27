package com.app.mais_jogos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class CadastroAdmin extends AppCompatActivity {

    class ResponseAdmin{
        private int id;
        private String nome;
        private String email;
        private String password;
    }

    EditText inputNome;
    EditText inputEmail;
    EditText inputSenha;
    EditText inputConfirmarSenha;
    TextView erroInputs;
    Button btnCadastrar;

    private static final String URL = "http://192.168.15.135:8080/api/adm/salvar";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_admin);

        inputNome = findViewById(R.id.txtNomeAdmin);
        inputEmail = findViewById(R.id.txtEmailAdmin);
        inputSenha = findViewById(R.id.txtSenhaAdmin);
        inputConfirmarSenha = findViewById(R.id.txtSenhaConfirmaAdmin);
        erroInputs = findViewById(R.id.erroCadastroAdmin);
        btnCadastrar = findViewById(R.id.btnNextAdmin);


        /* Title Gradient */
        TextView titlePage = findViewById(R.id.titleCadastroAdmin);
        TextPaint paint = titlePage.getPaint();
        float width = paint.measureText("Cadastre-se");

        Shader textShader = new LinearGradient(0, 0, width, titlePage.getTextSize(), new int[]{
                Color.parseColor("#7900FB"),
                Color.parseColor("#4B3068"),
        }, null, Shader.TileMode.CLAMP);
        titlePage.getPaint().setShader(textShader);
        /* Title Gradient */


        btnCadastrar.setOnClickListener( e ->{
            if(isInputsCorrected()){
                salvarLocal();
            }else{
                Log.i("cadastroAdmin", "tudo errado!");
                inputErro("Erro ao preencher as informações!");
            }
        });
    }

    private void salvarLocal(){
        Admin adm = new Admin();
        adm.setNome(inputNome.getText().toString());
        adm.setPassword(inputSenha.getText().toString());
        adm.setEmail(inputEmail.getText().toString());
        salvarAPi(adm);
    }

    private void salvarAPi(Admin adm){
        ExecutorService executer = Executors.newSingleThreadExecutor();

        executer.execute(() -> {
            OkHttpClient client = new OkHttpClient();

            Gson gson = new Gson();

            String adminEmJson = gson.toJson(adm);

            RequestBody body = RequestBody.create(adminEmJson, MediaType.get("application/json"));

            Request request = new Request.Builder().post(body).url(URL).build();

            Call call = client.newCall(request);

            try {
                Response response = call.execute();
                String responseString = response.body().string();
                Log.i("AdminCadastro","Sucesso!:\n" + responseString);

                ResponseAdmin adminData = gson.fromJson(responseString, ResponseAdmin.class);

                SharedPreferences sp = this.getSharedPreferences("cadastroAdmin", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("id", adminData.id);
                editor.apply();

                Intent intent = new Intent(this, LoginAdmin.class);
                startActivity(intent);

            } catch (IOException e) {
                Log.i("AdminCadastro", "Erro :(:\n" + e);
                inputErro("Não foi possível conectar com o servidor :(");
                throw  new RuntimeException(e);
            }
        });
    }

    private Boolean isInputsCorrected(){
        return inputEmail.getText().length() != 0
                && inputSenha.getText().length() != 0
                && inputConfirmarSenha.getText().length() != 0
                && inputSenha.getText().toString().equals(inputConfirmarSenha.getText().toString());
    }

    private void inputErro(String texto){
        erroInputs.setText(texto);
    }

}
