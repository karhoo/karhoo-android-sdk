package com.karhoo.sdk.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.model.KarhooInternalError
import retrofit2.HttpException
import java.net.SocketTimeoutException

enum class KarhooError(var code: String,
                       var internalMessage: String,
                       val userFriendlyMessage: String) {

    Unexpected("KSDK001",
               "Something went wrong but we don't know what it was",
               "Something went wrong but we don't know what it was."),

    RequiredRolesNotAvailable("KSDK02",
                              "The user does not have the required roles to make this request.",
                              "The user does not have the required roles to make this request."),

    UserAlreadyLoggedIn("KSDK03",
                        "User already logged in.",
                        "User already logged in."),

    InternalSDKError("KSDK04",
                     "Something went wrong internally. Please submit a bug report.",
                     "Something went wrong internally. Please submit a bug report."),

    NoConfigAvailableForView("KSDK05",
                             "There is no view config available for this view",
                             "There is no view config available for this view"),

    Timeout("KSDK05",
            "The request has timed out",
            "We seem to be having issues connecting to the platform"),

    @SerializedName("K0001")
    GeneralRequestError("K0001",
                        "General request error.",
                        "General request error."),

    @SerializedName("K0002")
    InvalidRequestPayload("K0002",
                          "Invalid request payload.",
                          "Invalid request payload."),

    @SerializedName("K0003")
    CouldNotReadAuthorisationToken("K0003",
                                   "could not read authorisation token",
                                   "could not read authorisation token"),

    @SerializedName("K0004")
    CouldNotParseAuthorisationToken("K0004",
                                    "Could not parse authorisation token.",
                                    "Could not parse authorisation token."),

    @SerializedName("K0005")
    AuthenticationRequired("K0005",
                           "Authentication required for this path.",
                           "Authentication required for this path."),

    @SerializedName("K0006")
    RateLimitExceeded("K0006",
                      "Rate limit exceeded.",
                      "Rate limit exceeded."),

    @SerializedName("K0007")
    CircuitBreakerTrigger("K0007",
                          "Circuit breaker triggered for this route.",
                          "Circuit breaker triggered for this route"),

    @SerializedName("K1001")
    Register("K1001",
             "Could not register user.",
             "Could not register user."),

    @SerializedName("K1003")
    RegisterInvalidRequest("K1003",
                           "Could not register user. Invalid request.",
                           "Could not register user. Invalid request."),

    @SerializedName("K1004")
    RegisterInvalidPhoneNumber("K1004",
                               "Could not register user. Invalid phone number.",
                               "Could not register user. Invalid phone number."),

    @SerializedName("K1005")
    CouldNotGetUserInvalidToken("K1005",
                                "Could not get user details. Invalid token.",
                                "Could not get user details. Invalid token."),

    @SerializedName("K1006")
    UserDoesNotExist("K1006",
                     "Could not get user details. User does not exist.",
                     "Could not get user details. User does not exist."),

    @SerializedName("K1007")
    CouldNotAddUserToOrganisation("K1007",
                                  "Could not add user to organisation.",
                                  "Could not add user to organisation."),

    @SerializedName("K1008")
    OrganisationDoesNotExist("K1008",
                             "Organisation does not exist.",
                             "Organisation does not exist."),

    @SerializedName("K1009")
    RoleDoesNotExist("K1009",
                     "Role does not exist.",
                     "Role does not exist."),

    @SerializedName("K1010")
    PermissionsRequiredForUser("K1010",
                               "You don’t have the required permissions to view the profile information of this user.",
                               "You don’t have the required permissions to view the profile information of this user."),

    @SerializedName("K1011")
    PasswordInvalid("K1011",
                    "Password is invalid.",
                    "Password must be at least 8 characters in length, and contain at least 3 of the following 3 types of characters: Lower case letters (a-z), Upper case letters (A-Z) and Numbers (i.e. 0-9)."),

    @SerializedName("K2001")
    CouldNotGetAddress("K2001",
                       "Could not get address.",
                       "Could not get address."),

    @SerializedName("K2002")
    CouldNotAutocompleteAddress("K2002",
                                "Could not autocomplete supplied address.",
                                "Could not autocomplete supplied address."),

    @SerializedName("K3001")
    CouldNotGetEstimates("K3001",
                         "Could not get estimates.",
                         "Could not get estimates."),

    @SerializedName("K3002")
    CouldNotGetEstimatesNoAvailability("K3002",
                                       "Could not get estimates (no availability found within requested area).",
                                       "Could not get estimates (no availability found within requested area)"),

    @SerializedName("K3003")
    CouldNotGetEstimatesCouldNotFindSpecifiedQuote("K3003",
                                                   "Could not get estimates (could not find specified quote).",
                                                   "Could not get estimates (could not find specified quote)."),

    @SerializedName("K4001")
    CouldNotBook("K4001",
                 "Could not book trip.",
                 "Could not book trip"),

    @SerializedName("K4002")
    CouldNotBookRequirePassengerDetails("K4002",
                                        "Could not book trip - invalid request payload (require at least 1 set of passenger-details).",
                                        "Could not book trip - invalid request payload (require at least 1 set of passenger-details)."),

    @SerializedName("K4003")
    CouldNotBookCouldNotFindSpecifiedQuote("K4003",
                                           "Could not book trip (could not find specified quote)",
                                           "Could not book trip (could not find specified quote)"),

    @SerializedName("K4004")
    CouldNotBookExpiredQuote("K4004",
                             "Could not book trip (attempt to book an expired quote).",
                             "Could not book trip (attempt to book an expired quote)."),

    @SerializedName("K4005")
    CouldNotBookPermissionDenied("K4005",
                                 "Could not book trip (permission denied)",
                                 "Could not book trip (permission denied)"),

    @SerializedName("K4006")
    CouldNotBookPaymentPreAuthFailed("K4006",
                                     "Could not book trip (payment pre-authorisation failed)",
                                     "Could not book trip (payment pre-authorisation failed)"),

    @SerializedName("K4007")
    CouldNotCancel("K4007",
                   "Could not cancel trip.",
                   "Could not cancel trip."),

    @SerializedName("K4008")
    CouldNotCancelCouldNotFindSpecifiedTrip("K4008",
                                            "Could not cancel trip (could not find specified trip)",
                                            "Could not cancel trip (could not find specified trip)"),

    @SerializedName("K4009")
    CouldNotCancelPermissionDenied("K4009",
                                   "Could not cancel trip (permission denied)",
                                   "Could not cancel trip (permission denied)"),

    @SerializedName("K4010")
    CouldNotCancelAlreadyCancelled("K4010",
                                   "Could not cancel trip (already cancelled)",
                                   "Could not cancel trip (already cancelled)"),

    @SerializedName("K4011")
    CouldNotGetTrip("K4011",
                    "Could not get trip",
                    "Could not get trip"),

    @SerializedName("K4012")
    CouldNotGetTripCouldNotFindSpecifiedTrip("K4012",
                                             "Could not get trip (could not find specified trip)",
                                             "Could not get trip (could not find specified trip)"),

    @SerializedName("K4013")
    CouldNotGetTripPermissionDenied("K4013",
                                    "Could not get trip (permission denied)",
                                    "Could not get trip (permission denied)"),

    @SerializedName("K4014")
    CouldNotBookTripAsAgent("K4014",
                            "Could not book trip as agent.",
                            "Could not book trip as agent."),

    @SerializedName("K4015")
    CouldNotBookTripAsTraveller("K4015",
                                "Could not book trip as traveller.",
                                "Could not book trip as traveller."),

    @SerializedName("K4018")
    CouldNotBookTripQuoteNoLongerAvailable("K4018",
                                           "Could not book trip as this Quote is no longer available",
                                           "Could not book trip as this quote is no longer available"),

    @SerializedName("K5001")
    CouldNotGetEstimatesInternalError("K5001",
                                      "Could not get estimates",
                                      "Could not get estimates"),

    @SerializedName("K5002")
    CouldNotGetAvailabilityNoneFound("K5002",
                                     "Could not get availability (no availability found within requested area)",
                                     "Could not get availability (no availability found within requested area)"),

    @SerializedName("K5003")
    CouldNotGetAvailabilityNoCategories("K5003",
                                        "Could not get availability (no categories found within requested area)",
                                        "Could not get availability (no categories found within requested area)"),

    @SerializedName("K6001")
    CouldNotAuthenticate("K6001",
                         "Could not authenticate",
                         "Could not authenticate"),

    @SerializedName("Q0001")
    OriginAndDestinationIdentical("Q0001",
                                  "Origin and destination cannot be the same.",
                                  "Origin and destination cannot be the same."),

    @SerializedName("P0001")
    FailedToGetUserId("P0001",
                      "Failed to retrieve UserID from token.",
                      "Failed to retrieve UserID from token."),

    @SerializedName("P0002")
    FailedToCallMoneyService("P0002",
                             "Failed calling money service.",
                             "Failed calling money service.");

    companion object {
        fun fromThrowable(t: Throwable?): KarhooError {
            t?.let {
                return when (t) {
                    is HttpException -> parseHttpException(t)
                    is SocketTimeoutException -> Timeout
                    else -> Unexpected.apply {
                        Log.d("CODES::", "internalMessage:: $t")
                        internalMessage = t.message.orEmpty()
                    }
                }
            }
            return Unexpected
        }
    }

}

private fun parseHttpException(error: HttpException): KarhooError {
    return try {
        val responseBody = error.response()?.errorBody()?.string()
        Gson().fromJson(responseBody, KarhooInternalError::class.java)?.let {
            return Gson().fromJson(it.code, KarhooError::class.java)
        }
        KarhooError.Unexpected.apply {
            code = error.code().toString()
            internalMessage = error.message()
        }
    } catch (e: Exception) {
        KarhooError.Unexpected.apply {
            code = error.code().toString()
            internalMessage = error.message()
        }
    }
}
