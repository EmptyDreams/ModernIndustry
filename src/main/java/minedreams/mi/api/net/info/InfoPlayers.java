package minedreams.mi.api.net.info;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;

/**
 * 玩家列表管理(只存储名称)
 * @author EmptyDremas
 * @version V1.0
 */
public final class InfoPlayers implements SimpleImplInfo<String> {
	
	final List<String> infos = new ArrayList<>(1);
	
	/**
	 * 该方法返回值为内部存储的数据，根据规定用户不应该对返回值内容进行修改
	 */
	@Override
	public List<String> getInfos() {
		return infos;
	}
	
	@Override
	public String getInfo() {
		return (infos.size() == 0) ? null : infos.get(0);
	}

	@Override
	public void add(String info) {
		infos.add(info);
	}

	@Override
	public void delete(String info) {
		infos.remove(info);
	}
	
	@Override
	public void writeTo(ByteBuf buf) {
		buf.writeInt(infos.size());
		
		byte[] bytes;
		for (String name : infos) {
			bytes = name.getBytes();
			buf.writeInt(bytes.length);
			buf.writeBytes(bytes);
		}
	}

	@Override
	public void readFrom(ByteBuf buf) {
		int size = buf.readInt();
		
		byte[] bytes;
		for (int i = 0; i < size; ++i) {
			int length = buf.readInt();
			bytes = new byte[length];
			buf.readBytes(bytes);
			infos.add(new String(bytes));
		}
	}

}
