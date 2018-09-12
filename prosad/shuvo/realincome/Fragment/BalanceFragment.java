package realincome.prosad.shuvo.realincome.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

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


public class BalanceFragment extends Fragment {

    private  String url = "http://www.stardesignbd.com/demo/apps/include/show-blance.php";
    private String memberID;
    private View v;
    private TextView bal_vailable_blance,bal_ttl_payout,bal_ttl_income,bal_ref_income,bal_mainincome,bal_self_income,bal_down_income;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_balance, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        v = (View) getActivity().findViewById(R.id.drawer_layout);
        bal_mainincome = (TextView) getActivity().findViewById(R.id.bal_mainincome);
        bal_ref_income = (TextView) getActivity().findViewById(R.id.bal_ref_income);
        bal_ttl_income = (TextView) getActivity().findViewById(R.id.bal_ttl_income);
        bal_ttl_payout = (TextView) getActivity().findViewById(R.id.bal_ttl_payout);
        bal_vailable_blance = (TextView) getActivity().findViewById(R.id.bal_vailable_blance);
        bal_self_income = (TextView) getActivity().findViewById(R.id.bal_self_income);
        bal_down_income = (TextView) getActivity().findViewById(R.id.bal_down_income);

        apiCall();

    }

    public void apiCall() {
        load_login_info();

        JSONObject js = new JSONObject();
        try {
            js.put("memberID",memberID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    bal_mainincome.setText(response.getString("main_income"));
                    bal_ref_income.setText(response.getString("ref_income"));
                    bal_ttl_income.setText(response.getString("ttl_income"));
                    bal_ttl_payout.setText(response.getString("ttl_payout"));
                    bal_vailable_blance.setText(response.getString("vailable_blance"));
                    bal_self_income.setText(response.getString("today_income"));
                    bal_down_income.setText(response.getString("today_down_income"));
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
}
