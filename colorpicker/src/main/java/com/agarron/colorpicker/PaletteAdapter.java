package com.agarron.colorpicker;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class PaletteAdapter extends BaseAdapter implements OnColorSelectedListener {

    private static float defaultItemSizeDp = 30;
    private static int[] defaultColorResArray = {
        android.R.color.holo_red_light,
        android.R.color.holo_red_dark,
        android.R.color.holo_blue_light,
        android.R.color.holo_blue_dark,
        android.R.color.holo_green_light,
        android.R.color.holo_green_dark
    };

    private final Context context;
    private final float itemSizeDP;

    @ColorInt
    private final int[] colorArray;

    private OnColorSelectedListener listener;

    private PaletteAdapter(final Context context, final @ColorInt int[] colorArray, final float itemSizeDP) {
        this.context = context;
        this.colorArray = colorArray;
        this.itemSizeDP = itemSizeDP;
    }

    public void configure(final GridView gridView) {
        gridView.setColumnWidth(dpToPx(itemSizeDP));
        gridView.setAdapter(this);
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return colorArray.length;
    }

    @Override
    public Object getItem(int i) {
        return colorArray[i];
    }

    @Override
    public long getItemId(int i) {
        //what is this??
        return 0;
    }

    @Override
    public View getView(int i, View oldView, ViewGroup viewGroup) {
        if (oldView == null) {
            ColorItem colorItem;
            // if it's not recycled, initialize some attributes
            colorItem = new ColorItem(context, colorArray[i], false);

            final int itemSizePX = dpToPx(itemSizeDP);
            colorItem.setLayoutParams(new GridView.LayoutParams(itemSizePX, itemSizePX));

            colorItem.setOnColorSelectedListener(this);
            return colorItem;
        } else {
            return oldView;
        }
    }

    private int dpToPx(final float numDPs) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, numDPs, r.getDisplayMetrics());
    }

    private static int[] getColorArray(final Context context, final int[] colorResArray) {
        final int[] colorArray = new int[colorResArray.length];
        for (int i = 0; i < colorResArray.length; i++) {
            colorArray[i] = context.getResources().getColor(colorResArray[i]);
        }
        return colorArray;
    }

    @Override
    public void onColorSelected(final @ColorInt int color) {
        if (listener != null) {
            listener.onColorSelected(color);
        }
    }

    public static class Builder {

        private final Context context;

        private @ColorInt int[] colorArray;
        private float itemSizeDp = defaultItemSizeDp;

        public Builder(final Context context) {
            this.context = context;
            colorArray = getColorArray(context, defaultColorResArray);
        }

        public Builder setColorResArray(final int[] colorResArray) {
            colorArray = new int[colorResArray.length];
            for (int i = 0; i < colorResArray.length; i++) {
                colorArray[i] = context.getResources().getColor(colorResArray[i]);
            }
            return this;
        }

        public Builder setItemSizeDp(final float itemSizeDp) {
            this.itemSizeDp = itemSizeDp;
            return this;
        }

        public PaletteAdapter build() {
            return new PaletteAdapter(context, colorArray, itemSizeDp);
        }
    }
}
