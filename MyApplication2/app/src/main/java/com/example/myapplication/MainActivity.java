package com.example.myapplication;

import android.os.Bundle;
import com.qualtrics.digital.*;
import com.google.android.material.snackbar.Snackbar;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //linea de agregacion onCreate Qualtrics
        Qualtrics.instance().initializeProject("paecx", "ZN_3QJiBs5aRghxvim", "SI", MainActivity.this);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Qualtrics.instance().evaluateProject(new MyCallback());
            }
        });


        Button secondButton = (Button) findViewById(R.id.button);
        secondButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Qualtrics.instance().evaluateIntercept("SI_7ZYqfEaV2IOuOA6", new IQualtricsCallback(){
                    @Override
                    public void run(TargetingResult targetingResult) {
                        if (targetingResult.passed()) {
                            Qualtrics.instance().displayIntercept(MainActivity.this, "SI_7ZYqfEaV2IOuOA6");
                        } else if (targetingResult.getTargetingResultStatus() != null) {
                            if (targetingResult.getError() != null) {
                                Log.v("Qualtrics", targetingResult.getError().getMessage());
                            } else {
                                Log.v("Qualtrics", targetingResult.getTargetingResultStatus().toString());
                            }
                        }

                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private class MyCallback implements IQualtricsProjectEvaluationCallback {
        @Override
        public void run(Map<String, TargetingResult> targetingResults) {
            for (Map.Entry<String,TargetingResult> result: targetingResults.entrySet())
                if (result.getValue().passed()) {
                    Qualtrics.instance().displayIntercept(MainActivity.this, result.getKey());
                }
        }
    }
}


