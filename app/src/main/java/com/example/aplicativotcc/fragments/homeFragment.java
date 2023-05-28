package com.example.aplicativotcc.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aplicativotcc.R;


public class homeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Definir animações de transição para o fragmento
        Slide slide = new Slide(Gravity.START);  //Alterado para Gravity.START
        slide.setDuration(300);
        setEnterTransition(slide);

        Slide slideExit = new Slide(Gravity.END); // Alterado para Gravity.END
        slideExit.setDuration(300);
        setExitTransition(slideExit);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}