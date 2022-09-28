package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.VehicleMapping
import com.karhoo.sdk.api.model.VehicleMappings
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

class VehicleMappingsInteractorTest : BaseKarhooUserInteractorTest() {
    internal lateinit var interactor: VehicleMappingsInteractor

    override fun setUp() {
        super.setUp()
        interactor = VehicleMappingsInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to get the quotes images rule sets
     * When:    There is a request created
     * Then:    The request is executed
     **/
    @Test
    fun `When the interactor successfully obtains a Rules List, the request is executed`() {
        val rulesList = arrayListOf<VehicleMapping>()
        rulesList.add(VehicleMapping(VEHICLE_TYPE, VEHICLE_TAGS, VEHICLE_IMAGE_PNG, VEHICLE_IMAGE_SVG))
        whenever(apiTemplate.vehicleMappings(anyString(), anyString()))
            .thenReturn(CompletableDeferred(Resource.Success(VehicleMappings(rulesList))))

        var returnedRuleList: VehicleMappings? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedRuleList = result.data
                    is Resource.Failure -> Assert.fail()
                }
            }
            delay(5)
        }

        verify(apiTemplate).vehicleMappings(anyString(), anyString())
    }

    /**
     * Given:   A request is made to get the quotes images rule sets
     * When:    There is a list
     * Then:    A response with the correct data is returned
     **/
    @Test
    fun `When the interactor successfully obtains a Rules List, the information is correctly returned`() {
        val rulesList = arrayListOf<VehicleMapping>()
        rulesList.add(VehicleMapping(VEHICLE_TYPE, VEHICLE_TAGS, VEHICLE_IMAGE_PNG, VEHICLE_IMAGE_SVG))
        whenever(apiTemplate.vehicleMappings(anyString(), anyString()))
            .thenReturn(CompletableDeferred(Resource.Success(VehicleMappings(rulesList))))

        var returnedRuleList: VehicleMappings? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedRuleList = result.data
                    is Resource.Failure -> Assert.fail()
                }
            }
            delay(5)
        }

        Assert.assertNotNull(returnedRuleList)
        Assert.assertEquals(returnedRuleList?.mappings?.get(0)?.vehicleImagePNG, VEHICLE_IMAGE_PNG)
        Assert.assertEquals(returnedRuleList?.mappings?.get(0)?.vehicleImageSVG, VEHICLE_IMAGE_SVG)
        Assert.assertEquals(returnedRuleList?.mappings?.get(0)?.vehicleType, VEHICLE_TYPE)
        Assert.assertEquals(returnedRuleList?.mappings?.get(0)?.vehicleTags, VEHICLE_TAGS)

        verify(apiTemplate).vehicleMappings(anyString(), anyString())
    }

    @Test
    fun `When receiving a failure result, then the error is the correct one`() {
        var shouldBeNull: VehicleMappings? = null
        var error: KarhooError? = null

        whenever(apiTemplate.vehicleMappings(anyString(), anyString()))
            .thenReturn(CompletableDeferred(Resource.Failure(KarhooError.InternalSDKError)))

        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(5)
        }

        Assert.assertEquals(KarhooError.InternalSDKError, error)
        Assert.assertNull(shouldBeNull)
    }

    companion object {
        private const val VEHICLE_TYPE = "standard"
        private val VEHICLE_TAGS = arrayListOf<String>().apply {
            add("electric")
            add("luxe")
        }
        private const val VEHICLE_IMAGE_PNG = "png"
        private const val VEHICLE_IMAGE_SVG = "svg"
    }
}
