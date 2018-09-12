package realincome.prosad.shuvo.realincome.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import realincome.prosad.shuvo.realincome.Activity.CompleteActivity;
import realincome.prosad.shuvo.realincome.Adapter.TaskGridViewAdapter;
import realincome.prosad.shuvo.realincome.Connection.Server_request;
import realincome.prosad.shuvo.realincome.Model.CheckTaskModel;
import realincome.prosad.shuvo.realincome.R;
import realincome.prosad.shuvo.realincome.RecyclerItemClickListener;


public class TaskFragment extends Fragment {
    public static View v;
    private static Context activity;
    private RecyclerView task_grid;
    private static ArrayList<CheckTaskModel> task_list;
    private static RecyclerView.Adapter task_grid_adapter;

    public static String item_clicked;
    private TaskFragment taskFragment;
    private InterstitialAd mPublisherInterstitialAd;
    private InterstitialAd mPublisherInterstitialAd2;
    private InterstitialAd mPublisherInterstitialAd5;

    private String url ="http://www.stardesignbd.com/demo/apps/include/grd-tsk-lst.php";
    private static String taskurl ="http://www.stardesignbd.com/demo/apps/include/pass-chck-tsk.php";
    private String adclickurl ="http://www.stardesignbd.com/demo/apps/include/blck-usrs-clck.php";
    private String cross_image_url ="http://www.clker.com/cliparts/W/M/1/A/L/k/incomplete-symbol-hi.png";
    private String check_image_url ="https://cdn3.iconfinder.com/data/icons/flat-actions-icons-9/792/Tick_Mark_Circle-512.png";
    public static String taskListNo="";

    private SwipeRefreshLayout task_swipe_refresh;
    public static Handler handler;
    public static Runnable runnable;
    private Context appContext;

    public String adId;
    public String clickAdId;
    public static int i = 0;

    final private int delay = 2000;

    public static Boolean Ad5th = false;
    private ProgressBar today_progressbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        taskListNo = getArguments().getString("taskno");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        v = (View) getActivity().findViewById(R.id.drawer_layout);
        appContext = getActivity().getApplicationContext();

