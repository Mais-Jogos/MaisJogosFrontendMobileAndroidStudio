package com.app.mais_jogos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginAdmin extends AppCompatActivity {

    TextView cadastroAdmin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_admin);

        cadastroAdmin = findViewById(R.id.txtLoginCadastro);

        cadastroAdmin.setOnClickListener( e ->{
            Intent gotoSignUpPage = new Intent(this, CadastroAdmin.class);

            startActivity(gotoSignUpPage);
        });

    }
}
