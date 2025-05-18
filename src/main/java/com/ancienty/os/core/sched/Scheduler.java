package com.ancienty.os.core.sched;

import com.ancienty.os.core.process.PCB;
import java.util.Collection;

public interface Scheduler {
    void addProcess(PCB pcb);
    void terminateProcess(int pid);
    PCB nextProcess();
    Collection<PCB> getReadyQueueSnapshot();
    int getQuantum();
}
