package com.example.uipractice.recyclerview.callback;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

class ItemTouchCallback extends ItemTouchHelper.Callback {
    RecyclerView recyclerView;

    List list;
    public ItemTouchCallback(List list) {
        this.list = list;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        this.recyclerView = recyclerView;
        //控制拖拽的方向（一般是上下左右）
        int dragFlags= ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        //控制快速滑动的方向（一般是左右）
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        return makeMovementFlags(dragFlags, swipeFlags);//计算movement flag值
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // 拖拽时，每移动一个位置就会调用一次。

        if (recyclerView == null){
            return false;
        }

        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null){
            return false;
        }

        if (list != null && list.size() > 0) {
            //获取被拖拽的Item的Position
            int from = viewHolder.getAdapterPosition();
            //获取目标Item的Position
            int endPosition = target.getAdapterPosition();
            //交换List集合中两个元素的位置
            Collections.swap(list, from, endPosition);
            //交换界面上两个Item的位置
            adapter.notifyItemMoved(from, endPosition);
        }
        return true;
    }

    @Override
    public float getMoveThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        //设置拖拽距离百分比
        //如果RecyclerView是垂直方向，那么当拖拽到当前Item高度的50%时开始执行onMove方法
        return .5f;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        //滑动处理
        int position = viewHolder.getAdapterPosition();
        if(list != null && list.size() > 0){
            //删除List中对应的数据
            list.remove(position);

            if (recyclerView.getAdapter() == null){
                return;
            }
            //刷新页面
            recyclerView.getAdapter().notifyItemRemoved(position);

        }
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
    }


    private Paint paint;
    private RectF rect;

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        //Item底部底部绘制图形
        c.translate(-dX, 0);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);

        //在布局中，我设置的是4dp，这里需要转成px
        float padding = 4 * recyclerView.getContext().getResources().getDisplayMetrics().density + 0.5f;

        //获取X轴Item位置
        float x = viewHolder.itemView.getX() + padding;
        float y = viewHolder.itemView.getY() + padding;
        float width = viewHolder.itemView.getWidth() - 2 * padding;
        float height = viewHolder.itemView.getHeight() - 2 * padding;
        rect = new RectF(x, y, x + width, y + height);
        c.drawRect(rect, paint);

    }


    @Override
    public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        return 1000;
    }


    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //Item上面绘制图形
        //c.translate(-dX, 0);
        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        //在布局中，我设置的是4dp，这里需要转成px
        float padding = 4 * recyclerView.getContext().getResources().getDisplayMetrics().density + 0.5f;

        //获取X轴Item位置
        float x = viewHolder.itemView.getX() + padding;
        float y = viewHolder.itemView.getY() + padding;
        float width = viewHolder.itemView.getWidth() - 2 * padding;
        float height = viewHolder.itemView.getHeight() - 2 * padding;
        rect = new RectF(x + width - 200, y + height / 2 - 50, x + width - 100, y + height / 2 + 50);
        c.drawRect(rect, paint);
    }


    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        //返回值滑动消失的距离, 这里是相对于RecycleView的宽度，0.5f表示为RecycleView的宽度的一半，取值为0~1f之间
        return .5f;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        //返回值滑动消失的距离，滑动小于这个值不消失，大于消失，默认为屏幕的三分之一
        //defaultValue默认值为120dp，转成px为 defaultValue * density
        return defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        //设置滑动速率，这里的速率是指以一定的速率滑动Item，松开时Item依然从某一个方向移动的速率（可以理解为惯性的速率）
        //defaultValue默认值为800dp，转成px为 defaultValue * density
        return defaultValue;
    }
}
