package project.helper;

import java.util.List;

import ir.map.servicesdk.model.inner.SearchItem;

public interface LocationSearchListener {
    void OnResponseComplete(List<SearchItem> searchItems);
}
