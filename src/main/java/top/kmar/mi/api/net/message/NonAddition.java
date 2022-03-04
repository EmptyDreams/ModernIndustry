package top.kmar.mi.api.net.message;

import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.dor.interfaces.IDataWriter;

/**
 * 空信息
 * @author EmptyDreams
 */
public final class NonAddition implements IMessageAddition {
	
	@Override
	public void writeTo(IDataWriter writer) { }
	
	@Override
	public void readFrom(IDataReader reader) { }
	
}