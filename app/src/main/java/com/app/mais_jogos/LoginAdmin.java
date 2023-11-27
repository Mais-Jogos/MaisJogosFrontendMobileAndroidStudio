package com.app.mais_jogos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginAdmin extends AppCompatActivity {

    class DadosLoginAdmin {
        private String login;
        private String password;
    }

    TextView cadastroAdmin;

    EditText inputEmail;
    EditText inputSenha;

    TextView erroLogin;

    Button btnEntrar;

    private final String URL = "http://192.168.15.135:8080/login";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_admin);

        cadastroAdmin = findViewById(R.id.txtLoginCadastro);
        inputEmail = findViewById(R.id.txtEmailLogin);
        inputSenha = findViewById(R.id.txtSenhaLogin);
        erroLogin = findViewById(R.id.erroLogin);
        btnEntrar = findViewById(R.id.btnLogin);


        btnEntrar.setOnClickListener( e ->{
            if(isInputsCorrected()){
                DadosLoginAdmin loginData = new DadosLoginAdmin();
                loginData.login = inputEmail.getText().toString();
                loginData.password = inputSenha.getText().toString();
                loginApi(loginData);
            }else{
                erroLogin.setText("Preencha os campos!");
            }
        });

        cadastroAdmin.setOnClickListener( e ->{
            Intent gotoSignUpPage = new Intent(this, CadastroAdmin.class);

            startActivity(gotoSignUpPage);
        });
    }

    private Boolean isInputsCorrected(){
        return inputEmail.getText().length() != 0 && inputSenha.getText().length() != 0;
    }

    private void loginApi(DadosLoginAdmin admin){
        SharedPreferences sp = this.getSharedPreferences("cadastroAdmin", MODE_PRIVATE);
        Log.i("LoginAdmin", "" + sp.getInt("id", 0));

        ExecutorService executer = Executors.newSingleThreadExecutor();

        executer.execute(() ->{
            OkHttpClient client = new OkHttpClient();

            Gson gson = new Gson();

            String adminLoginEmString = gson.toJson(admin);

            RequestBody body = RequestBody.create(adminLoginEmString, MediaType.get("application/json"));

            Request request = new Request.Builder().post(body).url(URL).build();

            Call call = client.newCall(request);

            try {
                Response response = call.execute();
                String responseString = response.body().string();
                Log.i("LoginAdmin","Sucesso!:\n" + response);

            } catch (IOException err) {
                Log.i("LoginAdmin", "Erro :(:\n" + err);
                throw  new RuntimeException(err);
            }
        });
    }
}
