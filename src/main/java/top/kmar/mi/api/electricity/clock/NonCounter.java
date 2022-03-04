package top.kmar.mi.api.electricity.clock;

/**
 * 没有任何作用的计数器
 * @author EmptyDreams
 */
public final class NonCounter extends OverloadCounter {
	
	private static final NonCounter COUNTER = new NonCounter();
	
	public static NonCounter getInstance() { return COUNTER; }
	
	private NonCounter() { }
	
	@Override
	public void overload() { }
	
	@Override
	public void clean() { }
	
	@Override
	public int getTime() { return 0; }
	
	@Override
	public int getMaxTime() { return Integer.MAX_VALUE; }
	
	@Override
	public void setMaxTime(int maxTime) { }
	
	@Override
	protected void setTime(int time) { }
	
	@Override
	public void plus() { }
	
	@Override
	public void plus(int amount) { }
	
}