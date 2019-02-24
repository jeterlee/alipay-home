package com.github.jeterlee.alipayhome.adapter;

import android.content.Context;

import com.github.jeterlee.alipayhome.entity.MenuEntity;

/**
 * <pre>
 * Title: CategoryManager
 * Description: 菜单管理
 * </pre>
 *
 * @author <a href="https://www.github.com/jeterlee"></a>
 * @date 2019/2/23 0023
 */
public interface CategoryManager {
    /**
     * 获取 context
     *
     * @return context
     */
    Context getContext();

    /**
     * 获取 Item
     *
     * @param cateModel
     */
    void getItem(MenuEntity cateModel);

    /**
     * 增加菜单
     *
     * @param menuEntity
     */
    void addMenu(MenuEntity menuEntity);

    /**
     * 删除菜单
     *
     * @param indexData
     * @param position
     */
    void deleteMenu(MenuEntity indexData, int position);
}
