package top.kmar.mi.content.blocks.machine.user;

import com.google.common.collect.Lists;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.regedits.block.annotations.AutoBlockRegister;
import top.kmar.mi.content.blocks.BlockGuiList;
import top.kmar.mi.content.blocks.CommonUtil;
import top.kmar.mi.content.blocks.base.TEBlockBase;
import top.kmar.mi.content.tileentity.user.MuffleFurnace;
import top.kmar.mi.data.properties.MIProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static top.kmar.mi.data.properties.MIProperty.getHorizontal;

/**
 * 高温熔炉
 * @author EmptyDreams
 */
@AutoBlockRegister(registryName = "muffle_furnace")
public class MuffleFurnaceBlock extends TEBlockBase {
    
    private final Item ITEM = new ItemBlock(this);
    
    public MuffleFurnaceBlock() {
        super(Material.ROCK);
        setDefaultState(blockState.getBaseState()
                .withProperty(getHorizontal(), EnumFacing.NORTH)
                .withProperty(MIProperty.getWorking(), false));
        setCreativeTab(ModernIndustry.TAB_BLOCK);
        setSoundType(SoundType.STONE);
        setHardness(3.5F);
        setHarvestLevel("pickaxe", 1);
    }
    
    @SuppressWarnings("deprecation")
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(this);
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        return CommonUtil.openGui(playerIn, BlockGuiList.getHighFurnace(), pos);
    }
    
    @Nullable
    @Override
    public List<ItemStack> dropItems(World world, BlockPos pos) {
        MuffleFurnace furnace = (MuffleFurnace) world.getTileEntity(pos);
        //noinspection ConstantConditions
        return Lists.newArrayList(
                furnace.getInputStack(),
                furnace.getFuelStack(),
                furnace.getOutputStack()
        );
    }
    
    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return CommonUtil.getStateFromMeta(this, meta);
    }
    
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ, int meta,
                                            EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(
                getHorizontal(), placer.getHorizontalFacing().getOpposite());
    }
    
    @Override
    public int getMetaFromState(@Nonnull IBlockState state) {
        return CommonUtil.getMetaFromState(state);
    }
    
    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return CommonUtil.createBlockState(this);
    }
    
    @Override
    public int quantityDropped(@Nonnull Random random) {
        return 1;
    }
    
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new MuffleFurnace();
    }
    
    @Nonnull
    @Override
    public Item getBlockItem() {
        return ITEM;
    }
    
}