package com.github.jeterlee.alipayhome.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.orhanobut.logger.Logger;

/**
 * <pre>
 * Title: DragScrollView
 * Description: 后续扩展功能，此处没有进行任何处理，可以不使用
 * </pre>
 *
 * @author <a href="https://www.github.com/jeterlee"></a>
 * @date 2019/2/23 0023
 */
public class DragScrollView extends ScrollView implements DragCallback {
    private boolean isDrag;

    public DragScrollView(Context context) {
        super(context);
    }

    public DragScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("isDrag", ev.getAction() + "");
        if (ev.getAction() == MotionEvent.ACTION_DOWN
                || ev.getAction() == MotionEvent.ACTION_UP
                || ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (isDrag) {
                return false;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void startDrag(int position) {
        Logger.i("start drag at ", "" + position);
        isDrag = true;
    }

    @Override
    public void endDrag(int position) {
        Logger.i("end drag at ", "" + position);
        isDrag = false;
    }
}

