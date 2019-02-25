package com.github.jeterlee.alipayhome.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jeterlee.alipayhome.Config;
import com.github.jeterlee.alipayhome.R;
import com.github.jeterlee.alipayhome.adapter.CategoryManager;
import com.github.jeterlee.alipayhome.adapter.MenuParentAdapter;
import com.github.jeterlee.alipayhome.adapter.MenuSelectAdapter;
import com.github.jeterlee.alipayhome.drag.DragGridView;
import com.github.jeterlee.alipayhome.drag.DragScrollView;
import com.github.jeterlee.alipayhome.entity.MenuEntity;
import com.github.jeterlee.alipayhome.util.FileUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Title: MenuManageActivity
 * Description: 菜单管理界面
 *
 * toolbar 坑：
 * 1. 增加 menu , 要想让 Toolbar 本身的 inflateMenu 生效，则必须删去这两句代码。
 *    setSupportActionBar(toolbar);
 *    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
 *    Toolbar.OnMenuItemClickListener
 * <a href='https://www.jianshu.com/p/e7ccc077f9a7'>Android toolbar inflateMenu 无法加载布局</href>
 * <a href='https://www.cnblogs.com/lyh1299259684/p/6795223.html'>解决Toolbar中的Menu不显示图片的问题</href>
 *
 * 1.在Toolbar所在的Activity中使用setSupportActionBar(mToolbar)方法设置ActionBar为当前的Toolbar
 *   使用该方法的注意事项（多次遇到的坑）：
 *   a. 设置toolbar的显示UI一定要在setSupportActionBar之前设置，否则不会显示。
 *      例如：mToolbar.setTitle("这是toolbar主标题");
 *   b. toolbar的监听一定要写在setSupportActionBar的下面，否则不会触发事件。
 *   c. mToolbar.inflateMenu(R.menu.toolbar)使用该方法不会显示菜单选项，需要在activity中重写
 *      onCreateOptionsMenu方法，在该方法中使用getMenuInflater().inflate(R.menu.toolbar, menu)
 *      加载menu，才会显示。
 *
 *   <a href='https://www.cnblogs.com/livelihood/p/6758709.html'>Toolbar-标题栏的使用</href>
 *   <a href='https://www.cnblogs.com/chhom/p/5264647.html'>Android Menu的基本用法</href>
 * </pre>
 *
 * @author <a href="https://www.github.com/jeterlee"></a>
 * @date 2019/2/23 0023
 */
