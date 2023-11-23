package com.app.mais_jogos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CadastroAdmin extends AppCompatActivity {

    EditText inputEmail;
    EditText inputSenha;
    EditText inputConfirmarSenha;
    TextView erroInputs;
    Button btnCadastrar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_admin);

        inputEmail = findViewById(R.id.txtEmailAdmin);
        inputSenha = findViewById(R.id.txtSenhaAdmin);
        inputConfirmarSenha = findViewById(R.id.txtSenhaConfirmaAdminLogin);
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
                Log.i("cadastroAdmin", "Data:\n" + inputEmail.getText().toString() + " " + inputSenha.getText().toString() + " " + inputConfirmarSenha.getText().toString());
                // Chama o back e cadastra

                Intent intent = new Intent(this, PerfilAdminActivity.class);

                startActivity(intent);
            }else{
                Log.i("cadastroAdmin", "tudo errado!");
                erroInputs.setText("Erro ao preencher as informações!");
            }
        });
    }

    private Boolean isInputsCorrected(){
        return inputEmail.getText().length() != 0
                && inputSenha.getText().length() != 0
                && inputConfirmarSenha.getText().length() != 0
                && inputSenha.getText().toString().equals(inputConfirmarSenha.getText().toString());
    }
}
