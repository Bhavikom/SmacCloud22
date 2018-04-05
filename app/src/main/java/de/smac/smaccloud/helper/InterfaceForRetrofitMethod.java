package de.smac.smaccloud.helper;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by androidwarriors on 12/3/2015.
 */
public interface InterfaceForRetrofitMethod {

    @Multipart
    @POST("Check/ServiceHealth")
    Call<String> getHostedUrl(@Part("OrgId") RequestBody orgID);

    @Multipart
    @POST("Channel")
    Call<String> createChannel(@Part("Request") String email,
                               @Part MultipartBody.Part file);

    @Multipart
    @POST("File")
    Call<String> createFolder(@Part("Request") String email,
                              @Part MultipartBody.Part file);

    @Multipart
    @POST("File")
    Call<String> createFile(@Part("Request") String email);

    @Multipart
    @POST("File")
    Call<String> addMediaContent(@Part("Request") String email,
                                 @Part MultipartBody.Part file);

    @Multipart
    @POST("Authenticate")
    Call<String> getAuthenticate(@Part("UserName") RequestBody email,
                                 @Part("Password") RequestBody password,
                                 @Part("DeviceType") RequestBody devicetype,
                                 @Part("DeviceToken") RequestBody devicetoken,
                                 @Part("AppVersion") RequestBody appversion,
                                 @Part("OrgUser") RequestBody orgUser);

    @Multipart
    @POST("Get/Profiles")
    Call<String> getProfiles(@Part("Lang") String lang);

    @Multipart
    @POST("Get/Dashboard")
    Call<String> getDashboard(@Part("DateRange") RequestBody daterange,
                              @Part("Psearch") RequestBody psearch, @Part("Tsearch") RequestBody tsearch,
                              @Part("POrder") RequestBody porder, @Part("Limit") RequestBody limit,
                              @Part("TOrder") RequestBody torder);

    @POST("Update/Token")
    Call<String> getNewAccessToken();

    @GET("logindetails/token={token}/name={name}/password={password}")
    Call<String> callLoginService(@Path("token") String token,
                                  @Path("name") String name, @Path("password") String password);

    @Multipart
    @POST("Create/Profile")
    Call<String> createProfile(@Part("UserId") String userid,
                               @Part("Name") String name,
                               @Part("ColsDetail") String coldetail,
                               @Part("ExpandedIds") String expandid,
                               @Part("Lang") String lang);

    @Multipart
    @POST("Update/Profile")
    Call<String> saveSetting(@Part("Id") String profileId,
                             @Part("RibbonFilter") String ribbon,
                             @Part("GeneralFilter") String general);

    @Multipart
    @POST("Delete/Profile")
    Call<String> deleteProfile(@Part("Id") String id);

    @Multipart
    @POST("Get/Profile")
    Call<String> callGetProfileFormID(@Part("Id") String profileId);

    @Multipart
    @POST("Update/Profile")
    Call<String> updateProfile(@Part("Id") String profileId,
                               @Part("Columns") String columns);

    @Multipart
    @POST("Logout")
    Call<String> singOut(@Part("DeviceType") RequestBody deviceType,
                         @Part("DeviceToken") RequestBody deviceToken,
                         @Part("AppVersion") RequestBody appVersion);

    @POST("Get/ResponsiblePersonsAndClients")
    Call<String> getResponsibleAndClient();


}
