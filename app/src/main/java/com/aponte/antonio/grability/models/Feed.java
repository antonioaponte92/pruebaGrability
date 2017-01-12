package com.aponte.antonio.grability.models;

import java.util.List;

/**
 * Created by Antonio on 9/1/2017.
 */
public class Feed {
    private List<Entry> entry;
    private Author author;

    public Feed() {
    }

    public List<Entry> getEntry() {
        return entry;
    }

    public void setEntry(List<Entry> entry) {
        this.entry = entry;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
