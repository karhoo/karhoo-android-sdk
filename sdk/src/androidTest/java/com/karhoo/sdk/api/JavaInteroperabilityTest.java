package com.karhoo.sdk.api;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.karhoo.sdk.api.model.TripInfo;
import com.karhoo.sdk.api.model.TripState;
import com.karhoo.sdk.api.network.observable.Observable;
import com.karhoo.sdk.api.network.observable.Observer;
import com.karhoo.sdk.api.network.request.TripBooking;
import com.karhoo.sdk.api.network.response.Resource;
import com.karhoo.sdk.api.util.ServerRobot;
import com.karhoo.sdk.api.util.TestData;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import kotlin.Pair;
import kotlin.Unit;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/***
 * The purpose of these tests is to demonstrate the api usage with Java
 */
@RunWith(AndroidJUnit4.class)
public class JavaInteroperabilityTest {

    @Rule
    public WireMockRule rule = new WireMockRule(8089);

    private KarhooApi api = KarhooApi.INSTANCE;
    private ServerRobot serverRobot = new ServerRobot();

    @Before
    public void setUp() {
        serverRobot.successfulToken();
    }

    @After
    public void tearDown() {
        rule.resetAll();
    }

    /**
     * Given:   Book Trip is requested
     * When:    Successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    public void testCallInteroperability_book() throws InterruptedException {
        TripInfo trip = ServerRobot.Companion.getTRIP_DER();

        CountDownLatch latch = new CountDownLatch(1);

        serverRobot.bookingResponseWithNonce(HTTP_CREATED, trip, 0, new Pair("", ""));

        final TripInfo[] result = new TripInfo[1];

        TripBooking bookTrip = TestData.Companion.getBOOK_TRIP_WITH_NONCE();

        api.getTripService().book(bookTrip).execute(resource -> {
            if (resource instanceof Resource.Success) {
                Resource.Success success = (Resource.Success) resource;
                result[0] = (TripInfo) success.component1();
                latch.countDown();
            }
            return Unit.INSTANCE;
        });

        latch.await(2, TimeUnit.SECONDS);

        //Issue with comparing date due to Gson parsing milliseconds. Not an issue and is covered in other tests.
        assertThat(latch.getCount()).isZero();
        assertEquals(trip.getDestination(), result[0].getDestination());
        assertEquals(trip.getOrigin(), result[0].getOrigin());
        assertEquals(trip.getDisplayTripId(), result[0].getDisplayTripId());
        assertEquals(trip.getMeetingPoint(), result[0].getMeetingPoint());
        assertEquals(trip.getQuote(), result[0].getQuote());
        assertEquals(trip.getTripId(), result[0].getTripId());
    }

    /**
     * Given:   A booking id has been polled for tripstate
     * When:    Success 200 responses
     * Then:    The correctly parsed trip state is returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    public void testPollCallInteroperability_tripStatus() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(4);

        serverRobot.tripStatusResponse(HTTP_OK, ServerRobot.Companion.getTRIP_STATE(), 0, ServerRobot.Companion.getBOOKING_ID());

        final TripState[] result = new TripState[1];

        Observer<Resource<TripState>> observer = value -> {
            if (value instanceof Resource.Success) {
                Resource.Success success = (Resource.Success) value;
                result[0] = (TripState) success.component1();
                latch.countDown();
            }
        };

        Observable<TripState> observable = api.getTripService().status(TestData.BOOKING_ID).observable();

        observable.subscribe(observer, 300L);
        latch.await(1, TimeUnit.SECONDS);
        observable.unsubscribe(observer);

        TripState expectedResult = TestData.Companion.getTRIP_STATE();
        assertThat(latch.getCount()).isZero();
        assertThat(result[0]).isEqualTo(expectedResult);
    }


}