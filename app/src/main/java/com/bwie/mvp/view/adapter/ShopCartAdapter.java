package com.bwie.mvp.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bwie.mvp.event.OnResfreshListener;
import com.bwie.mvp.view.bean.ShopBean;
import com.bwie.mvp.view.holder.ShopCartHolder;
import com.bwie.wang.MainActivity;
import com.bwie.wang.R;

import java.util.List;

/**
 * Created by wangbingjun on 2018/9/25.
 */

public class ShopCartAdapter extends RecyclerView.Adapter<ShopCartHolder> {
    private Context context;
    private List<ShopBean.DataBean.ListBean> data;

    public ShopCartAdapter(Context context, List<ShopBean.DataBean.ListBean> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ShopCartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShopCartHolder(LayoutInflater.from(context).inflate(R.layout.shop_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShopCartHolder holder, final int position) {
        //商品图片
        Glide.with(context).load(data.get(position).getImages().split("\\|")[0]).into(holder.ivShopCartClothPic);
        //商品信息
        holder.tvShopCartClothName.setText(data.get(position).getShopName());
        holder.tvShopCartClothPrice.setText("￥"+data.get(position).getPrice());
        holder.etShopCartClothNum.setText(data.get(position).getNum()+"");
        //显示前面的选中状态
        if (data.get(position).getSelected()==0){
            holder.ivShopCartClothSel.setImageDrawable(context.getResources().getDrawable(R.drawable.shopcart_selected));
        }else {
            holder.ivShopCartClothSel.setImageDrawable(context.getResources().getDrawable(R.drawable.shopcart_unselected));
        }
        if (data.get(position).getSelected() == 0){
            holder.ivShopCartShopSel.setImageDrawable(context.getResources().getDrawable(R.drawable.shopcart_selected));
        }else {
            holder.ivShopCartShopSel.setImageDrawable(context.getResources().getDrawable(R.drawable.shopcart_unselected));
        }
        //判断是否显示商铺
        if (position>0){
            //判断是否是一个商铺的商品
            if (data.get(position).getSellerid()==data.get(position-1).getSellerid()){
                holder.llShopCartHeader.setVisibility(View.GONE);
            }else {
                holder.llShopCartHeader.setVisibility(View.VISIBLE);
            }

        }else {
            holder.llShopCartHeader.setVisibility(View.VISIBLE);
        }
        //判断是否全选和计算
        if (mOnResfreshListener !=null){
            boolean isSelect = false;
            for (int i = 0;i<data.size();i++){
                if (data.get(i).getSelected()==1){
                    isSelect = false;
                    break;
                }else {
                    isSelect = true;
                }
            }
            mOnResfreshListener.onResfresh(isSelect);
        }
        //商品数量加
        holder.ivShopCartClothAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.get(position).setNum(data.get(position).getNum()+1);
                notifyDataSetChanged();
            }
        });
        //商品数量减
        holder.ivShopCartClothMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.get(position).getNum()>1){
                    data.get(position).setNum(data.get(position).getNum()-1);
                    notifyDataSetChanged();
                }
            }
        });
        //删除
        holder.ivShopCartClothDelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(position);
                MainActivity.isSelectFirst(data);
                notifyDataSetChanged();
            }
        });
        //单个商品选中状态
        holder.ivShopCartClothSel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.get(position).setSelected(data.get(position).getSelected()==0 ? 1:0);
                //通过循环找出不同的第一个商品位置
                for (int i = 0;i<data.size();i++){
                    if (data.get(i).isFirst()){
                        for (int j = 0;j<data.size();j++){
                            if (data.get(j).getSellerid() == data.get(i).getSellerid()&&data.get(j).getSelected()==1){
                                data.get(i).setShopSelect(false);
                                break;
                            }else {
                                data.get(i).setShopSelect(true);
                            }

                        }
                    }
                }
                notifyDataSetChanged();
            }
        });
        //商铺选中状态
        holder.ivShopCartShopSel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.get(position).isFirst()){
                    data.get(position).setShopSelect(!data.get(position).isShopSelect());
                    for (int i = 0;i<data.size();i++){
                        if (data.get(i).getSellerid() == data.get(position).getSellerid()){
                            data.get(i).setSelected(data.get(position).isShopSelect()?0:1);
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    //刷新接口
    private OnResfreshListener mOnResfreshListener;

    public void setmOnResfreshListener(OnResfreshListener mOnResfreshListener) {
        this.mOnResfreshListener = mOnResfreshListener;
    }
}