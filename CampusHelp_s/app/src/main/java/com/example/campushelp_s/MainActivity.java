package com.example.campushelp_s;

import android.os.Bundle;

import com.example.campushelp_s.ViewModel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import bean.User;
import cn.bmob.v3.Bmob;

public class MainActivity extends AppCompatActivity {

    private User currentUser;
    private String currentUserObjectId;
    private UserViewModel userViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "3fdb919b080c6aec487233c1f30126ab");
        currentUser = (User)getIntent().getSerializableExtra("user");
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setUser(currentUser);//传入共享的值

        currentUserObjectId=currentUser.getObjectId();
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        Bmob.initialize(this,"3fdb919b080c6aec487233c1f30126ab");
        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_square, R.id.navigation_mytask,  R.id.navigation_my, R.id.navigation_submit_task)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavGraph graph = navController.getGraph();
        NavArgument argument = new NavArgument.Builder()
                .setDefaultValue(currentUserObjectId)
                .build();
        graph.addArgument("currentUserObjectId", argument);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

}
