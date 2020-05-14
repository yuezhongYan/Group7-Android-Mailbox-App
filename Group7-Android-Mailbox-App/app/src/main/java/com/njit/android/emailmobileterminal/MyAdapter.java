package com.njit.android.emailmobileterminal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.njit.android.emailmobileterminal.bean.Email;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private List<Email> datas;
    private Context context;
    private int normalType = 0;
    private int footType = 1;
    private boolean hasMore = true;
    private boolean fadeTips = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public MyAdapter(List<Email> datas, Context context, boolean hasMore) {
        this.datas = datas;
        this.context = context;
        this.hasMore = hasMore;
    }
    public  interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , Object data);
    }
    @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == normalType) {
            View viewTmp = LayoutInflater.from(context).inflate(R.layout.email_list, null);
            NormalHolder normalHolder = new NormalHolder(viewTmp);
            viewTmp.setOnClickListener(this);
            return normalHolder;
        } else {
            return new FootHolder(LayoutInflater.from(context).inflate(R.layout.footview, null));
        }
    }
    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view,view.getTag());
        }
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalHolder) {
            if(datas.get(position)!=null){
                ((NormalHolder) holder).addresserTV.setText(datas.get(position).getAddresser());
                ((NormalHolder) holder).subjectTV.setText(datas.get(position).getSubject());
                ((NormalHolder) holder).dateTV.setText(datas.get(position).getDate());
                holder.itemView.setTag(position);
            }
        } else {
            ((FootHolder) holder).tips.setVisibility(View.VISIBLE);
            if (hasMore == true) {
                fadeTips = false;
                if (datas.size() > 0) {
                    ((FootHolder) holder).tips.setText("loading...");
                }
            } else {
                if (datas.size() > 0) {
                    ((FootHolder) holder).tips.setText("no more");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((FootHolder) holder).tips.setVisibility(View.GONE);
                            fadeTips = true;
                            hasMore = true;
                        }
                    }, 500);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas.size() + 1;
    }

    public int getRealLastPosition() {
        return datas.size();
    }


    public void updateList(List<Email> newDatas, boolean hasMore) {
        if (newDatas != null) {
            datas.addAll(newDatas);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    class NormalHolder extends RecyclerView.ViewHolder {
        TextView addresserTV;
        TextView subjectTV;
        TextView dateTV;

        public NormalHolder(View itemView) {
            super(itemView);
            addresserTV = itemView.findViewById(R.id.textView_addresser);
            subjectTV = itemView.findViewById(R.id.textView_subject);
            dateTV = itemView.findViewById(R.id.textView_date);
        }
    }

    class FootHolder extends RecyclerView.ViewHolder {
        private TextView tips;

        public FootHolder(View itemView) {
            super(itemView);
            tips = (TextView) itemView.findViewById(R.id.tips);
        }
    }

    public boolean isFadeTips() {
        return fadeTips;
    }

    public void resetDatas() {
        datas = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return footType;
        } else {
            return normalType;
        }
    }
}

