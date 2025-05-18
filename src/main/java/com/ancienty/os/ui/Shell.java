package com.ancienty.os.ui;

import com.ancienty.os.core.process.PCB;
import com.ancienty.os.core.sched.RoundRobinScheduler;
import com.ancienty.os.core.sched.Scheduler;

import java.util.Scanner;

public final class Shell {
    private static final int DEFAULT_QUANTUM = 50;
    private final Scheduler scheduler = new RoundRobinScheduler(DEFAULT_QUANTUM);
    private final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        new Shell().repl();
    }

    private void repl() {
        System.out.println("ancMobileOS shell (type 'help' for commands)");
        while (true) {
            System.out.print("> ");
            if (!in.hasNext()) break;
            String cmd = in.next().trim().toLowerCase();
            switch (cmd) {
                case "fork"  -> handleFork();
                case "ps"    -> handlePs();
                case "kill"  -> handleKill();
                case "tick"  -> handleTick();
                case "help"  -> printHelp();
                case "exit"  -> { System.out.println("Bye."); return; }
                default      -> System.out.println("Unknown cmd — try 'help'.");
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
        System.out.printf("Forked %s with PID %d%n", appName, pcb.getPid());
    }

    private void handlePs() {
        System.out.printf("%-5s %-10s %-10s %-10s%n", "PID", "STATE", "PC", "PRIO");
        for (PCB pcb : scheduler.getReadyQueueSnapshot()) {
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

    private void printHelp() {
        System.out.println("""
            Commands:
              fork <AppName>    create a new process
              ps               list ready queue
              kill <pid>       terminate process
              tick             run one scheduler tick
              exit             quit shell
            """);
    }
}
