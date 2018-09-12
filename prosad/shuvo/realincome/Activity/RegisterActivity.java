package realincome.prosad.shuvo.realincome.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
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
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import realincome.prosad.shuvo.realincome.Connection.Server_request;
import realincome.prosad.shuvo.realincome.Fragment.TaskFragment;
import realincome.prosad.shuvo.realincome.R;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final String URL_FOR_REGISTRATION = "http://www.stardesignbd.com/demo/apps/include/register-usr.php";
    private String url = "http://www.stardesignbd.com/demo/apps/include/register-usr.php";
    private EditText signupInputName;
    private EditText signupInputEmail;
    private EditText signupInputPassword;
    private EditText signupInputRepeatPassword;
    private EditText signupInputMobile;
    private EditText signupInputReferal;
    private Button btnSignUp;
    private Spinner spinner;
    private View v;
    private View loginFormView;
    private String name, email, password, repeatPassword, mobile, imei, country, referal, warning;

    protected ProgressBar loading_bar;
    private TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

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
                Snackbar.make(v, "This permission is needed for the registration process", Snackbar.LENGTH_LONG).show();
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

        //Snackbar.make(v,"iemi :"+iemi,Snackbar.LENGTH_LONG).show();


        v = (View) findViewById(R.id.reg_activity);
        loginFormView = (View) findViewById(R.id.form_reg_layout);
        signupInputName = (EditText) findViewById(R.id.signup_input_name);
        signupInputEmail = (EditText) findViewById(R.id.signup_input_email);
        signupInputPassword = (EditText) findViewById(R.id.signup_input_password);
        signupInputMobile = (EditText) findViewById(R.id.signup_input_mobile);
        signupInputReferal = (EditText) findViewById(R.id.signup_input_referal);
        signupInputRepeatPassword = (EditText) findViewById(R.id.signup_input_repeat_password);

        spinner = (Spinner) findViewById(R.id.spinner1);

        loading_bar = (ProgressBar) findViewById(R.id.progressBar_reg);
        loading_bar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.MULTIPLY);

        btnSignUp = (Button) findViewById(R.id.register_btn_signup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {
                    apiCall();
                }
            }
        });

    }

    private boolean validation() {

        name = signupInputName.getText().toString();
        mobile = signupInputMobile.getText().toString();
        password = signupInputPassword.getText().toString();
        repeatPassword = signupInputRepeatPassword.getText().toString();
        email = signupInputEmail.getText().toString();
        country = spinner.getSelectedItem().toString();
        referal = signupInputReferal.getText().toString();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            imei = telephonyManager.getDeviceId();
        }else {
            imei = "";
        }

        if (TextUtils.isEmpty(imei)) {
            Snackbar.make(v, "Unable to retrive IMEI number", Snackbar.LENGTH_LONG).show();
            finish();
            return false;
        }

        if (TextUtils.isEmpty(name)) {
            signupInputName.setError("Required");
            return false;
        }

        if (TextUtils.isEmpty(mobile)) {
            signupInputMobile.setError("Required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            signupInputPassword.setError("Required");
            return false;
        } else if (TextUtils.isEmpty(repeatPassword)) {
            signupInputRepeatPassword.setError("Required");
            return false;
        } else if (!password.equals(repeatPassword)) {
            signupInputPassword.setError("Passwords dosen't match");
            return true;
        } else {
            return true;
        }
    }

    public void apiCall() {
        show_loading();

        JSONObject js = new JSONObject();
        try {
            js.put("name",name);
            js.put("mobile",mobile);
            js.put("password",password);
            js.put("email",email);
            js.put("imei",imei);
            js.put("country",country);
            js.put("referal",referal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hide_loading();
                String s ="";
                try {
                    s = response.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (s.equals("1")){
                    go_to_verification_activity();
                }
                Snackbar.make(v,s,Snackbar.LENGTH_LONG).show();
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

    protected void go_to_verification_activity(){
        Intent intent = new Intent(this, VerificationActivity.class);
        String message = "Shuvo";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    protected void show_loading(){
        loginFormView.setVisibility(View.GONE);
        loading_bar.setVisibility(View.VISIBLE);
        loading_bar.setIndeterminate(true);
    }
    protected void hide_loading(){
        loginFormView.setVisibility(View.VISIBLE);
        loading_bar.setVisibility(View.GONE);
        loading_bar.setIndeterminate(false);
    }
}
