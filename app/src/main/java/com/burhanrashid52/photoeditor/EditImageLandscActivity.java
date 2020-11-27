package com.burhanrashid52.photoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.burhanrashid52.photoeditor.API.ApiInterface;
import com.burhanrashid52.photoeditor.MyViewModel.BackgroundViewModel;
import com.burhanrashid52.photoeditor.Request.SaveVisitingReq;
import com.burhanrashid52.photoeditor.Responses.UploadDesignedCardRes;
import com.burhanrashid52.photoeditor.base.BaseActivity;
import com.burhanrashid52.photoeditor.filters.FilterListener;
import com.burhanrashid52.photoeditor.filters.FilterViewAdapter;
import com.burhanrashid52.photoeditor.photoViewLib.OnPhotoEditorListener;
import com.burhanrashid52.photoeditor.photoViewLib.OnSaveBitmap;
import com.burhanrashid52.photoeditor.photoViewLib.PhotoEditor;
import com.burhanrashid52.photoeditor.photoViewLib.PhotoEditorView;
import com.burhanrashid52.photoeditor.photoViewLib.PhotoFilter;
import com.burhanrashid52.photoeditor.photoViewLib.TextStyleBuilder;
import com.burhanrashid52.photoeditor.photoViewLib.ViewType;
import com.burhanrashid52.photoeditor.tools.EditingToolsAdapter;
import com.burhanrashid52.photoeditor.tools.ToolType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditImageLandscActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected, FilterListener, BackgroundBSFragment.BackgroundListener, SymbolFragment.SymbolListener, BackgroundBSFragment.SeekBarListener, BackgroundBSFragment.HueBarListener {

    private static final String TAG = EditImageLandscActivity.class.getSimpleName();
    public static final String FILE_PROVIDER_AUTHORITY = "com.burhanrashid52.photoeditor.fileprovider";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private SymbolFragment msymbolFragment;
    private StickerBSFragment mStickerBSFragment;
    private BackgroundBSFragment mbackgroundBSFragment;
    private TextView mTxtCurrentTool;
    private Typeface mWonderFont;
    private RecyclerView mRvTools, mRvFilters;
    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;ImageView imgbrighteness;


    @Nullable
    @VisibleForTesting
    Uri mSaveImageUri;
    BackgroundViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_image_landsc);
        model = new ViewModelProvider(this).get(BackgroundViewModel.class);


        initViews();

        handleIntentImage(mPhotoEditorView.getSource());

        mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");

        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mbackgroundBSFragment =new BackgroundBSFragment();
        msymbolFragment = new SymbolFragment();
        msymbolFragment.setSymbolListener(this);
        mStickerBSFragment.setStickerListener(this);
        mbackgroundBSFragment.setHueChangeListener(this);
        mbackgroundBSFragment.setBackgroundListener(this);
        mbackgroundBSFragment.setSeekChangeListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);


        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);
        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

        //Set Image Dynamically
        // mPhotoEditorView.getSource().setImageResource(R.drawable.color_palette);
    }

    private void handleIntentImage(ImageView source) {
        Intent intent = getIntent();
        if (intent != null) {
            String intentType = intent.getType();
            if (intentType != null && intentType.startsWith("image/")) {
                Uri imageUri = intent.getData();
                if (imageUri != null) {
                    source.setImageURI(imageUri);
                }
            }
        }
    }

    private void initViews() {
        ImageView imgUndo;
        ImageView imgRedo;
        ImageView imgCamera;
        ImageView imgGallery;
        ImageView imgSave;
        ImageView imgClose;
        ImageView imgShare;

        ImageView imgbase;

        imgbase = findViewById(R.id.imgbase);
        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRvTools = findViewById(R.id.rvConstraintTools);
        mRvFilters = findViewById(R.id.rvFilterView);
        mRootView = findViewById(R.id.rootView);

        imgUndo = findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);

        imgRedo = findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);

        imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);

        imgGallery = findViewById(R.id.imgGallery);
        imgGallery.setOnClickListener(this);

        imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);

        imgShare = findViewById(R.id.imgShare);
        imgShare.setOnClickListener(this);

        Log.d(TAG, "initViews: "+model.getSelected());
        imgShare.setVisibility(View.GONE);


        imgbrighteness = findViewById(R.id.imgbrighteness);
    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(colorCode);

                mPhotoEditor.editText(rootView, inputText, styleBuilder);
                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgUndo:
                mPhotoEditor.undo();
                break;

            case R.id.imgRedo:
                mPhotoEditor.redo();
                break;

            case R.id.imgSave:
                saveImage();
                break;

            case R.id.imgClose:
                onBackPressed();
                break;
            case R.id.imgShare:
                shareImage();
                break;

            case R.id.imgCamera:
                /*Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);*/

                Intent intent = new Intent(EditImageLandscActivity.this, ImagePickerActivity.class);

                intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);
                // setting aspect ratio
                intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
                intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
                intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
                // setting maximum bitmap width and height
                intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, false);
                intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 550);
                intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 700);
                startActivityForResult(intent, CAMERA_REQUEST);
                break;

            case R.id.imgGallery:
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);*/
                Intent gintent = new Intent(EditImageLandscActivity.this, ImagePickerActivity.class);
                gintent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
                // setting aspect ratio
                gintent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
                gintent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
                gintent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
                // setting maximum bitmap width and height
                gintent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, false);
                gintent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 550);
                gintent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 700);
                startActivityForResult(gintent, PICK_REQUEST);
                break;
        }
    }

    private void shareImage() {
        if (mSaveImageUri == null) {
            showSnackbar(getString(R.string.msg_save_image_to_share));
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, buildFileProviderUri(mSaveImageUri));
        startActivity(Intent.createChooser(intent, getString(R.string.msg_share_image)));
    }

    private Uri buildFileProviderUri(@NonNull Uri uri) {
        return FileProvider.getUriForFile(this,
                FILE_PROVIDER_AUTHORITY,
                new File(uri.getPath()));
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP
                );

                mPhotoEditorView.getSource().setImageBitmap(saveBitmap);
        /*String encoded = Base64.encodeToString(byteArray,"UTF-8");
        fileInputStreamReader.read(byteArray);
        String(Base64.encodeBase64(bytes), "UTF-8");*/
