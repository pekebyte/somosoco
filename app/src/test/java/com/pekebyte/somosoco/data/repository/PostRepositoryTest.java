package com.pekebyte.somosoco.data.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.pekebyte.somosoco.data.models.OcoPosts;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(JUnit4.class)
public class PostRepositoryTest {
    private PostRepository postRepo;
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        postRepo = mock(PostRepository.class) ;
    }

    @Test
    public void testGetPosts(){

        MutableLiveData<OcoPosts> apiResponse = new MutableLiveData<>();
        when(postRepo.getPosts(null)).thenReturn(apiResponse);

        Observer<OcoPosts> observer = mock(Observer.class);
        postRepo.getPosts(null).observeForever(observer);

        OcoPosts response = new OcoPosts();
        apiResponse.postValue(response);

        verify(observer).onChanged(response);
    }
}
