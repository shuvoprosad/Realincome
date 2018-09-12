package realincome.prosad.shuvo.realincome.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

public class Profile_Fragment extends Fragment {

    private String url = "http://www.stardesignbd.com/demo/apps/include/show-user.php";
    private String posturl = "http://www.stardesignbd.com/demo/apps/include/edt-usr-prf.php";
    private EditText nameET,emailET,refET,mobileET,passET,cpassET;
    private String name, mobile, password, email, country, repeatPassword,referal,memberID;
    private Spinner spinner;
    private View v;
    private Button edit_btn,save_btn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        v = (View) getActivity().findViewById(R.id.drawer_layout);
        nameET = (EditText) getActivity().findViewById(R.id.edittext_name);
        mobileET = (EditText) getActivity().findViewById(R.id.edittext_mobile);
        refET = (EditText) getActivity().findViewById(R.id.edittext_referalcode);
        emailET = (EditText) getActivity().findViewById(R.id.edittext_email);
        passET = (EditText) getActivity().findViewById(R.id.edittext_password);
        cpassET = (EditText) getActivity().findViewById(R.id.edittext_confirmpassword);

        spinner = (Spinner) getActivity().findViewById(R.id.spinner_country);

        edit_btn = (Button) getActivity().findViewById(R.id.profile_btn_edit);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enable_editing();
            }
        });

        save_btn = (Button) getActivity().findViewById(R.id.profile_btn_save);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiCall();
                disable_editing();
            }
        });
        disable_editing();
        load_data_shared_pref();
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
                    nameET.setText(name);

                if(!TextUtils.isEmpty(mobile) && ref.length() > 0)
                    mobileET.setText(mobile);

                if(!TextUtils.isEmpty(email) && ref.length() > 0)
                    emailET.setText(email);

                if(!TextUtils.isEmpty(ref) && ref.length() > 0)
                    refET.setText(ref);
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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preference", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user",null);

        if(user != null){
            load_user_info(user);
        }else {
            Snackbar.make(v,"You are not logged in",Snackbar.LENGTH_LONG).show();
        }
    }

    public void apiCall() {
        name = nameET.getText().toString();
        mobile = mobileET.getText().toString();
        password = passET.getText().toString();
        repeatPassword = cpassET.getText().toString();
        email = emailET.getText().toString();
        country = spinner.getSelectedItem().toString();
        referal = refET.getText().toString();
        load_login_info();

        JSONObject js = new JSONObject();
        try {
            js.put("name",name);
            js.put("mobile",mobile);
            js.put("password",password);
            js.put("email",email);
            js.put("country",country);
            js.put("referal",referal);
            js.put("memberID",memberID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, posturl,js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String s ="";
                try {
                    s = response.getString("message");
                    Snackbar.make(v,s,Snackbar.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    public void load_login_info(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preference",Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user",null);

        if( user != null) {
           memberID = user;
        }
    }

    private void enable_editing(){
        nameET.setEnabled(true);
        emailET.setEnabled(true);
        passET.setVisibility(View.VISIBLE);
        cpassET.setVisibility(View.VISIBLE);
        save_btn.setVisibility(View.VISIBLE);
        edit_btn.setVisibility(View.GONE);
    }

    private void disable_editing(){
        nameET.setEnabled(false);
        mobileET.setEnabled(false);
        emailET.setEnabled(false);
        refET.setEnabled(false);
        passET.setVisibility(View.GONE);
        cpassET.setVisibility(View.GONE);
        save_btn.setVisibility(View.GONE);
        edit_btn.setVisibility(View.VISIBLE);

    }

}
