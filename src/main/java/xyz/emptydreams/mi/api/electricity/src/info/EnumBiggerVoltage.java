package xyz.emptydreams.mi.api.electricity.src.info;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.tools.property.IProperty;
import xyz.emptydreams.mi.utils.BlockPosUtil;

/**
 * 机器过载操作
 *
 * @author EmptyDreams
 * @version V1.0
 */
public enum EnumBiggerVoltage {
	
	/** 什么都不做 */
	NON((te, bigger) -> { }),
	/** 爆炸 */
	BOOM((te, bigger) -> {
		BlockPos pos = te.getPos();
		EntityExplosion explosion = new EntityExplosion(te.getWorld(),
							pos.getX(), pos.getY(), pos.getZ(), bigger.getBoomStrength());
		te.getWorld().spawnEntity(explosion);
		te.getWorld().playSound(null, explosion.posX, explosion.posY, explosion.posZ,
				SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}),
	/** 引起火灾 */
	FIRE((te, bigger) -> {
		BlockPos pos = te.getPos();
		if (bigger.getFireRadius() == 1) {
			BlockPosUtil.setFire(te.getWorld(), pos);
			te.getWorld().markBlockRangeForRenderUpdate(pos, pos);
		} else {
			int z = bigger.getFireRadius() + 1;
			int x = pos.getX() - z;
			int y = pos.getY() - z;
			z = pos.getZ() - z;
			int r = bigger.getFireRadius();
			for (int i = 0; i < r; ++i) {
				for (int k = 0; k < r; ++k) {
					for (int j = 0; j < r; ++j) {
						if (bigger.getFirePer() > IProperty.RANDOM_PROPERTY.nextFloat()) {
							BlockPos b = new BlockPos(x + i, y + k, z + j);
							BlockPosUtil.setFire(te.getWorld(), b);
							te.getWorld().markBlockRangeForRenderUpdate(b, b);
						}
					}
				}
			}
		}
	});
	
	private final BiConsumer<TileEntity, BiggerVoltage> consumer;
	
	EnumBiggerVoltage(BiConsumer<TileEntity, BiggerVoltage> consumer) {
		this.consumer = consumer;
	}
	
	public void overload(TileEntity tileEntity, BiggerVoltage bigger) {
		consumer.accept(tileEntity, bigger);
	}
	
	private final static class EntityExplosion extends Entity {
		
		private static final DataParameter<Integer> FUSE =
				EntityDataManager.createKey(EntityExplosion.class, DataSerializers.VARINT);
		private float strength;
		
		public EntityExplosion(World worldIn) {
			super(worldIn);
			this.preventEntitySpawning = true;
			this.isImmuneToFire = true;
			this.setSize(0.98F, 0.98F);
		}
		
		public EntityExplosion(World worldIn, int x, int y, int z, float strength) {
			this(worldIn);
			this.setPosition(x, y, z);
			this.strength = strength;
		}
		
		protected void entityInit() {
			this.dataManager.register(FUSE, 80);
		}
		protected boolean canTriggerWalking() {
			return false;
		}
		public boolean canBeCollidedWith() {
			return !this.isDead;
		}
		
		public void onUpdate() {
			this.setDead();
			if (!this.world.isRemote) {
				this.explode();
			}
		}
		
		private void explode() {
			this.world.createExplosion(
					this, this.posX, this.posY + (double)(this.height / 16.0F), this.posZ,
					strength, true);
		}
		
		protected void writeEntityToNBT(NBTTagCompound compound) { }
		protected void readEntityFromNBT(NBTTagCompound compound) { }
		public float getEyeHeight() { return 0.0F; }
		
	}
	
}