//        Log.d(TAG, "onBitmapReady: "+encoded.trim());
                System.out.println(encoded.trim());
                nameYourCard(EditImageLandscActivity.this,encoded);
//                CallApiToSaveCard(encoded);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

    private void CallApiToSaveCard(String encoded,String cardName) {
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
            Date date = new Date();
            String currentdate = (formatter.format(date));
            Log.d(TAG, "CallApiToSaveCard: " + currentdate);

            OkHttpClient.Builder client = new OkHttpClient.Builder();
            HttpLoggingInterceptor registrationInterceptor = new HttpLoggingInterceptor();
            registrationInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(registrationInterceptor);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiInterface request = retrofit.create(ApiInterface.class);
            SaveVisitingReq saveVisitingReq = new SaveVisitingReq();
            saveVisitingReq.setDesignedCardFileName(cardName+ ".png");
            saveVisitingReq.setDesignedCardOrientation(Constants.portait);
            saveVisitingReq.setDesignedCardData(encoded);


            Call<UploadDesignedCardRes> call = request.SaveCard(saveVisitingReq);
            call.enqueue(new Callback<UploadDesignedCardRes>() {


                @Override
                public void onResponse(Call<UploadDesignedCardRes> call, Response<UploadDesignedCardRes> response) {
                    if (response.isSuccessful()) {
                        UploadDesignedCardRes uploadDesignedCardRes = response.body();


                        if (uploadDesignedCardRes.getResponseCode().equals("200")) {
                            Log.d("TAG", "onResponse Symbol List: " + uploadDesignedCardRes.getResponseMessage());
                            showSnackbar("Visiting Card Uploaded Successfully.");

                            startActivity(new Intent(EditImageLandscActivity.this, SavedVisitngCard.class));
                            finish();
                        } else {
                            showSnackbar("There is a problem in Uploading.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadDesignedCardRes> call, Throwable t) {


                }
            });
        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void ChooseImageAsABgOrSymbol(Context activity, final Bitmap photo) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.choose_image_as_bg_or_symbol);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;



        TextView text = dialog.findViewById(R.id.tv_background);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditorView.getSource().setImageBitmap(photo);
                dialog.dismiss();
            }
        });

        TextView dialogButton = dialog.findViewById(R.id.tv_symbol);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPhotoEditor.addImage(photo);

                dialog.dismiss();
            }
        });

        dialog.show();

        dialog.getWindow().setAttributes(lp);
    }

    public void nameYourCard(Context activity, final String encoded) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
        dialog.setContentView(R.layout.name_visiting_card);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        final EditText text = dialog.findViewById(R.id.editText);


        ImageView saveName = dialog.findViewById(R.id.tv_save);
        saveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text.getText().toString().equals("")) {
                    Log.d(TAG, "onClick: "+text.getText().toString());
                    CallApiToSaveCard(encoded,text.getText().toString());
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "Name Your Card.", Toast.LENGTH_LONG).show();
                }


            }
        });

        dialog.show();

        dialog.getWindow().setAttributes(lp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
//                    mPhotoEditor.clearAllViews();

                    if (resultCode == RESULT_OK) {
                        Uri uri = data.getParcelableExtra("path");
//                        cropImage(getCacheImagePath(fileName));
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(EditImageLandscActivity.this.getContentResolver(), uri);

                            ChooseImageAsABgOrSymbol(EditImageLandscActivity.this, bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

//                        Bitmap photo = (Bitmap) data.getExtras().get("data");

                    }


                    break;
                case PICK_REQUEST:
                    Uri uri = data.getParcelableExtra("path");
                    try {
                        // You can update this bitmap to your server
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(EditImageLandscActivity.this.getContentResolver(), uri);
                        ChooseImageAsABgOrSymbol(EditImageLandscActivity.this, bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_sticker);
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.msg_save_image));
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               finish();
            }
        });
        builder.create().show();

    }
