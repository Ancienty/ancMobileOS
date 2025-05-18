package com.ancienty.os.core.process;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class PCB {
    private static final AtomicInteger PID_GEN = new AtomicInteger(1);
    private final int pid;
    private String name;
    private ProcessState state;
    private long pc;
    private final int[] registers = new int[8];
    private int priority;
    private int batteryHint;

    public PCB(String name) {
        this.pid = PID_GEN.getAndIncrement();
        this.name = name;
        this.state = ProcessState.NEW;
        this.pc = 0L;
        this.priority = 0;
        this.batteryHint = 100;
    }

    public PCB() {
        this("Process-" + PID_GEN.getAndIncrement());
    }

    public int getPid() { return pid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ProcessState getState() { return state; }
    public void setState(ProcessState state) { this.state = state; }
    public long getPc() { return pc; }
    public void setPc(long pc) { this.pc = pc; }
    public int[] getRegisters() { return registers; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public int getBatteryHint() { return batteryHint; }
    public void setBatteryHint(int batteryHint) { this.batteryHint = batteryHint; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PCB)) return false;
        PCB pcb = (PCB) o;
        return pid == pcb.pid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid);
    }

    @Override
    public String toString() {
        return "PCB{" +
                "pid=" + pid +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", pc=" + pc +
                '}';
    }
}
