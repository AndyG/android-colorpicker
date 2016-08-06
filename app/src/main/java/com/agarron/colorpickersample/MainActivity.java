package com.agarron.colorpickersample;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.agarron.colorpicker.OnColorSelectedListener;
import com.agarron.colorpicker.PaletteAdapter;

public class MainActivity extends AppCompatActivity implements OnColorSelectedListener {

    private static final String TAG_COLOR_PICKER = "TAG_COLOR_PICKER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView colorsGridView = (GridView) findViewById(R.id.colors_gridview);
        new PaletteAdapter.Builder(this)
            .setItemSizeDp(80)
            .build()
            .configure(colorsGridView);

        findViewById(R.id.show_picker_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchColorPicker();
            }
        });
    }

    private void launchColorPicker() {
        int[] colors = getColors();
        ColorPickerDialogFragment.create(colors).show(getSupportFragmentManager(), TAG_COLOR_PICKER);
    }

    private int[] getColors() {
        int[] colors = new int[defaultColorResArray.length];
        for (int i = 0; i < defaultColorResArray.length; i++) {
            colors[i] = getResources().getColor(defaultColorResArray[i]);
        }
        return colors;
    }

    private static int[] defaultColorResArray = {
        android.R.color.holo_green_light,
        android.R.color.holo_green_dark,
        android.R.color.holo_blue_light,
        android.R.color.holo_blue_dark,
        android.R.color.holo_red_light,
        android.R.color.holo_red_dark
    };

    @Override
    public void onColorSelected(@ColorInt int color) {
        Toast.makeText(this, "Selected color: " + Integer.toHexString(color), Toast.LENGTH_SHORT).show();
    }
}
