package runningtracker.data.source.suggestlocation;

import java.util.List;

import runningtracker.data.model.suggestplace.SuggestLocation;



public interface SuggetsCallback {
    void onSuccess(List<SuggestLocation> suggestLocationList);
}
