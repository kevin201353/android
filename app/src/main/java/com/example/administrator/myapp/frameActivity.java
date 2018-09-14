package com.example.administrator.myapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class frameActivity extends AppCompatActivity {

    private RecyclerView recyView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
        recyView = (RecyclerView)findViewById(R.id.rv);
        recyView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyAdapter(getData());
        recyView.setAdapter(mAdapter);
        recyView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.SetOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(frameActivity.this,"click " + position + " item", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(frameActivity.this,"long click " + position + " item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<String> getData() {
        ArrayList<String> arry = new ArrayList<>();
        String temp = "  item";
        for (int i=0; i<100; i++) {
            arry.add(i + temp);
        }
        return arry;
    }

    //MyAdapter class
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
        private ArrayList<String> mData;
        private OnItemClickListener   mOnItemClickListener;

        public MyAdapter(ArrayList<String> data) {
            mData = data;
        }

        public void SetOnItemClickListener(OnItemClickListener  onItemClick) {
            mOnItemClickListener = onItemClick;
        }
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flight, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(final MyHolder holder, int position) {
            //bind data
            holder.txtName.setText(mData.get(position));
            holder.txtState.setText(mData.get(position));
            holder.txtTime.setText(mData.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(holder.itemView, pos);
                    }
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemClickListener != null) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onLongClick(holder.itemView, pos);
                    }
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0:mData.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {
            TextView txtName;
            TextView txtState;
            TextView txtTime;
            public MyHolder(View itemView) {
                super(itemView);
                txtName = (TextView) itemView.findViewById(R.id.name);
                txtState = (TextView) itemView.findViewById(R.id.state);
                txtTime = (TextView) itemView.findViewById(R.id.time);
            }
        }
    }
    //interface Listener
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onLongClick(View view, int position);
    }
}
