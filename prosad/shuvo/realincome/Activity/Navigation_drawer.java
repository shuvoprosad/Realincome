package realincome.prosad.shuvo.realincome.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import realincome.prosad.shuvo.realincome.Connection.Server_request;
import realincome.prosad.shuvo.realincome.Fragment.BalanceFragment;
import realincome.prosad.shuvo.realincome.Fragment.ContactUsFragment;
import realincome.prosad.shuvo.realincome.Fragment.Profile_Fragment;
import realincome.prosad.shuvo.realincome.Fragment.ReferalFragment;
import realincome.prosad.shuvo.realincome.Fragment.TaskFragment;
import realincome.prosad.shuvo.realincome.Fragment.TaskListFragment;
import realincome.prosad.shuvo.realincome.Fragment.WidthDrawFragment;
import realincome.prosad.shuvo.realincome.R;

public class Navigation_drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private AdView mAdView;
    private View v;
    public TextView tv_name,tv_referal,tv_mobile,tv_email;
    private TaskListFragment taskListFragment;
    private TaskFragment taskFragment;
    private Profile_Fragment profileFragment;
    private WidthDrawFragment widthdrawFragment;
    private BalanceFragment balanceFragment;
    private ReferalFragment referalFragment;
    private ContactUsFragment contactFragment;
    public FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;

    public static Context appcontext;
    private String url = "http://www.stardesignbd.com/demo/apps/include/show-user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appcontext = this;
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        v = (View) findViewById(R.id.coordinatorLayout);
        tv_name = (TextView) headerView.findViewById(R.id.textView_drawer_name);
        tv_referal = (TextView) headerView.findViewById(R.id.textView_drawer_ref_code);
        tv_mobile = (TextView) headerView.findViewById(R.id.textView_drawer_mobile);
        tv_email = (TextView) headerView.findViewById(R.id.textView_drawer_email);

        load_data_shared_pref();
        Bundle extra = getIntent().getExtras();
        if( extra.containsKey("no")) {
            loadTaskFragment();
        }
        else{
            loadTaskListFragment();
        }

        detect_vpn();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            loadProfileFragment();
        }
        else if (id == R.id.nav_my_team) {
            load_Referal_Fragment();
        }
        else if (id == R.id.nav_withdraw){
            load_Widthdraw_Fragment();
        }
        else if (id == R.id.nav_contact_us){
            load_Contact_Fragment();
        }
        else if (id == R.id.nav_my_task){
            loadTaskListFragment();
        }
        else if (id == R.id.nav_balance){
            load_Balance_Fragment();
        }
        else if (id == R.id.nav_logout){
            logout_close();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void load_user_info(String memberid){
        JSONObject js = new JSONObject();
        try {
            js.put("memberID",memberid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //hide_loading();
                String mobile = "";
                String name = "";
                String ref = "";
                String email = "";
                try {
                    mobile = response.getString("member_mobile");
                    name = response.getString("member_name");
                    ref = response.getString("member_ref");
                    email = response.getString("member_email");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(!TextUtils.isEmpty(name) && name.length() > 0)
                tv_name.setText(name);

                if(!TextUtils.isEmpty(ref) && ref.length() > 0)
                tv_referal.setText(ref);

                if(!TextUtils.isEmpty(mobile) && ref.length() > 0)
                tv_mobile.setText(mobile);

                if(!TextUtils.isEmpty(email) && ref.length() > 0)
                tv_email.setText(email);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hide_loading();
                Snackbar.make(v,"error :"+error.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }};
        Server_request.getInstance().addToRequestQueue(jsonObjReq);
    }
    public void load_data_shared_pref(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("shared preference", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user",null);

        if(user != null){
            load_user_info(user);
        }else {
            Snackbar.make(v,"You are not logged in",Snackbar.LENGTH_LONG).show();
        }
    }

    public void loadTaskListFragment() {
        taskListFragment = new TaskListFragment();
        fragmentManager =getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, taskListFragment);
        fragmentTransaction.addToBackStack("Task fragment");
        fragmentTransaction.commit();
    }
    public void loadTaskFragment() {
        Bundle bundle=new Bundle();
        bundle.putString("taskno",TaskFragment.taskListNo);
        taskFragment = new TaskFragment();
        taskFragment.setArguments(bundle);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment,taskFragment);
        fragmentTransaction.addToBackStack("UserProfileFragment");
        fragmentTransaction.commit();
    }
    public void loadProfileFragment() {
        profileFragment = new Profile_Fragment();
        fragmentManager =getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, profileFragment);
        fragmentTransaction.addToBackStack("profile fragment");
        fragmentTransaction.commit();
    }
    public void load_Referal_Fragment() {
        referalFragment = new ReferalFragment();
        fragmentManager =getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, referalFragment);
        fragmentTransaction.addToBackStack("referal fragment");
        fragmentTransaction.commit();
    }
    public void load_Widthdraw_Fragment() {
        widthdrawFragment = new WidthDrawFragment();
        fragmentManager =getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, widthdrawFragment);
        fragmentTransaction.addToBackStack("widthdraw fragment");
        fragmentTransaction.commit();
    }
    public void load_Contact_Fragment() {
        contactFragment = new ContactUsFragment();
        fragmentManager =getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, contactFragment);
        fragmentTransaction.addToBackStack("contact fragment");
        fragmentTransaction.commit();
    }
    public void load_Balance_Fragment() {
        balanceFragment = new BalanceFragment();
        fragmentManager =getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, balanceFragment);
        fragmentTransaction.addToBackStack("balance fragment");
        fragmentTransaction.commit();
    }

    public void detect_vpn(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            Network[] networks = new Network[0];

            networks = cm.getAllNetworks();

            for (int i = 0; i < networks.length; i++) {

                NetworkCapabilities caps = cm.getNetworkCapabilities(networks[i]);
                if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    this.finish();
                }

            }
        }
        try {
            for( NetworkInterface intf : Collections.list(NetworkInterface.getNetworkInterfaces())) {

                // Pass over dormant interfaces
                if(!intf.isUp() || intf.getInterfaceAddresses().size() == 0)
                    continue;

                if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())){
                    this.finish();
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public void logout_close(){
        Snackbar snackbar = Snackbar
                .make(v, "Sure want to logout", Snackbar.LENGTH_LONG)
                .setAction("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SharedPreferences sharedpref = getApplicationContext().getSharedPreferences("shared preference", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpref.edit();
                        editor.remove("mobile");
                        editor.remove("password");
                        editor.apply();
                        finish();
                    }
                });

        snackbar.show();
    }

}
