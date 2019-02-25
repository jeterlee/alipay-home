package com.github.jeterlee.alipayhome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jeterlee.alipayhome.R;
import com.github.jeterlee.alipayhome.entity.MenuEntity;

import java.util.List;

/**
 * <pre>
 * Title: MenuChildAdapter
 * Description: {@link MenuParentAdapter} 是整个整个菜单排列的框架，即整体的布局，
 * 称为菜单的父适配器，{@link MenuChildAdapter} 是每个菜单的适配器，包含菜单的加减图标，
 * 图标，菜单名字，称为菜单的子适配器。
 * 原理：将菜单的子适配器 {@link MenuChildAdapter} 放到 菜单的父适配器 {@link MenuParentAdapter}
 * 完成整个布局（双重菜单适配器）。
 * </pre>
 *
 * @author <a href="https://www.github.com/jeterlee"></a>
 * @date 2019/2/23 0023
 */
public class MenuChildAdapter extends BaseAdapter {
    private List<MenuEntity> menuList;
    private CategoryManager mManager;
    private boolean isEdit;
    private Context mContext;

    MenuChildAdapter(CategoryManager manager, List<MenuEntity> menuList, boolean isEdit) {
        this.mManager = manager;
        this.menuList = menuList;
        this.isEdit = isEdit;
        mContext = manager.getContext();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.items_category_child, null);
            viewHolder = new ViewHolder();
            viewHolder.nameText = convertView.findViewById(R.id.child_tv_name_text);
            viewHolder.deleteImg = convertView.findViewById(R.id.delete_img);
            viewHolder.iconImg = convertView.findViewById(R.id.icon_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final MenuEntity menuEntity = menuList.get(position);

        if (isEdit) {
            viewHolder.deleteImg.setVisibility(View.VISIBLE);
            if (menuEntity.isSelect()) {
                viewHolder.deleteImg.setBackgroundResource(R.mipmap.menu_select);
            } else {
                viewHolder.deleteImg.setBackgroundResource(R.mipmap.menu_add);
            }
        } else {
            viewHolder.deleteImg.setVisibility(View.GONE);
        }
        viewHolder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!menuEntity.isSelect()) {
                    mManager.addMenu(menuEntity);
                }
            }
        });

        // 获取资源图片
        int drawableId = mContext.getResources().getIdentifier(menuEntity.getIco(),
                "mipmap", mContext.getPackageName());
        viewHolder.iconImg.setImageResource(drawableId);
        viewHolder.nameText.setText(menuEntity.getTitle());
        return convertView;
    }

    private class ViewHolder {
        private TextView nameText;
        private ImageView deleteImg, iconImg;
    }

}
