package com.ancienty.os.core.sched;

import com.ancienty.os.core.process.PCB;
import com.ancienty.os.core.process.ProcessState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;

public class RoundRobinScheduler implements Scheduler {
    private final Queue<PCB> readyQueue = new ArrayDeque<>();
    private final int quantum;

    public RoundRobinScheduler(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public void addProcess(PCB pcb) {
        pcb.setState(ProcessState.READY);
        readyQueue.offer(pcb);
    }

    @Override
    public void terminateProcess(int pid) {
        Iterator<PCB> it = readyQueue.iterator();
        while (it.hasNext()) {
            PCB p = it.next();
            if (p.getPid() == pid) {
                p.setState(ProcessState.TERMINATED);
                it.remove();
            }
        }
    }

    @Override
    public PCB nextProcess() {
        PCB pcb = readyQueue.poll();
        if (pcb == null) {
            return null;
        }
        pcb.setState(ProcessState.RUNNING);
        pcb.setPc(pcb.getPc() + quantum);
        pcb.setState(ProcessState.READY);
        readyQueue.offer(pcb);
        return pcb;
    }

    @Override
    public Collection<PCB> getReadyQueueSnapshot() {
        return Collections.unmodifiableList(new ArrayList<>(readyQueue));
    }

    @Override
    public int getQuantum() {
        return quantum;
    }
}
