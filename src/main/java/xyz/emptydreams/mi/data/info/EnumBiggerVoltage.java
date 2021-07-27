package xyz.emptydreams.mi.data.info;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.utils.MathUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import java.util.function.BiConsumer;

/**
 * 机器过载操作
 * @author EmptyDreams
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
			WorldUtil.setFire(te.getWorld(), pos);
		} else {
			int r = bigger.getFireRadius();
			int x = pos.getX() - r;
			int y = pos.getY() - r;
			int z = pos.getZ() - r;
			for (int i = 1; i <= r; ++i) {
				for (int k = 1; k <= r; ++k) {
					for (int j = 1; j <= r; ++j) {
						if (bigger.getFirePer() > MathUtil.random().nextFloat()) {
							WorldUtil.setFire(te.getWorld(), new BlockPos(x + i, y + k, z + j));
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
	
	public final static class EntityExplosion extends Entity {
		
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