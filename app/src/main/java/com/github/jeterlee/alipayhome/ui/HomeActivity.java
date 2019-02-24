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
public class HomeActivity extends AppCompatActivity {
    private LineGridView lineGridView;
    private List<MenuEntity> indexDataAll = new ArrayList<>();
    private List<MenuEntity> indexDataList = new ArrayList<>();
    private IndexDataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lineGridView = findViewById(R.id.home_line_gridview);
        lineGridView.setFocusable(false);

        String jsonString = FileUtils.getJson(this, Config.FILE_NAME);
        Logger.e(jsonString);
        // 将 JSON 的 String 转成一个 JsonArray 对象
        JsonArray jsonArray = new JsonParser().parse(jsonString).getAsJsonArray();
        Gson gson = new Gson();
        // 加强 for 循环遍历 JsonArray
        for (JsonElement indexArr : jsonArray) {
            // 使用 Gson，直接转成 Bean 对象
            MenuEntity menuEntity = gson.fromJson(indexArr, MenuEntity.class);
            indexDataAll.add(menuEntity);
        }

        // 复制
        FileUtils.saveObject(getApplicationContext(), (Serializable) indexDataAll, Config.KEY_ALL);
        List<MenuEntity> indexDataUser = (List<MenuEntity>) FileUtils.readObject(getApplicationContext(), Config.KEY_USER);
        if (indexDataUser == null || indexDataUser.size() == 0) {
            FileUtils.saveObject(getApplicationContext(), (Serializable) indexDataAll, Config.KEY_USER);
        }
        // indexDataList = (List<MenuEntity>) FileUtils.readObject(getApplicationContext(), Config.KEY_USER);

        // MenuEntity allMenuEntity = new MenuEntity();
        // allMenuEntity.setIco("");
        // allMenuEntity.setId("all");
        // allMenuEntity.setTitle("全部");
        // indexDataList.add(allMenuEntity);
        // adapter = new IndexDataAdapter(this, indexDataList);
        // lineGridView.setAdapter(adapter);

        lineGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                // Bundle bundle = new Bundle();
                String title = indexDataList.get(position).getTitle();
                String strId = indexDataList.get(position).getId();
                Logger.i(title + strId);
                // 点击更多
                if ("all".equals(strId)) {
                    intent.setClass(HomeActivity.this, MenuManageActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        indexDataList.clear();
        indexDataList = (List<MenuEntity>) FileUtils.readObject(getApplicationContext(), Config.KEY_USER);
        MenuEntity allMenuEntity = new MenuEntity();
        allMenuEntity.setIco("all_big_ico");
        allMenuEntity.setId("all");
        allMenuEntity.setTitle("全部");
        indexDataList.add(allMenuEntity);
        adapter = new IndexDataAdapter(HomeActivity.this, indexDataList);
        lineGridView.setAdapter(adapter);
    }
}
