package com.app.mais_jogos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SelectPlayer extends AppCompatActivity {

    LinearLayout layoutAdmin;
    LinearLayout layoutDev;
    LinearLayout layoutUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_player);

        layoutAdmin = findViewById(R.id.lytAdmin);
        layoutDev = findViewById(R.id.lytDev);
        layoutUser = findViewById(R.id.lytUser);

        layoutAdmin.setOnClickListener(e ->{
            Intent gotToSignUpAdmin = new Intent(this, CadastroAdmin.class);

            startActivity(gotToSignUpAdmin);
        });
        layoutDev.setOnClickListener(e ->{
            Intent gotToSignUpDev = new Intent(this, CadastroDevPrimeiraEtapa.class);

            startActivity(gotToSignUpDev);
        });
        layoutUser.setOnClickListener(e ->{
            Intent gotToSignUpDev = new Intent(this, CadastroUserPrimeiraEtapa.class);

            startActivity(gotToSignUpDev);
        });
    }
}
