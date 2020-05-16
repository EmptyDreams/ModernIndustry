package xyz.emptydreams.mi.api.utils.timetask;

/**
 * 时间任务的执行接口
 * @author EmptyDreams
 * @version V1.0
 */
public interface ITimeTask {
	
	/**
	 * 当执行到指定时长时执行该方法
	 * @return 是否删除当前任务，返回true自动删除该任务
	 */
	boolean accept();
	
	/** 获取当前时间 */
	int getTime();
	
	/** 增加计数器 */
	int plus();
	
	/** 设置时间 */
	void setTime(int time);
	
	/** 获取最大时间 */
	int getMaxTime();
	
}
