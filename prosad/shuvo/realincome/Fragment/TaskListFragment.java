package realincome.prosad.shuvo.realincome.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import realincome.prosad.shuvo.realincome.Adapter.TaskListAdapter;
import realincome.prosad.shuvo.realincome.Connection.Server_request;
import realincome.prosad.shuvo.realincome.Model.CheckListModel;
import realincome.prosad.shuvo.realincome.R;
import realincome.prosad.shuvo.realincome.RecyclerItemClickListener;

public class TaskListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<CheckListModel> dataset;
    private View v;
    public FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;
    private TaskFragment taskFragment;
    private ProgressBar taskListProgressbar;
    private SwipeRefreshLayout tasklist_swipe_refresh;

    private String url = "http://www.stardesignbd.com/demo/apps/include/task-list.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        tasklist_swipe_refresh = (SwipeRefreshLayout) getActivity().findViewById(R.id.tasklist_swiperefresh);
//        tasklist_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                loadData();
//                if(tasklist_swipe_refresh.isRefreshing())
//                tasklist_swipe_refresh.setRefreshing(false);
//            }
//        });

        mRecyclerView = (RecyclerView)getActivity().findViewById(R.id.my_recycler_view);
        v = (View) getActivity().findViewById(R.id.drawer_layout);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        taskListProgressbar = (ProgressBar) getActivity().findViewById(R.id.progressBar_taskList);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        dataset = new ArrayList<>();
        // specify an adapter (see also next example)
        mAdapter = new TaskListAdapter(dataset);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {

                    @Override public void onItemClick(View view, int position) {
                        String res = dataset.get(position).getMessage();
                        if (res.equals("1")){
                            load_task_fragment(dataset.get(position).getNo());
                        }
                        else {
                            Snackbar.make(v,"your task is incomplete",Snackbar.LENGTH_LONG).show();
                        }
                    }
                    @Override public void onLongItemClick(View view, final int position) {
                        // do whatever
                    }
                })
        );

        loadData();
    }

    public void load_list_api(String userid){
        show_loading();
        JSONArray js = new JSONArray();
        try {
            js.put(0,userid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.POST, url,js, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                dataset.clear();
                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        CheckListModel model = new CheckListModel();
                        model.setNo(obj.getString("item_code"));
                        model.setTask(obj.getString("item_name"));
                        model.setMessage(obj.getString("message"));
                        dataset.add(model);
                        mAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(v,"json parse error",Snackbar.LENGTH_LONG).show();
                        hide_loading();
                    }
                    hide_loading();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getName(), "error :"+error.getMessage());
                hide_loading();
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

    public void loadData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preference",Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user",null);

        if(user != null){
            load_list_api(user);
        }else {
            Snackbar.make(v,"You are not logged in",Snackbar.LENGTH_LONG).show();
        }
    }

    public  void load_task_fragment(String taskno){
        Bundle bundle=new Bundle();
        bundle.putString("taskno",taskno);
        taskFragment = new TaskFragment();
        taskFragment.setArguments(bundle);
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment,taskFragment);
        fragmentTransaction.addToBackStack("TaskListFragment");
        fragmentTransaction.commit();
    }
    public void show_loading(){
        taskListProgressbar.setVisibility(View.VISIBLE);
    }

    public void hide_loading(){
        taskListProgressbar.setVisibility(View.GONE);
    }
}
