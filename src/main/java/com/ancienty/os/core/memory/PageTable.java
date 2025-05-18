package com.ancienty.os.core.memory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PageTable {
    private final Map<Integer,Integer> vpageToFrame = new HashMap<>();

    public Integer getFrame(int vpage) {
        return vpageToFrame.get(vpage);
    }

    public void mapPage(int vpage, int frameId) {
        vpageToFrame.put(vpage, frameId);
    }

    public void unmapPage(int vpage) {
        vpageToFrame.remove(vpage);
    }

    public Map<Integer,Integer> getMappings() {
        return Collections.unmodifiableMap(vpageToFrame);
    }
}
