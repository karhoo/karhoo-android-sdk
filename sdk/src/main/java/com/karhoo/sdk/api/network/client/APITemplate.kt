package com.karhoo.sdk.api.network.client

import com.karhoo.sdk.api.EnvironmentDetails
import com.karhoo.sdk.api.model.BraintreeSDKToken
import com.karhoo.sdk.api.model.Categories
import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.model.DriverTrackingInfo
import com.karhoo.sdk.api.model.Fare
import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.model.PaymentsNonce
import com.karhoo.sdk.api.model.Places
import com.karhoo.sdk.api.model.QuoteId
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.model.TripList
import com.karhoo.sdk.api.model.TripState
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.model.Vehicles
import com.karhoo.sdk.api.model.VehiclesV2
import com.karhoo.sdk.api.model.adyen.AdyenPaymentMethods
import com.karhoo.sdk.api.model.adyen.AdyenPaymentsResponse
import com.karhoo.sdk.api.network.annotation.NoAuthorisationHeader
import com.karhoo.sdk.api.network.request.AddPaymentRequest
import com.karhoo.sdk.api.network.request.AvailabilityRequest
import com.karhoo.sdk.api.network.request.CancellationRequest
import com.karhoo.sdk.api.network.request.AdyenPaymentMethodsRequest
import com.karhoo.sdk.api.network.request.AdyenPaymentsRequest
import com.karhoo.sdk.api.network.request.LocationInfoRequest
import com.karhoo.sdk.api.network.request.NonceRequest
import com.karhoo.sdk.api.network.request.PlaceSearch
import com.karhoo.sdk.api.network.request.QuotesRequest
import com.karhoo.sdk.api.network.request.QuotesV2Request
import com.karhoo.sdk.api.network.request.RefreshTokenRequest
import com.karhoo.sdk.api.network.request.ResetPasswordRequest
import com.karhoo.sdk.api.network.request.TripBooking
import com.karhoo.sdk.api.network.request.TripSearch
import com.karhoo.sdk.api.network.request.UserDetailsUpdateRequest
import com.karhoo.sdk.api.network.request.UserLogin
import com.karhoo.sdk.api.network.request.UserRegistration
import com.karhoo.sdk.api.network.response.Resource
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface APITemplate {

    companion object {
        const val TOKEN_METHOD = "/v1/auth/token"
        const val TOKEN_REFRESH_METHOD = "/v1/auth/refresh"
        const val PASSWORD_RESET_METHOD = "/v1/directory/users/password-reset"
        const val REGISTER_USER_METHOD = "/v1/directory/users"
        const val USER_PROFILE_METHOD = "/v1/directory/users/me"
        const val USER_PROFILE_UPDATE_METHOD = "/v1/directory/users/{id}"
        const val ADDRESS_AUTOCOMPLETE_METHOD = "/v1/locations/address-autocomplete"
        const val PLACE_DETAILS_METHOD = "/v1/locations/place-details"
        const val REVERSE_GEO_METHOD = "/v1/locations/reverse-geocode"

        @Deprecated("Availabilities endpoint is deprecated")
        const val AVAILABILITY_METHOD = "/v1/quotes/availability"
        const val QUOTE_REQUEST_METHOD = "/v1/quotes"
        const val QUOTES_METHOD = "/v1/quotes/{id}"
        const val QUOTES_V2_REQUEST_METHOD = "/v2/quotes"
        const val QUOTES_V2_METHOD = "/v2/quotes/{id}"
        const val BOOKING_METHOD = "/v1/bookings/with-nonce"
        const val BOOKING_DETAILS_METHOD = "/v1/bookings/{id}"
        const val GUEST_BOOKING_DETAILS_METHOD = "/v1/bookings/follow/{id}"
        const val BOOKING_STATUS_METHOD = "/v1/bookings/{id}/status"
        const val GUEST_BOOKING_STATUS_METHOD = "/v1/bookings/follow/{id}/status"
        const val TRACK_DRIVER_METHOD = "/v1/bookings/{id}/track"
        const val GUEST_BOOKING_TRACK_DRIVER_METHOD = "/v1/bookings/follow/{id}/track"
        const val BOOKING_HISTORY_METHOD = "/v1/bookings/search"
        const val CANCEL_BOOKING_METHOD = "/v1/bookings/{id}/cancel"
        const val CANCEL_GUEST_BOOKING_METHOD = "/v1/bookings/follow/{id}/cancel"
        const val SDK_INITIALISER_METHOD = "/v2/payments/payment-methods/braintree/client-tokens"
        const val ADD_CARD_METHOD = "/v2/payments/payment-methods/braintree/add-payment-details"
        const val NONCE_METHOD = "/v2/payments/payment-methods/braintree/get-payment-method"
        const val FARE_DETAILS = "/v1/fares/trip/{id}"

        const val AUTH_TOKEN_METHOD = "/karhoo/anonymous/token-exchange"
        const val AUTH_REVOKE_METHOD = "/oauth/v2/revoke"
        const val AUTH_USER_INFO_METHOD = "/oauth/v2/userinfo"
        const val AUTH_REFRESH_METHOD = "/oauth/v2/token"

        const val GET_PROVIDERS_METHOD = "/v3/payments/providers"
        const val GET_ADYEN_PUBLIC_KEY_METHOD = "/v3/payments/adyen/public-key"
        const val GET_ADYEN_PAYMENT_METHODS_METHOD = "/v3/payments/adyen/payments-methods"
        const val GET_ADYEN_PAYMENTS_METHOD = "/v3/payments/adyen/payments"

        const val IDENTIFIER_ID = "id"
        const val IDENTIFIER_LATITUDE = "latitude"
        const val IDENTIFIER_LONGITUDE = "longitude"
        const val IDENTIFIER_ORG = "organisation_id"
        const val IDENTIFIER_CURRENCY = "currency"

        private fun authHost() = EnvironmentDetails.current().authHost
    }

    @NoAuthorisationHeader
    @POST(TOKEN_METHOD)
    fun login(@Body userLogin: UserLogin): Deferred<Resource<Credentials>>

    @POST(TOKEN_REFRESH_METHOD)
    fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Deferred<Resource<Credentials>>

    @POST(PASSWORD_RESET_METHOD)
    fun passwordReset(@Body resetPasswordRequest: ResetPasswordRequest): Deferred<Resource<Void>>

    @POST(REGISTER_USER_METHOD)
    fun register(@Body userDetails: UserRegistration): Deferred<Resource<UserInfo>>

    @GET(USER_PROFILE_METHOD)
    fun userProfile(): Deferred<Resource<UserInfo>>

    @PUT(USER_PROFILE_UPDATE_METHOD)
    fun userProfileUpdate(@Path(IDENTIFIER_ID) userId: String, @Body usersDetailsUpdateRequestRequest: UserDetailsUpdateRequest): Deferred<Resource<UserInfo>>

    @POST(ADDRESS_AUTOCOMPLETE_METHOD)
    fun placeSearch(@Body placeSearch: PlaceSearch): Deferred<Resource<Places>>

    @POST(PLACE_DETAILS_METHOD)
    fun locationInfo(@Body locationInfoRequest: LocationInfoRequest): Deferred<Resource<LocationInfo>>

    @GET(REVERSE_GEO_METHOD)
    fun reverseGeocode(@Query(IDENTIFIER_LATITUDE) latitude: Double, @Query(IDENTIFIER_LONGITUDE) longitude: Double): Deferred<Resource<LocationInfo>>

    @POST(AVAILABILITY_METHOD)
    @Deprecated("Availabilities endpoint is deprecated")
    fun availabilities(@Body availabilityRequest: AvailabilityRequest): Deferred<Resource<Categories>>

    @POST(QUOTE_REQUEST_METHOD)
    fun quotes(@Body quotesRequest: QuotesRequest): Deferred<Resource<QuoteId>>

    @GET(QUOTES_METHOD)
    fun quotes(@Path(IDENTIFIER_ID) id: String): Deferred<Resource<Vehicles>>

    @POST(QUOTES_V2_REQUEST_METHOD)
    fun quotesv2(@Body quotesV2Request: QuotesV2Request): Deferred<Resource<QuoteId>>

    @GET(QUOTES_V2_METHOD)
    fun quotesv2(@Path(IDENTIFIER_ID) id: String): Deferred<Resource<VehiclesV2>>

    @POST(BOOKING_METHOD)
    fun book(@Body tripBooking: TripBooking): Deferred<Resource<TripInfo>>

    @GET(BOOKING_DETAILS_METHOD)
    fun tripDetails(@Path(IDENTIFIER_ID) id: String): Deferred<Resource<TripInfo>>

    @GET(GUEST_BOOKING_DETAILS_METHOD)
    fun guestTripDetails(@Path(IDENTIFIER_ID) id: String): Deferred<Resource<TripInfo>>

    @GET(BOOKING_STATUS_METHOD)
    fun status(@Path(IDENTIFIER_ID) tripId: String): Deferred<Resource<TripState>>

    @GET(GUEST_BOOKING_STATUS_METHOD)
    fun guestBookingStatus(@Path(IDENTIFIER_ID) tripIdentifier: String): Deferred<Resource<TripState>>

    @GET(TRACK_DRIVER_METHOD)
    fun trackDriver(@Path(IDENTIFIER_ID) tripIdentifierId: String): Deferred<Resource<DriverTrackingInfo>>

    @GET(GUEST_BOOKING_TRACK_DRIVER_METHOD)
    fun guestBookingTrackDriver(@Path(IDENTIFIER_ID) tripIdentifier: String):
            Deferred<Resource<DriverTrackingInfo>>

    @POST(BOOKING_HISTORY_METHOD)
    fun tripHistory(@Body tripHistoryRequest: TripSearch): Deferred<Resource<TripList>>

    @POST(CANCEL_BOOKING_METHOD)
    fun cancel(@Path(IDENTIFIER_ID) tripId: String, @Body cancellationRequest: CancellationRequest): Deferred<Resource<Void>>

    @POST(CANCEL_GUEST_BOOKING_METHOD)
    fun cancelGuestBooking(@Path(IDENTIFIER_ID) tripIdentifier: String, @Body cancellationRequest: CancellationRequest): Deferred<Resource<Void>>

    @POST(SDK_INITIALISER_METHOD)
    fun sdkInitToken(@Query(IDENTIFIER_ORG) organisationId: String, @Query(IDENTIFIER_CURRENCY) currency: String): Deferred<Resource<BraintreeSDKToken>>

    @POST(ADD_CARD_METHOD)
    fun addPayment(@Body addPaymentRequest: AddPaymentRequest): Deferred<Resource<PaymentsNonce>>

    @POST(NONCE_METHOD)
    fun nonce(@Body nonceRequest: NonceRequest): Deferred<Resource<PaymentsNonce>>

    @GET(FARE_DETAILS)
    fun fareDetails(@Path(IDENTIFIER_ID) tripId: String): Deferred<Resource<Fare>>

    @GET(GET_PROVIDERS_METHOD)
    fun getPaymentProvider(): Deferred<Resource<PaymentProvider>>

    @GET(GET_ADYEN_PUBLIC_KEY_METHOD)
    fun getAdyenPublicKey(): Deferred<Resource<String>>

    @POST(GET_ADYEN_PAYMENT_METHODS_METHOD)
    fun getAdyenPaymentMethods(@Body adyenPaymentMethodsRequest: AdyenPaymentMethodsRequest):
            Deferred<Resource<ResponseBody>>

    @Headers("Content-Type: application/json")
    @POST(GET_ADYEN_PAYMENTS_METHOD)
    fun getAdyenPayments(@Body adyenPaymentsRequest: String):
            Deferred<Resource<ResponseBody>>

    @POST
    @FormUrlEncoded
    @Headers("accept: application/json")
    fun authToken(@FieldMap(encoded = true) params: Map<String, String>,
                  @Url url: String = authHost() + AUTH_TOKEN_METHOD): Deferred<Resource<Credentials>>

    @GET
    fun authUserInfo(@Url url: String = authHost() + AUTH_USER_INFO_METHOD): Deferred<Resource<UserInfo>>

    @POST
    @FormUrlEncoded
    fun authRevoke(@FieldMap(encoded = true) params: Map<String, String>, @Url url: String = authHost() + AUTH_REVOKE_METHOD): Deferred<Resource<Void>>

    @POST
    @FormUrlEncoded
    fun authRefresh(@FieldMap(encoded = true) params: Map<String, String>, @Url url: String = authHost() + AUTH_REFRESH_METHOD): Deferred<Resource<Credentials>>
}
