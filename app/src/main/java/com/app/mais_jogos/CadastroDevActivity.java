package com.app.mais_jogos;

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
import androidx.viewpager.widget.ViewPager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CadastroDevActivity extends AppCompatActivity {

    TextView titleGradient;
    TextView errorCadastroDev;
    EditText nomeDev;
    EditText dataDev;
    EditText sobreDev;
    Button btnNextDev;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_dev);

        /* Title Gradient */
        titleGradient =  findViewById(R.id.titleCadastroDev);
        TextPaint paint = titleGradient.getPaint();
        float width = paint.measureText("Cadastre-se");

        Shader textShader = new LinearGradient(0, 0, width, titleGradient.getTextSize(),
                new int[]{
                        Color.parseColor("#7900FB"),
                        Color.parseColor("#4B3068"),
                }, null, Shader.TileMode.CLAMP);
        titleGradient.getPaint().setShader(textShader);
        /* Title Gradient */

        nomeDev = findViewById(R.id.txtNomeDev);
        dataDev = findViewById(R.id.txtDataDev);
        sobreDev = findViewById(R.id.txtSobreDev);
        btnNextDev = findViewById(R.id.btnNextDev);
        errorCadastroDev = findViewById(R.id.errorCadastroDev);

        btnNextDev.setOnClickListener(e -> {
            if (validar() == true){
                errorCadastroDev.setText("");
                Dev d = getInfoDev();
                Intent loginDev = new Intent(this, LoginDevActivity.class);
                loginDev.putExtra("Dev", d);
                startActivity(loginDev);
            }else{
                errorCadastroDev.setText("Preencha todos os campos!");
            }
        });
    }

    private boolean validar(){
        if (nomeDev.getText().length() != 0 &&
            dataDev.getText().length() != 0 &&
            sobreDev.getText().length() != 0){
            return true;
        }else{
            return false;
        }
    }
    private Dev getInfoDev(){
        Dev d = new Dev();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        d.setNome(nomeDev.getText().toString());
        d.setDataNasc(dataDev.getText().toString());
        d.setSobre(sobreDev.getText().toString());
        return d;
    }
}
