package com.aponte.antonio.grability.models;

/**
 * Created by Antonio on 9/1/2017.
 */
public class Object {
    private Feed feed;

    public Object() {
    }

    public Object(Feed feed) {
        this.feed = feed;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }
}
