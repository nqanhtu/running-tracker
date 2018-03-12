package runningtracker.repository.suggestlocation;

import java.util.List;

import runningtracker.data.model.suggest_place.SuggestLocation;

/**
 * Created by Tri on 3/11/2018.
 */

public interface SuggetsCallback {
    void onSuccess(List<SuggestLocation> suggestLocationList);
}
