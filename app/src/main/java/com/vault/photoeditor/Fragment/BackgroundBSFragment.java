package com.vault.photoeditor.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.vault.photoeditor.ApplicationClass.ApplicationClass;
import com.vault.photoeditor.Models.AllTemplates;
import com.vault.photoeditor.MyViewModel.BackgroundViewModel;
import com.vault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
//import com.vault.photoeditor.Activity.Constants;
import com.vault.photoeditor.Const.*;

import java.util.ArrayList;

public class BackgroundBSFragment extends BottomSheetDialogFragment {
    private BackgroundViewModel model;
    ArrayList<byte[]> image= new ArrayList<>();
    public BackgroundBSFragment() {
        // Required empty public constructor
    }

    private BackgroundListener mStickerListener;
    private SeekBarListener mSeekBarListener;
    private HueBarListener mHueBarListener;

    public void setBackgroundListener(BackgroundListener stickerListener) {
        mStickerListener = stickerListener;
    }

    public void setSeekChangeListener(SeekBarListener stickerListener) {
        mSeekBarListener = stickerListener;
    }
    public void setHueChangeListener(HueBarListener hueBarListener) {
        mHueBarListener = hueBarListener;
    }
    public interface BackgroundListener {
        void onBackgrounChnage(Bitmap bitmap);
    }
    public interface SeekBarListener {
        void onSeekChnage(int fl);
    }
    public interface HueBarListener {
        void onHueChnage(int fl);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_background_dialog, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        //seekBar

        SeekBar seekBar=contentView.findViewById(R.id.seekBar);
        seekBar.setProgress(10);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                mSeekBarListener.onSeekChnage(progress);
//                Toast.makeText(getApplicationContext(),"seekbar progress: "+progress, Toast.LENGTH_SHORT).show();

         /*       if(
                        progress==100
                ){
                    b=100;
                }
               int c = (progress*b)/progress;
                progresss =(float) c/100;

                mPhotoEditorView.getSource().setAlpha((float)progresss);
                Log.d("TAG", "onProgressChanged: "+progress+" "+(float)progresss);*/
//                decresevalueB(b);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
            }
        });


        SeekBar hueseekBar=contentView.findViewById(R.id.hue);
        //hue
        hueseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                mHueBarListener.onHueChnage(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
            }
        });
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        RecyclerView rvEmoji = contentView.findViewById(R.id.rvEmoji);

        LinearLayoutManager llmTools = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvEmoji.setLayoutManager(llmTools);
        image= new ArrayList<>();
        if(ApplicationClass.getOrientattion().equals(Constants.PORTRAIT)){
        ArrayList<AllTemplates> allTemplates1 =   ApplicationClass.getAllTemplates1();
        for (AllTemplates a: allTemplates1) {
            image.add(a.getBitmap());

        }
        }

        if(ApplicationClass.getOrientattion().equals(Constants.LANDSCAPE)){
            ArrayList<AllTemplates> allTemplates1 =   ApplicationClass.getAllTemplatesLan();
            for (AllTemplates a: allTemplates1) {
                image.add(a.getBitmap());

            }
        }
//
        StickerAdapter stickerAdapter = new StickerAdapter(getContext(),image);
        rvEmoji.setAdapter(stickerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {
        View view;
        private Context context;
        private ArrayList<byte[]> image;

        public StickerAdapter(Context context, ArrayList<byte[]> image) {
            this.context = context;
            this.image = image;
            Log.d("TAG", "StickerAdapter: "+image);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           if(ApplicationClass.getOrientattion().equals(Constants.LANDSCAPE)) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_back, parent, false);
            }
           else {
               view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sticker_pro, parent, false);

           }

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Bitmap decodedImage = BitmapFactory.decodeByteArray(image.get(position), 0, image.get(position).length);
            Glide.with(context).asBitmap().load(decodedImage).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                    holder.imgSticker.setImageBitmap(bitmap);
                }
                @Override
                public void onLoadCleared(Drawable placeholder) {
                }
            });
        }

        @Override
        public int getItemCount() {
            return image.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        model = new ViewModelProvider(requireActivity()).get(BackgroundViewModel.class);
                        if (mStickerListener != null) {

                            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(image.get(getLayoutPosition()), 0, image.get(getLayoutPosition()).length);
                           mStickerListener.onBackgrounChnage(decodedBitmap);
                        }
                        dismiss();
                    }
                });
            }
        }
    }

    private String convertEmoji(String emoji) {
        String returnedEmoji = "";
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = getEmojiByUnicode(convertEmojiToInt);
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}
