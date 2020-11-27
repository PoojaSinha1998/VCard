package com.vault.photoeditor.API;


import com.vault.photoeditor.Request.SaveVisitingReq;
import com.vault.photoeditor.Responses.AllSymbolsRes;
import com.vault.photoeditor.Responses.AllTemplateRes;
import com.vault.photoeditor.Responses.UploadDesignedCardRes;
import com.vault.photoeditor.Responses.UploadedDesignedCardRes;

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