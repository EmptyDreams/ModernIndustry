package xyz.emptydreams.mi.api.fluid.data;

import javax.annotation.Nonnull;

/**
 * 流体管道的流体数据
 * @author EmptyDreams
 */
public class FluidPipeData {
	
	private FluidData data = FluidData.empty();

	private final int max;
	
	public FluidPipeData() {
		this(1000);
	}
	
	public FluidPipeData(int max) {
		this.max = max;
	}
	
	public int getAmount() {
		return data.getAmount();
	}
	
	/**
	 * <p>插入指定的流体数据
	 * <p>若本身存储的流体类型与插入的流体类型不相符，则直接将本身所有流体数据全部挤出
	 * @param insert 要插入的数据
	 * @param simulate 是否为模拟，为true时不修改数据
	 * @return 挤出的数据
	 */
	@Nonnull
	public FluidDataList insert(FluidData insert, boolean simulate) {
		FluidDataList result = new FluidDataList();
		if (insert.matchFluid(data)) {
			int amount = insert.getAmount() + getAmount();
			if (amount > max) {
				result.add(insert.copy(amount - max));
				if (!simulate) data.setAmount(max);
			} else {
				data.setAmount(amount);
			}
		} else {
			result.add(data);
			if (!simulate) data = insert.copy();
		}
		return result;
	}
	
	/**
	 * 从容器中取出指定的流体
	 * @param extract 要取出的流体，若流体类型与存储类型不相符则不取出
	 * @param simulate 是否为模拟，为true时不修改数据
	 * @return 真实取出的数据量
	 */
	public int extract(FluidData extract, boolean simulate) {
		if (!extract.matchFluid(data)) return 0;
		return extract(extract.getAmount(), simulate);
	}
	
	/**
	 * 从容器中取出指定量的流体
	 * @param amount 要取出的流体量
	 * @param simulate 是否为模拟，为true时不修改数据
	 * @return 真实取出的数据量
	 */
	public int extract(int amount, boolean simulate) {
		amount = Math.min(amount, getAmount());
		if (simulate) return amount;
		data.minusAmount(amount);
		return amount;
	}
	
}