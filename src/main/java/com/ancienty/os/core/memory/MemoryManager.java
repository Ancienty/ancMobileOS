package com.ancienty.os.core.memory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class MemoryManager {
    private final int totalFrames;
    private final Deque<Frame> freeList = new ArrayDeque<>();
    private final Queue<Frame> fifoQueue = new ArrayDeque<>();
    private final Map<Integer,PageTable> pageTables = new HashMap<>();
    private final Map<Integer,Frame> frameTable = new HashMap<>();

    public MemoryManager(int totalFrames) {
        this.totalFrames = totalFrames;
        for (int i = 0; i < totalFrames; i++) {
            Frame f = new Frame(i);
            freeList.addLast(f);
            frameTable.put(i, f);
        }
    }

    public void registerProcess(int pid) {
        pageTables.put(pid, new PageTable());
    }

    public void unregisterProcess(int pid) {
        PageTable pt = pageTables.remove(pid);
        if (pt != null) {
            for (Map.Entry<Integer,Integer> e : pt.getMappings().entrySet()) {
                int frameId = e.getValue();
                Frame f = frameTable.get(frameId);
                if (f != null) {
                    f.free();
                    freeList.addLast(f);
                    fifoQueue.remove(f);
                }
            }
        }
    }

    public MemResult accessPage(int pid, int vpage) {
        PageTable pt = pageTables.get(pid);
        if (pt == null) {
            registerProcess(pid);
            pt = pageTables.get(pid);
        }

        Integer frameId = pt.getFrame(vpage);
        if (frameId != null) {
            return new MemResult(frameId, true);
        } else {
            Frame allocated;
            if (!freeList.isEmpty()) {
                allocated = freeList.removeFirst();
            } else {
                allocated = fifoQueue.remove();
                PageTable victimPt = pageTables.get(allocated.getPid());
                if (victimPt != null) {
                    victimPt.unmapPage(allocated.getVpage());
                }
                allocated.free();
            }
            allocated.assign(pid, vpage);
            fifoQueue.add(allocated);
            pt.mapPage(vpage, allocated.getId());
            return new MemResult(allocated.getId(), false);
        }
    }

    public static class MemResult {
        private final int frameId;
        private final boolean hit;

        public MemResult(int frameId, boolean hit) {
            this.frameId = frameId;
            this.hit = hit;
        }

        public int getFrameId() {
            return frameId;
        }

        public boolean isHit() {
            return hit;
        }
    }

    public int getTotalFrames() {
        return totalFrames;
    }
}
