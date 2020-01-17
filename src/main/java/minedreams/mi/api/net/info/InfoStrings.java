package minedreams.mi.api.net.info;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class InfoStrings implements SimpleImplInfo<String> {
	
	private List<String> list = new ArrayList<>();
	
	@Override
	public String getInfo() {
		return (list.size() == 0 ? null : list.get(0));
	}
	
	@Override
	public List<String> getInfos() {
		return list;
	}
	
	@Override
	public void add(String info) {
		list.add(info);
	}
	
	@Override
	public void delete(String info) {
		list.remove(info);
	}
	
	@Override
	public void writeTo(ByteBuf buf) {
		buf.writeInt(list.size());
		list.forEach(str -> ByteBufUtils.writeUTF8String(buf, str));
	}
	
	@Override
	public void readFrom(ByteBuf buf) {
		int size = buf.readInt();
		list = new ArrayList<>(size);
		for (int i = 0; i < size; ++i)
			list.add(ByteBufUtils.readUTF8String(buf));
	}
}
