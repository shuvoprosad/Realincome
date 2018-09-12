package realincome.prosad.shuvo.realincome.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import realincome.prosad.shuvo.realincome.Model.CheckListModel;
import realincome.prosad.shuvo.realincome.Model.ReferaLModel;
import realincome.prosad.shuvo.realincome.R;

public class ReferalAdapter extends RecyclerView.Adapter<ReferalAdapter.ViewHolder> {
private ArrayList<ReferaLModel>  mDataset;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public static class ViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    private TextView nameTextView;
    private TextView noTextView;
    public ViewHolder(View v) {
        super(v);
        nameTextView = (TextView)itemView.findViewById(R.id.ref_name_tv);
        noTextView = (TextView)itemView.findViewById(R.id.ref_no_tv);
    }
}

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReferalAdapter(ArrayList<ReferaLModel> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReferalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.referal_list_item, parent, false);

        ReferalAdapter.ViewHolder vh = new ReferalAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ReferalAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.nameTextView.setText(mDataset.get(position).getName());
        holder.noTextView.setText(mDataset.get(position).getNumber());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
