package minedreams.mi.api.electricity.info;

/**
 * 保存电力输出信息
 */
public enum OutPutResult {
	
	/** 完全成功，所需电力完全输出 */
	YES,
	/** 完全失败，没有任何电力输出 */
	FAILURE,
	/** 部分失败，只输出了部分电力 */
	NOT_ENOUGH

}