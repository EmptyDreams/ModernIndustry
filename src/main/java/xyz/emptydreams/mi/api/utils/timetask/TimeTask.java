package xyz.emptydreams.mi.api.utils.timetask;

/**
 * @author EmptyDreams
 * @version V1.0
 */
abstract public class TimeTask implements ITimeTask {
	
	private int nowTime = 0;
	public final int maxTime;
	
	public TimeTask(int maxTime) {
		this.maxTime = maxTime;
	}
	
	@Override
	public int getTime() { return nowTime; }
	@Override
	public int plus() { return ++nowTime; }
	@Override
	public void setTime(int time) { nowTime = time; }
	@Override
	public int getMaxTime() { return maxTime; }
	
}
