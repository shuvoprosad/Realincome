package realincome.prosad.shuvo.realincome.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import realincome.prosad.shuvo.realincome.Model.CheckTaskModel;
import realincome.prosad.shuvo.realincome.R;

public class TaskGridViewAdapter extends RecyclerView.Adapter<TaskGridViewAdapter.MyViewHolder>  {
    private Context c;
    private ArrayList<CheckTaskModel> TaskList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name_tv;
        ImageView image_niv;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.name_tv = (TextView)itemView.findViewById(R.id.product_name);
            this.image_niv = (ImageView) itemView.findViewById(R.id.product_image);
        }
    }

    public TaskGridViewAdapter(Context c, ArrayList<CheckTaskModel> TaskList) {
        this.c = c;
        this.TaskList = TaskList;
    }

    @Override
    public TaskGridViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);

        TaskGridViewAdapter.MyViewHolder myViewHolder = new TaskGridViewAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(TaskGridViewAdapter.MyViewHolder holder, int position) {
        TextView name_tvbh = holder.name_tv;

        name_tvbh.setText(TaskList.get(position).getTask_name());

        Glide.with(c)
                .load(TaskList.get(position).getTask_image())
                .centerCrop()
                .placeholder(R.drawable.loading)
                .crossFade()
                .into(holder.image_niv);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return TaskList.size();
    }
}
