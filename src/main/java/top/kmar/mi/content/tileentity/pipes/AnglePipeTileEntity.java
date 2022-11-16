package top.kmar.mi.content.tileentity.pipes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.net.messages.block.BlockMessage;
import top.kmar.mi.api.net.messages.block.cap.BlockNetworkCapability;
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
    @AutoSave private EnumFacing facing = NORTH;
    /** 后侧方向 */
    @AutoSave private EnumFacing after = UP;
    /** 连接数据 */
    @AutoSave
    private final IndexEnumMap<EnumFacing> linkedData = new IndexEnumMap<>(EnumFacing.values());
    
    public AnglePipeTileEntity() {
        super(1000);
    }
    
    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == BlockNetworkCapability.getCapObj()) return true;
        return super.hasCapability(capability, facing);
    }
    
    @Nullable
    @Override
    public <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == BlockNetworkCapability.getCapObj()) {
            return BlockNetworkCapability.getCapObj().cast((it, ctx) -> {
                int value = ((NBTTagShort) it).getInt();
                this.facing = EnumFacing.values()[value >> 8];
                this.after = EnumFacing.values()[value & 0xFF];
                world.markBlockRangeForRenderUpdate(pos, pos);
            });
        }
        return super.getCapability(capability, facing);
    }
    
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        int value = (facing.ordinal() << 8) | after.ordinal();
        tag.setShort("lnk", (short) value);
        return tag;
    }
    
    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        int value = tag.getShort("lnk");
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
    public boolean linkFluidBlock(@NotNull TileEntity entity, @NotNull EnumFacing facing) {
        if (isLink(facing)) return true;
        if (!FluidPipeEntity.tryLink(this, linkedData, entity, facing)) return false;
        if (!hasChannel(facing)) {
            if (isLink(this.facing)) {
                if (isLink(after)) return false;
                after = facing;
            } else if (facing.getAxis() == Axis.Y) {
                assert !isLink(after);
                after = facing;
            } else this.facing = facing;
            int value = (this.facing.ordinal() << 8) | after.ordinal();
            BlockMessage.sendToClient(this, new NBTTagShort((short) value));
        }
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