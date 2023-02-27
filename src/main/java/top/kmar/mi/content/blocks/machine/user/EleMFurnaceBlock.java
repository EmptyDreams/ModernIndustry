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
import top.kmar.mi.content.tileentity.user.EUMFurnace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.util.EnumFacing.NORTH;
import static top.kmar.mi.data.properties.MIProperty.getHorizontal;
import static top.kmar.mi.data.properties.MIProperty.getWorking;

/**
 * 高温火炉的Block
 * @author EmptyDreams
 */
@AutoBlockRegister(registryName = "ele_mfurnace")
public class EleMFurnaceBlock extends MachineBlock {

    private final Item ITEM = new ItemBlock(this);

    public EleMFurnaceBlock() {
        super(Material.IRON);
        setDefaultState(blockState.getBaseState()
                .withProperty(getHorizontal(), NORTH)
                .withProperty(getWorking(), false));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        return CommonUtil.openGui(playerIn, BlockGuiList.getEleHighFurnace(), pos);
    }

    @Nullable
    @Override
    public List<ItemStack> dropItems(World world, BlockPos pos) {
        EUMFurnace furnace = (EUMFurnace) world.getTileEntity(pos);
        //noinspection ConstantConditions
        return Lists.newArrayList(furnace.getInputStack(), furnace.getOutputStack());
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getHorizontal(), getWorking());
    }

    @Override
    public int getMetaFromState(@Nonnull IBlockState state) {
        return CommonUtil.getMetaFromState(state);
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return CommonUtil.getStateFromMeta(this, meta);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new EUMFurnace();
    }

    @Nonnull
    @Override
    public Item getBlockItem() {
        return ITEM;
    }

}