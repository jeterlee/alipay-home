package com.github.jeterlee.alipayhome.ui;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * <pre>
 * Title: HomeActivity
 * Description: 首页
 * </pre>
 *
 * @author <a href="https://www.github.com/jeterlee"></a>
 * @date 2019/2/23 0023
 */
public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<MenuEntity> indexUserMenuData = new ArrayList<>();
    IndexDataAdapter adapter;
    MenuEntity menuEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LineGridView lineGridView = findViewById(R.id.home_line_gridview);
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
            intent.setClass(HomeActivity.this, MenuManageActivity.class);
            startActivity(intent);
        }
    }
}
