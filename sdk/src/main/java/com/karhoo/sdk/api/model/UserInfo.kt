package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfo(@SerializedName("first_name") var firstName: String = "",
                    @SerializedName("last_name") var lastName: String = "",
                    @SerializedName("email") var email: String = "",
                    @SerializedName("country_code") val countryCode: String = "",
                    @SerializedName("phone_number") var phoneNumber: String = "",
                    @SerializedName("user_id") val userId: String = "",
                    @SerializedName("locale") val locale: String = "",
                    @SerializedName("organisations") val organisations: List<Organisation> = mutableListOf(),
                    @SerializedName("primary_organisation_id") val primaryOrganisationId: String? = "",
                    @SerializedName("upstream") val externalUserInfo: Upstream = Upstream(),
                    @SerializedName("sub") val sub: String = "", //karhoo user id
                    @SerializedName("avatar_url") val avatarUrl: String? = "")
    : Parcelable
