package minedreams.mi.api.net.message;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minedreams.mi.api.net.info.InfoStrings;
import minedreams.mi.api.net.info.SimpleImplInfo;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class MessageList {
	
	private Map<String, byte[]> byteArray = new LinkedHashMap<>();
	private Map<String, Short> shorts = new LinkedHashMap<>();
	private Map<String, Byte> bytes = new LinkedHashMap<>();
	private Map<String, Integer> ints = new LinkedHashMap<>();
	private Map<String, Long> longs = new LinkedHashMap<>();
	private Map<String, SimpleImplInfo<?>> infos = new LinkedHashMap<>();
	private InfoStrings names = new InfoStrings();
	
	private List<EntityPlayerMP> players = new LinkedList<>();
	
	//----------------------------------------写入----------------------------------------//
	
	public void writeByteArray(String key, byte[] bs) {
		byteArray.put(key, Arrays.copyOf(bs, bs.length));
	}
	
	public void writeShort(String key, short s) {
		shorts.put(key, s);
	}
	
	public void writeByte(String key, byte b) {
		bytes.put(key, b);
	}
	
	/** 添加一个信息到列表 */
	public void writeInfo(String key, SimpleImplInfo<?> im) {
		infos.put(key, im);
		names.add(im.getClass().getName());
	}
	
	/**
	 * 向玩家列表添加元素，该列表表示需要接收消息的玩家，只需在服务端调用
	 */
	public void addPlayer(EntityPlayerMP player) {
		players.add(player);
	}
	
	/**
	 * 写入long类型
	 */
	public void writeLong(String key, long l) {
		longs.put(key, l);
	}
	
	/**
	 * 写入int类型
	 */
	public void writeInt(String key, int i) {
		ints.put(key, i);
	}
	
	
	//----------------------------------------读取----------------------------------------//
	
	public byte[] readByteArray(String key) {
		byte[] b = byteArray.get(key);
		return Arrays.copyOf(b, b.length);
	}
	
	public byte readByte(String key) {
		return bytes.get(key);
	}
	
	public short readShort(String key) {
		return shorts.get(key);
	}
	
	public SimpleImplInfo<?> readInfo(String key) {
		return infos.get(key);
	}
	
	/**
	 * 获取每个信息对应的完整类名
	 */
	public InfoStrings getNames() {
		return names;
	}
	
	/**
	 * 获取复制的玩家列表，改变该列表不会影响源数据，只需在服务端调用
	 */
	public List<EntityPlayerMP> getPlayers() {
		return new LinkedList<>(players);
	}
	
	/**
	 * 获取指定int
	 *
	 * @throws NullPointerException 若不存在key
	 */
	public int readInt(String key) {
		return ints.get(key);
	}
	
	/**
	 * 获取指定long
	 *
	 * @throws NullPointerException 若不存在key
	 */
	public long readLong(String key) {
		return longs.get(key);
	}
	
	
	//----------------------------------------大小----------------------------------------//
	
	public int byteArraySize() { return byteArray.size(); }
	
	public int shortSize() { return shorts.size(); }
	
	public int byteSize() { return bytes.size(); }
	
	/** 获取存储的int数据的数量 */
	public int intSize() {
		return ints.size();
	}
	
	/** 获取存储的long类型数据的数量 */
	public int longSize() {
		return longs.size();
	}
	
	/** 获取存储的info类型数据的数量 */
	public int infoSize() { return infos.size(); }
	
	
	//----------------------------------------遍历----------------------------------------//
	
	public void forEachByteArray(TaskByteArray task) {
		for (Map.Entry<String, byte[]> entry : byteArray.entrySet()) {
			task.run(entry.getKey(), entry.getValue());
		}
	}
	
	public void forEachShort(TaskShort task) {
		for (Map.Entry<String, Short> entry : shorts.entrySet()) {
			task.run(entry.getKey(), entry.getValue());
		}
	}
	
	public void forEachByte(TaskByte task) {
		for (Map.Entry<String, Byte> entry : bytes.entrySet()) {
			task.run(entry.getKey(), entry.getValue());
		}
	}
	
	/** 遍历所有info数据 */
	public void forEacnInfo(TaskInfo task) {
		for (Map.Entry<String, SimpleImplInfo<?>> entry : infos.entrySet()) {
			task.run(entry.getKey(), entry.getValue());
		}
	}
	
	/** 遍历所有int数据 */
	public void forEachInt(TaskInt task) {
		for (Map.Entry<String, Integer> entry : ints.entrySet()) {
			task.run(entry.getKey(), entry.getValue());
		}
	}
	
	/** 遍历所有long数据 */
	public void forEachLong(TaskLong task) {
		for (Map.Entry<String, Long> entry : longs.entrySet()) {
			task.run(entry.getKey(), entry.getValue());
		}
	}
	
}
