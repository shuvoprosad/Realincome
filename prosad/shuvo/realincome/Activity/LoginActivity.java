package realincome.prosad.shuvo.realincome.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import realincome.prosad.shuvo.realincome.Connection.Server_request;
import realincome.prosad.shuvo.realincome.Fragment.TaskFragment;
import realincome.prosad.shuvo.realincome.Fragment.TaskListFragment;
import realincome.prosad.shuvo.realincome.R;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class LoginActivity extends AppCompatActivity {
    private EditText loginInputMobile, loginInputPassword;
    private String mobile,password,imei;
    private Button btnSignUp;
    private Button btnLogin;
    private String url = "http://www.stardesignbd.com/demo/apps/include/lgin-go.php";
    private View v;
    public FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;
    private TaskListFragment taskListFragment;

    private TelephonyManager telephonyManager;
    protected ProgressBar loading_bar;
    protected View layout_login_form;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
    }

    public void initialize (){
        v = (View) findViewById(R.id.login_activity);
        loginInputMobile = (EditText) findViewById(R.id.login_input_mobile);
        loginInputPassword = (EditText) findViewById(R.id.login_input_password);
        btnLogin = (Button) findViewById(R.id.btn_login_loginpage);
        btnSignUp = (Button) findViewById(R.id.btn_registration_loginpage);

        layout_login_form = (View) findViewById(R.id.layout_login_form);
        loading_bar = (ProgressBar) findViewById(R.id.progressBar_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = loginInputMobile.getText().toString();
                password = loginInputPassword.getText().toString();
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    imei = telephonyManager.getDeviceId();
                }else {
                    imei = "";
                }
                apiCall(mobile,password,imei);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_to_reg_activity();
            }
        });

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(v, "This permission is needed for the login process", Snackbar.LENGTH_LONG).show();
            } else {
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        200);
            }
        }

       load_login_info();
    }

    public void apiCall(final String m,final String p , final String imei) {
        show_loading();

        JSONObject js = new JSONObject();
        try {
            js.put("username",m);
            js.put("password",p);
            js.put("imei",imei);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String s = "";
                String error = "";
                String userID = "";
                try {
                    s = response.getString("messageID");
                    error = response.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(v,"json parse err msg msgid",Snackbar.LENGTH_LONG).show();
                }

                if (s.equals("1")){
                    try {
                        userID = response.getString("memberID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(v,"json parse err userid",Snackbar.LENGTH_LONG).show();
                    }
                    saveData(userID);
                    save_login_info(m,p);
                    go_to_ad_activity();
                }
                else if(s.equals("0")){
                    Snackbar.make(v,error,Snackbar.LENGTH_LONG).show();
                }
                else if(s.equals("3")){
                    Snackbar.make(v,error,Snackbar.LENGTH_LONG).show();
                }
                hide_loading();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hide_loading();
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

    private void go_to_ad_activity(){

        Intent intent = new Intent(this, Navigation_drawer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String message = "Shuvo";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
        finish();

    }

    private void go_to_reg_activity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        String message = "Shuvo";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void saveData(String user){

        SharedPreferences sharedpref = this.getSharedPreferences("shared preference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putString("user",user);
        editor.apply();
    }

    public void save_login_info(String mobile , String password){

        SharedPreferences sharedpref = this.getSharedPreferences("shared preference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putString("mobile",mobile);
        editor.putString("password",password);
        editor.apply();
    }

    public void load_login_info(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("shared preference",Context.MODE_PRIVATE);
        String mobile = sharedPreferences.getString("mobile",null);
        String password = sharedPreferences.getString("password",null);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            imei = telephonyManager.getDeviceId();
        }else {
            imei = "";
        }


        if(mobile != null && password != null && !imei.isEmpty()) {
            apiCall(mobile,password,imei);
        }
    }

    protected void show_loading(){
        layout_login_form.setVisibility(View.GONE);
        loading_bar.setVisibility(View.VISIBLE);
        loading_bar.setIndeterminate(true);
    }
    protected void hide_loading(){
        layout_login_form.setVisibility(View.VISIBLE);
        loading_bar.setVisibility(View.GONE);
        loading_bar.setIndeterminate(false);
    }

}
