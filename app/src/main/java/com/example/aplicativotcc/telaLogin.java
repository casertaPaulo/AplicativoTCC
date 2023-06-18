package com.example.aplicativotcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class telaLogin extends AppCompatActivity {
    private EditText edit_email, edit_senha;
    private Button bt_entrar, text_tela_cadastro;
    private ProgressBar progressBar;
    private ImageView mostrarSenha, ocultarSenha;
    String[] mensagens = {"Preencha todos os campos", "Login Realizado com Sucesso!"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);

        IniciarComponentes();

        //Evento que troca a visibilidade da senha ====================================================================
        mostrarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_senha.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                mostrarSenha.setVisibility(View.GONE);
                ocultarSenha.setVisibility(View.VISIBLE);
                edit_senha.setSelection(edit_senha.getText().length());
            }
        });

        ocultarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_senha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ocultarSenha.setVisibility(View.GONE);
                mostrarSenha.setVisibility(View.VISIBLE);
                edit_senha.setSelection(edit_senha.getText().length());
            }
        });

        //Define um evento escutador no botão "text_tela_cadastro" ====================================================================
        text_tela_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cria uma intenção ao evento escutador, manda pra tela de "telaCadastro"
                Intent intent = new Intent(telaLogin.this, telaCadastro.class);
                startActivity(intent);
            }
        });

        //Define um evento escutador no botão "Entrar"
        bt_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Método onde o teclado é ocultado quando BT_ENTRAR é clicado
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                //Joga as informações para variáveis para assim começar as verificações
                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();

                if (email.isEmpty() || senha.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    AutentificarUsuario(v);
                }
            }
        });
    }

    private void AutentificarUsuario(View v){
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        //Recupera a instancia do firebase auth, usa o método signIn através de email e senha e logo após
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.VISIBLE);

                    //Cria uma animação que executa um método após 3 segundos depois de clicar no botão entrar
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TelaPrincipal();
                        }
                    }, 2000);
                }else{
                    String erro;
                    try {
                        throw task.getException();
                    }
                    catch (Exception e){
                        erro = "E-mail ou senha inválido!";
                    }
                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioAtual != null){
            TelaPrincipal();
        }
    }

    //Instancia um intenção onde é direcionado para a tela principal
    private void TelaPrincipal(){
        Intent intent = new Intent(telaLogin.this, telaPrincipal.class);
        startActivity(intent);
        finish();
    }

    private void IniciarComponentes(){ //Cria um método que inicia junto com a Activity e se resume em referenciar o componente com id "text_tela_cadastro
        text_tela_cadastro = findViewById(R.id.text_tela_cadastro);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        bt_entrar  = findViewById(R.id.bt_entrar);
        progressBar = findViewById(R.id.progressBar);
        mostrarSenha = findViewById(R.id.mostrarSenha);
        ocultarSenha = findViewById(R.id.ocultarSenha);
    }
}