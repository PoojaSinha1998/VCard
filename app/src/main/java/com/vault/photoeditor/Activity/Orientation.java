package com.vault.photoeditor.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vault.photoeditor.API.ApiInterface;
import com.vault.photoeditor.ApplicationClass.ApplicationClass;
import com.vault.photoeditor.Const.Constants;
import com.vault.photoeditor.Models.AllTemplates;
import com.vault.photoeditor.R;
import com.vault.photoeditor.Responses.AllSymbolsRes;
import com.vault.photoeditor.Responses.AllTemplateRes;
import com.vault.photoeditor.Responses.SymbolsList;
import com.vault.photoeditor.Responses.TemplateDetail;
import com.vault.photoeditor.Request.SymboeRequest;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Orientation extends AppCompatActivity {
    LinearLayout textView,tvLayout3;
    Button button;
    Resources resources;

    private Dialog progress;
    private static final int PERMISSION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide();
        setContentView(R.layout.activity_orientation);
        //ProgressBar



      //hide the title bar
        textView = findViewById(R.id.tvLayout);
        button = findViewById(R.id.button);
        resources = button.getResources();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked(view);
            }
        });
        tvLayout3 =findViewById(R.id.tvLayout3);
        tvLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity(new Intent(Orientation.this, SavedVisitngCard.class));
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Toast.makeText(Orientation.this, " We are in portrait mode",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Orientation.this, "We are in Landscape mode",
                            Toast.LENGTH_SHORT).show();
                }*/
            }
        });
        progress = new Dialog(Orientation.this, android.R.style.Theme_Translucent);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //here we set layout of progress dialog
        progress.setContentView(R.layout.progress_dialog);
        progress.setCancelable(true);
        progress.show();
        Log.d("TAG", "onCreate: "+progress.isShowing());
        GetAllTheSymbols();
        GetAllTheTemplates();
        if(!checkPermission())
        {
            requestPermission();
        }
    }
    public void showAlertDialogButtonClicked(View view)
    {

        // Create an alert builder
        AlertDialog.Builder builder
                = new AlertDialog.Builder(this);

        // set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.custom_layout,
                        null);
        LinearLayout linearLayout =customLayout.findViewById(R.id.linearLayout);
        LinearLayout linearLayout2 =customLayout.findViewById(R.id.linearLayout2);
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Orientation.this, EditImageActivity.class));
                ApplicationClass.setOrientattion(Constants.PORTRAIT);
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Orientation.this, EditImageLandscActivity.class));
                ApplicationClass.setOrientattion(Constants.LANDSCAPE);
                Log.d("TAG", "onClick: "+Constants.LANDSCAPE);
            }
        });
        builder.setView(customLayout);


        // create and show
        // the alert dialog
        AlertDialog dialog
                = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    // API CAlling for gettng all the Template List
    private void GetAllTheTemplates() {
        progress.show();
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            HttpLoggingInterceptor registrationInterceptor = new HttpLoggingInterceptor();
            registrationInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(registrationInterceptor);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Log.d("TAG", "onResponse CODE: 1 ");

            ApiInterface request = retrofit.create(ApiInterface.class);


            Call<AllTemplateRes> call = request.showTemplates();
            Log.d("TAG", "onResponse CODE: 2 ");
            call.enqueue(new Callback<AllTemplateRes>() {


                @Override
                public void onResponse(Call<AllTemplateRes> call, Response<AllTemplateRes> response) {

                    Log.d("TAG", "onResponse CODE: 3 "+response);
                        AllTemplateRes allTemplateRes = response.body();
                        AllTemplates allTemplates;
                        ArrayList<AllTemplates> allTemplatesPro =new ArrayList();
                        ArrayList<AllTemplates> allTemplatesLan =new ArrayList();

                        Log.d("TAG", "onResponse CODE: 4 "+allTemplateRes.getResponseCode());
                        if (allTemplateRes.getResponseCode().equals("200")) {
                            Log.d("TAG", "onResponse: "+allTemplateRes.getTemplateDetails());
                            ArrayList<TemplateDetail>  templateDetails =allTemplateRes.getTemplateDetails();

                            for (TemplateDetail m:templateDetails)
                                  {
                                      byte[] decodedString = Base64.decode(m.getCardTemplateData(), Base64.DEFAULT);
                                      Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                      if(m.getCardTemplateOrientation().equals("Landscape")) {
                                          allTemplatesLan.add(new AllTemplates(decodedString, m.getCardTemplateFileName()));
                                          Log.d("TAG", "onResponse: LAN :"+allTemplatesLan.size());
                                      }
                                      else if(m.getCardTemplateOrientation().equals("Portrait")){
                                          allTemplatesPro.add(new AllTemplates(decodedString, m.getCardTemplateFileName()));
                                          Log.d("TAG", "onResponse: PRO "+allTemplatesPro.size());
                                      }


                            }
                            ApplicationClass.setAllTemplatesLan(allTemplatesLan);
                            ApplicationClass.setAllTemplatePro(allTemplatesPro);
                            progress.dismiss();

                        }else {
                            progress.dismiss();
                        }

                }

                @Override
                public void onFailure(Call<AllTemplateRes> call, Throwable t) {
                    progress.dismiss();

                }
            });
        } catch (Exception e) {
            progress.dismiss();
            e.printStackTrace();

        }

    }
    private void GetAllTheSymbols() {
        progress.show();
        try {
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
            SymboeRequest symboleRequest = new SymboeRequest();
            symboleRequest.setKey("value");



            Call<AllSymbolsRes> call = request.showSymbols();
            call.enqueue(new Callback<AllSymbolsRes>() {


                @Override
                public void onResponse(Call<AllSymbolsRes> call, Response<AllSymbolsRes> response) {
                    if (response.isSuccessful()) {
                        AllSymbolsRes allTemplateRes = response.body();
                        AllSymbolsRes allTemplates;


                        ArrayList<ArrayList<String>> grupedSym =new ArrayList();
                        ArrayList<String> symbolData =new ArrayList();
                        ArrayList<String> names =new ArrayList();
                        if (allTemplateRes.getResponseCode().equals("200")) {
//                            Log.d("TAG", "onResponse Symbol List: "+allTemplateRes.getSymbolsList());
                            ArrayList<SymbolsList>  symboleDetails =allTemplateRes.getSymbolsList();


                            for (SymbolsList m:symboleDetails)
                            {
                                symbolData =new ArrayList();

//                                Log.d("TAG", "onResponse Symbol: "+m.getGroupedSymbolsDTOs().get(0).getCategory());
                                for(int i=0;i<m.getGroupedSymbolsDTOs().size();i++)
                                {
//                                    Log.d("TAG", "onResponse Symbol: "+m.getGroupedSymbolsDTOs().get(i).getSymbolData());
                                    symbolData.add(m.getGroupedSymbolsDTOs().get(i).getSymbolData());
                                }

//                                byte[] decodedString = Base64.decode(m.getSymbolData(), Base64.DEFAULT);
//                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                grupedSym.add(symbolData);

//                                Log.d("TAG", "onResponse Symbol: "+grupedSym);
                                names.add(m.getGroupedSymbolsDTOs().get(0).getCategory());


                            }
                            ApplicationClass.setAllSymbols(symboleDetails);
                            ApplicationClass.setCategorizedSymbole(grupedSym);
                            ApplicationClass.setNames(names);
                            progress.dismiss();

                        }else {
                            progress.dismiss();
                        }
                    }
                }

                @Override
                public void onFailure(Call<AllSymbolsRes> call, Throwable t) {
                    progress.dismiss();

                }
            });
        } catch (Exception e) {
            progress.dismiss();
            e.printStackTrace();

        }

    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Orientation.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Orientation.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(Orientation.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Orientation.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
            break;
        }
    }
}