package com.example.aplicativotcc;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.gesture.Gesture;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.example.aplicativotcc.fragments.homeFragment;
import com.example.aplicativotcc.fragments.profileFragment;
import com.example.aplicativotcc.fragments.regFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class telaPrincipal extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);


        //Configuração do ButtomNavigation e fragmentos
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Configuração do viewPager2
        viewPager = findViewById(R.id.viewPager);
        // Configurar o adaptador para o ViewPager2
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Conectar o ViewPager2 com o Navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    navController.navigate(getFragmentId(position));
                }
            });
        } else {
            throw new IllegalStateException("NavHostFragment not found.");
        }

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                int position = getPositionFromFragmentId(destination.getId());
                viewPager.setCurrentItem(position, true);
            }
        });


        // Criar um GestureDetector
        GestureDetector gestureDetector = new GestureDetector(this, new SwipeGestureListener());

        // Configurar o listener de toques do ViewPager
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });


        //FIM DA CLASS ONCREATE
    }

    // Mapear cada posição do ViewPager2 para o ID do fragmento correspondente no Navigation
    private @IdRes int getFragmentId(int position) {
        switch (position) {
            case 0:
                return R.id.homeFragment;
            case 1:
                return R.id.regFragment;
            case 2:
                return R.id.profileFragment;
            default:
                return R.id.homeFragment;
        }
    }

    //Criação classe MyPageAdapter
    public class MyPagerAdapter extends FragmentStateAdapter {

        public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Retorne o fragmento correspondente à posição
            switch (position) {
                case 0:
                    return new homeFragment();
                case 1:
                    return new regFragment();
                case 2:
                    return new profileFragment();
                default:
                    return new homeFragment();
            }
        }

        @Override
        public int getItemCount() {
            // Retorne o número total de fragmentos
            return 3;
        }
    }


    //CRIAÇÃO DA CLASSE SwipeGestureListener
    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Deslizamento para a direita
                        int currentFragmentIndex = viewPager.getCurrentItem();
                        if (currentFragmentIndex > 0) {
                            viewPager.setCurrentItem(currentFragmentIndex - 1);
                            Log.d("OnFling", "deslizamento para a direita"); // Debug log
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            return true;
                        }
                    } else {
                        // Deslizamento para a esquerda
                        int currentFragmentIndex = viewPager.getCurrentItem();
                        if (currentFragmentIndex < viewPager.getAdapter().getItemCount() - 1) {
                            viewPager.setCurrentItem(currentFragmentIndex + 1);
                            Log.d("OnFling", "deslizamento para a esquerda"); // Debug log

                        }
                    }
                }
            }

            return false;
        }
    }

    private int getPositionFromFragmentId(@IdRes int fragmentId) {
        SparseArray<Integer> fragmentPositions = new SparseArray<>();
        fragmentPositions.put(R.id.homeFragment, 0);
        fragmentPositions.put(R.id.regFragment, 1);
        fragmentPositions.put(R.id.profileFragment, 2);

        Integer position = fragmentPositions.get(fragmentId);
        return position != null ? position : 0;
    }


}



