package com.example.feedler.PagedList;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;

import com.example.feedler.Post;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostStorage {
    interface Callback<R> {
        public void onResult(String nextFrom, R result, Throwable error);
    }

    public PostStorage() {
    }

    public void getData(final String startFrom, final int count, final Callback<List<Post>> callback){
        final Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put(VKApiConst.FILTERS, "post");
        paramsMap.put("count", count);

        if (startFrom != null) {
            paramsMap.put(VKApiConst.FILTERS, startFrom);
        }

        final VKParameters params = new VKParameters(paramsMap);
        final VKRequest request = new VKRequest("newsfeed.get", params); //Запрос с фильтром post
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                try {
                    final List<Post> list = new ArrayList<>();
                    JSONObject jsonObject = (JSONObject) response.json.get("response"); //получаем JSON объект по запросу
                    JSONArray jsonArray = (JSONArray) jsonObject.get("items"); //Получаем конкретно посты
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject post = (JSONObject) jsonArray.get(i);
                        list.add( new Post("" ,null,post.optString("text"),i));
                        System.out.println(post.optString("text"));//Производим перебор и получаем то, что является телом поста
                    }


                    final String nextFrom = jsonObject.optString("next_from");
                    callback.onResult(nextFrom, list, null);
                } catch (Throwable e) {
                    e.printStackTrace();
                    callback.onResult(startFrom, null, e);
                }
            }
        });
    }
    public static class PostDiffUtilCallback extends DiffUtil.ItemCallback<Post> {

        public PostDiffUtilCallback() {
        }

        @Override
        public boolean areItemsTheSame(@NonNull Post oldPost, @NonNull Post newPost) {
            return oldPost.getId() == newPost.getId();
        }

        @Override
        public boolean areContentsTheSame(Post oldPost, Post newPost) {
            return oldPost.getPostText().equals(newPost.getPostText());
        }

    }
}
