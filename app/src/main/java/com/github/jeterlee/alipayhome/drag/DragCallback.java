package com.github.jeterlee.alipayhome.drag;

/**
 * <pre>
 * Title: DragCallback
 * Description: 拖动操作
 * </pre>
 *
 * @author <a href="https://www.github.com/jeterlee"></a>
 * @date 2019/2/23 0023
 */
public interface DragCallback {
    /**
     * 开始拖动
     *
     * @param position
     */
    void startDrag(int position);

    /**
     * 结束拖动
     *
     * @param position
     */
    void endDrag(int position);
}
