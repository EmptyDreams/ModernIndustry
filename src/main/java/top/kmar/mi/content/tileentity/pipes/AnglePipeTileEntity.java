package top.kmar.mi.content.tileentity.pipes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.NotNull;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.pipes.FluidPipeEntity;
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity;
import top.kmar.mi.api.utils.container.IndexEnumMap;

import static net.minecraft.util.EnumFacing.*;

/**
 * 直角拐弯的管道的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("AnglePipe")
public class AnglePipeTileEntity extends FluidPipeEntity {
    
    /** 正方向 */
    @AutoSave private EnumFacing facing;
    /** 后侧方向 */
    @AutoSave private EnumFacing after;
    /** 连接数据 */
    @AutoSave
    private final IndexEnumMap<EnumFacing> linkedData = new IndexEnumMap<>(EnumFacing.values());
    
    public AnglePipeTileEntity() {
        this(NORTH, UP);
    }
    
    public AnglePipeTileEntity(EnumFacing facing, EnumFacing after) {
        super(1000);
        this.facing = facing;
        this.after = after;
    }
    
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        int value = (facing.ordinal() << 8) | after.ordinal();
        tag.setShort("data", (short) value);
        return tag;
    }
    
    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        int value = tag.getShort("data");
        facing = EnumFacing.values()[value >> 8];
        after = EnumFacing.values()[value & 0xFF];
    }
    
    @Override
    public boolean isLink(EnumFacing facing) {
        return linkedData.get(facing);
    }
    
    @Override
    public boolean hasChannel(EnumFacing facing) {
        return facing == this.facing || facing == after;
    }
    
    @Override
    public boolean linkFluidBlock(@NotNull EnumFacing facing) {
        if (isLink(facing)) return true;
        if (!hasChannel(facing)) {
            if (isLink(this.facing)) {
                if (isLink(after)) return false;
                after = facing;
            } else if (facing.getAxis() == Axis.Y) {
                assert isLink(after);
                after = facing;
            } else this.facing = facing;
        }
        linkedData.set(facing, true);
        return true;
    }
    
    @Override
    public void unlinkFluidBlock(@NotNull EnumFacing facing) {
        linkedData.set(facing, false);
    }
    
    public EnumFacing getFacing() {
        if (facing == UP || facing == DOWN) return after;
        return facing;
    }
    
    public EnumFacing getAfter() {
        if (facing == UP || facing == DOWN) return facing;
        return after;
    }

}