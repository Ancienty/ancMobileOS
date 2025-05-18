package com.ancienty.os.ui;

import com.ancienty.os.core.process.PCB;
import com.ancienty.os.core.sched.RoundRobinScheduler;
import com.ancienty.os.core.sched.Scheduler;
import com.ancienty.os.core.memory.MemoryManager;
import com.ancienty.os.core.memory.MemoryManager.MemResult;

import java.util.Collection;
import java.util.Scanner;

public final class Shell {
    private static final int DEFAULT_QUANTUM = 50;
    private static final int DEFAULT_FRAMES = 32;
    private final Scheduler scheduler = new RoundRobinScheduler(DEFAULT_QUANTUM);
    private final MemoryManager memoryManager = new MemoryManager(DEFAULT_FRAMES);
    private final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        new Shell().repl();
    }

    private void repl() {
        System.out.println("ancMobileOS shell (type 'help' for commands)");
        while (true) {
            System.out.print("> ");
            if (!in.hasNext()) {
                break;
            }
            String cmd = in.next().trim().toLowerCase();
            switch (cmd) {
                case "fork"  -> handleFork();
                case "ps"    -> handlePs();
                case "kill"  -> handleKill();
                case "tick"  -> handleTick();
                case "memtrace" -> handleMemtrace();
                case "help"  -> printHelp();
                case "exit"  -> { System.out.println("Bye."); return; }
                default       -> System.out.println("Unknown cmd — try 'help'.");
            }
        }
    }

    private void handleFork() {
        if (!in.hasNext()) {
            System.out.println("Usage: fork <AppName>");
            return;
        }
        String appName = in.next();
        PCB pcb = new PCB(appName);
        scheduler.addProcess(pcb);
        memoryManager.registerProcess(pcb.getPid());
        System.out.printf("Forked %s with PID %d%n", appName, pcb.getPid());
    }

    private void handlePs() {
        System.out.printf("%-5s %-10s %-10s %-10s%n", "PID", "STATE", "PC", "PRIO");
        Collection<PCB> procs = scheduler.getReadyQueueSnapshot();
        for (PCB pcb : procs) {
            System.out.printf("%-5d %-10s %-10d %-10d%n",
                    pcb.getPid(), pcb.getState(), pcb.getPc(), pcb.getPriority());
        }
    }

    private void handleKill() {
        if (!in.hasNextInt()) {
            System.out.println("Usage: kill <pid>");
            return;
        }
        int pid = in.nextInt();
        scheduler.terminateProcess(pid);
        memoryManager.unregisterProcess(pid);
        System.out.printf("PID %d terminated%n", pid);
    }

    private void handleTick() {
        PCB running = scheduler.nextProcess();
        if (running == null) {
            System.out.println("No runnable processes.");
        } else {
            System.out.printf("Ran PID %d for %d ticks%n",
                    running.getPid(), scheduler.getQuantum());
        }
    }

    private void handleMemtrace() {
        if (!in.hasNextInt()) {
            System.out.println("Usage: memtrace <pid> <vpage>");
            return;
        }
        int pid = in.nextInt();
        if (!in.hasNextInt()) {
            System.out.println("Usage: memtrace <pid> <vpage>");
            return;
        }
        int vpage = in.nextInt();
        MemResult res = memoryManager.accessPage(pid, vpage);
        if (res.isHit()) {
            System.out.printf("HIT (frame %d)%n", res.getFrameId());
        } else {
            System.out.printf("FAULT -> loaded at frame %d%n", res.getFrameId());
        }
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  fork <AppName>    create a new process");
        System.out.println("  ps               list ready queue");
        System.out.println("  kill <pid>       terminate process");
        System.out.println("  tick             run one scheduler tick");
        System.out.println("  memtrace <pid> <vpage>   simulate memory access");
        System.out.println("  exit             quit shell");
    }
}