        task_swipe_refresh = (SwipeRefreshLayout) getActivity().findViewById(R.id.task_swiperefresh);
        task_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
                if(task_swipe_refresh.isRefreshing())
                task_swipe_refresh.setRefreshing(false);
            }
        });

        today_progressbar = (ProgressBar) getActivity().findViewById(R.id.progressBar_todays_task);
        task_list = new ArrayList<>();
        task_grid = (RecyclerView) getActivity().findViewById(R.id.task_gridview);
        task_grid_adapter = new TaskGridViewAdapter(getActivity(), task_list);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        task_grid.setLayoutManager(gridLayoutManager);
        task_grid.setHasFixedSize(true);
        task_grid.setItemAnimator(new DefaultItemAnimator());
        task_grid.setAdapter(task_grid_adapter);
        task_grid.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), task_grid ,new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                show_loading();

                String res = task_list.get(position).getComplete();
                if(position == 20 && check_arraylist()){
                    position++;
                    item_clicked = String.valueOf(position);
                    show_5th_ad();
                }
                else if(position == 20 && !check_arraylist()){
                    Snackbar.make(v,"complete all 20 tasks",Snackbar.LENGTH_LONG).show();
                }
                else if (res.equals("2") && position != 20){
                    position++;
                    item_clicked = String.valueOf(position);
                    show_first_ad();
                }
                else if (res.equals("1")){
                    Snackbar.make(v,"this task is complete",Snackbar.LENGTH_LONG).show();
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        hide_loading();
                    }
                }, delay);

            }
            @Override
            public void onLongItemClick(View view, int position) {

            }
        } ));
        activity = getActivity();
        MobileAds.initialize(getActivity(),getResources().getString(R.string.apid));
        adId = getResources().getString(R.string.interstialadid);
        clickAdId = getResources().getString(R.string.clickadid);
        prepare_ads();
        loadData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        prepare_ads();
        if(Ad5th){
           pass_ad_click();
        }
    }

    public void prepare_ads(){

        mPublisherInterstitialAd = new InterstitialAd(getContext());
        mPublisherInterstitialAd.setAdUnitId(adId);
        mPublisherInterstitialAd.loadAd(new AdRequest.Builder().build());
        mPublisherInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdOpened() {
                super.onAdOpened();
                show_2nd_ad();
                Toast.makeText(getActivity(), "DO not click on ad",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                pass_ad_click();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                pass_ad_click();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d("TAG", "The interstitial wasn't loaded yet.");
                hide_loading();
            }
        });
        mPublisherInterstitialAd2 = new InterstitialAd(getContext());
        mPublisherInterstitialAd2.setAdUnitId(adId);
        mPublisherInterstitialAd2.loadAd(new AdRequest.Builder().build());
        mPublisherInterstitialAd2.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                pass_ad_click();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                pass_ad_click();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d("TAG", "The interstitial wasn't loaded yet.");
                hide_loading();
            }
        });
        mPublisherInterstitialAd5 = new InterstitialAd(getContext());
        mPublisherInterstitialAd5.setAdUnitId(clickAdId);
        mPublisherInterstitialAd5.loadAd(new AdRequest.Builder().build());
        mPublisherInterstitialAd5.setAdListener(new AdListener(){
            @Override
            public void onAdOpened() {
                super.onAdOpened();

            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();

            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                handler.removeCallbacks(runnable);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d("TAG", "The interstitial wasn't loaded yet.");
                hide_loading();
            }
        });

    }

    public void show_ad_toast(String msg){
        Toast.makeText(getActivity(), "Ad number: "+msg,Toast.LENGTH_SHORT).show();
    }

    public void show_first_ad(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (mPublisherInterstitialAd.isLoaded()) {
                    mPublisherInterstitialAd.show();
                    show_ad_toast("1");
                    hide_loading();
                }
                else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        }, delay);
    }

    public void show_2nd_ad(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (mPublisherInterstitialAd2.isLoaded()) {
                    mPublisherInterstitialAd2.show();
                    show_ad_toast("2");
                    load_Complete_Activity();

                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        }, delay);

    }

    public void show_5th_ad(){
        Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            public void run() {
                if (mPublisherInterstitialAd5.isLoaded()) {
                    Ad5th = true;
                    mPublisherInterstitialAd5.show();
                    Toast.makeText(getActivity(), "Please click on the Ad",Toast.LENGTH_LONG).show();
                    hide_loading();
                    load2_Complete_Activity();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        }, delay);
    }

    public void load2_Complete_Activity(){
        handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                Ad5th = false;
                pass_task_complete();
                taskFragment = new TaskFragment();
                taskFragment = new TaskFragment();
                Bundle bundle = new Bundle();
                bundle.putString("taskno",taskListNo);
                taskFragment.setArguments(bundle);
                Intent intent = new Intent(getActivity(), CompleteActivity.class);
                startActivity(intent);
            }
        }, 70000);
    }

    public void load_Complete_Activity(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                pass_task_complete();
                taskFragment = new TaskFragment();
                Bundle bundle = new Bundle();
                bundle.putString("taskno",taskListNo);
                taskFragment.setArguments(bundle);
                Intent intent = new Intent(getActivity(), CompleteActivity.class);
                startActivity(intent);
            }
        }, delay);
    }

    public static void pass_task_complete(){
        JSONObject js = new JSONObject();
        try {
            js.put("memberID",loadData(0));
            js.put("tasklistID",taskListNo);
            js.put("taskgridID",item_clicked);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, taskurl,js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String error = "";
                try {
                    error = response.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(v,"json parse err msg msgid",Snackbar.LENGTH_LONG).show();
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

    public void pass_ad_click(){

        JSONObject js = new JSONObject();
        try {
            js.put("memberID",loadData(0));
            js.put("tasklistID",taskListNo);
            js.put("taskgridID",item_clicked);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, adclickurl,js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String error = "";
                try {
                    error = response.getString("message");
                    Snackbar.make(v,error,Snackbar.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"json parse err msg msgid",
                            Toast.LENGTH_LONG).show();
                    Snackbar.make(v,"json parse err msg msgid",Snackbar.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error :"+error.getMessage(),
                        Toast.LENGTH_LONG).show();
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
        logout_close();
    }

    public void logout_close(){
        SharedPreferences sharedpref = getActivity().getSharedPreferences("shared preference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.remove("mobile");
        editor.remove("password");
        editor.apply();
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getActivity().finishAffinity();
            }
            else {
                getActivity().moveTaskToBack(true);
                getActivity().finish();
            }
        }
        catch (Exception e){
            Log.d(getClass().getName(), "error : getActivity().moveTaskToBack(true);");
        }

    }

    public void load_list_api(String userid){
        show_loading();
        JSONArray js = new JSONArray();
        try {
            js.put(0,userid);
            js.put(1,taskListNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.POST, url,js, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                task_list.clear();
                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        CheckTaskModel model = new CheckTaskModel();
                        model.setTask_name(obj.getString("title"));
                        model.setTask_id(obj.getString("tasklistID"));
                        model.setComplete(obj.getString("message"));
                        if(model.getComplete().equals("2")){
                            model.setTask_image(cross_image_url);
                        }else{
                            model.setTask_image(check_image_url);
                        }

                        task_list.add(model);
                        task_grid_adapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(v,"json parse error",Snackbar.LENGTH_LONG).show();
                        hide_loading();
                    }

                }
                hide_loading();
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

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preference", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user",null);

        if(user != null){
            load_list_api(user);
        }else {
            Snackbar.make(v,"You are not logged in",Snackbar.LENGTH_LONG).show();
        }
    }

    public static String loadData(int getuserid){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("shared preference", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user",null);

        if(user != null){
            return user;
        }else {
            Snackbar.make(v,"You are not logged in",Snackbar.LENGTH_LONG).show();
            return  "";
        }
    }

    public void show_loading(){
        today_progressbar.setVisibility(View.VISIBLE);
    }

    public void hide_loading(){
        today_progressbar.setVisibility(View.GONE);
    }

    public Boolean check_arraylist(){
        int count = 0;
        if (!task_list.isEmpty()){
            for (int i = 0; i < task_list.size(); i++){
                String x = task_list.get(i).getComplete();
                if(x.equals("1")) {
                    count++;
                }
            }
        }

        if(count > 19){
            return true;
        }
        else {
            return false;
        }
    }

}
