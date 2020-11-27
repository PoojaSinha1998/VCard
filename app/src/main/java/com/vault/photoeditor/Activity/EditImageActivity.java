package com.vault.photoeditor.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import retrofit2.Callback;

import com.vault.photoeditor.API.ApiInterface;
//import com.vault.photoeditor.Const.Constants;
import com.vault.photoeditor.Const.Constants;
import com.vault.photoeditor.Fragment.BackgroundBSFragment;
import com.vault.photoeditor.Fragment.EmojiBSFragment;
import com.vault.photoeditor.MyViewModel.BackgroundViewModel;
import com.vault.photoeditor.Fragment.PropertiesBSFragment;
import com.vault.photoeditor.R;
import com.vault.photoeditor.Request.SaveVisitingReq;
import com.vault.photoeditor.Responses.UploadDesignedCardRes;
import com.vault.photoeditor.Fragment.StickerBSFragment;
import com.vault.photoeditor.Fragment.SymbolFragment;
import com.vault.photoeditor.Fragment.TextEditorDialogFragment;
import com.vault.photoeditor.base.BaseActivity;
import com.vault.photoeditor.filters.FilterListener;
import com.vault.photoeditor.filters.FilterViewAdapter;
import com.vault.photoeditor.photoViewLib.OnPhotoEditorListener;
import com.vault.photoeditor.photoViewLib.OnSaveBitmap;
import com.vault.photoeditor.photoViewLib.PhotoEditor;
import com.vault.photoeditor.photoViewLib.PhotoEditorView;
import com.vault.photoeditor.photoViewLib.PhotoFilter;
import com.vault.photoeditor.photoViewLib.TextStyleBuilder;
import com.vault.photoeditor.photoViewLib.ViewType;
import com.vault.photoeditor.tools.EditingToolsAdapter;
import com.vault.photoeditor.tools.ToolType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.core.content.FileProvider.getUriForFile;

public class EditImageActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected, FilterListener, BackgroundBSFragment.BackgroundListener, SymbolFragment.SymbolListener, BackgroundBSFragment.SeekBarListener, BackgroundBSFragment.HueBarListener {

    private static final String TAG = EditImageActivity.class.getSimpleName();
    public static final String FILE_PROVIDER_AUTHORITY = "com.burhanrashid52.photoeditor.fileprovider";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    public PhotoEditor mPhotoEditor;
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
    private boolean mIsFilterVisible;
    String stringByte;
    SeekBar seekBar;
    float progresss ;
    int a=1;
    int b=100;
    public static String fileName;
    private boolean lockAspectRatio = false, setBitmapMaxWidthHeight = false;
    private int ASPECT_RATIO_X = 16, ASPECT_RATIO_Y = 9, bitmapMaxWidth = 1000, bitmapMaxHeight = 1000;
    private int IMAGE_COMPRESSION = 80;
    boolean backgroundSelected = false;
    ImageView imgbrighteness;
    @Nullable
    @VisibleForTesting
    public
    Uri mSaveImageUri;
    BackgroundViewModel model;
    Bitmap mBitmap;
    ImageView imgbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_edit_image);
        initViews();

        handleIntentImage(mPhotoEditorView.getSource());

        mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");

        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mbackgroundBSFragment = new BackgroundBSFragment();
        msymbolFragment = new SymbolFragment();
        msymbolFragment.setSymbolListener(this);
        mStickerBSFragment.setStickerListener(this);
        mbackgroundBSFragment.setBackgroundListener(this);
        mbackgroundBSFragment.setSeekChangeListener(this);
        mbackgroundBSFragment.setHueChangeListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);


        Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                .setDefaultTextTypeface(mTextRobotoTf)
                .setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);
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
        imgShare.setVisibility(View.GONE);
        imgShare.setOnClickListener(this);
        imgbrighteness = findViewById(R.id.imgbrighteness);


      /*  seekBar=findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

//                Toast.makeText(getApplicationContext(),"seekbar progress: "+progress, Toast.LENGTH_SHORT).show();

                if(
                        progress==100
                ){
                    b=100;
                }
               int c = (progress*b)/progress;
                progresss =(float) c/100;

                mPhotoEditorView.getSource().setAlpha((float)progresss);
                Log.d(TAG, "onProgressChanged: "+progress+" "+(float)progresss);
                decresevalueB(b);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
            }
        });*/

