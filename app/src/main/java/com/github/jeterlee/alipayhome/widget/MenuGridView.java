package com.github.jeterlee.alipayhome.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/**
 * <pre>
 * Title: MenuManagerView
 * Description: 带有分隔线的 GridView，是菜单管理页面的 GridView
 * </pre>
 *
 * @author <a href="https://www.github.com/jeterlee"></a>
 * @date 2019/2/23 0023
 */
public class MenuManagerView extends GridView {

    public MenuManagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (getChildAt(0) != null) {
            View localView1 = getChildAt(0);
            int column = getWidth() / localView1.getWidth();
            int childCount = getChildCount();
            int row;
            if (childCount % column == 0) {
                row = childCount / column;
            } else {
                row = childCount / column + 1;
            }
            int endAllColumn = (row - 1) * column;
            Paint localPaint, localPaint2;
            localPaint = new Paint();
            localPaint2 = new Paint();
            localPaint.setStyle(Paint.Style.STROKE);
            localPaint2.setStyle(Paint.Style.STROKE);
            localPaint.setStrokeWidth(1);
            localPaint2.setStrokeWidth(1);
            localPaint.setColor(Color.parseColor("#f0f0ed"));
            localPaint2.setColor(Color.parseColor("#f0f0ed"));
            for (int i = 0; i < childCount; i++) {
                View cellView = getChildAt(i);
                if ((i + 1) % column != 0) {
                    canvas.drawLine(cellView.getRight(), cellView.getTop(),
                            cellView.getRight(), cellView.getBottom(),
                            localPaint);
                    canvas.drawLine(cellView.getRight() + 1, cellView.getTop(),
                            cellView.getRight() + 1, cellView.getBottom(),
                            localPaint2);
                }
                if ((i + 1) <= endAllColumn) {
                    canvas.drawLine(cellView.getLeft(), cellView.getBottom(),
                            cellView.getRight(), cellView.getBottom(),
                            localPaint);
                    canvas.drawLine(cellView.getLeft(),
                            cellView.getBottom() + 1, cellView.getRight(),
                            cellView.getBottom() + 1, localPaint2);
                }
            }
            if (childCount % column != 0) {
                for (int j = 0; j < (column - childCount % column); j++) {
                    View lastView = getChildAt(childCount - 1);
                    canvas.drawLine(lastView.getRight() + lastView.getWidth()
                                    * j, lastView.getTop(), lastView.getRight()
                                    + lastView.getWidth() * j, lastView.getBottom(),
                            localPaint);
                    canvas.drawLine(lastView.getRight() + lastView.getWidth()
                                    * j + 1, lastView.getTop(), lastView.getRight()
                                    + lastView.getWidth() * j + 1,
                            lastView.getBottom(), localPaint2);
                }
            }
        }
    }

}
