package project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shokoofeadeli.iranianmapapi.R;

import java.util.List;

import ir.map.servicesdk.model.inner.SearchItem;

public class AddressRecyclerAdapter extends RecyclerView.Adapter<AddressRecyclerAdapter.ViewHolder> {
    List<SearchItem> searchItems;
    Context context;
    AddressListener addressListener;

    public AddressRecyclerAdapter(List<SearchItem> searchItems, Context context, AddressListener addressListener) {
        this.searchItems = searchItems;
        this.context = context;
        this.addressListener = addressListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String address = searchItems.get(position).getTitle() + " \n " +
                searchItems.get(position).getAddress();
        holder.txtAddress.setText(address);
        holder.txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double latitude = searchItems.get(position).getGeom().getLatitude();
                double longitude = searchItems.get(position).getGeom().getLongitude();
                addressListener.onResponseAddress(latitude,longitude);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewGroup root;
        TextView txtAddress;

        public ViewHolder(View view) {
            super(view);
            root = (ViewGroup) view;
            txtAddress = view.findViewById(R.id.txtAddress);
        }
    }
}