//        Log.d(TAG, "initViews: "+model.getSelected());
    }
    void decresevalueB(int b)
    {
        this.b--;
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
                Intent intent = new Intent(EditImageActivity.this, ImagePickerActivity.class);
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
                Intent gintent = new Intent(EditImageActivity.this, ImagePickerActivity.class);
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

    public void clearHelperBox() {
        for (int i = 0; i < mPhotoEditorView.getChildCount(); i++) {
            View childAt = mPhotoEditorView.getChildAt(i);
            FrameLayout frmBorder = childAt.findViewById(R.id.frmBorder);
            if (frmBorder != null) {
                frmBorder.setBackgroundResource(0);
            }
            ImageView imgClose = childAt.findViewById(R.id.imgPhotoEditorClose);
            if (imgClose != null) {
                imgClose.setVisibility(View.GONE);
            }
        }
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
                System.out.println(encoded.trim());
                nameYourCard(EditImageActivity.this,encoded);
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

                            startActivity(new Intent(EditImageActivity.this, SavedVisitngCard.class));
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
//todo
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

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                            bitmap = MediaStore.Images.Media.getBitmap(EditImageActivity.this.getContentResolver(), uri);

                            ChooseImageAsABgOrSymbol(EditImageActivity.this, bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        setResultCancelled();
                    }


                    break;
                case PICK_REQUEST:
                    Uri uri = data.getParcelableExtra("path");
                    try {
                        // You can update this bitmap to your server
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(EditImageActivity.this.getContentResolver(), uri);
                        ChooseImageAsABgOrSymbol(EditImageActivity.this, bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

    @Override
    public void onColorChanged(int colorCode) {
//        mPhotoEditor.setBrushColor(colorCode);

        if(backgroundSelected) {
            Log.d("TAG", "onColorChanged: "+backgroundSelected);
            mPhotoEditorView.getSource().setColorFilter(getResources().getColor(R.color.white));

            mPhotoEditorView.getSource().setColorFilter(colorCode);

            Bitmap resultBitmap = mBitmap.copy(mBitmap.getConfig(), true);
            Paint paint = new Paint();
            ColorFilter filter = new LightingColorFilter(colorCode, 1);
            paint.setColorFilter(filter);
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawBitmap(resultBitmap, 0, 0, paint);
//        return resultBitmap;

            mPhotoEditorView.getSource().setImageBitmap(resultBitmap);

            ;
        }
        else {
            Log.d("TAG", "onColorChanged: "+backgroundSelected);
            mPhotoEditorView.getSource().setColorFilter(getResources().getColor(R.color.red_color_picker));

            BitmapDrawable BD = (BitmapDrawable) getResources().getDrawable(R.drawable.lands_4);;
            Bitmap bitmap = BD.getBitmap();
            Bitmap O = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), bitmap.getConfig());

            for(int i=0; i<bitmap.getWidth(); i++){
                for(int j=0; j<bitmap.getHeight(); j++){
                    int p = bitmap.getPixel(i, j);
                    int b = colorCode;

                    int x =  0;
                    int y =  0;
                    b =  b+150;
                    O.setPixel(i, j, Color.argb(Color.alpha(p), x, y, b));
                }
            }
//            I.setImageBitmap(O);
            mPhotoEditorView.getSource().setImageBitmap(O);


        }
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

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case BRUSH:
//                mPhotoEditor.setBrushDrawingMode(true);
//                mTxtCurrentTool.setText(R.string.label_brush);
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
//                mPhotoEditor.brushEraser();
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
        mBitmap = bitmap;
        backgroundSelected = true;
        BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);

        mPhotoEditor.setFilterEffect(PhotoFilter.BRIGHTNESS);
        mPhotoEditorView.getSource().setAdjustViewBounds(true);

        mPhotoEditorView.getSource().setImageBitmap(bitmap);

        Log.d(TAG, "onBackgrounChnage: "+bitmap);

        Log.d(TAG, "onBackgrounChnage: " + bitmap);
//        mPhotoEditorView.getSource().setImageAlpha();
    }
    public Bitmap makeTransparent(Bitmap src, int value) {
        Bitmap bitmap = null;
        BitmapDrawable bd = new BitmapDrawable(mBitmap);
        bd.setAlpha(10);
        if(bd.getIntrinsicWidth() <= 0 || bd.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(bd.getIntrinsicWidth(), bd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas1 = new Canvas(bitmap);
        bd.setBounds(0, 0, canvas1.getWidth(), canvas1.getHeight());
        bd.draw(canvas1);
        return bitmap;
    }
    @Override
    public void onSymbolChnage(Bitmap bitmap) {
        BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);

        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_emoji);
        Log.d(TAG, "onBackgrounChnage: " + bitmap);
    }


    private void setResultOk(Uri imagePath) {
        Intent intent = new Intent();
        intent.putExtra("path", imagePath);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void setResultCancelled() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);

        return getUriForFile(EditImageActivity.this, getPackageName() + ".provider", image);
    }

    private static String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    @Override
    public void onSeekChnage(int fl) {
        float b = fl/10;

//        int c = (fl*b)/progress;
//        progresss =(float) c/100;
            if(fl==1) {

                mPhotoEditorView.getSource().setImageBitmap(makeTransparent(mBitmap,1));
//                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent9));
            }
            if(fl==2) {
                mPhotoEditorView.getSource().setImageBitmap(makeTransparent(mBitmap,2));
//                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent8));
            }
            if(fl==3) {
                mPhotoEditorView.getSource().setImageBitmap(makeTransparent(mBitmap,3));
//                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent7));
            }
            if(fl==4) {
                mPhotoEditorView.getSource().setImageBitmap(makeTransparent(mBitmap,4));
//                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent6));
            }
            if(fl==5) {
                mPhotoEditorView.getSource().setImageBitmap(makeTransparent(mBitmap,5));
//                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent5));
            }
            if(fl==6) {
                mPhotoEditorView.getSource().setImageBitmap(makeTransparent(mBitmap,6));
//                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent4));
            }
            if(fl==7) {
                mPhotoEditorView.getSource().setImageBitmap(makeTransparent(mBitmap,7));
//                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent3));
            }
            if(fl==8) {
                mPhotoEditorView.getSource().setImageBitmap(makeTransparent(mBitmap,8));
//                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent2));
            }
            if(fl==9) {
                mPhotoEditorView.getSource().setImageBitmap(makeTransparent(mBitmap,9));
//                imgbrighteness.setBackgroundColor(getResources().getColor(R.color.black_transparent0));
            }


        mPhotoEditorView.getSource().setImageBitmap(makeTransparent(mBitmap,fl));
        Log.d(TAG, "onSeekChnage: FL"+fl);

    }
    private void adjustOpacity(Bitmap bitmap, int opacity)
    {
        Bitmap mutableBitmap = bitmap.isMutable()
                ? bitmap
                : bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        int colour = (opacity & 0xFF) << 10;
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN);

        mPhotoEditorView.getSource().setImageBitmap(mutableBitmap);
//        return mutableBitmap;
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


}
