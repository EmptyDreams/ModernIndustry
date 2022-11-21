package top.kmar.mi.content.blocks.machine.user;

import com.google.common.collect.Lists;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.kmar.mi.api.regedits.block.annotations.AutoBlockRegister;
import top.kmar.mi.content.blocks.BlockGuiList;
import top.kmar.mi.content.blocks.CommonUtil;
import top.kmar.mi.content.blocks.base.MachineBlock;
import top.kmar.mi.content.tileentity.user.EUCompressor;
import top.kmar.mi.data.properties.MIProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static top.kmar.mi.data.properties.MIProperty.*;

/**
 * 压缩机
 * @author EmptyDremas
 */
@AutoBlockRegister(registryName = CompressorBlock.NAME, field = "INSTANCE")
public class CompressorBlock extends MachineBlock {
    
    /** 方块内部名称 */
    public static final String NAME = "compressor";
    @SuppressWarnings("unused")
    private static CompressorBlock INSTANCE;
    
    private final Item ITEM = new ItemBlock(this);
    
    public CompressorBlock() {
        super(Material.ROCK);
        setDefaultState(blockState.getBaseState().withProperty(
                        MIProperty.getHorizontal(), EnumFacing.EAST)
                .withProperty(getWorking(), false)
                .withProperty(getEmpty(), true));
    }
    
    @Nonnull
    @Override
    public Item getBlockItem() {
        return ITEM;
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return CommonUtil.openGui(playerIn, BlockGuiList.getCompressor(), pos);
    }
    
    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public List<ItemStack> dropItems(World world, BlockPos pos) {
        EUCompressor nbt = (EUCompressor) world.getTileEntity(pos);
        ItemStack is = nbt.getInputUpStack();
        ItemStack is2 = nbt.getInputDownStack();
        ItemStack is3 = nbt.getOutputStack();
        return Lists.newArrayList(is, is2, is3);
    }
    
    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getHorizontal(), getWorking(), getEmpty());
    }
    
    @Override
    public int quantityDropped(@Nonnull Random random) {
        return 0;
    }
    
    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getHorizontal(meta & 0b0011);
        boolean burning = (meta & 0b0100) == 0b0100;
        boolean isEmpty = (meta & 0b1000) == 0b1000;
        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        return getDefaultState().withProperty(getHorizontal(), enumfacing)
                .withProperty(getWorking(), burning)
                .withProperty(getEmpty(), isEmpty);
    }
    
    /**
     * @return 返回一个int值，其中后两位存储方向数据<br>
     * 			第二位存储是否正在工作，0表示没有工作<br>
     * 			第一位存储内部是否有物品，1表示内部为空
     */
    @Override
    public int getMetaFromState(@Nonnull IBlockState state) {
        return state.getValue(getHorizontal()).getHorizontalIndex()
                | (state.getValue(getWorking()) ? 0b0100 : 0b0000)
                | (state.getValue(getEmpty()) ? 0b1000 : 0b0000);
    }
    
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new EUCompressor();
    }
    
    public static CompressorBlock instance() {
        return INSTANCE;
    }
    
}