package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;
import static net.sf.extcos.util.Assert.ise;
import static net.sf.extcos.util.StringUtils.append;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.extcos.util.Assert;

public class ThreadManager {
	private static ThreadManager instance;

	private final AtomicInteger registered = new AtomicInteger();
	private final AtomicInteger invoked = new AtomicInteger();
	private final AtomicInteger finished = new AtomicInteger();
	private ThreadPoolExecutor executor;
	private final Object sync = new Object();

	private ThreadManager() {
	}

	public static ThreadManager getInstance() {
		if (instance == null) {
			instance = new ThreadManager();
		}

		return instance;
	}

	public void register() {
		registered.incrementAndGet();
	}

	public void invoke(final Runnable runnable) {
		Assert.isTrue(invoked.get() < registered.get(), ise());
		Assert.notNull(runnable, iae());

		invoked.incrementAndGet();
		if (invoked.get() == 1) {			// if root filter interceptor invoked
			invokeBlocking(runnable);
		} else {							// if matching filter interceptor invoked
			invokeNonBlocking(runnable);
		}
	}

	private void invokeBlocking(final Runnable runnable) {
		Thread t = new Thread(runnable, append("eXtcos managed thread ", invoked));
		t.setDaemon(true);
		t.start();

		while (t.isAlive()) {
			try {
				t.join();
			} catch (InterruptedException ignored) { /* ignored */ }
		}

		finished.incrementAndGet();

		while (finished.get() < registered.get()) {
			try {
				synchronized (sync) {
					sync.wait();
				}
			} catch (InterruptedException ignored) { /* ignored */ }
		}

		if (executor != null) {			// if returning all without storing any there's no executor
			executor.shutdownNow();
		}
	}

	private void invokeNonBlocking(final Runnable runnable) {
		getExecutor().execute(new Runnable() {
			@Override
			public void run() {
				runnable.run();
				finished.incrementAndGet();

				synchronized (sync) {
					sync.notify();
				}
			}
		});
	}

	// for lazy initialization
	private ThreadPoolExecutor getExecutor() {
		if (executor == null) {
			executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
			executor.setCorePoolSize(5 > registered.get() ? registered.get() : 5);
			executor.setMaximumPoolSize(10);
			executor.setThreadFactory(new ThreadFactory() {
				private ThreadGroup threadGroup;

				@Override
				public Thread newThread(final Runnable runnable) {
					Thread thread = new Thread(getThreadGroup(), runnable, append("eXtcos managed thread ", getInvoked()));
					thread.setDaemon(true);
					return thread;
				}

				private ThreadGroup getThreadGroup() {
					if (threadGroup == null) {
						threadGroup = new ThreadGroup("eXtcos Thread Group");
						threadGroup.setDaemon(true);
					}

					return threadGroup;
				}
			});
			executor.prestartCoreThread();
		}

		return executor;
	}

	private int getInvoked() {
		return invoked.get();
	}
}