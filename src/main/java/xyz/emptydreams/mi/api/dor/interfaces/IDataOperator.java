package xyz.emptydreams.mi.api.dor.interfaces;

import javax.annotation.Nonnull;

/**
 * 同时支持数据读写的Operator
 * @author EmptyDreams
 */
public interface IDataOperator extends IDataReader, IDataWriter{
	
	@Nonnull
	@Override
	IDataOperator copy();
	
}