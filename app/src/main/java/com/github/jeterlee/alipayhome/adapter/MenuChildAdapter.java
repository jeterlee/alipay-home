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
 * Description: 全部待选择的菜单，要添加的单个 menu 适配器，此适配器绑定拖动上标，
 * 是属于 {@link MenuParentAdapter} 的子适配器，仅供其使用。
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
            viewHolder.childName = convertView.findViewById(R.id.child_name);
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

        // Glide.with(mContext).load(menuEntity.getIco()).placeholder(R.mipmap.ic_launcher)
        //         .error(R.mipmap.ic_launcher).into(viewHolder.iconImg);

        viewHolder.childName.setText(menuEntity.getTitle());
        return convertView;
    }

    private class ViewHolder {
        private TextView childName;
        private ImageView deleteImg, iconImg;
    }

}
