package com.github.jeterlee.alipayhome.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.github.jeterlee.alipayhome.R;
import com.github.jeterlee.alipayhome.entity.MenuEntity;
import com.github.jeterlee.alipayhome.widget.MenuGridView;

import java.util.List;

/**
 * <pre>
 * Title: MenuParentAdapter
 * Description: {@link MenuParentAdapter} 是整个整个菜单排列的框架，称为菜单的父适配器，
 * {@link MenuChildAdapter} 是每个菜单的适配器，包含菜单的加减图标，图标，菜单名字，称为菜单的子适配器。
 * 原理：将菜单的子适配器 {@link MenuChildAdapter} 放到 菜单的父适配器 {@link MenuParentAdapter}
 * 完成整个布局（双重菜单适配器）。
 * </pre>
 *
 * @author <a href="https://www.github.com/jeterlee"></a>
 * @date 2019/2/23 0023
 */
public class MenuParentAdapter extends BaseExpandableListAdapter
        implements OnItemClickListener, OnItemLongClickListener {
    private CategoryManager mManager;
    private List<MenuEntity> menuList;
    private LayoutInflater inflater;
    private boolean isEdit;

    public MenuParentAdapter(CategoryManager manager, List<MenuEntity> datas) {
        this.mManager = manager;
        this.menuList = datas;
        inflater = LayoutInflater.from(manager.getContext());
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return menuList.get(groupPosition).getChilds();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.items_category_grid_child, null);
        MenuGridView menuGridView = convertView.findViewById(R.id.category_mgv_grid_child);
        // 设置每行列数
        menuGridView.setNumColumns(4);
        // 位置居中
        menuGridView.setGravity(Gravity.CENTER);
        // 水平间隔
        // menuManagerView.setHorizontalSpacing(10);
        MenuChildAdapter adapter = new MenuChildAdapter(mManager,
                menuList.get(groupPosition).getChilds(), isEdit);
        // 设置菜单 Adapter
        menuGridView.setAdapter(adapter);
        menuGridView.setOnItemClickListener(this);
        menuGridView.setOnItemLongClickListener(this);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return menuList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return menuList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.items_category_parent, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.categoryName = convertView.findViewById(R.id.parent_tv_category_name);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.categoryName.setText(menuList.get(groupPosition).getTitle());
        return convertView;
    }

    class GroupViewHolder {
        TextView categoryName;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        MenuEntity indexData = (MenuEntity) adapterView.getItemAtPosition(position);
        if (indexData != null) {
            mManager.getItem(indexData);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        return false;
    }

    public void setEdit() {
        isEdit = true;
        notifyDataSetChanged();
    }

    public void endEdit() {
        isEdit = false;
        notifyDataSetChanged();
    }

}