/*
    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }*/

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case BRUSH:
                mPhotoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                        styleBuilder.withTextColor(colorCode);

                        mPhotoEditor.addText(inputText, styleBuilder);
                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;
            case ERASER:
                mPhotoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.label_eraser_mode);
                break;
            case FILTER:
                mTxtCurrentTool.setText(R.string.label_filter);
                showFilter(true);
                break;
            case EMOJI:
                mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
                break;
            case STICKER:
                msymbolFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                break;
            case BACKGROUND:
                mbackgroundBSFragment.show(getSupportFragmentManager(), mbackgroundBSFragment.getTag());

        }
    }


    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    @Override
    public void onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false);
            mTxtCurrentTool.setText(R.string.app_name);
        } else if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackgrounChnage(Bitmap bitmap) {
//        BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);

        mPhotoEditor.setFilterEffect(PhotoFilter.BRIGHTNESS);
        mPhotoEditorView.getSource().setAdjustViewBounds(true);
        Glide.with(EditImageLandscActivity.this).asBitmap().load(bitmap).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                mPhotoEditorView.getSource().setImageBitmap(bitmap);
            }
            @Override
            public void onLoadCleared(Drawable placeholder) {
            }
        });
//        mPhotoEditorView.getSource().setImageBitmap(bitmap);;

        Log.d(TAG, "onBackgrounChnage: "+bitmap);

    }

    @Override
    public void onSymbolChnage(Bitmap bitmap) {
        BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);

        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_emoji);
//        mPhotoEditorView.getSource().setImageBitmap(bitmap);;
        Log.d(TAG, "onBackgrounChnage: "+bitmap);
    }

    @Override
    public void onSeekChnage(int fl) {
        float b = fl/10;

//        int c = (fl*b)/progress;
//        progresss =(float) c/100;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if(fl==1) {
                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent9));
            }
            if(fl==2) {
                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent8));
            }
            if(fl==3) {
                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent7));
            }
            if(fl==4) {
                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent6));
            }
            if(fl==5) {
                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent5));
            }
            if(fl==6) {
                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent4));
            }
            if(fl==7) {
                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent3));
            }
            if(fl==8) {
                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent2));
            }
            if(fl==9) {
                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent0));
            }
        }


//        mPhotoEditorView.getSource().setAlpha(b);
//        Bitmap mutableBitmap = mBitmap.isMutable()? mBitmap: mBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        Canvas canvas = new Canvas(mutableBitmap);
//        int colour = (fl << 24) & 0xFF000000;
//
//        canvas.drawColor(colour, PorterDuff.Mode.OVERLAY);
//        Log.d(TAG, "onSeekChnage: "+colour);
//
//        mBitmap=mutableBitmap;
//        mPhotoEditorView.getSource().setImageBitmap(mBitmap);

    }

    @Override
    public void onHueChnage(int fl) {
        if(fl==1) {
            imgbrighteness.setBackgroundColor(getResources().getColor(R.color.pink_transparent0));
        }
        if(fl==2) {
            imgbrighteness.setBackgroundColor(getResources().getColor(R.color.pink_transparent1));
        }
        if(fl==3) {
            imgbrighteness.setBackgroundColor(getResources().getColor(R.color.pink_transparent2));
        }
        if(fl==4) {
            imgbrighteness.setBackgroundColor(getResources().getColor(R.color.pink_transparent3));
        }
        if(fl==5) {
            imgbrighteness.setBackgroundColor(getResources().getColor(R.color.pink_transparent4));
        }
        if(fl==6) {
            imgbrighteness.setBackgroundColor(getResources().getColor(R.color.pink_transparent5));
        }
        if(fl==7) {
            imgbrighteness.setBackgroundColor(getResources().getColor(R.color.pink_transparent6));
        }
        if(fl==8) {
            imgbrighteness.setBackgroundColor(getResources().getColor(R.color.pink_transparent7));
        }
        if(fl==9) {
            imgbrighteness.setBackgroundColor(getResources().getColor(R.color.pink_transparent8));
        }
    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

   /* @Override
    public void onFilterSelected(PhotoFilter photoFilter) {

    }*/
}
