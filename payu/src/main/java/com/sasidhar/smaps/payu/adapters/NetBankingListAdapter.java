package com.sasidhar.smaps.payu.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.payu.india.Model.PaymentDetails;
import com.sasidhar.smaps.payu.R;

import java.util.ArrayList;

/**
 * Created by SASi on 25-May-16.
 */
public class NetBankingListAdapter extends
        RecyclerView.Adapter<NetBankingListAdapter.NetBankingViewHolder> implements Filterable {

    private Context context;
    private ArrayList<PaymentDetails> netBankingDetails;
    private ArrayList<PaymentDetails> netBankingFilteredList;
    private LayoutInflater layoutInflater;
    private OnRecyclerItemClickListener recyclerItemClickListener;

    public NetBankingListAdapter(Context context, ArrayList<PaymentDetails> netBankingDetails) {
        this.context = context;
        this.netBankingDetails = netBankingDetails;
        this.netBankingFilteredList = new ArrayList<>(netBankingDetails);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public NetBankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.netbanking_card, parent, false);
        return new NetBankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NetBankingViewHolder holder, int position) {
        PaymentDetails details = netBankingFilteredList.get(position);
        holder.textView.setText(details.getBankName());
        holder.textView.setTag(details.getBankCode());
    }

    @Override
    public int getItemCount() {
        return netBankingFilteredList.size();
    }

    public PaymentDetails getItem(int position) {
        return netBankingFilteredList.get(position);
    }

    @Override
    public Filter getFilter() {
        return new NetBankingListFilter(this, this.netBankingDetails);
    }

    class NetBankingListFilter extends Filter {

        private final NetBankingListAdapter netBankingListAdapter;
        private ArrayList<PaymentDetails> paymentDetails;
        private ArrayList<PaymentDetails> filteredList;

        public NetBankingListFilter(NetBankingListAdapter netBankingListAdapter, ArrayList<PaymentDetails> paymentDetails) {
            super();
            this.netBankingListAdapter = netBankingListAdapter;
            this.paymentDetails = paymentDetails;
            filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults filterResults = new FilterResults();

            if (constraint.length() == 0) {
                filteredList.addAll(paymentDetails);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for (final PaymentDetails details : paymentDetails) {
                    if (details.getBankName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(details);
                    }
                }
            }
            filterResults.values = filteredList;
            filterResults.count = filteredList.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            netBankingListAdapter.netBankingFilteredList.clear();
            netBankingListAdapter.netBankingFilteredList.addAll((ArrayList<PaymentDetails>) results.values);
            netBankingListAdapter.notifyDataSetChanged();
        }
    }

    class NetBankingViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public NetBankingViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textViewName);
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
