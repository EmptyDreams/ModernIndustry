package top.kmar.mi.api.tools;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import top.kmar.mi.api.electricity.EleTileEntity;

import javax.annotation.Nullable;

/**
 * 带方向的机器方块
 * @author EmptyDreams
 */
public abstract class FrontTileEntity extends EleTileEntity {

    /**
     * 获取正面
     * @return 返回null表示任何方向都可以执行操作
     */
    @Nullable
    public abstract EnumFacing getFront();

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (facing == getFront()) return null;
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (facing == getFront()) return false;
        return super.hasCapability(capability, facing);
    }
}