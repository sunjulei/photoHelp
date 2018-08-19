package com.sunlee.photoaide;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<String> filePathList;
    private MyRecyclerViewItem recyclerViewItem;

    public HomeAdapter(Context context, List<String> filePathList) {
        this.context = context;
        this.filePathList = filePathList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)//获取布局
    {
//        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
//                context).inflate(R.layout.item_head_path, parent,
//                false));
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_head_path, parent,false);
        MyViewHolder holder = new MyViewHolder(itemView,recyclerViewItem);

        return holder;
    }
    /**
     * 设置Item点击监听
     * @param
     */
    public void setOnItemClickListener(MyRecyclerViewItem recyclerViewItem){
        this.recyclerViewItem = recyclerViewItem;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)//设置控件
    {

        String filePath = filePathList.get(position);
        if(filePath.contains("/")){
            filePath = filePath.substring(filePath.lastIndexOf("/")+1);
        }
        if(position==0){
            ((MyViewHolder) holder).tv_file_path.setText("内部存储"+"  >");
        }else{
            ((MyViewHolder) holder).tv_file_path.setText(filePath+"  >");
        }

    }

    @Override
    public int getItemCount()//获取数据
    {

        return filePathList.size();
    }




    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener //初始化控件
    {

        TextView tv_file_path;

        public MyViewHolder(View view, MyRecyclerViewItem recyclerViewItem1) {
            super(view);
            tv_file_path = (TextView) view.findViewById(R.id.tv_file_path);
            view.setOnClickListener(this);
            recyclerViewItem = recyclerViewItem1;
        }

        @Override
        public void onClick(View view) {
            recyclerViewItem.onItemClick(view,getPosition());
        }
    }

    public void setAllData(List<String> filePathList){
        this.filePathList = filePathList;
        notifyDataSetChanged();
    }

}