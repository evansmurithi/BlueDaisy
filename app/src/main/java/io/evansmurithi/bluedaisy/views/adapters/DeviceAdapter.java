package io.evansmurithi.bluedaisy.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.evansmurithi.bluedaisy.R;

/**
 * Created by evans on 3/4/17.
 *
 * DeviceAdapter class.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceVH> {

    private static final String TAG = "DeviceAdapter";

    static class DeviceVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_device_name)
        TextView deviceName;
        @BindView(R.id.text_device_address)
        TextView deviceAddress;

        final DeviceAdapter adapter;

        DeviceVH(View itemView, DeviceAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (adapter.mCallback == null) {
                return;
            }

            adapter.mCallback.onItemClicked(getAdapterPosition());
        }
    }

    public interface Callback {
        void onItemClicked(int index);
    }

    private final List<Map<String, String>> mPairedDevices;
    private Callback mCallback;

    public DeviceAdapter(List<Map<String, String>> pairedDevices) {
        this.mPairedDevices = pairedDevices;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public DeviceVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_device, parent, false);

        return new DeviceVH(view, this);
    }

    @Override
    public void onBindViewHolder(DeviceVH holder, int position) {
        holder.deviceName.setText(mPairedDevices.get(position).get("device_name"));
        holder.deviceAddress.setText(mPairedDevices.get(position).get("device_address"));
    }

    @Override
    public int getItemCount() {
        return mPairedDevices.size();
    }

    public void add(int position, Map<String, String> device) {
        mPairedDevices.add(position, device);
        notifyItemInserted(position);
    }
}
