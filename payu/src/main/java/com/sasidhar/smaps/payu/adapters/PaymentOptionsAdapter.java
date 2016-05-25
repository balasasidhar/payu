package com.sasidhar.smaps.payu.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sasidhar.smaps.payu.R;
import com.sasidhar.smaps.payu.models.PaymentOptionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SASi on 24-May-16.
 */
public class PaymentOptionsAdapter extends RecyclerView.Adapter<PaymentOptionsAdapter.PaymentOptionViewHolder> {

    private List<PaymentOptionModel> paymentOptionModels;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnRecyclerItemClickListener recyclerItemClickListener;

    public PaymentOptionsAdapter(Context context, List<PaymentOptionModel> optionModels) {
        this.context = context;
        this.paymentOptionModels = new ArrayList<>();
        this.paymentOptionModels.addAll(optionModels);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public PaymentOptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.payment_option_card, parent, false);
        return new PaymentOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PaymentOptionViewHolder holder, int position) {
        PaymentOptionModel optionModel = paymentOptionModels.get(position);
        holder.name.setText(optionModel.getName());
        holder.icon.setImageResource(optionModel.getIcon());
    }

    @Override
    public int getItemCount() {
        return paymentOptionModels.size();
    }

    public class PaymentOptionViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;

        public PaymentOptionViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.imageView);
            name = (TextView) itemView.findViewById(R.id.textViewName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerItemClickListener.onRecyclerItemClicked(view, getAdapterPosition());
                }
            });
        }
    }

    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClicked(View view, int position);
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }
}
