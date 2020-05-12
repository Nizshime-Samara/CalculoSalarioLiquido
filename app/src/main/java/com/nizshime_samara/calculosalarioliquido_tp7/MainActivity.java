package com.nizshime_samara.calculosalarioliquido_tp7;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.io.OutputStreamWriter;

import java.text.DecimalFormat;

public class MainActivity < onClickListiner > extends AppCompatActivity {


    TextView inssDesc;//Inss, //
    TextView inssResult;//Inss, //
    EditText salarioBase;
    EditText pensaoVal;
    EditText dependentesQtd;
    EditText planoSaude;
    TextView pensaoAlimenticia;
    TextView IrffDesc;// Irrf//
    TextView irffResult;// Irrf//
    TextView ResultsalarioLiquido;
    EditText valPassagem;
    EditText editQtdDep;
    EditText editSalResilt;
    EditText conteudoArq;

    Button buttonInss;
    Button BtnCalcularLiqSal;
    Button salvarArquivoTxt;
    Button lerArquivo;


    public static final String TAG = "LOG";

    DecimalFormat currency = new DecimalFormat("00.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        inssDesc = (findViewById(R.id.textInss));//resulta desc inss//
        IrffDesc = (findViewById(R.id.TextIrff));//resulta desc irff//
        inssResult = (findViewById(R.id.inssResult));//resulta salario com desc inss//
        irffResult = (findViewById(R.id.irffResult)); // resulta salario com desc irrf//
        pensaoAlimenticia = (findViewById(R.id.textTotalPensao));
        salarioBase = (findViewById(R.id.editSalatioBase)); // insere o bruto//
        dependentesQtd = (findViewById(R.id.editDependentes));
        pensaoVal = (findViewById(R.id.editPensao));
        editQtdDep = (findViewById(R.id.editQtdDep));
        editSalResilt = (findViewById(R.id.editSalResilt));
        planoSaude = (findViewById(R.id.editPlano));
        valPassagem = (findViewById(R.id.editPassagem));
        ResultsalarioLiquido = (findViewById(R.id.SalarioLiquido));
        BtnCalcularLiqSal = (findViewById(R.id.buttonCalc));
        conteudoArq = (findViewById(R.id.conteudo));

        buttonInss = (findViewById(R.id.button1));
        salvarArquivoTxt = (findViewById(R.id.btnSalvarArq));
        lerArquivo = (findViewById(R.id.btnLerArq));


        salvarArquivoTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                alerta.setTitle("Aviso");
                alerta
                        .setIcon(R.mipmap.ic_info_alert)
                        .setMessage("Deseja salvar arquivo txt em memória na interna?")
                        .setCancelable(false) // se colocar True e usuario clicar fora da caixa o dialogo some//
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Arquivo não Salvo", Toast.LENGTH_SHORT).show();
                                verificarArquivo();
                            }
                        })
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Arquivo Salvo com sucesso", Toast.LENGTH_SHORT).show();
                                gerarRelatorio();
                            }
                        });
                AlertDialog alertDialog = alerta.create();
                alertDialog.show();
            }
        });

    }

    public void LerRelatorio(View view) {
        String conteudoDados =  conteudoArq.getText().toString();

        Bundle params = new Bundle();
        params.putString("conteudoDados",conteudoDados);

        Intent intent =  new Intent(this,RelatorioSalario.class);
        intent.putExtras(params);

        startActivity(intent);

    }

    private void Mensagem(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    public void verificarArquivo() {
        File file = new File(getFilesDir(), "relatorioSalario.txt");
        if (file.exists()) {
            new File(getFilesDir(), toString() + "relatorio.txt").delete();
            if (file.delete()) {
               Log.i(TAG, "arqivo deletado");
            }
        }
    }


    public void calcTotpensão(View view) {
       String numeroDeps =  dependentesQtd.getText().toString();
       String valPensao = pensaoVal.getText().toString();

        if (numeroDeps.equals("")||valPensao.equals("")){
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("Aviso");
            dialog.setMessage("Por favor, insira um valor no campo 'R$: pensão' e 'Nº Dependentes' (Caso não haja, insira 0 nos campos correspondentes). ");
            dialog.setNeutralButton("oK", null);
            dialog.show();
            return;
        }

            double deps = Double.parseDouble(dependentesQtd.getText().toString());
            double valpensao = Double.parseDouble(pensaoVal.getText().toString());

            double totalPgPensao = deps*valpensao; //calculo de pensão a ser desc//

            if (totalPgPensao >=1){
                pensaoAlimenticia.setText("R$ " + currency.format(totalPgPensao)+("; "));

            }else if(totalPgPensao == 0){
                pensaoAlimenticia.setText("Não há valores "+currency.format(totalPgPensao)+("; "));
            }
    }


// agora INSS///



    public void CalcIrff(View view) {

        String numeroDeps =  dependentesQtd.getText().toString();
        String valSalResultInss = editSalResilt.getText().toString();

        if (numeroDeps.equals("")||valSalResultInss.equals("")){
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("Aviso");
            dialog.setMessage("Por favor, insira um valor no campo 'Insira Salário/INSS' e 'Nº Dependentes' (Caso não haja dependentes, insira 0 no campo correspondente). ");
            dialog.setNeutralButton("oK", null);
            dialog.show();
            return;
        }

        double baseInssSal = Double.parseDouble(editSalResilt.getText().toString());
        double deps = Double.parseDouble(editQtdDep.getText().toString());
        double deduçaoDeps = deps * 189.59;


        double Aliquota1A = 00.00;
        double Aliquota1 =  0.075;
        double Aliquota2 =  0.15;
        double Aliquota3 =  0.225;
        double Aliquota3A =  0.275;

        double ParcelaDeduzir1A = 00.00;
        double ParcelaDeduzir1 = 142.08;
        double ParcelaDeduzir2 =  354.08;
        double ParcelaDeduzir3 = 636.13;
        double ParcelaDeduzir3A = 869.36;

        double irffFaixa1A = baseInssSal *Aliquota1A - ParcelaDeduzir1A;
        double irffFaixa1 = baseInssSal * Aliquota1 - ParcelaDeduzir1;
        double irffFaixa2 = baseInssSal * Aliquota2 - ParcelaDeduzir2;
        double irffFaixa3 = baseInssSal * Aliquota3 - ParcelaDeduzir3;
        double irffFaixa3A = baseInssSal * Aliquota3A - ParcelaDeduzir3A;

        double resultFaixa1A = ( baseInssSal - irffFaixa1A) - deduçaoDeps;
        double resultFaixa1 = ( baseInssSal - irffFaixa1) - deduçaoDeps;
        double resultFaixa2 = ( baseInssSal - irffFaixa2) - deduçaoDeps;
        double resultFaixa3 = ( baseInssSal - irffFaixa3) - deduçaoDeps;
        double resultFaixa3A = (baseInssSal -irffFaixa3A ) - deduçaoDeps;

        if (baseInssSal<=1903.98)
        {
            IrffDesc.setText(String.format(" R$ "+ currency.format(irffFaixa1A )+("; ")));
            irffResult.setText(String.format("R$ "+ currency.format(resultFaixa1A)));

        }else  if (baseInssSal  >= 1903.99 && baseInssSal <= 2826.65){
            IrffDesc.setText(String.format(" R$ "+ currency.format(irffFaixa1 )+("; ")));
            irffResult.setText(String.format(" R$ "+ currency.format(resultFaixa1)));
        }else if (baseInssSal  >= 2826.66 && baseInssSal <= 3751.05){
            IrffDesc.setText(String.format(" R$ "+ currency.format(irffFaixa2 )+("; ")));
            irffResult.setText(String.format(" R$ "+ currency.format(resultFaixa2)+("; ")));
    }else if (baseInssSal  >= 3751.06 && baseInssSal <= 4664.68){
            IrffDesc.setText(String.format(" R$ "+ currency.format(irffFaixa3 )+("; ")));
            irffResult.setText(String.format(" R$ "+ currency.format(resultFaixa3)+(";")));
        }else if (baseInssSal >= 4664.69){
            IrffDesc.setText(String.format(" R$ "+ currency.format(irffFaixa3A )+("; ")));
           irffResult.setText(String.format(" R$ "+ currency.format(resultFaixa3A)+("; ")));
        }
    }

    public void calcularTotalLiq(View view) {
        String salbase = salarioBase.getText().toString();
        String salIPRF = editSalResilt.getText().toString();
        if (salbase.equals("")|| salIPRF.equals("")){
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("Aviso");
            dialog.setMessage("Por favor,insira um valor para cálculo no campo 'Salário Bruto' e 'Salário/INSS'.");
            dialog.setNeutralButton("oK", null);
            dialog.show();
            return;
        }
        double salario = Double.parseDouble(editSalResilt.getText().toString());

        double deps = Double.parseDouble(dependentesQtd.getText().toString());
        double pensao = Double.parseDouble(pensaoVal.getText().toString());

        double plano = Double.parseDouble(planoSaude.getText().toString());
        double passagem = Double.parseDouble(valPassagem.getText().toString());

        double totPgPensao = deps*pensao;

        double salTotLiquid = salario - (totPgPensao + plano + passagem);

        if (salTotLiquid>=0){
            ResultsalarioLiquido.setText(String.format(" R$  " + salTotLiquid +"; "));
        }

    }

    public void LimparResultados(View view) {
        inssDesc.setText("");//Inss, //
        inssResult.setText("");//Inss, //
         salarioBase.setText("");
         pensaoVal.setText("");
         dependentesQtd.setText("");
         planoSaude.setText("");
         pensaoAlimenticia.setText("");
         IrffDesc.setText("");// Irrf//
         irffResult.setText("");// Irrf//
         ResultsalarioLiquido.setText("");
         valPassagem.setText("");
         editQtdDep.setText("");
         editSalResilt.setText("");
         conteudoArq.setText("");

    }

    public void gerarRelatorio() {
        verificarArquivo();
        String fileName = "relatorioSalario.txt";
        FileOutputStream outputStream;

        String salarioBruto = ("Salário Bruto  = R$")+ salarioBase.getText().toString()+("                                      ");
        String inssDesconto =  ("---------------Descontos Impostos---------------")+("Desconto INSS: ")+inssDesc.getText().toString()+("                    ");
        String irffDesconto =  ("Desconto IPRF: ")+IrffDesc.getText().toString()+("                          ");
        String pensaoFilhos = ("------------------Outros descontos----------------")+("Pensão Alimentícia ")+ pensaoAlimenticia.getText().toString()+("                                 ");
        String planoSaud =    ("Plano de Saúde: R$ ")+ planoSaude.getText().toString()+(";")+("                                      ");
        String passagem =    ("Passagem:  ")+("R$ ")+valPassagem.getText().toString()+(";")+("                                       ");
        String liquidoTotal = ("--------------------Salário Líquido--------------------")+("Salário Líquido ")+ ResultsalarioLiquido.getText().toString();
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);


            OutputStreamWriter myOutWriter = new OutputStreamWriter(outputStream);
            myOutWriter.append(salarioBruto);
            myOutWriter.append(inssDesconto);
            myOutWriter.append(irffDesconto);
            myOutWriter.append(pensaoFilhos);
            myOutWriter.append(passagem);
            myOutWriter.append(liquidoTotal);

            outputStream.write(salarioBruto.getBytes());
            outputStream.write(inssDesconto.getBytes());
            outputStream.write(irffDesconto.getBytes());
            outputStream.write(pensaoFilhos.getBytes());
            outputStream.write(planoSaud.getBytes());
            outputStream.write(passagem.getBytes());
            outputStream.write(liquidoTotal.getBytes());

            Mensagem("Salvo");

            outputStream.close();
            outputStream.flush();
            outputStream.flush();
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        carregar();
    }

    public void carregar(){
        String linha;
        File arq;
        try {
            conteudoArq.setText("");
            arq = new File(getFilesDir(),"relatorioSalario.txt");
            BufferedReader br =new BufferedReader(new FileReader(arq));
            while ((linha = br.readLine()) != null){
                if(!conteudoArq.getText().toString().equals("")){
                    conteudoArq.append("\n");
                }
                conteudoArq.append(linha);
                conteudoArq.append(" ");
            }
            Mensagem(  " Arquivo carregado com sucesso");
        }catch (Exception e){
            Mensagem("Erro: "+ e.getMessage());
        }
    }

    public void CalcInss(View view) {
        String salbase = salarioBase.getText().toString();
        if (salbase.equals("")){
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("Aviso");
            dialog.setMessage("Por favor, insira um valor para cálculo no campo 'Salário Bruto'. ");
            dialog.setNeutralButton("oK", null);
            dialog.show();
            return;
        }

        double base = Double.parseDouble(salarioBase.getText().toString());
        double Aliquota1 =  0.075;
        double Aliquota2 =  0.9;
        double Aliquota3 =  0.12;
        double Aliquota3A =  0.14;

        double ParcelaDeduzir1 = 00.00;
        double ParcelaDeduzir2 = 15.684;
        double ParcelaDeduzir3 = 78.378;
        double ParcelaDeduzir3A = 141.068;

        double inssFaixa1 = base * Aliquota1 - ParcelaDeduzir1;
        double inssFaixa2 = (base * Aliquota2) - ParcelaDeduzir2;
        double inssFaixa3 = (base * Aliquota3) - ParcelaDeduzir3;
        double inssFaixa3A = (base * Aliquota3A) - ParcelaDeduzir3A;

        double resultFaixa1 = ( base - inssFaixa1);
        double resultFaixa2 = ( base - inssFaixa2);
        double resultFaixa3 = ( base- inssFaixa3);
        double resultFaixa3A = (base-inssFaixa3A);

        if (base <= 1045.00) {
            inssDesc.setText(String.format(" R$" + currency.format(inssFaixa1)+("; ")));
            inssResult.setText(String.format(" R$ " + currency.format(resultFaixa1)+("; ")));

        } else if (base >= 1045.01 && base <= 2089.60) {
            inssDesc.setText(String.format(" R$ " + currency.format(inssFaixa2)+("; ")));
            inssResult.setText(String.format(" R$ " + currency.format(resultFaixa2)+("; ")));
        } else if (base >= 2089.61 && base <= 3134.40) {
            inssDesc.setText(String.format(" R$ " + currency.format(inssFaixa3)+("; ")));
            inssResult.setText(String.format(" R$ " + currency.format(resultFaixa3)+("; ")));
        } else if (base >= 3134.41 && base <= 6101.06) {
            inssDesc.setText(String.format(" R$ " + currency.format(inssFaixa3A)+("; ")));
            inssResult.setText(String.format(" R$ " + currency.format(resultFaixa3A)+("; ")));
        } else if (base >= 6101.07){
            inssDesc.setText(String.format(" R$ " + currency.format(inssFaixa3A)+("; ")));
            inssResult.setText(String.format(" R$ " + currency.format(resultFaixa3A)+("; ")));
        }
    }
}