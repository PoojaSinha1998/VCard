package com.vault.photoeditor.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vault.photoeditor.API.ApiInterface;
import com.vault.photoeditor.Const.Constants;
import com.vault.photoeditor.R;
import com.vault.photoeditor.Responses.SavedVisiting;
import com.vault.photoeditor.Responses.UploadedDesignedCardRes;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SavedVisitngCard extends AppCompatActivity {

    RecyclerView rvSavedVisiting;
    private static final int PERMISSION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_saved_visiting);

        rvSavedVisiting = findViewById(R.id.rvsavedVisiting);
        GetAllTheTemplates();

    }

    // API CAlling for gettng all the Template List
    private void GetAllTheTemplates() {
        try {


            GridLayoutManager gridLayoutManager = new GridLayoutManager(SavedVisitngCard.this, 1);
            rvSavedVisiting.setLayoutManager(gridLayoutManager);



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


            Call<UploadedDesignedCardRes> call = request.getSavedVisitingCard();
            call.enqueue(new Callback<UploadedDesignedCardRes>() {


                @Override
                public void onResponse(Call<UploadedDesignedCardRes> call, Response<UploadedDesignedCardRes> response) {
                    if (response.isSuccessful()) {
                        UploadedDesignedCardRes uploadedDesignedCardRes = response.body();

                        if (uploadedDesignedCardRes.getResponseCode().equals("200")) {
                            Log.d("TAG", "onResponse: "+uploadedDesignedCardRes.getResponseMessage());
                            ArrayList<SavedVisiting>  templateDetails =uploadedDesignedCardRes.getSymbolsList();
                            SavedVisitingAdapter stickerAdapter = new SavedVisitingAdapter(templateDetails);
                            Log.d("TAG", "onResponse: "+templateDetails);
                            rvSavedVisiting.setAdapter(stickerAdapter);

                        }else {
                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadedDesignedCardRes> call, Throwable t) {


                }
            });
        } catch (Exception e) {

            e.printStackTrace();

        }

    }
    public class SavedVisitingAdapter extends RecyclerView.Adapter<SavedVisitingAdapter.ViewHolder> {


        private ArrayList<SavedVisiting> templateDetails;

        public SavedVisitingAdapter(ArrayList<SavedVisiting> templateDetails) {
            this.templateDetails = templateDetails;
        }

        @Override
        public SavedVisitingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_visiting_card, parent, false);
            return new SavedVisitingAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,final int position) {

            byte[] decodedString = Base64.decode(templateDetails.get(position).getDesignedCardData().getBytes(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

//            Log.d("TAG", "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "] "+ ""+ templateDetails.get(position).getDesignedCardData());
//            Bitmap decodedImage = BitmapFactory.decodeByteArray(templateDetails.get(position).getDesignedCardData().getBytes(), 0, templateDetails.get(position).getDesignedCardData().getBytes().length);


            holder.imgSticker.setImageBitmap(decodedByte);
            holder.txSticker.setText(templateDetails.get(position).getDesignedCardFileName());
            holder.imgSticker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent =new Intent(getApplicationContext(),EditImageActivity.class);
//                    intent.putExtra("IMAGE", templateDetails.get(position).getDesignedCardData());
//                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return templateDetails.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;
            TextView txSticker;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);
                txSticker = itemView.findViewById(R.id.txSticker);


            }
        }
    }

}