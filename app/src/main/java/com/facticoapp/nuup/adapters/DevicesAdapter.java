package com.facticoapp.nuup.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facticoapp.nuup.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edgar Z. on 6/20/16.
 */

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ItemViewHolder> {
    public static final String TAG = DevicesAdapter.class.getName();

    private Context context;
    private List<BluetoothDevice> items = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    protected class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public TextView address;

        public ItemViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.device_name);
            address = (TextView) view.findViewById(R.id.device_address);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public DevicesAdapter(Context context) {
        this.context = context;
    }

    public DevicesAdapter(Context context, List<BluetoothDevice> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        BluetoothDevice item = items.get(position);

        if (item == null)
            return;

        String name = (!TextUtils.isEmpty(item.getName())) ? item.getName() : context.getString(R.string.unknown_device);
        String address = (!TextUtils.isEmpty(item.getAddress())) ? item.getAddress() : "";

        holder.name.setText(name);
        holder.address.setText(address);
    }

    public void removeItems() {
        if (this.items == null)
            return;

        int startIndex = 0;
        int endIndex = this.items.size() > 0 ? this.items.size() - 1 : 0;

        this.items.clear();
        notifyItemRangeRemoved(startIndex, endIndex);
        notifyItemRangeChanged(startIndex, endIndex);
    }

    @Override
    public int getItemCount() {
        if (this.items == null)
            return 0;

        return this.items.size();
    }

    public BluetoothDevice getItem(int position) {
        if (this.items == null)
            return null;

        return this.items.get(position);
    }

    public void addItem(BluetoothDevice item) {
        this.items.add(item);
        notifyItemInserted(this.items.size() > 0 ? this.items.size() - 1 : 0);
    }

    public void addItem(BluetoothDevice item, int position) {
        this.items.add(position, item);
        notifyItemInserted(position);
    }

    public void addItems(List<BluetoothDevice> items) {
        this.items.addAll(items);
        int startIndex = this.items.size() > 0 ? this.items.size() - 1 : this.items.size();
        notifyItemRangeInserted(startIndex, startIndex + items.size());
        notifyItemRangeChanged(startIndex, startIndex + items.size());
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null)
            this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
