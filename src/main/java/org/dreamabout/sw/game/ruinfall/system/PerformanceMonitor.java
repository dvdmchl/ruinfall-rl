package org.dreamabout.sw.game.ruinfall.system;
public class PerformanceMonitor {
    private final boolean enabled = Boolean.getBoolean("ruinfall.debugPerf");
    public <T> T measure(String name, java.util.concurrent.Callable<T> c) throws Exception {
        if(!enabled) return c.call();
        long start = System.nanoTime();
        try { return c.call(); } finally {
            long dur = System.nanoTime()-start;
            System.out.println("[PERF] " + name + " took " + (dur/1_000_000.0) + " ms");
        }
    }
}
