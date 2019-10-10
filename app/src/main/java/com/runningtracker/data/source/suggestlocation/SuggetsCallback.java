package com.runningtracker.data.source.suggestlocation;

import com.runningtracker.data.model.suggestplace.SuggestLocation;

import java.util.List;


public interface SuggetsCallback {
    void onSuccess(List<SuggestLocation> suggestLocationList);
}
