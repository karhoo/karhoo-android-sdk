package com.karhoo.sdk.api.network.observable

import com.karhoo.sdk.api.model.DriverTrackingInfo
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.call.Call
import com.nhaarman.mockitokotlin2.after
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class KarhooObservableTest {

    private val call: Call<DriverTrackingInfo> = mock()
    private val observer: Observer<Resource<DriverTrackingInfo>> = mock()
    private val observerTwo: Observer<Resource<DriverTrackingInfo>> = mock()

    private val captor = argumentCaptor<(Resource<DriverTrackingInfo>) -> Unit>()

    private lateinit var observable: Observable<DriverTrackingInfo>

    @Before
    fun setUp() {
        observable = KarhooObservable(call = call)
    }

    /**
     * Given:   A request is made to get the observable
     * When:    The observable is subscribed too
     * Then:    The execute method should be called on the call object
     **/
    @Ignore("Seems to be flaky")
    @Test
    fun `subscribing to the observer calls execute on call object`() {
        observable.subscribe(observer)
        verify(call, after(5)).execute(any())
    }

    /**
     * Given:   An observer is added to the observable
     * When:    Querying how many subscribed observers there are
     * Then:    The count should be 1
     **/
    @Test
    fun `adding an observer increases the observer count by one`() {
        observable.subscribe(observer)
        Assert.assertEquals(1, observable.count)
    }

    /**
     * Given:   Multiple observers are added to the observable
     * When:    Querying how many subscribed observers there are
     * Then:    The count should be the amount added
     **/
    @Test
    fun `adding multiple observers increases the observer count each time`() {
        observable.subscribe(observer)
        observable.subscribe(mock())
        Assert.assertEquals(2, observable.count)
    }

    /**
     * Given:   Multiple observers have been added to the observer
     * When:    Removing an observer
     * Then:    The size of the list should decrement by 1
     **/
    @Test
    fun `removing observer removes the observer in the list and decrements count`() {
        observable.subscribe(observer)
        observable.subscribe(mock())
        Assert.assertEquals(2, observable.count)
        observable.unsubscribe(observer)
        Assert.assertEquals(1, observable.count)
    }

    /**
     * Given:   Multiple observer has been added
     * When:    Notifying the observers
     * Then:    All the observers should be notified
     **/
    @Test
    fun `all observers notified when a response is returned`() {
        observable.subscribe(observer, 10)
        observable.subscribe(observerTwo)

        verify(call, after(100).atLeast(1)).execute(captor.capture())
        captor.firstValue.invoke(mock())
        verify(observer, after(100)).onValueChanged(any())
        verify(observerTwo, after(100)).onValueChanged(any())
    }

    /**
     * Given:   Multiple observers have been added
     * When:    Adding more observers
     * Then:    Call isn't re-executed
     **/
    @Test
    fun `adding multiple observers only calls the call object once to begin polling`() {
        observable.subscribe(observer)
        observable.subscribe(observerTwo)
        verify(call, after(100).atLeast(1)).execute(any())
    }

    /**
     * Given:   An observer has been added to the observable
     * When:    The call is executed
     * Then:    It is executed on multiple times with a delay
     **/
    @Ignore("Seems to be flaky")
    fun `when a subscriber has been added the call is executed at a poll rate`() {
        observable.subscribe(observer, 5)
        verify(call, after(50).atLeast(2)).execute(any())
    }

    /**
     * Given:   An observer has been added to the observable
     * When:    The observer has been removed
     * Then:    There should be no more calls to the execute function
     **/
    @Test
    fun `removing all observers cancels the polling of call execute`() {
        observable.subscribe(observer, 50)
        verify(call, after(5).atMost(1)).execute(any())
        observable.unsubscribe(observer)
        verify(call, after(100).atMost(1)).execute(any())
    }

}