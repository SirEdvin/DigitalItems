// SPDX-FileCopyrightText: 2025 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0

package site.siredvin.digitalitems.testmod.mixin;

import com.mojang.datafixers.DataFixer;
import dan200.computercraft.core.ComputerContext;
import dan200.computercraft.core.computer.computerthread.ComputerThread;
import dan200.computercraft.shared.computer.core.ServerContext;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.net.Proxy;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/** Testiarium 0.1.1 has no scheduler barrier on 1.20; do not advance Lua timers past queued work. */
@Mixin(GameTestServer.class)
abstract class CctGameTestServerMixin extends MinecraftServer {
    @Unique
    private static final MethodHandle digitalitems$context;
    @Unique
    private static final MethodHandle digitalitems$queueSize;
    @Unique
    private static final MethodHandle digitalitems$workerCount;
    @Unique
    private static final MethodHandle digitalitems$idleWorkers;

    static {
        // shortcut: pinned CC 1.113 internals, replace when upgrading the harness.
        try {
            var lookup = MethodHandles.lookup();
            var context = ServerContext.class.getDeclaredField("context");
            context.setAccessible(true);
            digitalitems$context = lookup.unreflectGetter(context);
            var queue = ComputerThread.class.getDeclaredMethod("computerQueueSize");
            queue.setAccessible(true);
            digitalitems$queueSize = lookup.unreflect(queue);
            var workers = ComputerThread.class.getDeclaredMethod("workerCount");
            workers.setAccessible(true);
            digitalitems$workerCount = lookup.unreflect(workers);
            var idle = ComputerThread.class.getDeclaredField("idleWorkers");
            idle.setAccessible(true);
            digitalitems$idleWorkers = lookup.unreflectGetter(idle);
        } catch (ReflectiveOperationException error) {
            throw new ExceptionInInitializerError(error);
        }
    }

    CctGameTestServerMixin(Thread serverThread, LevelStorageSource.LevelStorageAccess storageSource,
                          PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer fixerUpper,
                          Services services, ChunkProgressListenerFactory progressListenerFactory) {
        super(serverThread, storageSource, packRepository, worldStem, proxy, fixerUpper, services, progressListenerFactory);
    }

    @Overwrite
    @Override
    public void waitUntilNextTick() {
        while (true) {
            runAllTasks();
            if (!haveTestsStarted() || digitalitems$computersIdle()) break;
            LockSupport.parkNanos(100_000);
        }
    }

    @Unique
    private boolean digitalitems$computersIdle() {
        try {
            var context = (ComputerContext) digitalitems$context.invokeExact(ServerContext.get(this));
            var thread = (ComputerThread) context.computerScheduler();
            var queued = (int) digitalitems$queueSize.invokeExact(thread);
            var workers = (int) digitalitems$workerCount.invokeExact(thread);
            var idle = (AtomicInteger) digitalitems$idleWorkers.invokeExact(thread);
            return queued == 0 && idle.get() >= workers;
        } catch (Throwable error) {
            throw new IllegalStateException("Cannot synchronize pinned CC:Tweaked 1.113 GameTests", error);
        }
    }

    @Shadow
    private boolean haveTestsStarted() {
        throw new AssertionError("Mixin shadow");
    }
}
