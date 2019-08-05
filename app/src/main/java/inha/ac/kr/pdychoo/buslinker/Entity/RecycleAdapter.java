package inha.ac.kr.pdychoo.buslinker.Entity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import inha.ac.kr.pdychoo.buslinker.R;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
    Context context;
    List<Deal> dealList;

    public RecycleAdapter(Context context, List<Deal> dealList) {
        this.context = context;
        this.dealList = dealList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_deal, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Deal deal = dealList.get(i);
        viewHolder.startTmnTV.setText("출발: " + deal.getStartTmn());
        String[] endTmnList = deal.getEndTmn().split(";;");
        String endTmn = "도착: ";
        for (String tmn : endTmnList)
            endTmn += tmn + "\n";
        endTmn = endTmn.substring(0, endTmn.length() - 1);
        endTmn = endTmn.replace("\n", "\n         ");
        viewHolder.endTmnTV.setText(endTmn);
        String divTime = "배송시간: ";
        String[] divTimeList = deal.getDivTime().split(";;");
        for (String time : divTimeList)
            divTime += time + "\n";
        divTime = divTime.substring(0, divTime.length() - 1);
        divTime = divTime.replace("\n", "\n                 ");
        viewHolder.divTimeTV.setText(divTime);

        viewHolder.paydateTV.setText("배송일자: " + deal.getPaydate().substring(0, 10));

        int status=deal.getStatus();
        String statusStr="배송준비중";
        switch (status){
            case 0:
                statusStr="배송준비중";
                break;
            case 1:
                statusStr="배송중";
                break;
            case 2: statusStr="배송완료";
            break;
        }
        viewHolder.statusTV.setText("상태: "+statusStr);
        viewHolder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("Deal", deal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dealList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView startTmnTV, endTmnTV, divTimeTV, paydateTV, statusTV;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            paydateTV = itemView.findViewById(R.id.paydateTV);
            startTmnTV = itemView.findViewById(R.id.startTmnTV);
            endTmnTV = itemView.findViewById(R.id.endTmnTV);
            divTimeTV = itemView.findViewById(R.id.divTimeTV);
            cardview = itemView.findViewById(R.id.cardview);
            statusTV = itemView.findViewById(R.id.statusTV);
        }
    }
}
