package runningtracker.running.model;

import java.util.List;
import java.util.Map;

public interface IdHistoryCallback {
    void onSuccess ( List<Map<String, Object>> histories);
}
