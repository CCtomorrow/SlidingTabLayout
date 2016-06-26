package com.yong.slidingtablayout.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        no = (Button) findViewById(R.id.wu);
    }

    public void goFragment(View view) {
        no.setVisibility(View.GONE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        SampleFragment fragment = new SampleFragment();
        transaction.replace(R.id.sample_content_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (no.getVisibility() == View.GONE) {
            no.setVisibility(View.VISIBLE);
        }
    }

}
