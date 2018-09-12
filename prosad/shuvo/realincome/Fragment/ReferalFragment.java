package realincome.prosad.shuvo.realincome.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import realincome.prosad.shuvo.realincome.Adapter.ReferalAdapter;
import realincome.prosad.shuvo.realincome.Adapter.TaskListAdapter;
import realincome.prosad.shuvo.realincome.Connection.Server_request;
import realincome.prosad.shuvo.realincome.Model.CheckListModel;
import realincome.prosad.shuvo.realincome.Model.ReferaLModel;
import realincome.prosad.shuvo.realincome.R;
import realincome.prosad.shuvo.realincome.RecyclerItemClickListener;

public class ReferalFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ReferaLModel> dataset;
    private View v;
    private String url = "http://www.stardesignbd.com/demo/apps/include/reflist.php";
    private ProgressBar referal_Progressbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_referal, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = (RecyclerView)getActivity().findViewById(R.id.referal_listview);
        v = (View) getActivity().findViewById(R.id.drawer_layout);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        referal_Progressbar = (ProgressBar) getActivity().findViewById(R.id.referal_progressbar);
        referal_Progressbar.getProgressDrawable().setColorFilter( Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);

        dataset = new ArrayList<>();
        // specify an adapter (see also next example)
        mAdapter = new ReferalAdapter(dataset);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                    }

                    @Override public void onLongItemClick(View view, final int position) {
                        // do whatever

                    }
                })
        );

        loadData();

    }
    public void loadData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preference",Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user",null);

        if(user != null){
            load_referal_list(user);
        }else {
            Snackbar.make(v,"You are not logged in",Snackbar.LENGTH_LONG).show();
        }
    }
    public void load_referal_list(String userid){
        referal_Progressbar.setVisibility(View.VISIBLE);
        JSONArray js = new JSONArray();
        try {
            js.put(0,userid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.POST, url,js, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        ReferaLModel model = new ReferaLModel();
                        model.setName(obj.getString("name"));
                        model.setNumber(obj.getString("mobile"));
                        dataset.add(model);
                        mAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(v,"json parse error",Snackbar.LENGTH_LONG).show();

                    }
                    referal_Progressbar.setVisibility(View.GONE);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                referal_Progressbar.setVisibility(View.GONE);
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
