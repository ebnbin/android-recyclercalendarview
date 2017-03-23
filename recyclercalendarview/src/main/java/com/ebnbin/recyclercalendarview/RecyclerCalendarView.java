package com.ebnbin.recyclercalendarview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 使用 {@link RecyclerView} 展示日历.
 */
public class RecyclerCalendarView extends FrameLayout {
    public RecyclerCalendarView(@NonNull Context context) {
        super(context);

        init();
    }

    public RecyclerCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public RecyclerCalendarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public RecyclerCalendarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private RecyclerView mCalendarRecyclerView;

    private GridLayoutManager mLayoutManager;
    private Adapter mAdapter;

    private void init() {
        Res.init(getContext());

        inflate(getContext(), R.layout.view_recycler_calendar, this);

        mCalendarRecyclerView = (RecyclerView) findViewById(R.id.calendar);

        mLayoutManager = new GridLayoutManager(getContext(), 7);
        mCalendarRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new Adapter();
        mAdapter.listeners.add(new Adapter.Listener() {
            @Override
            public void onDayClick(int position) {
                super.onDayClick(position);

                selectPosition(position, true);
            }
        });
        mCalendarRecyclerView.setAdapter(mAdapter);
    }

    //*****************************************************************************************************************
    // Range.

    /**
     * 设置年月范围.
     *
     * @param yearMonthFrom
     *         开始年月.
     * @param yearMonthTo
     *         结束年月.
     */
    public void setRange(@NonNull int[] yearMonthFrom, @NonNull int[] yearMonthTo) {
        List<Entity> calendarEntities = Entity.newCalendarEntities(yearMonthFrom, yearMonthTo);
        mAdapter.setNewData(calendarEntities);
    }

    //*****************************************************************************************************************
    // Selected.

    /**
     * 当前选中的位置.
     */
    private int mSelectedPosition = -1;

    /**
     * 选中某日期.
     */
    public void selectDate(@NonNull int[] date) {
        selectPosition(getPosition(date), false);
    }

    /**
     * 返回指定日期的位置, 如果没找到则返回 -1.
     */
    private int getPosition(@NonNull int[] date) {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            Entity entity = mAdapter.getItem(i);
            if (entity instanceof Entity.Day
                    && Arrays.equals(((Entity.Day) entity).date, date)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 选中某位置.
     */
    private void selectPosition(int position, boolean callback) {
        if (mSelectedPosition == position) {
            return;
        }

        if (mSelectedPosition != -1) {
            setPositionSelected(mSelectedPosition, false);
            mSelectedPosition = -1;
        }

        if (position == -1) {
            return;
        }

        setPositionSelected(position, true);
        mSelectedPosition = position;

        if (callback) {
            onSelected(position);
        }
    }

    /**
     * 设置位置的选中状态.
     */
    private void setPositionSelected(int position, boolean selected) {
        Entity entity = mAdapter.getItem(position);
        if (!(entity instanceof Entity.Day)) {
            return;
        }

        Entity.Day dayEntity = (Entity.Day) entity;
        if (dayEntity.selected == selected) {
            return;
        }

        dayEntity.selected = selected;
        mAdapter.notifyItemChanged(position);
    }

    //*****************************************************************************************************************
    // Scroll.

    private int mScrollToPosition = -1;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        scrollToPosition(mScrollToPosition);
    }

    /**
     * 滚动到选中到位置, 如果没有选中到位置则不滚动.
     */
    public void scrollToSelected() {
        scrollToPosition(mSelectedPosition);
    }

    /**
     * 滚动到指定的位置, 如果为 -1 则不滚动.
     */
    private void scrollToPosition(int position) {
        mScrollToPosition = position;

        if (mScrollToPosition == -1) {
            return;
        }

        int height = mCalendarRecyclerView.getMeasuredHeight();
        if (height <= 0) {
            return;
        }

        int offset = (height - Res.getInstance().dimen_size_day) / 2;
        mLayoutManager.scrollToPositionWithOffset(mScrollToPosition, offset);
        mScrollToPosition = -1;
    }

    //*****************************************************************************************************************
    // Callbacks and listeners.

    public final List<Listener> listeners = new ArrayList<>();

    /**
     * 监听.
     */
    public interface Listener {
        void onSelected(int[] date);
    }

    /**
     * 回调.
     */
    private void onSelected(int position) {
        Entity entity = mAdapter.getItem(position);
        if (!(entity instanceof Entity.Day)) {
            return;
        }

        Entity.Day dayEntity = (Entity.Day) entity;
        int[] date = dayEntity.date;

        for (Listener listener : listeners) {
            listener.onSelected(date);
        }
    }
}
