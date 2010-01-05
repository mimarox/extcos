package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;
import static net.sf.extcos.util.Assert.ise;
import static net.sf.extcos.util.StringUtils.append;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.extcos.util.Assert;

import com.google.inject.Singleton;

@Singleton
public class ThreadManager {
	private AtomicInteger registered = new AtomicInteger();
	private AtomicInteger invoked = new AtomicInteger();
	private ThreadPoolExecutor executor;
	
	public void register() {
		registered.incrementAndGet();
	}
	
	public void invoke(Runnable runnable) {
		Assert.isTrue(invoked.get() < registered.get(), ise());
		Assert.notNull(runnable, iae());
		
		invoked.incrementAndGet();
		if (invoked.get() == 1) {
			invokeBlocking(runnable);
		} else {
			invokeNonBlocking(runnable);
		}
	}
	
	private void invokeBlocking(Runnable runnable) {
		Thread t = new Thread(runnable, append("eXtcos managed thread ", invoked));
		t.setDaemon(true);
		t.start();
		
		while (t.isAlive()) {
			try {
				t.join();
			} catch (InterruptedException ignored) {
			}
		}
	}
	
	private void invokeNonBlocking(Runnable runnable) {
		getExecutor().execute(runnable);
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
				public Thread newThread(Runnable runnable) {
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