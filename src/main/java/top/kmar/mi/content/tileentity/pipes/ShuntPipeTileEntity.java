package top.kmar.mi.content.tileentity.pipes;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.NotNull;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.pipes.FluidPipeEntity;
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity;
import top.kmar.mi.api.utils.container.IndexEnumMap;
import top.kmar.mi.api.utils.expands.WorldExpandsKt;
import top.kmar.mi.data.properties.MIProperty;

import java.util.List;

import static net.minecraft.util.EnumFacing.Axis;
import static net.minecraft.util.EnumFacing.values;

/**
 * 十字管道的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("ShuntPipe")
public class ShuntPipeTileEntity extends FluidPipeEntity {
    
    @AutoSave
    private final IndexEnumMap<EnumFacing> linkedData = new IndexEnumMap<>(EnumFacing.values());
    
    public ShuntPipeTileEntity() {
        super(1000);
    }
    
    @Override
    public boolean hasChannel(EnumFacing facing) {
        return facing.getAxis() != getSide();
    }
    
    @Override
    public boolean linkFluidBlock(@NotNull TileEntity entity, @NotNull EnumFacing facing) {
        if (isLink(facing)) return true;
        if (!FluidPipeEntity.tryLink(this, linkedData, entity, facing)) return false;
        List<Axis> maySides = calculateSides();
        if (maySides.isEmpty()) {
            linkedData.set(facing, false);
            return false;
        }
        if (!maySides.contains(getSide())) {
            IBlockState old = world.getBlockState(pos);
            IBlockState newState = old.withProperty(MIProperty.getAXIS(), maySides.get(0));
            WorldExpandsKt.setBlockWithMark(world, pos, newState);
        }
        return true;
    }
    
    @Override
    public void unlinkFluidBlock(@NotNull EnumFacing facing) {
        linkedData.set(facing, false);
    }
    
    @Override
    public boolean isLink(EnumFacing facing) {
        return linkedData.get(facing);
    }
    
    /** 计算side应该在哪个方向 */
    protected List<Axis> calculateSides() {
        List<Axis> all = Lists.newArrayList(Axis.values());
        for (EnumFacing value : values()) {
            if (isLink(value)) all.remove(value.getAxis());
        }
        return all;
    }
    
    public Axis getSide() {
        return world.getBlockState(pos).getValue(MIProperty.getAXIS());
    }

}