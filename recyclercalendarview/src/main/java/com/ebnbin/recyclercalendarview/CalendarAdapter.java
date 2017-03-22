package com.ebnbin.recyclercalendarview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 日历 {@link RecyclerView.Adapter}.
 */
final class CalendarAdapter extends BaseMultiItemQuickAdapter<CalendarEntity, BaseViewHolder> {
    CalendarAdapter() {
        super(null);

        addItemType(CalendarEntity.MONTH, R.layout.item_month);
        addItemType(CalendarEntity.DAY, R.layout.item_day);
        addItemType(CalendarEntity.EMPTY_DAY, R.layout.item_empty_day);
    }

    @Override
    protected void convert(final BaseViewHolder helper, CalendarEntity item) {
        switch (helper.getItemViewType()) {
            case CalendarEntity.MONTH: {
                CalendarEntity.Month month = (CalendarEntity.Month) item;

                helper.setText(R.id.month, month.monthString);

                break;
            }
            case CalendarEntity.DAY: {
                CalendarEntity.Day day = (CalendarEntity.Day) item;

                helper.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (Listener listener : listeners) {
                            listener.onDayClick(helper.getLayoutPosition());
                        }
                    }
                });

                helper.getView(R.id.day_wrapper).setBackground(day.getBackground());

                helper.setText(R.id.day, day.dayString);
                helper.setTextColor(R.id.day, day.getTextColor());

                break;
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
        final int spanCount = gridLayoutManager.getSpanCount();

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = getItemViewType(position);
                if (itemViewType == CalendarEntity.MONTH) {
                    return spanCount;
                }

                if (spanSizeLookup != null) {
                    return spanSizeLookup.getSpanSize(position);
                }

                return 1;
            }
        });
    }

    //*****************************************************************************************************************
    // Listener.

    public final List<Listener> listeners = new ArrayList<>();

    static abstract class Listener {
        public void onDayClick(int position) {
        }
    }
}
