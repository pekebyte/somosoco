package com.pekebyte.somosoco.data.network;

import com.pekebyte.somosoco.data.models.OcoPosts;
import com.pekebyte.somosoco.data.models.Post;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;

public class MockWebservice implements Webservice {
    private final BehaviorDelegate<Webservice> delegate;

    public MockWebservice(BehaviorDelegate<Webservice> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<OcoPosts> getPosts(String key, String token) {
        OcoPosts response = new OcoPosts();
        List<Post> posts = new ArrayList<Post>();
        Post post = new Post();
        post.setId("FAKEID");
        post.setContent("FAKECONTENT");
        posts.add(post);
        response.setPosts(posts);
        return delegate.returningResponse(response).getPosts(key,token);
    }
}
