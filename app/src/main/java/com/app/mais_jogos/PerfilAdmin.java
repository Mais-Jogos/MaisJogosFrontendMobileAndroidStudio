package com.app.mais_jogos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilAdmin extends AppCompatActivity {

    Button excluirAdmin;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_admin);

        excluirAdmin = findViewById(R.id.btnExcluirAdmin);

        excluirAdmin.setOnClickListener( e ->{
            // Modal
            AlertDialog.Builder confirmaExlusao = new AlertDialog.Builder(PerfilAdmin.this);
            confirmaExlusao.setTitle("Atenção!!");
            confirmaExlusao.setMessage("Tem certeza que deseja excluir a sua conta?\nEssa ação não pode ser desfeita!");
            confirmaExlusao.setCancelable(false);

            confirmaExlusao.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Conecta com Back e exclui

                    Intent intent = new Intent(getApplicationContext(), SelectPlayer.class);

                    startActivity(intent);
                }
            });

            confirmaExlusao.setNegativeButton("Não", null);

            confirmaExlusao.create().show();
        });

    }
}
