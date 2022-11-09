package top.kmar.mi.content.items.debug;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.world.World;
import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.electricity.cables.EleCableEntity;
import top.kmar.mi.api.electricity.cables.IdAllocator;
import top.kmar.mi.api.electricity.cables.InvalidCacheManager;
import top.kmar.mi.api.electricity.cables.InvalidCodeManager;
import top.kmar.mi.api.regedits.item.AutoItemRegister;
import top.kmar.mi.api.utils.MISysInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 调试工具
 * @author EmptyDreams
 */
@AutoItemRegister("class_info_viewer")
public class ClassInfoViewer extends Item {
    
    public ClassInfoViewer() {
        setCreativeTab(ModernIndustry.TAB_DEBUG);
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos,
                                      EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ) {
        TileEntity target = worldIn.getTileEntity(pos);
        if (target == null) {
            if (worldIn.isRemote || !player.isSneaking()) return EnumActionResult.FAIL;
            StringBuilder sb = new StringBuilder();
            sb.append("---------- Debug ----------")
                    .append("\nidList: ")
                    .append(IdAllocator.getCableCacheIdAllocator(worldIn).idList())
                    .append("\n\ninfoList: ")
                    .append(IdAllocator.getCableCacheIdAllocator(worldIn).infoList())
                    .append("\n\ninvalidIdMap: ")
                    .append(InvalidCacheManager.getInvalidCacheData(worldIn))
                    .append("\n\ninvalidCodeManager: ")
                    .append(InvalidCodeManager.getInvalidCodeManager(worldIn));
            player.sendMessage(new TextComponentBase() {
                @Override
                public String getUnformattedComponentText() {
                    return sb.toString();
                }
                @Override
                public ITextComponent createCopy() {
                    return this;
                }
            });
            return EnumActionResult.SUCCESS;
        }
        StringBuilder sb = new StringBuilder();
        if (worldIn.isRemote) sb.append("---------- Client ----------\n");
        else sb.append("---------- Service ----------\n");
        Class<?> clazz = target.getClass();
        try {
            if (target instanceof EleCableEntity) {
                if (worldIn.isRemote) return EnumActionResult.SUCCESS;
                EleCableEntity cable = (EleCableEntity) target;
                sb.append("oldCode: ")
                        .append(cable.getCode())
                        .append("\ncacheId: ")
                        .append(cable.getCacheId())
                        .append("\nnewCode: ")
                        .append(cable.getCode());
            } else {
                while (clazz != Object.class) {
                    sb.append(clazz.getName()).append('\n');
                    for (Field field : clazz.getDeclaredFields()) {
                        if (!Modifier.isPublic(field.getModifiers())) field.setAccessible(true);
                        sb.append('\t')
                                .append(field.getName())
                                .append(":\t")
                                .append(field.get(target))
                                .append('\n');
                    }
                    clazz = clazz.getSuperclass();
                }
            }
        } catch (Exception e) {
            MISysInfo.err("打印信息时遇到意料之外的错误", e);
            return EnumActionResult.FAIL;
        }
        MISysInfo.print(sb);
        return EnumActionResult.SUCCESS;
    }
    
}