public class MenuManageActivity extends AppCompatActivity implements CategoryManager,
        Toolbar.OnMenuItemClickListener, OnClickListener {
    private static final int ADD_MENU_MAX_VALUE = 10;
    private DragGridView dragGridView;
    private DragScrollView dragScrollView;
    private MenuSelectAdapter adapterSelect;
    private MenuParentAdapter menuParentAdapter;
    private MenuItem manager;
    private TextView dragTip;

    private List<MenuEntity> menuList = new ArrayList<>();
    private List<MenuEntity> indexSelectMenuData = new ArrayList<>();
    private List<MenuEntity> indexUserMenuData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_manage);
        dragGridView = findViewById(R.id.dgv_gridview);
        dragScrollView = findViewById(R.id.ds_scrollview);
        dragTip = findViewById(R.id.drag_tip);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ((TextView) findViewById(R.id.toolbar_title)).setText("全部应用");
        findViewById(R.id.toolbar_back).setOnClickListener(this);
        toolbar.inflateMenu(R.menu.menu);
        manager = toolbar.getMenu().findItem(R.id.manger);
        toolbar.setOnMenuItemClickListener(this);
        initView();
        initData();
    }

    private void initView() {
        // 获取设置保存到本地的菜单
        indexUserMenuData = (List<MenuEntity>) FileUtils.
                readObject(getApplicationContext(), Config.USER_MENU_DATA);
        if (indexUserMenuData != null) {
            indexSelectMenuData.clear();
            indexSelectMenuData.addAll(indexUserMenuData);
        }

        adapterSelect = new MenuSelectAdapter(this, indexSelectMenuData);
        dragGridView.setAdapter(adapterSelect);
        dragGridView.setDragCallback(dragScrollView);
        dragGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("setOnItemClickListener", adapterSelect.getEditStatue() + "");
                if (!adapterSelect.getEditStatue()) {
                    // dragGridView.clicked(position);
                    MenuEntity cateModel = indexSelectMenuData.get(position);
                    getItem(cateModel);
                }
            }
        });
        dragGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (manager.getTitle().equals("管理")) {
                    manager.setTitle("完成");
                    adapterSelect.setEdit();
                    if (menuParentAdapter != null) {
                        menuParentAdapter.setEdit();
                    }
                    dragTip.setVisibility(View.VISIBLE);
                }
                dragGridView.startDrag(position);
                return false;
            }
        });
    }

    private List<MenuEntity> getDefaultMenuData() {
        List<MenuEntity> menuEntities = new ArrayList<>();
        String jsonString = FileUtils.getJson(getApplicationContext(),
                Config.MENU_DATA_FILE_NAME);
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

    private void initData() {
        List<MenuEntity> allMenuData = getDefaultMenuData();
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        expandableListView.setGroupIndicator(null);
        menuList.clear();
        try {
            MenuEntity index = new MenuEntity();
            index.setTitle("流程审批");
            index.setId("1");
            List<MenuEntity> indexLC = new ArrayList<>();
            for (int i = 0; i < allMenuData.size(); i++) {
                if (allMenuData.get(i).getId().equals("1")) {
                    indexLC.add(allMenuData.get(i));
                }
                if (allMenuData.get(i).getId().equals("2")) {
                    indexLC.add(allMenuData.get(i));
                }
                if (allMenuData.get(i).getId().equals("3")) {
                    indexLC.add(allMenuData.get(i));
                }
                if (allMenuData.get(i).getId().equals("4")) {
                    indexLC.add(allMenuData.get(i));
                }
                if (allMenuData.get(i).getId().equals("5")) {
                    indexLC.add(allMenuData.get(i));
                }
            }
            for (int i = 0; i < indexLC.size(); i++) {
                for (int j = 0; j < indexSelectMenuData.size(); j++) {
                    if (indexLC.get(i).getTitle().equals(indexSelectMenuData.get(j).getTitle())) {
                        indexLC.get(i).setSelect(true);
                    }
                }
            }
            index.setChilds(indexLC);
            menuList.add(index);

            MenuEntity index1 = new MenuEntity();
            index1.setTitle("绩效考核");
            index1.setId("1");

            List<MenuEntity> indexJX = new ArrayList<>();
            for (int i = 0; i < allMenuData.size(); i++) {
                if (allMenuData.get(i).getId().equals("6")) {
                    indexJX.add(allMenuData.get(i));
                }
                if (allMenuData.get(i).getId().equals("7")) {
                    indexJX.add(allMenuData.get(i));
                }
                if (allMenuData.get(i).getId().equals("8")) {
                    indexJX.add(allMenuData.get(i));
                }
                if (allMenuData.get(i).getId().equals("9")) {
                    indexJX.add(allMenuData.get(i));
                }
                if (allMenuData.get(i).getId().equals("10")) {
                    indexJX.add(allMenuData.get(i));
                }
                if (allMenuData.get(i).getId().equals("11")) {
                    indexJX.add(allMenuData.get(i));
                }
                if (allMenuData.get(i).getId().equals("12")) {
                    indexJX.add(allMenuData.get(i));
                }
            }
            for (int i = 0; i < indexJX.size(); i++) {
                for (int j = 0; j < indexSelectMenuData.size(); j++) {
                    if (indexJX.get(i).getTitle().equals(indexSelectMenuData.get(j).getTitle())) {
                        indexJX.get(i).setSelect(true);
                    }
                }
            }
            index1.setChilds(indexJX);
            menuList.add(index1);

            MenuEntity index2 = new MenuEntity();
            index2.setTitle("其他");
            index2.setId("2");

            List<MenuEntity> indexQT = new ArrayList<>();
            for (int i = 0; i < allMenuData.size(); i++) {
                if (allMenuData.get(i).getId().equals("13")) {
                    indexQT.add(allMenuData.get(i));
                }
                if (allMenuData.get(i).getId().equals("14")) {
                    indexQT.add(allMenuData.get(i));
                }
                if (allMenuData.get(i).getId().equals("15")) {
                    indexQT.add(allMenuData.get(i));
                }
            }
            for (int i = 0; i < indexQT.size(); i++) {
                for (int j = 0; j < indexSelectMenuData.size(); j++) {
                    if (indexQT.get(i).getTitle().equals(indexSelectMenuData.get(j).getTitle())) {
                        indexQT.get(i).setSelect(true);
                    }
                }
            }
            index2.setChilds(indexQT);
            menuList.add(index2);

            menuParentAdapter = new MenuParentAdapter(this, menuList);
            expandableListView.setAdapter(menuParentAdapter);

            // expandableListView.expandGroup(6); // 在分组列表视图中 展开一组
            // expandableListView.isGroupExpanded(0); //判断此组是否展开
            for (int i = 0; i < menuParentAdapter.getGroupCount(); i++) {
                expandableListView.expandGroup(i);
            }
            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                    MenuEntity cateModel = menuList.get(groupPosition);
                    getItem(cateModel);
                    return true;
                }
            });
            expandableListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    if (manager.getTitle().equals("管理")) {
                        MenuEntity cateModel = menuList.get(arg2);
                        getItem(cateModel);
                    }
                }
            });
            expandableListView.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    if (manager.getTitle().equals("管理")) {
                        manager.setTitle("完成");
                        adapterSelect.setEdit();
                        menuParentAdapter.setEdit();
                        dragTip.setVisibility(View.VISIBLE);
                    }
                    return false;
                }
            });

        } catch (Exception e) {
        }
    }

    // protected void postMenu() {
    //     userMenuData = (List<MenuEntity>) FileUtils.readObject(getApplicationContext(),
    //             Config.USER_MENU_TEMP);
    //     FileUtils.saveObject(getApplicationContext(), (Serializable) userMenuData, Config.USER_MENU);
    // }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.manger) {
            if ("管理".equals(manager.getTitle())) {
                manager.setTitle("完成");
                adapterSelect.setEdit();
                if (menuParentAdapter != null) {
                    menuParentAdapter.setEdit();
                }
                dragTip.setVisibility(View.VISIBLE);
            } else {
                manager.setTitle("管理");
                dragTip.setVisibility(View.GONE);
                adapterSelect.endEdit();
                if (menuParentAdapter != null) {
                    menuParentAdapter.endEdit();
                }
                FileUtils.saveObject(getApplicationContext(), (Serializable) indexSelectMenuData,
                        Config.USER_MENU_DATA);
                // postMenu();
            }
            return true;
        }
        return false;
    }

    @Override
    public void getItem(MenuEntity cateModel) {
        if (manager.getTitle().equals("管理")) {
            String title = cateModel.getTitle();
            String strId = cateModel.getId();
            Toast.makeText(this, title + " " + strId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void deleteMenu(MenuEntity indexData, int position) {
        for (int i = 0; i < menuList.size(); i++) {
            for (int k = 0; k < menuList.get(i).getChilds().size(); k++) {
                if (menuList.get(i).getChilds().get(k).getTitle().equals(indexData.getTitle())) {
                    menuList.get(i).getChilds().get(k).setSelect(false);
                }
            }
        }
        if (menuParentAdapter != null) {
            menuParentAdapter.notifyDataSetChanged();
        }
        adapterSelect.notifyDataSetChanged();
    }

    @Override
    public void addMenu(MenuEntity menuEntity) {
        if (indexSelectMenuData.size() > ADD_MENU_MAX_VALUE) {
            Toast.makeText(this, "添加已达到上限了！", Toast.LENGTH_LONG).show();
            return;
        }
        indexSelectMenuData.add(menuEntity);
        for (int i = 0; i < menuList.size(); i++) {
            for (int k = 0; k < menuList.get(i).getChilds().size(); k++) {
                if (menuList.get(i).getChilds().get(k).getTitle().equals(menuEntity.getTitle())) {
                    menuList.get(i).getChilds().get(k).setSelect(true);
                }
            }
        }
        menuParentAdapter.notifyDataSetChanged();
        adapterSelect.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }
}
