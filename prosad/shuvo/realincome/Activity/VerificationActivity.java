package realincome.prosad.shuvo.realincome.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import realincome.prosad.shuvo.realincome.R;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class VerificationActivity extends AppCompatActivity {

    private String url = "http://www.stardesignbd.com/demo/apps/include/cde-chck.php";
    private String imei;
    private String verifyCode;
    private View v;
    private View verifyLO, congractsLO;
    private Button verifyBtn;
    private Button gotologinBtn;
    private EditText verifyEdittext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        v = (View) findViewById(R.id.verification_activity);
        verifyLO = (View) findViewById(R.id.verifyLO);
        congractsLO = (View) findViewById(R.id.congractsLO);
        verifyBtn = (Button) findViewById(R.id.btn_verify);
        gotologinBtn = (Button) findViewById(R.id.btn_goto_login);
        verifyEdittext = (EditText) findViewById(R.id.verifyET);

        gotologinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                String message = "Shuvo";
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_api();
            }
        });

        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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
                Snackbar.make(v,"This permission is needed for the registration process",Snackbar.LENGTH_LONG).show();
            } else {
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        200);
            }
        } else {
            // Permission has already been granted
            imei = telephonyManager.getDeviceId();
        }
        if (TextUtils.isEmpty(imei)){
            Snackbar.make(v,"Unable to retrive IMEI number",Snackbar.LENGTH_LONG).show();
            finish();
            System.exit(0);
        }

    }

    protected void call_api(){
        verifyCode = verifyEdittext.getText().toString();
        JSONObject js = new JSONObject();
        try {
            js.put("code",verifyCode);
            js.put("imei",imei);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String s ="";
                try {
                    s = response.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (s.equals("1")){
                    verifyLO.setVisibility(View.GONE);
                    congractsLO.setVisibility(View.VISIBLE);
                }else
                Snackbar.make(v,"Something went wrong",Snackbar.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
}
