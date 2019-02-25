package com.github.jeterlee.alipayhome.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.github.jeterlee.alipayhome.Config;
import com.github.jeterlee.alipayhome.R;
import com.github.jeterlee.alipayhome.adapter.IndexDataAdapter;
import com.github.jeterlee.alipayhome.entity.MenuEntity;
import com.github.jeterlee.alipayhome.util.FileUtils;
import com.github.jeterlee.alipayhome.widget.LineGridView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AliHomeActivity extends AppCompatActivity implements
        AppBarLayout.OnOffsetChangedListener , AdapterView.OnItemClickListener {

    private List<MenuEntity> indexUserMenuData = new ArrayList<>();
    IndexDataAdapter adapter;
    MenuEntity menuEntity;

    private AppBarLayout appBar;
    /**
     * 大布局背景，遮罩层
     */
    private View bgContent;
    /**
     * 展开状态下toolbar显示的内容
     */
    private View toolbarOpen;
    /**
     * 展开状态下toolbar的遮罩层
     */
    private View bgToolbarOpen;
    /**
     * 收缩状态下toolbar显示的内容
     */
    private View toolbarClose;
    /**
     * 收缩状态下toolbar的遮罩层
     */
    private View bgToolbarClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ali_home);
        appBar = findViewById(R.id.app_bar);
        bgContent = findViewById(R.id.bg_content);
        toolbarOpen = findViewById(R.id.include_toolbar_open);
        bgToolbarOpen = findViewById(R.id.bg_toolbar_open);
        toolbarClose = findViewById(R.id.include_toolbar_close);
        bgToolbarClose = findViewById(R.id.bg_toolbar_close);

        appBar.addOnOffsetChangedListener(this);


        LineGridView lineGridView = findViewById(R.id.home_line_gridview);
        // GridView lineGridView = findViewById(R.id.home_line_gridview);
        lineGridView.setFocusable(false);
        indexUserMenuData = (List<MenuEntity>) FileUtils.
                readObject(getApplicationContext(), Config.USER_MENU_DATA);
        if (indexUserMenuData == null) {
            indexUserMenuData = getDefaultMenuData();
            FileUtils.saveObject(getApplicationContext(), (Serializable) indexUserMenuData, Config.USER_MENU_DATA);
        }
        menuEntity = new MenuEntity();
        lineGridView.setOnItemClickListener(this);
        adapter = new IndexDataAdapter(this, indexUserMenuData);
        lineGridView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (indexUserMenuData != null) {
            indexUserMenuData.clear();
        }
        indexUserMenuData = (List<MenuEntity>) FileUtils.
                readObject(getApplicationContext(), Config.USER_MENU_DATA);
        menuEntity.setIco("all_big_ico");
        menuEntity.setId("-1");
        menuEntity.setTitle("全部");
        indexUserMenuData.add(menuEntity);
        adapter.setList(indexUserMenuData);
    }

    private List<MenuEntity> getDefaultMenuData() {
        List<MenuEntity> menuEntities = new ArrayList<>();
        String jsonString = FileUtils.getJson(getApplicationContext(),
                Config.USER_MENU_DATA_FILE_NAME);
        // Logger.e(jsonString);
        // 将 JSON 的 String 转成一个 JsonArray 对象
        JsonArray jsonArray = new JsonParser().parse(jsonString).getAsJsonArray();
        Gson gson = new Gson();
        // 加强 for 循环遍历 JsonArray
        for (JsonElement indexArr : jsonArray) {
            // 使用 Gson，直接转成 Bean 对象
            MenuEntity menuEntity = gson.fromJson(indexArr, MenuEntity.class);
            menuEntities.add(menuEntity);
        }
        return menuEntities;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        // Bundle bundle = new Bundle();
        String title = indexUserMenuData.get(position).getTitle();
        String strId = indexUserMenuData.get(position).getId();
        Logger.i(title + strId);
        // 点击更多
        if ("-1".equals(strId)) {
            intent.setClass(AliHomeActivity.this, MenuManageActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appBar.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //垂直方向偏移量
        int offset = Math.abs(verticalOffset);
        //最大偏移距离
        int scrollRange = appBarLayout.getTotalScrollRange();
        if (offset <= scrollRange / 2) {//当滑动没超过一半，展开状态下toolbar显示内容，根据收缩位置，改变透明值
            toolbarOpen.setVisibility(View.VISIBLE);
            toolbarClose.setVisibility(View.GONE);
            //根据偏移百分比 计算透明值
            float scale2 = (float) offset / (scrollRange / 2);
            int alpha2 = (int) (255 * scale2);
            bgToolbarOpen.setBackgroundColor(Color.argb(alpha2, 25, 131, 209));
        } else {//当滑动超过一半，收缩状态下toolbar显示内容，根据收缩位置，改变透明值
            toolbarClose.setVisibility(View.VISIBLE);
            toolbarOpen.setVisibility(View.GONE);
            float scale3 = (float) (scrollRange - offset) / (scrollRange / 2);
            int alpha3 = (int) (255 * scale3);
            bgToolbarClose.setBackgroundColor(Color.argb(alpha3, 25, 131, 209));
        }
        //根据偏移百分比计算扫一扫布局的透明度值
        float scale = (float) offset / scrollRange;
        int alpha = (int) (255 * scale);
        bgContent.setBackgroundColor(Color.argb(alpha, 25, 131, 209));
    }
}
