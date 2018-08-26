package com.pekebyte.somosoco.data.network;

import com.pekebyte.somosoco.data.models.OcoPosts;
import com.pekebyte.somosoco.ui.helpers.Constants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

@RunWith(JUnit4.class)
public class WebserviceTest {
    Webservice ws;

    @Before
    public void init() {
        ws = new MockRestAdapter().build();
    }

    @Test
    public void testGetPosts() throws IOException {
        //Actual Test
        Call<OcoPosts> quote = ws.getPosts(Constants.BLOGGER_KEY, null);
        Response<OcoPosts> response = quote.execute();
        Assert.assertNotNull(response.body());
        Assert.assertEquals(response.body().getPosts().get(0).getId(),"FAKEID");

    }


}
