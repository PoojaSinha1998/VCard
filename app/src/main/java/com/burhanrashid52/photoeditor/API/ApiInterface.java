package com.burhanrashid52.photoeditor.API;


import com.burhanrashid52.photoeditor.Request.SaveVisitingReq;
import com.burhanrashid52.photoeditor.Responses.AllSymbolsRes;
import com.burhanrashid52.photoeditor.Responses.AllTemplateRes;
import com.burhanrashid52.photoeditor.Responses.UploadDesignedCardRes;
import com.burhanrashid52.photoeditor.Responses.UploadedDesignedCardRes;
import com.burhanrashid52.photoeditor.SymboeRequest;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {


    //for Fetching Templates
    @GET("showAllCardTemplates")
    Call<AllTemplateRes> showTemplates();

    //for Fetching Symbols
    @GET("showAllSymbols")
    Call<AllSymbolsRes> showSymbols();

    //for Saving designed Card
    @POST("addDesignedVisitingCard")
    Call<UploadDesignedCardRes> SaveCard(@Body SaveVisitingReq saveVisitingReq);

    //for Fetching Saved Visiting Card
    @GET("showAllDesignedVisitingCard")
    Call<UploadedDesignedCardRes> getSavedVisitingCard();


}