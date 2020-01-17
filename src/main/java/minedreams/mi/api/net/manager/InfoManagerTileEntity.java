package minedreams.mi.api.net.manager;

import java.util.List;

import io.netty.buffer.ByteBuf;
import minedreams.mi.blocks.te.TileEntityWire;
import minedreams.mi.api.net.info.InfoBooleans;
import minedreams.mi.api.net.info.InfoLocation;

/**
 * 为{@link minedreams.mi.simpleImpl.SimpleImplWire}定做的信息管理类
 * @author EmptyDremas
 * @version V1.0
 */
public final class InfoManagerTileEntity implements InfoManager<TileEntityWire> {

	/** 存储电线的连接方向 */
	private final InfoBooleans bools = new InfoBooleans();
	/** 存储连接的电线 */
	private final InfoLocation[] links = new InfoLocation[2];
	/** 存储当前坐标 */
	private final InfoLocation pos = new InfoLocation();
	/** 存储世界类型 */
	private int dimension = -1;
	
	public InfoManagerTileEntity() {
		links[0] = new InfoLocation();
		links[1] = new InfoLocation();
	}
	
	@Override
	public int size() {
		return 5;
	}
	
	@Override
	public Object get(int index) throws ArrayIndexOutOfBoundsException {
		switch (index) {
			case 0 : return bools;
			case 1 : return links[0];
			case 2 : return links[1];
			case 3 : return pos;
			case 4 : return dimension;
			default : throw new ArrayIndexOutOfBoundsException(index);
		}
	}

	@Override
	public void writeTo(TileEntityWire t, ByteBuf buf) {
		readFrom(t);
		buf.writeInt(dimension);
		bools.writeTo(buf);
		links[0].writeTo(buf);
		links[1].writeTo(buf);
		pos.writeTo(buf);
	}
	
	@Override
	public void readFrom(TileEntityWire t) {
		bools.add(t.getUp());
		bools.add(t.getDown());
		bools.add(t.getEast());
		bools.add(t.getWest());
		bools.add(t.getSouth());
		bools.add(t.getNorth());
		links[0].add(t.getNext());
		links[1].add(t.getPrev());
		pos.add(t.getPos());
		dimension = t.getWorld().provider.getDimension();
	}
	
	@Override
	public void readFrom(ByteBuf buf) {
		dimension = buf.readInt();
		bools.readFrom(buf);
		links[0].readFrom(buf);
		links[1].readFrom(buf);
		pos.readFrom(buf);
	}

	@Override
	public void readTo(TileEntityWire t) {
		List<Boolean> lists = bools.getInfos();
		t.setUp(lists.get(0));
		t.setDown(lists.get(1));
		t.setEast(lists.get(2));
		t.setWest(lists.get(3));
		t.setSouth(lists.get(4));
		t.setNorth(lists.get(5));
		t.deleteAllLink(false);
		t.linkForce(links[0].getInfo());
		t.linkForce(links[1].getInfo());
	}
	
}
