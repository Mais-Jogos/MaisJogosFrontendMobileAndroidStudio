package com.app.mais_jogos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    Button btnAvancar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        btnAvancar = findViewById(R.id.btnNext);

        btnAvancar.setOnClickListener( e ->{

            Intent goToSelectPlayerPage = new Intent(this, SelectPlayer.class);

            startActivity(goToSelectPlayerPage);
        });

    }
}
