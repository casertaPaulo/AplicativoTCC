package com.example.aplicativotcc.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.aplicativotcc.R;
import com.example.aplicativotcc.telaLogin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class profileFragment extends Fragment {

    private TextView NomeUser, EmailUser, TelefoneUser, NascimentoUser, SexoUser;
    private Button bt_deslogar;
    //Inicializa o Firebase com o nome db com a instância do mesmo
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Infla o layout do fragmento
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        NomeUser = view.findViewById(R.id.NomeUser);
        EmailUser = view.findViewById(R.id.EmailUser);
        TelefoneUser = view.findViewById(R.id.TelefoneUser);
        NascimentoUser = view.findViewById(R.id.NascimentoUser);
        SexoUser = view.findViewById(R.id.SexoUser);

        bt_deslogar = view.findViewById(R.id.bt_deslogar);

        //define um evento escutador que verifica se o botão deslogar foi acionado e depois cria uma intenção que nos manda para a tela de login
        bt_deslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), telaLogin.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }

    //Faz uma ligação com o db e busca os dados de usuário (nome, email), adiciona pra um documento de referência e depois adiociona para variáveis
    //em STRING
    public void onStart() {
        super.onStart();

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        DocumentReference documentReference = db.collection("Usuários").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null){
                    NomeUser.setText(documentSnapshot.getString("nome"));
                    EmailUser.setText(email.toString());
                    TelefoneUser.setText(documentSnapshot.getString("telefone"));
                    NascimentoUser.setText(documentSnapshot.getString("data_de_nascimento"));
                    SexoUser.setText(documentSnapshot.getString("sexo"));
                }
            }
        });
    }

}
