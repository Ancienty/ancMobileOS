package com.ancienty.os;

import com.ancienty.os.core.process.PCB;
import com.ancienty.os.core.sched.RoundRobinScheduler;
import com.ancienty.os.core.sched.Scheduler;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoundRobinSchedulerTest {
    @Test
    void orderWrapsCorrectly() {
        Scheduler sched = new RoundRobinScheduler(50);
        PCB p1 = new PCB("A");
        PCB p2 = new PCB("B");
        sched.addProcess(p1);
        sched.addProcess(p2);
        assertEquals(p1, sched.nextProcess());
        assertEquals(p2, sched.nextProcess());
        assertEquals(p1, sched.nextProcess());
    }

    @Test
    void quantumIsReported() {
        RoundRobinScheduler rr = new RoundRobinScheduler(75);
        assertEquals(75, rr.getQuantum());
    }
}
