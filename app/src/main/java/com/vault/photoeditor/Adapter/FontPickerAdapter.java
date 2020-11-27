package com.vault.photoeditor.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.vault.photoeditor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed Adel on 5/8/17.
 */

public class FontPickerAdapter extends RecyclerView.Adapter<FontPickerAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<String> colorPickerColors;
    private OnFontPickerClickListener onFontListener;

    FontPickerAdapter(@NonNull Context context, @NonNull List<Integer> colorPickerColorss) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        colorPickerColors = new ArrayList<>();
        colorPickerColors.add("Montserrat-Black.ttf");
        colorPickerColors.add("Montserrat-BlackItalic.ttf");
        colorPickerColors.add("Montserrat-Bold.ttf");
        colorPickerColors.add("Montserrat-BoldItalic.ttf");
        colorPickerColors.add("Montserrat-ExtraBoldItalic.ttf");
        colorPickerColors.add("Montserrat-ExtraLight.ttf");
        colorPickerColors.add("Montserrat-ExtraLightItalic.ttf");
        colorPickerColors.add("Montserrat-ThinItalic.ttf");
        colorPickerColors.add("Montserrat-MediumItalic.ttf");
        colorPickerColors.add("Montserrat-LightItalic.ttf");
        colorPickerColors.add("OpenSans-Bold.ttf");

        colorPickerColors.add("OpenSans-BoldItalic.ttf");
        colorPickerColors.add("OpenSans-ExtraBold.ttf");
        colorPickerColors.add("OpenSans-ExtraBoldItalic.ttf");
        colorPickerColors.add("OpenSans-Italic.ttf");
        colorPickerColors.add("OpenSans-Regular.ttf");

        colorPickerColors.add("Romanesco-Regular.ttf");
        colorPickerColors.add("Michroma-Regular.ttf");
        colorPickerColors.add("Roboto-BlackItalic.ttf");
        colorPickerColors.add("Roboto-Bold.ttf");
        colorPickerColors.add("Roboto-BoldItalic.ttf");
        colorPickerColors.add("Roboto-MediumItalic.ttf");
    }

    public FontPickerAdapter(@NonNull Context context) {
        this(context, getDefaultColors(context));
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.font_picker_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.colorPickerView.setBackgroundColor(colorPickerColors.get(position));
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/"+colorPickerColors.get(position));
        holder.colorPickerView.setText("Select font type");
        holder.colorPickerView.setTextSize(15f);
        holder.colorPickerView.setTypeface(typeface);
    }

    @Override
    public int getItemCount() {
        return colorPickerColors.size();
    }

    private void buildColorPickerView(View view, int colorCode) {
        view.setVisibility(View.VISIBLE);

        ShapeDrawable biggerCircle = new ShapeDrawable(new OvalShape());
        biggerCircle.setIntrinsicHeight(20);
        biggerCircle.setIntrinsicWidth(20);
        biggerCircle.setBounds(new Rect(0, 0, 20, 20));
        biggerCircle.getPaint().setColor(colorCode);

        ShapeDrawable smallerCircle = new ShapeDrawable(new OvalShape());
        smallerCircle.setIntrinsicHeight(5);
        smallerCircle.setIntrinsicWidth(5);
        smallerCircle.setBounds(new Rect(0, 0, 5, 5));
        smallerCircle.getPaint().setColor(Color.WHITE);
        smallerCircle.setPadding(10, 10, 10, 10);
        Drawable[] drawables = {smallerCircle, biggerCircle};

        LayerDrawable layerDrawable = new LayerDrawable(drawables);

        view.setBackgroundDrawable(layerDrawable);
    }

    public void setOnFontPickerClickListener(OnFontPickerClickListener onColorPickerClickListener) {
        this.onFontListener = onColorPickerClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView colorPickerView;

        public ViewHolder(View itemView) {
            super(itemView);
            colorPickerView = itemView.findViewById(R.id.color_picker_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onFontListener != null)
                        onFontListener.onFontPickerClickListener(colorPickerColors.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnFontPickerClickListener {
        void onFontPickerClickListener(String colorCode);
    }

    public static List<Integer> getDefaultColors(Context context) {
        ArrayList<Integer> colorPickerColors = new ArrayList<>();
        colorPickerColors.add(ContextCompat.getColor(context, R.color.blue_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.brown_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.green_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.orange_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.red_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.black));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.red_orange_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.sky_blue_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.violet_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.white));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.yellow_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.yellow_green_color_picker));
        return colorPickerColors;
    }
}
