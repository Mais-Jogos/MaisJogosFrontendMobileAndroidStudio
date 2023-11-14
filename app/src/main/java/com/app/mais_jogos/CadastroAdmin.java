package com.app.mais_jogos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CadastroAdmin extends AppCompatActivity {

    TextView loginPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_admin);

        loginPage = findViewById(R.id.txtLoginAdmin);

        loginPage.setOnClickListener( e ->{
            Intent gotoLoginPage = new Intent(this, LoginAdmin.class);

            startActivity(gotoLoginPage);
        });
    }
}
