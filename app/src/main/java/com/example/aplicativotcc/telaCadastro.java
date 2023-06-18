package com.example.aplicativotcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class telaCadastro extends AppCompatActivity {

    private EditText edit_nome, edit_email, edit_senha, edit_telefone, edit_nascimento, edit_sexo;
    private Button bt_cadastrar;
    String[] mensagens = {"Preencha todos os campos", "Cadastro Realizado com Sucesso!"};
    String usuarioID;
    final String[] OpSexo = {"Masculino", "Feminino"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro);

        IniciarComponentes();

        //===========================================FORMATAÇÃO EDIT TELEFONE========================================================
        // Adiciona o TextWatcher
        edit_telefone.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private boolean isDeleting;
            private final String mask = "(##) #####-####";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isDeleting = count > after;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting)
                    return;

                isFormatting = true;

                // Remove qualquer formatação anteriormente aplicada
                String unmaskedText = s.toString().replaceAll("[^\\d.]", "");

                StringBuilder formattedText = new StringBuilder();
                int maskIndex = 0;
                int unmaskedIndex = 0;

                while (maskIndex < mask.length() && unmaskedIndex < unmaskedText.length()) {
                    char currentMaskChar = mask.charAt(maskIndex);

                    if (currentMaskChar == '#') {
                        formattedText.append(unmaskedText.charAt(unmaskedIndex));
                        unmaskedIndex++;
                    } else {
                        formattedText.append(currentMaskChar);
                    }

                    maskIndex++;
                }

                // Aplica a formatação completa
                edit_telefone.setText(formattedText.toString());
                edit_telefone.setSelection(formattedText.length());

                isFormatting = false;
            }
        });


        //===========================================FORMATAÇÃO EDIT DATA NASCIMNTO========================================================
        edit_nascimento.addTextChangedListener(new TextWatcher() {
            private final String DATE_MASK = "##/##/####";
            private boolean isUpdating = false;
            private String oldText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nada a ser feito antes da mudança no texto
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nada a ser feito durante a mudança no texto
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) {
                    return;
                }

                String newText = s.toString();

                // Verifica se o novo texto é igual ao texto antigo
                if (newText.equals(oldText)) {
                    return;
                }

                // Remove as barras do novo texto
                String textWithoutMask = newText.replaceAll("[^0-9]", "");

                // Verifica se o novo texto está vazio
                if (textWithoutMask.isEmpty()) {
                    isUpdating = true;
                    s.clear();
                    isUpdating = false;
                    return;
                }

                // Formata o novo texto com as barras
                StringBuilder formattedText = new StringBuilder();
                int index = 0;
                for (char maskChar : DATE_MASK.toCharArray()) {
                    if (maskChar == '#') {
                        if (index < textWithoutMask.length()) {
                            formattedText.append(textWithoutMask.charAt(index));
                            index++;
                        } else {
                            break;
                        }
                    } else {
                        formattedText.append(maskChar);
                    }
                }

                isUpdating = true;
                s.replace(0, s.length(), formattedText);
                isUpdating = false;

                oldText = formattedText.toString();
            }
        });


        //Cria um alert onde é possível escolher o sexo, atualizando o edit e sendo assim possível mandar para o BD
        edit_sexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(telaCadastro.this);
                builder.setTitle("Escolha");
                builder.setItems(OpSexo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Atualizar o EditText com o sexo selecionado
                        edit_sexo.setText(OpSexo[which]);
                    }
                });
                builder.show();
            }
        });

        //Define um evento escutador para cadastrar o usuário
        bt_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = edit_nome.getText().toString();
                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();
                String telefone = edit_telefone.getText().toString();
                String data_nascimento = edit_telefone.getText().toString();
                String sexo = edit_sexo.getText().toString();

                //Verifica se há algum campo vazio
                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || telefone.isEmpty() || data_nascimento.isEmpty() || sexo.isEmpty()) {
                    exibirSnackbar(v, "Preencha todas as informações!");
                }else{
                    if (!telefone.matches("\\(\\d{2}\\) \\d{4,5}-\\d{4}")) {
                        // Formato inválido de telefone
                        exibirSnackbar(v, "Telefone inválido! Utilize o formato (XX) XXXXX-XXXX");
                    } else if ((!sexo.equals("Masculino") && !sexo.equals("Feminino"))) {
                        // Valor inválido para o sexo
                        exibirSnackbar(v, "Sexo inválido! Utilize M para masculino ou F para feminino");
                    } else {
                        CadastrarUsuario(v);
                    }
                }
            }
        });
    }

    private void CadastrarUsuario(View v){
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();
        String telefone = edit_telefone.getText().toString();
        String data_nascimento = edit_nascimento.getText().toString();
        String sexo = edit_sexo.getText().toString();


        //pega a instância do firebase no servidor e usa o método criar usuario com email e senha
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //task -> cadastro || se verdadeiro, cadastra e salva os dados
                if (task.isSuccessful()){
                    SalvarDados();
                    Snackbar snackbar = Snackbar.make(v, mensagens[1], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                    //Cria uma animação que manda para a tela principal após meio segundo
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TelaPrincipal();
                        }
                    }, 500);
                }else{
                    String erro;

                    //tratar as exceções, senhas curtas, emails errados, emails repetidos etc.
                    try {
                        //tenta buscar uma exceção
                        throw Objects.requireNonNull(task.getException());

                        //verifica se a senha tem 6 carateres
                    }catch (FirebaseAuthWeakPasswordException e){
                        erro = "Digite uma senha com no mínimo 6 caracteres";
                    }
                    //verifica no banco de o e-mail já foi cadastrado
                    catch (FirebaseAuthUserCollisionException e){
                        erro = "Esse e-mail já foi cadastrado!";
                    }
                    //verifica se a estrutura do e-mail está correta
                    catch (FirebaseAuthInvalidCredentialsException e){
                        erro = "E-mail invalido!";
                    }
                    //verifica se ainda há algum erro
                    catch(Exception e) {
                        erro = "Erro ao cadastrar usuário! Tente novamente";
                    }

                    exibirSnackbar(v, erro);
                }
            }
        });
    }

    private void SalvarDados(){
        String nome = edit_nome.getText().toString();
        String telefone = edit_telefone.getText().toString();
        String data_nascimento = edit_nascimento.getText().toString();
        String sexo = edit_sexo.getText().toString();

        //atribui a "db", a instancia do banco de dados Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> usuarios = new HashMap<>();
        //é possível adicionar outros componentes
        usuarios.put("nome", nome);
        usuarios.put("telefone", telefone);
        usuarios.put("sexo", sexo);
        usuarios.put("data_de_nascimento", data_nascimento);

        //.getCurrentUser() é o nosso usuário ATUAL || .getUid() pega o id do usuário
        //Recupera a instancia do database AUTH, pega o usuario ATUAL e pega o ID referente a esse user, logo após, atribui a "usuarioID"
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Cada usuário criado no banco de dados, terá um documento específico
        DocumentReference documentReference = db.collection("Usuários").document(usuarioID);
        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("db", "Sucesso ao salvar os dados!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db_error", "Erro ao salvar os dados!" + e.toString());
                    }
                });
    }
    private void IniciarComponentes(){
        edit_nome = findViewById(R.id.edit_nome);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        edit_email = findViewById(R.id.edit_email);
        edit_nascimento = findViewById(R.id.edit_nascimento);
        edit_sexo = findViewById(R.id.edit_sexo);
        edit_telefone = findViewById(R.id.edit_telefone);
        bt_cadastrar = findViewById(R.id.bt_cadastrar);
    }

    //Instancia um intenção onde é direcionado para a tela principal
    private void TelaPrincipal(){
        Intent intent = new Intent(telaCadastro.this, telaPrincipal.class);
        startActivity(intent);
        finish();
    }

    private void exibirSnackbar(View view, String mensagem) {
        Snackbar snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbar.setTextColor(Color.parseColor("#0d46af"));
        snackbar.setBackgroundTint(Color.WHITE);
        snackbarLayout.setMinimumHeight(120);
        snackbar.show();
    }
}