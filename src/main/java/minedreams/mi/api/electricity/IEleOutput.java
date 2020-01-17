package minedreams.mi.api.electricity;

/**
 * 可以输出电能的TE实现这个接口，同时需要实现Electricity类
 * @author EmptyDreams
 * @version V1.0
 */
public interface IEleOutput {
	
	/**
	 * 获取1tick最大电能输出量
	 * @return int 返回值应大于等于0
	 */
	int getOutputMax();
	
}
