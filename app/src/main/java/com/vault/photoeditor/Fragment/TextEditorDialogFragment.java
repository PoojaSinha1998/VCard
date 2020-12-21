package com.vault.photoeditor.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vault.photoeditor.Adapter.ColorPickerAdapter;
import com.vault.photoeditor.Adapter.FontPickerAdapter;
import com.vault.photoeditor.R;

/**
 * Created by Burhanuddin Rashid on 1/16/2018.
 */

public class TextEditorDialogFragment extends DialogFragment {

    public static final String TAG = TextEditorDialogFragment.class.getSimpleName();
    public static final String EXTRA_INPUT_TEXT = "extra_input_text";
    public static final String EXTRA_COLOR_CODE = "extra_color_code";
    private EditText mAddTextEditText;
    private TextView mAddTextDoneTextView;
    private InputMethodManager mInputMethodManager;
    private int mColorCode;
    private Typeface mFontCode;
    private TextEditor mTextEditor;
    private RecyclerView add_font_familiy;

    public interface TextEditor {
        void onDone(String inputText, int colorCode,Typeface typeface);
    }


    //Show dialog with provide text and text color
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity,
                                                @NonNull String inputText,
                                                @ColorInt int colorCode) {
        Bundle args = new Bundle();
        args.putString(EXTRA_INPUT_TEXT, inputText);
        args.putInt(EXTRA_COLOR_CODE, colorCode);
        TextEditorDialogFragment fragment = new TextEditorDialogFragment();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return fragment;
    }

    //Show dialog with default text input as empty and text color white
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity) {
        return show(appCompatActivity,
                "", ContextCompat.getColor(appCompatActivity, R.color.white));
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        //Make dialog full screen with transparent background
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_text_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddTextEditText = view.findViewById(R.id.add_text_edit_text);
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mAddTextDoneTextView = view.findViewById(R.id.add_text_done_tv);
        ImageView im_bold =view.findViewById(R.id.imgbold);
        ImageView im_italic =view.findViewById(R.id.imgitalic);
        ImageView im_underline =view.findViewById(R.id.imgunder);
        im_bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAddTextEditText.getTypeface().equals(Typeface.DEFAULT_BOLD)) {
                    mAddTextEditText.setTypeface(mAddTextEditText.getTypeface(), Typeface.NORMAL);
                }
                else {

                    mAddTextEditText.setTypeface(mAddTextEditText.getTypeface(), Typeface.BOLD);
                }
            }
        });
        im_underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAddTextEditText.setTypeface(mAddTextEditText.getTypeface(), Typeface.NORMAL);

            }
        });
        im_italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAddTextEditText.getTypeface().equals(Typeface.ITALIC)) {
                    mAddTextEditText.setTypeface(mAddTextEditText.getTypeface(), Typeface.NORMAL);
                }
                else {

                    mAddTextEditText.setTypeface(mAddTextEditText.getTypeface(), Typeface.ITALIC);
                }
            }
        });


        add_font_familiy = view.findViewById(R.id.add_font_familiy);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        add_font_familiy.setLayoutManager(layoutManager1);
        add_font_familiy.setHasFixedSize(true);
        FontPickerAdapter fontPickerAdapter = new FontPickerAdapter(getActivity());
        fontPickerAdapter.setOnFontPickerClickListener(new FontPickerAdapter.OnFontPickerClickListener() {
            @Override
            public void onFontPickerClickListener(String colorCode) {
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "font/"+colorCode);
                mAddTextEditText.setTypeface(typeface);
                mFontCode = typeface;

            }
        });

        add_font_familiy.setAdapter(fontPickerAdapter);
        //Setup the color picker for text color
        RecyclerView addTextColorPickerRecyclerView = view.findViewById(R.id.add_text_color_picker_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        addTextColorPickerRecyclerView.setLayoutManager(layoutManager);
        addTextColorPickerRecyclerView.setHasFixedSize(true);
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(getActivity());
        //This listener will change the text color when clicked on any color from picker
        colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
            @Override
            public void onColorPickerClickListener(int colorCode, int adapterPosition) {
                mColorCode = colorCode;
                mAddTextEditText.setTextColor(colorCode);
            }
        });
        addTextColorPickerRecyclerView.setAdapter(colorPickerAdapter);
        mAddTextEditText.setText(getArguments().getString(EXTRA_INPUT_TEXT));
        mColorCode = getArguments().getInt(EXTRA_COLOR_CODE);
        mAddTextEditText.setTextColor(mColorCode);
        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //Make a callback on activity when user is done with text editing
        mAddTextDoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                dismiss();
                String inputText = mAddTextEditText.getText().toString();
                if (!TextUtils.isEmpty(inputText) && mTextEditor != null) {
                    mTextEditor.onDone(inputText, mColorCode,mFontCode);
                }
            }
        });

    }


    //Callback to listener if user is done with text editing
    public void setOnTextEditorListener(TextEditor textEditor) {
        mTextEditor = textEditor;
    }
}
