package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.SavedPaymentInfo
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.CardType
import com.karhoo.sdk.api.model.PaymentsNonce
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.NonceRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class GetNonceInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                      private val apiTemplate: APITemplate,
                                                      private val userManager: UserManager,
                                                      private val context: CoroutineContext = Dispatchers.Main)
    : BaseCallInteractor<PaymentsNonce>(true, credentialsManager, apiTemplate, context) {

    var nonceRequest: NonceRequest? = null

    override fun createRequest(): Deferred<Resource<PaymentsNonce>> {
        nonceRequest?.let {
            return GlobalScope.async {
                return@async getNonceAndSaveCard(it)
            }
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

    private suspend fun getNonceAndSaveCard(nonceRequest: NonceRequest): Resource<PaymentsNonce> {
        val nonce = apiTemplate.nonce(nonceRequest).await()
        return when (nonce) {
            is Resource.Success -> {
                savePaymentInfo(SavedPaymentInfo(nonce.data.lastFour, nonce.data.cardType))
                nonce
            }
            is Resource.Failure -> {
                savePaymentInfo(SavedPaymentInfo(lastFour = "", cardType = CardType.NOT_SET))
                Resource.Failure(error = nonce.error)
            }
        }
    }

    private fun savePaymentInfo(savedPaymentInfo: SavedPaymentInfo) {
        GlobalScope.launch(context) {
            savedPaymentInfo.cardType?.let {
                userManager.savedPaymentInfo = savedPaymentInfo
            }
        }
    }

}