package com.nizshime_samara.calculosalarioliquido_tp7;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class RelatorioSalario extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        Intent intent = getIntent();

        if (intent!= null){
            Bundle params;
            params = intent.getExtras();

            if (params!=null){
                String dados = params.getString("conteudoDados");
                EditText conteudoArqView = (EditText) findViewById(R.id.conteudo);

                conteudoArqView.setText(dados);

            }
        }

    }


}
