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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Title: MenuManageActivity
 * Description: 菜单管理界面
 * </pre>
 *
 * @author <a href="https://www.github.com/jeterlee"></a>
 * @date 2019/2/23 0023
 */
public class MenuManageActivity extends AppCompatActivity implements CategoryManager,
        Toolbar.OnMenuItemClickListener, OnClickListener {
    private DragGridView dragGridView;
    private DragScrollView dragScrollView;
    private MenuSelectAdapter adapterSelect;
    private MenuItem manager;

    private ArrayList<MenuEntity> menuList = new ArrayList<>();
    private MenuParentAdapter menuParentAdapter;
    private TextView dragTip;

    private List<MenuEntity> indexSelect = new ArrayList<>();
    private List<MenuEntity> indexDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_manage);
        dragGridView = findViewById(R.id.dgv_gridview);
        dragScrollView = findViewById(R.id.ds_scrollview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        dragTip = findViewById(R.id.drag_tip);

        /**
         * toolbar 坑：
         * 1. 增加 menu , 要想让 Toolbar 本身的 inflateMenu 生效，则必须删去这两句代码。
         * setSupportActionBar(toolbar);
         * getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         * Toolbar.OnMenuItemClickListener
         * <a href='https://www.jianshu.com/p/e7ccc077f9a7'>Android toolbar inflateMenu 无法加载布局</href>
         *
         *
         * <a href='https://www.cnblogs.com/lyh1299259684/p/6795223.html'>解决Toolbar中的Menu不显示图片的问题</href>
         *
         *1.在Toolbar所在的Activity中使用setSupportActionBar(mToolbar)方法设置ActionBar为当前的Toolbar
         *          * **
         *          *  * 使用该方法的注意事项（多次遇到的坑）：
         *          *  *   1.设置toolbar的显示UI一定要在setSupportActionBar之前设置，否则不会显示。
         *          *  *      例如：mToolbar.setTitle("这是toolbar主标题");
         *          *  *   2.toolbar的监听一定要写在setSupportActionBar的下面，否则不会触发事件。
         *          *  *   3.mToolbar.inflateMenu(R.menu.toolbar)使用该方法不会显示菜单选项，需要在activity中重写
         *          *  *      onCreateOptionsMenu方法，在该方法中使用getMenuInflater().inflate(R.menu.toolbar, menu)
         *          *  *      加载menu，才会显示。
         *          *  *
         *
         * <a href='https://www.cnblogs.com/livelihood/p/6758709.html'>Toolbar-标题栏的使用</href>
         * <a href='https://www.cnblogs.com/chhom/p/5264647.html'>Android Menu的基本用法</href>
         *
         *
         **/
        ((TextView) findViewById(R.id.toolbar_title)).setText("全部应用");
        findViewById(R.id.toolbar_back).setOnClickListener(this);
        toolbar.inflateMenu(R.menu.menu);
        manager = toolbar.getMenu().findItem(R.id.manger);
        toolbar.setOnMenuItemClickListener(this);
        initView();
        initData();
    }

    protected void postMenu() {
        indexDataList = (List<MenuEntity>) FileUtils.readObject(getApplicationContext(),
                Config.KEY_USER_TEMP);
        FileUtils.saveObject(getApplicationContext(), (Serializable) indexDataList, Config.KEY_USER);
    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    //     getMenuInflater().inflate(R.menu.menu, menu);
    //     manager = menu.findItem(R.id.manger);
    //     return true;
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
                postMenu();
            }
            return true;
        }
        return false;
    }

    private void initView() {
        // 获取设置保存到本地的菜单
        indexDataList = (List<MenuEntity>) FileUtils.
                readObject(getApplicationContext(), Config.KEY_USER);
        if (indexDataList != null) {
            indexSelect.clear();
            indexSelect.addAll(indexDataList);
        }

        adapterSelect = new MenuSelectAdapter(this, indexSelect);
        dragGridView.setAdapter(adapterSelect);
        dragGridView.setDragCallback(dragScrollView);
        dragGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("setOnItemClickListener", adapterSelect.getEditStatue() + "");
                if (!adapterSelect.getEditStatue()) {
                    //dragGridView.clicked(position);
                    MenuEntity cateModel = indexSelect.get(position);
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

    private void initData() {
        indexDataList =
                (List<MenuEntity>) FileUtils.readObject(getApplicationContext(), Config.KEY_ALL);
        init(indexDataList);
    }

    private void init(List<MenuEntity> indexAll) {
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        expandableListView.setGroupIndicator(null);
        menuList.clear();
        try {
            MenuEntity index = new MenuEntity();
            index.setTitle("流程审批");
            index.setId("1");
            List<MenuEntity> indexLC = new ArrayList<>();
            for (int i = 0; i < indexAll.size(); i++) {
                if (indexAll.get(i).getId().equals("92e44b6a-027c-4cd5-b35e-f90d29fe093f")) {
                    indexLC.add(indexAll.get(i));
                }
                if (indexAll.get(i).getId().equals("aa7f6c21-5227-4f4b-832e-e04b34a1389e")) {
                    indexLC.add(indexAll.get(i));
                }
                if (indexAll.get(i).getId().equals("a708b6d3-b5f5-439e-9544-5dc0508fc34b")) {
                    indexLC.add(indexAll.get(i));
                }
                if (indexAll.get(i).getId().equals("0c4ad7d6-cb7b-4a27-9adb-fbb82dbfe67f")) {
                    indexLC.add(indexAll.get(i));
                }
                if (indexAll.get(i).getId().equals("3d8b4e65-09b9-4731-ba97-6b3b1e317290")) {
                    indexLC.add(indexAll.get(i));
                }
            }
            for (int i = 0; i < indexLC.size(); i++) {
                for (int j = 0; j < indexSelect.size(); j++) {
                    if (indexLC.get(i).getTitle().equals(indexSelect.get(j).getTitle())) {
                        indexLC.get(i).setSelect(true);
                    }
                }
            }
            index.setChilds(indexLC);
            menuList.add(index);

            MenuEntity index1 = new MenuEntity();
            index1.setTitle("绩效考核");
            index1.setId("1");

            List<MenuEntity> indexJX = new ArrayList<MenuEntity>();
            for (int i = 0; i < indexAll.size(); i++) {
                if (indexAll.get(i).getId().equals("ac888f31-8392-4820-9254-49b11f71e2d3")) {
                    indexJX.add(indexAll.get(i));
                }
                if (indexAll.get(i).getId().equals("afce4ddf-194a-492a-b4ce-db79fd14801f")) {
                    indexJX.add(indexAll.get(i));
                }
                if (indexAll.get(i).getId().equals("8b2abd6b-18c2-4f8b-9990-b2d45f1aa91b")) {
                    indexJX.add(indexAll.get(i));
                }
                if (indexAll.get(i).getId().equals("f5462bb1-7151-4d1c-8d8e-d3653dc53e9a")) {
                    indexJX.add(indexAll.get(i));
                }
                if (indexAll.get(i).getId().equals("13673a54-fa67-4f02-aeea-e4725ffbc853")) {
                    indexJX.add(indexAll.get(i));
                }
                if (indexAll.get(i).getId().equals("14c0f70a-5f6a-47c9-9ea4-4356773aa225")) {
                    indexJX.add(indexAll.get(i));
                }
                if (indexAll.get(i).getId().equals("e924e4a9-0698-4624-8947-66cf883e8809")) {
                    indexJX.add(indexAll.get(i));
                }
            }
            for (int i = 0; i < indexJX.size(); i++) {
                for (int j = 0; j < indexSelect.size(); j++) {
                    if (indexJX.get(i).getTitle().equals(indexSelect.get(j).getTitle())) {
                        indexJX.get(i).setSelect(true);
                    }
                }
            }
            index1.setChilds(indexJX);
            menuList.add(index1);

            MenuEntity index2 = new MenuEntity();
            index2.setTitle("其他");
            index2.setId("2");

            List<MenuEntity> indexQT = new ArrayList<MenuEntity>();
            for (int i = 0; i < indexAll.size(); i++) {
                if (indexAll.get(i).getId().equals("1437cd9c-4595-46cb-8fde-e866e43f0825")) {
                    indexQT.add(indexAll.get(i));
                }
                if (indexAll.get(i).getId().equals("1cd85fc6-0b69-4f04-aa79-883c6ba8649e")) {
                    indexQT.add(indexAll.get(i));
                }
                if (indexAll.get(i).getId().equals("a4f08830-adaa-4412-9adf-55b9e773118e")) {
                    indexQT.add(indexAll.get(i));
                }
            }
            for (int i = 0; i < indexQT.size(); i++) {
                for (int j = 0; j < indexSelect.size(); j++) {
                    if (indexQT.get(i).getTitle().equals(indexSelect.get(j).getTitle())) {
                        indexQT.get(i).setSelect(true);
                    }
                }
            }
            index2.setChilds(indexQT);
            menuList.add(index2);

            menuParentAdapter = new MenuParentAdapter(MenuManageActivity.this, menuList);
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
                    }
                    return false;
                }
            });

        } catch (Exception e) {
        }
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
        indexSelect.add(menuEntity);
        String key = Config.KEY_USER_TEMP;
        FileUtils.saveObject(getApplicationContext(), (Serializable) indexSelect, key);

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
