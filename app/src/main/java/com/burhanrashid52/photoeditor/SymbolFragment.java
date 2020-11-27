package com.burhanrashid52.photoeditor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.burhanrashid52.photoeditor.ApplicationClass.ApplicationClass;
import com.burhanrashid52.photoeditor.MyViewModel.BackgroundViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class SymbolFragment extends BottomSheetDialogFragment {
    private BackgroundViewModel model;
    ArrayList<byte[]> image= new ArrayList<>();
    ArrayList<ArrayList<String>> allTemplates1;
    RecyclerView rvitemEmoji;
    ArrayList<String> names ;
    View contentView;
    public SymbolFragment() {
        // Required empty public constructor
    }

     SymbolListener mSymbolListener;

    public void setSymbolListener(SymbolListener stickerListener) {
        mSymbolListener = stickerListener;
    }

    public interface SymbolListener {
        void onSymbolChnage(Bitmap bitmap);
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
            contentView.setAlpha(slideOffset);
        }
    };


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, BottomSheetDialogFragment.STYLE_NORMAL);
         contentView = View.inflate(getContext(), R.layout.fragment_bottom_symbol_dialog, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();


        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        RecyclerView rvEmoji = contentView.findViewById(R.id.rvEmoji);
        rvitemEmoji = contentView.findViewById(R.id.rvitemEmoji);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvitemEmoji.setLayoutManager(linearLayoutManager);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvEmoji.setLayoutManager(linearLayoutManager1);
        image= new ArrayList<>();

        names = ApplicationClass.getNames();
        allTemplates1 =   ApplicationClass.getGrupedSym();



        StickerAdapter stickerAdapter = new StickerAdapter(names);
        rvEmoji.setAdapter(stickerAdapter);

       setAdapter(0);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {
        int lastPosition = 0;

//        int[] stickerList = new int[]{R.drawable.bg_1, R.drawable.bg_2};
        private ArrayList<String> image;

        public StickerAdapter(ArrayList<String> image) {
            this.image = image;
            Log.d("TAG", "StickerAdapter: "+image);
        }
//        byte[] stickerList =



        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sticker, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if(position == lastPosition)
            {

                holder.SelectedlinearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            if(position!=lastPosition) {

                holder.SelectedlinearLayout.setBackgroundColor(Color.parseColor("#000000"));
            }
//            Bitmap decodedImage = BitmapFactory.decodeByteArray(image.get(position), 0, image.get(position).length);
//
//            holder.imgSticker.setImageBitmap(decodedImage);
            Log.d("TAG", "onBindViewHolder:DATA "+image.get(position));
            holder.txSticker.setText(image.get(position));
//            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
//            holder.rvEmoji.setLayoutManager(gridLayoutManager);
//            allTemplates1 =   ApplicationClass.getGrupedSym();
            holder.txSticker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lastPosition =position;
                    Log.d("TAG", "onClick: "+lastPosition);

                    setAdapter(position);
                    notifyDataSetChanged();

                }
            });

            

        }

        @Override
        public int getItemCount() {
            Log.d("TAG", "getItemCount() called StickerAdapter"+image.size());
            return image.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;
            TextView txSticker;
            RecyclerView rvEmoji;
            LinearLayout SelectedlinearLayout;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);
                txSticker = itemView.findViewById(R.id.txSticker);
                rvEmoji =  itemView.findViewById(R.id.rvEmoji);
                SelectedlinearLayout = itemView.findViewById(R.id.SelectedlinearLayout);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        model = new ViewModelProvider(requireActivity()).get(BackgroundViewModel.class);
                        if (mSymbolListener != null) {

//                            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(image.get(getLayoutPosition()), 0, image.get(getLayoutPosition()).length);
                           /* mStickerListener.onBackgrounChnage(
                                    BitmapFactory.decodeResource(getResources(),
                                            image.get(getLayoutPosition())));*/
//                           mSymbolListener.onSymbolChnage(decodedBitmap);
                        }
                        dismiss();
                    }
                });
            }
        }
    }

    private void setAdapter(int i) {
        StickerItemAdapter stickeritemAdapter = new StickerItemAdapter(allTemplates1.get(i));
        rvitemEmoji.setAdapter(stickeritemAdapter);


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


    public class StickerItemAdapter extends RecyclerView.Adapter<StickerItemAdapter.ViewHolder> {

        //        int[] stickerList = new int[]{R.drawable.bg_1, R.drawable.bg_2};
        private ArrayList<String> image;

        public StickerItemAdapter(ArrayList<String> image) {
            this.image = image;
        }
//        byte[] stickerList =



        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_sticker, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            Log.d("TAG", "onBindViewHolder: StickeritemAdapter "+image.get(position));
//            Bitmap decodedImage = BitmapFactory.decodeByteArray(image.get(position).getBytes(), 0, image.get(position).length());

            byte [] encodeByte = Base64.decode(image.get(position),Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            Log.d("TAG", "onBindViewHolder: StickeritemAdapter decodedImage "+bitmap);
            holder.imgSticker.setImageBitmap(bitmap);



        }

        @Override
        public int getItemCount() {
//            Log.d("TAG", "getItemCount() called StickerItemAdapter"+image.size());
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
                        if (mSymbolListener != null) {

                            byte [] encodeByte = Base64.decode(image.get(getLayoutPosition()),Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(image.get(getLayoutPosition()).getBytes(), 0, image.get(getLayoutPosition()).length());
                         /*   mSymbolListener.onSymbolChnage(
                                    BitmapFactory.decodeResource(getResources(),
                                            Integer.parseInt(image.get(getLayoutPosition()))));*/
                           mSymbolListener.onSymbolChnage(bitmap);
                        }
                        dismiss();
                    }
                });
            }
        }
    }
}
