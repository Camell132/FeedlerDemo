package com.example.feedler.PagedList;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.example.feedler.Post;

import java.util.List;

public class MyPositionalDataSource extends PositionalDataSource<Post> {

    private final PostStorage postStorage;

    protected String mNextFrom = null;

    public MyPositionalDataSource(PostStorage postStorage) {
        this.postStorage = postStorage;
    }

    @Override
    public void loadInitial(@NonNull final LoadInitialParams params, @NonNull final LoadInitialCallback<Post> callback) {
        final PostStorage.Callback<List<Post>> postCallback = new PostStorage.Callback<List<Post>>() {
            @Override
            public void onResult(String nextFrom, List<Post> result, Throwable error) {
                mNextFrom = nextFrom;
                callback.onResult(result, params.requestedStartPosition);
            }
        };

        Log.d("MyPositionalDataSource", String.format("load(%s, %s)", mNextFrom, params.requestedLoadSize));
        postStorage.getData(mNextFrom, params.requestedLoadSize, postCallback);

    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, final @NonNull LoadRangeCallback<Post> callback) {
        Log.d("MyPositionalDataSource", "loadRange");
        final PostStorage.Callback<List<Post>> postCallback = new PostStorage.Callback<List<Post>>() {
            @Override
            public void onResult(String nextFrom, List<Post> result, Throwable error) {
                mNextFrom = nextFrom;
                callback.onResult(result);
            }
        };


        Log.d("MyPositionalDataSource", String.format("load(%s, %s)", mNextFrom, params.loadSize));
        postStorage.getData(mNextFrom, params.loadSize, postCallback);
    }
}