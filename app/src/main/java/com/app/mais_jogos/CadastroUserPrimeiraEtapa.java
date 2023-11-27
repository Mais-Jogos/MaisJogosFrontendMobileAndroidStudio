package com.app.mais_jogos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CadastroUserPrimeiraEtapa extends AppCompatActivity {

    TextView titleGradient;
    TextView errorCadastroUser;
    EditText nomeUser;
    EditText dataUser;
    EditText sobrenomeUser;
    Button btnNextUser;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_user_primeira_etapa);

        /* Title Gradient */
        titleGradient =  findViewById(R.id.titleCadastroUser);
        TextPaint paint = titleGradient.getPaint();
        float width = paint.measureText("Cadastre-se");

        Shader textShader = new LinearGradient(0, 0, width, titleGradient.getTextSize(),
                new int[]{
                        Color.parseColor("#7900FB"),
                        Color.parseColor("#4B3068"),
                }, null, Shader.TileMode.CLAMP);
        titleGradient.getPaint().setShader(textShader);
        /* Title Gradient */

        nomeUser = findViewById(R.id.txtNomeUser);
        dataUser = findViewById(R.id.txtDataUser);
        sobrenomeUser = findViewById(R.id.txtSobrenomeUser);
        btnNextUser = findViewById(R.id.btnNextUser);
        errorCadastroUser = findViewById(R.id.errorCadastroUser);

        btnNextUser.setOnClickListener(e -> {
            if (validar()){
                errorCadastroUser.setText("");
                User d = getInfoUser();
                Intent loginUser = new Intent(this, CadastroUserSegundaEtapa.class);
                loginUser.putExtra("User", d);
                startActivity(loginUser);
            }else{
                errorCadastroUser.setText("Preencha todos os campos!");
            }
        });
    }

    private boolean validar(){
        return nomeUser.getText().length() != 0 &&
                dataUser.getText().length() != 0 &&
                sobrenomeUser.getText().length() != 0;
    }
    private User getInfoUser(){
        User d = new User();
        d.setNome(nomeUser.getText().toString());
        d.setDataNasc(dataUser.getText().toString());
        d.setSobrenome(sobrenomeUser.getText().toString());
        return d;
    }
}
