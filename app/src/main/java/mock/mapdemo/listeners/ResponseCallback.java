package mock.mapdemo.listeners;

import mock.mapdemo.utils.JsonData;

public interface ResponseCallback {
    void onSuccess(String response);
    void onUpdate(int state, int index);
    void updateData(JsonData jsonData, int index, int totalCount);
    void onFailure(String errorMessage);
}
