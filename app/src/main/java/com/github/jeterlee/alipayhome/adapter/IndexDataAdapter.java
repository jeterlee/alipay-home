package com.github.jeterlee.alipayhome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.jeterlee.alipayhome.R;
import com.github.jeterlee.alipayhome.entity.MenuEntity;

import java.util.List;

import cn.bingoogolapple.badgeview.BGABadgeImageView;

/**
 * <pre>
 * Title: IndexDataAdapter
 * Description: 选中的次数
 * </pre>
 *
 * @author <a href="https://www.github.com/jeterlee"></a>
 * @date 2019/2/23 0023
 */
public class IndexDataAdapter extends BaseAdapter {
    private List<MenuEntity> list;
    private LayoutInflater layoutInflater;
    private Context context;

    public IndexDataAdapter(Context context, List<MenuEntity> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        MenuEntity entity = list.get(position);
        if (layoutInflater != null) {
            view = layoutInflater.inflate(R.layout.items_home_menu, null);

            BGABadgeImageView badgeImageView = view.findViewById(R.id.iv_badge);
            TextView title = view.findViewById(R.id.tv_title);

            title.setText(entity.getTitle());
            if ("0".equals(entity.getNum())) {
                badgeImageView.hiddenBadge();
            } else {
                badgeImageView.showTextBadge(entity.getNum());
            }

            // 获取资源图片
            int drawableId = context.getResources()
                    .getIdentifier(entity.getIco(), "mipmap", context.getPackageName());
            badgeImageView.setImageResource(drawableId);

            // 网络图片
            // if (!entity.getIco().isEmpty()) {
            //     Glide.with(context).load(entity.getIco()).into(badgeImageView);
            // } else {
            //     Glide.with(context).load(R.mipmap.all_big_ico)
            //             .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
            //             .into(badgeImageView);
            // }

            // 设置边框及颜色
            // badgeImageView.getBadgeViewHelper().setBadgeBorderWidthDp(2);
            // badgeImageView.getBadgeViewHelper().setBadgeBorderColorInt(Color.parseColor("#0000FF"));
        }
        return view;
    }
}
