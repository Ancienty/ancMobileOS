package com.ancienty.os.core.memory;

public class Frame {
    private final int id;
    private int pid = -1;
    private int vpage = -1;

    public Frame(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getPid() {
        return pid;
    }

    public int getVpage() {
        return vpage;
    }

    public void assign(int pid, int vpage) {
        this.pid = pid;
        this.vpage = vpage;
    }

    public void free() {
        this.pid = -1;
        this.vpage = -1;
    }

    public boolean isFree() {
        return pid == -1;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "id=" + id +
                ", pid=" + pid +
                ", vpage=" + vpage +
                '}';
    }
}
