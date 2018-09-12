package realincome.prosad.shuvo.realincome.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import realincome.prosad.shuvo.realincome.Fragment.BalanceFragment;
import realincome.prosad.shuvo.realincome.Fragment.ContactUsFragment;
import realincome.prosad.shuvo.realincome.Fragment.Profile_Fragment;
import realincome.prosad.shuvo.realincome.Fragment.ReferalFragment;
import realincome.prosad.shuvo.realincome.Fragment.TaskFragment;
import realincome.prosad.shuvo.realincome.Fragment.TaskListFragment;
import realincome.prosad.shuvo.realincome.Fragment.WidthDrawFragment;
import realincome.prosad.shuvo.realincome.R;

public class CompleteActivity extends AppCompatActivity {

    private TaskFragment taskFragment;
    String taskno="";
    private AdView mAdView1,mAdView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        mAdView1 = findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest1);

        mAdView2 = findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest2);

        Intent intent = getIntent();
        taskno = intent.getStringExtra("taskno");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    load_navigation_activity();
                    finish();
                }
            }, 5000);


    }

    public void load_navigation_activity() {
        taskFragment = new TaskFragment();
        Intent intent = new Intent(this, Navigation_drawer.class);
        intent.putExtra("no",taskno);
        startActivity(intent);
        finish();
    }
}
