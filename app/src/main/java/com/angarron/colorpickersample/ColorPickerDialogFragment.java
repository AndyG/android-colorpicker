package com.angarron.colorpickersample;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import com.angarron.colorpicker.OnColorSelectedListener;
import com.angarron.colorpicker.PaletteAdapter;

public class ColorPickerDialogFragment extends DialogFragment implements OnColorSelectedListener {
    private static final String ARG_COLORS_ARRAY_KEY = "colorsArray";

    public static ColorPickerDialogFragment create(int[] colors) {
        ColorPickerDialogFragment colorPickerDialogFragment = new ColorPickerDialogFragment();
        Bundle args = new Bundle();
        args.putIntArray(ARG_COLORS_ARRAY_KEY, colors);
        colorPickerDialogFragment.setArguments(args);
        return colorPickerDialogFragment;
    }

    public ColorPickerDialogFragment() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new  AlertDialog.Builder(getActivity())
            .setTitle("Pick a color")
            .setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
            );

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.color_picker_dialog, null);
        GridView colorsGridView = (GridView) view.findViewById(R.id.color_picker_gridview);
        PaletteAdapter paletteAdapter = new PaletteAdapter.Builder(getContext())
            .setItemSizeDp(60)
            .build();

        paletteAdapter.configure(colorsGridView);
        paletteAdapter.setOnColorSelectedListener(this);

        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onColorSelected(@ColorInt int color) {
        FragmentActivity host = getActivity();
        if (host != null && host instanceof OnColorSelectedListener) {
            ((OnColorSelectedListener) host).onColorSelected(color);
        }
        dismiss();
    }
}
