package inha.ac.kr.pdychoo.buslinker.Restful;


import inha.ac.kr.pdychoo.buslinker.Entity.Deal;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface RetrofitService {
    //로그인
    @POST("/Login")
    Call<LoginResponse> PostLogin(@Body LoginData loginData);

    //회원가입
    @POST("/SignUp")
    Call<PostResponse> PostSignUp(@Body SignUpData signUpData);

    //결제내역 생성
    @POST("/CreateDeal")
    Call<PostResponse> PostDeal(@Body Deal deal);

    //3개월 내 거래내역 개요
    @GET("/RecentDeal")
    Call<DealStatus> GetRecentDeal(@Query("userID") String userID);

    //전체 거래내역
    @GET("/DealList")
    Call<List<Deal>> GetDealList(@Query("userID") String userID);
}
