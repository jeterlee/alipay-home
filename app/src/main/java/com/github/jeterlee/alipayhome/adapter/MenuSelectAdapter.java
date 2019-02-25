package com.github.jeterlee.alipayhome.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jeterlee.alipayhome.R;
import com.github.jeterlee.alipayhome.drag.DragGridView;
import com.github.jeterlee.alipayhome.entity.MenuEntity;

import java.util.List;

/**
 * <pre>
 * Title: MenuSelectAdapter
 * Description: 已选择的菜单（可编辑的 menu，即“我的应用”）
 * </pre>
 *
 * @author <a href="https://www.github.com/jeterlee"></a>
 * @date 2019/2/23 0023
 */
public class MenuSelectAdapter extends BaseAdapter implements
        DragGridView.DragAdapter {
    private boolean isEdit = false;
    private List<MenuEntity> menuList;
    private CategoryManager mManager;
    private Context mContext;

    public MenuSelectAdapter(CategoryManager manager, List<MenuEntity> menuList) {
        this.mManager = manager;
        this.menuList = menuList;
        mContext = manager.getContext();
    }

    public void setDatas(List<MenuEntity> menuList) {
        this.menuList.clear();
        this.menuList.addAll(menuList);
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MenuEntity bean = menuList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.items_select_menu, null);
            holder.deleteImg = convertView.findViewById(R.id.delete_img);
            holder.iconImg = convertView.findViewById(R.id.icon_img);
            holder.nameText = convertView.findViewById(R.id.select_tv_name_text);
            holder.container = convertView.findViewById(R.id.item_container);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManager.deleteMenu(menuList.get(position), position);
                menuList.remove(position);
            }
        });
        if (isEdit) {
            holder.deleteImg.setVisibility(View.VISIBLE);
        } else {
            holder.deleteImg.setVisibility(View.GONE);
        }

        // 获取资源图片
        int drawableId = mContext.getResources()
                .getIdentifier(bean.getIco(), "mipmap", mContext.getPackageName());
        holder.iconImg.setImageResource(drawableId);
        holder.nameText.setText(bean.getTitle());
        holder.container.setBackgroundColor(Color.WHITE);
        return convertView;
    }

    class ViewHolder {
        ImageView deleteImg;
        ImageView iconImg;
        TextView nameText;
        View container;
    }

    @Override
    public void reOrder(int startPosition, int endPosition) {
        if (endPosition < menuList.size()) {
            MenuEntity object = menuList.remove(startPosition);
            menuList.add(endPosition, object);
            notifyDataSetChanged();
        }
    }

    public void setEdit() {
        isEdit = true;
        notifyDataSetChanged();
    }

    public void endEdit() {
        isEdit = false;
        notifyDataSetChanged();
    }

    public boolean getEditStatue() {
        return isEdit;
    }

}
