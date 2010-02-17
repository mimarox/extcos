package common;

public class StopWatch {
	private long start = 0L;
	private long stop  = 0L;
	
	public void start(){
		start = System.currentTimeMillis();
	}
	
	public void stop(){
		if (start == 0L){
			throw new IllegalStateException();
		}
		stop = System.currentTimeMillis();
	}
	
	public long getDuration(){
		return stop - start;
	}
	
	public void reset() {
		start = 0L;
		stop =  0L;
	}
}
