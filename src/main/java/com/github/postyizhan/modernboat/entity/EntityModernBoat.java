package com.github.postyizhan.modernboat.entity;

import com.github.postyizhan.modernboat.Constants;
import com.github.postyizhan.modernboat.registry.ModItems;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityModernBoat extends Entity {
    private static final int WATER_CHECK_SLICES = 5;
    private static final double GRAVITY = 0.04D;
    private static final double WATER_DRAG = 0.90D;
    private static final double LAND_DRAG = 0.50D;
    private static final double AIR_DRAG = 0.98D;
    private static final double PADDLE_FORCE = 0.04D;
    private static final float TURN_SPEED = 4.0F;

    private float paddlePhaseLeft;
    private float paddlePhaseRight;
    private int boatPosRotationIncrements;
    private double boatX;
    private double boatY;
    private double boatZ;
    private double boatYaw;
    private double boatPitch;
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private Entity lastRider;
    private int dismountPushCooldown;

    public EntityModernBoat(World world) {
        super(world);
        this.preventEntitySpawning = true;
        this.setSize(1.5F, 0.6F);
        this.yOffset = this.height / 2.0F;
    }

    public EntityModernBoat(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y + (double) this.yOffset, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(Constants.DW_INPUT_BITS, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(Constants.DW_PADDLE_L, Float.valueOf(0.0F));
        this.dataWatcher.addObject(Constants.DW_PADDLE_R, Float.valueOf(0.0F));
        this.dataWatcher.addObject(Constants.DW_DAMAGE, Float.valueOf(0.0F));
        this.dataWatcher.addObject(Constants.DW_TIME_HIT, Integer.valueOf(0));
        this.dataWatcher.addObject(Constants.DW_HURT_DIR, Integer.valueOf(1));
        this.dataWatcher.addObject(Constants.DW_BUBBLE_ANGLE, Float.valueOf(0.0F));
        this.dataWatcher.addObject(Constants.DW_EXTRA_RIDER, Integer.valueOf(-1));
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return entity.boundingBox;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    @Override
    public double getMountedYOffset() {
        return 0.0D;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public boolean canBePushed() {
        return this.dismountPushCooldown <= 0;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable() || this.worldObj.isRemote || this.isDead) {
            return true;
        }

        Entity attacker = source.getEntity();
        if (attacker == this.riddenByEntity) {
            return true;
        }

        this.setHurtDir(-this.getHurtDir());
        this.setTimeSinceHit(10);
        this.setDamage(this.getDamage() + amount * 10.0F);
        this.setBeenAttacked();

        boolean creativeHit = attacker instanceof EntityPlayer
            && ((EntityPlayer) attacker).capabilities.isCreativeMode;
        if (creativeHit || this.getDamage() > 40.0F) {
            if (this.riddenByEntity != null) {
                this.riddenByEntity.mountEntity(this);
            }
            if (!creativeHit && ModItems.itemOakBoat != null) {
                this.dropItemWithOffset(ModItems.itemOakBoat.itemID, 1, 0.0F);
            }
            this.setDead();
        }

        return true;
    }

    @Override
    public void performHurtAnimation() {
        this.setHurtDir(-this.getHurtDir());
        this.setTimeSinceHit(10);
        this.setDamage(this.getDamage() * 11.0F);
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int increments) {
        if (this.worldObj.isRemote && this.riddenByEntity != null) {
            return;
        }

        this.boatPosRotationIncrements = increments + 5;
        this.boatX = x;
        this.boatY = y;
        this.boatZ = z;
        this.boatYaw = (double) yaw;
        this.boatPitch = (double) pitch;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        if (this.worldObj.isRemote && this.riddenByEntity != null) {
            return;
        }

        this.motionX = x;
        this.velocityX = x;
        this.motionY = y;
        this.velocityY = y;
        this.motionZ = z;
        this.velocityZ = z;
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer
            && this.riddenByEntity != player) {
            return true;
        }
        if (!this.worldObj.isRemote) {
            player.mountEntity(this);
        }
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.getTimeSinceHit() > 0) {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }
        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }
        if (this.dismountPushCooldown > 0) {
            this.dismountPushCooldown--;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.worldObj.isRemote) {
            this.handleRiderLifecycle();
            if (this.riddenByEntity == null) {
                this.updateClientPosition();
                return;
            }
        }

        double waterCoverage = this.getWaterCoverage();
        boolean inWater = waterCoverage > 0.0D;

        if (inWater) {
            this.motionY = 0.0D;
        } else {
            this.motionY -= GRAVITY;
        }

        this.applyDriverInput(inWater);

        double horizontalSpeed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        if (horizontalSpeed > 0.35D) {
            double scale = 0.35D / horizontalSpeed;
            this.motionX *= scale;
            this.motionZ *= scale;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        double drag = inWater ? WATER_DRAG : (this.onGround ? LAND_DRAG : AIR_DRAG);
        this.motionX *= drag;
        this.motionY *= 0.95D;
        this.motionZ *= drag;

        if (this.onGround) {
            this.motionX *= 0.5D;
            this.motionZ *= 0.5D;
        }

        this.rotationPitch = 0.0F;
        this.updateRiderPosition();
        this.collideWithNearbyBoats();

        this.handleRiderLifecycle();

        if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
            this.riddenByEntity = null;
        }
    }

    @Override
    public void updateRiderPosition() {
        if (this.riddenByEntity == null) {
            return;
        }

        this.riddenByEntity.setPosition(
            this.posX,
            this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(),
            this.posZ
        );
    }

    public void setInput(boolean forward, boolean back, boolean left, boolean right) {
        byte bits = (byte) ((forward ? 1 : 0) | (back ? 2 : 0) | (left ? 4 : 0) | (right ? 8 : 0));
        this.dataWatcher.updateObject(Constants.DW_INPUT_BITS, Byte.valueOf(bits));
    }

    public void setDamage(float damage) {
        this.dataWatcher.updateObject(Constants.DW_DAMAGE, Float.valueOf(Math.max(0.0F, damage)));
    }

    public float getDamage() {
        return this.dataWatcher.getWatchableObjectFloat(Constants.DW_DAMAGE);
    }

    public void setTimeSinceHit(int timeSinceHit) {
        this.dataWatcher.updateObject(Constants.DW_TIME_HIT, Integer.valueOf(Math.max(0, timeSinceHit)));
    }

    public int getTimeSinceHit() {
        return this.dataWatcher.getWatchableObjectInt(Constants.DW_TIME_HIT);
    }

    public void setHurtDir(int hurtDir) {
        this.dataWatcher.updateObject(Constants.DW_HURT_DIR, Integer.valueOf(hurtDir == 0 ? 1 : hurtDir));
    }

    public int getHurtDir() {
        return this.dataWatcher.getWatchableObjectInt(Constants.DW_HURT_DIR);
    }

    private void handleRiderLifecycle() {
        if (this.riddenByEntity != null) {
            this.lastRider = this.riddenByEntity;
            return;
        }

        if (this.lastRider != null) {
            this.placeDismountedRider(this.lastRider);
            this.lastRider = null;
            this.dismountPushCooldown = 10;
        }
    }

    private void placeDismountedRider(Entity rider) {
        if (rider.isDead) {
            return;
        }

        double yawRadians = (double) this.rotationYaw * Math.PI / 180.0D;
        double sideX = Math.cos(yawRadians) * 1.25D;
        double sideZ = Math.sin(yawRadians) * 1.25D;
        rider.setPosition(this.posX + sideX, this.posY + 0.2D + rider.getYOffset(), this.posZ + sideZ);
        rider.motionX = 0.0D;
        rider.motionY = 0.0D;
        rider.motionZ = 0.0D;
    }

    private void updateClientPosition() {
        if (this.boatPosRotationIncrements > 0) {
            double nextX = this.posX + (this.boatX - this.posX) / (double) this.boatPosRotationIncrements;
            double nextY = this.posY + (this.boatY - this.posY) / (double) this.boatPosRotationIncrements;
            double nextZ = this.posZ + (this.boatZ - this.posZ) / (double) this.boatPosRotationIncrements;
            double yawDelta = MathHelper.wrapAngleTo180_double(this.boatYaw - (double) this.rotationYaw);
            this.rotationYaw = (float) ((double) this.rotationYaw + yawDelta / (double) this.boatPosRotationIncrements);
            this.rotationPitch = (float) ((double) this.rotationPitch
                + (this.boatPitch - (double) this.rotationPitch) / (double) this.boatPosRotationIncrements);
            this.boatPosRotationIncrements--;
            this.setPosition(nextX, nextY, nextZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        } else {
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= AIR_DRAG;
            this.motionY *= 0.95D;
            this.motionZ *= AIR_DRAG;
        }
        this.updateRiderPosition();
    }

    private double getWaterCoverage() {
        double coverage = 0.0D;
        double height = this.boundingBox.maxY - this.boundingBox.minY;

        for (int i = 0; i < WATER_CHECK_SLICES; i++) {
            double minY = this.boundingBox.minY + height * (double) i / (double) WATER_CHECK_SLICES - 0.125D;
            double maxY = this.boundingBox.minY + height * (double) (i + 1) / (double) WATER_CHECK_SLICES - 0.125D;
            AxisAlignedBB slice = AxisAlignedBB.getBoundingBox(
                this.boundingBox.minX,
                minY,
                this.boundingBox.minZ,
                this.boundingBox.maxX,
                maxY,
                this.boundingBox.maxZ
            );
            if (this.worldObj.isAABBInMaterial(slice, Material.water)) {
                coverage += 1.0D / (double) WATER_CHECK_SLICES;
            }
        }

        return coverage;
    }

    private void applyDriverInput(boolean inWater) {
        boolean forward;
        boolean back;
        boolean left;
        boolean right;

        if (this.riddenByEntity instanceof EntityLivingBase) {
            EntityLivingBase driver = (EntityLivingBase) this.riddenByEntity;
            forward = driver.moveForward > 0.0F;
            back = driver.moveForward < 0.0F;
            left = driver.moveStrafing > 0.0F;
            right = driver.moveStrafing < 0.0F;
        } else {
            byte input = this.dataWatcher.getWatchableObjectByte(Constants.DW_INPUT_BITS);
            forward = (input & 1) != 0;
            back = (input & 2) != 0;
            left = (input & 4) != 0;
            right = (input & 8) != 0;
        }

        if (left) {
            this.rotationYaw -= TURN_SPEED;
        }
        if (right) {
            this.rotationYaw += TURN_SPEED;
        }

        float force = 0.0F;
        if (forward) {
            force += 1.0F;
        }
        if (back) {
            force -= 0.5F;
        }

        if (force != 0.0F) {
            double yawRadians = (double) this.rotationYaw * Math.PI / 180.0D;
            this.motionX += -Math.sin(yawRadians) * PADDLE_FORCE * (double) force;
            this.motionZ += Math.cos(yawRadians) * PADDLE_FORCE * (double) force;
        }

        this.advancePaddles(forward, back, left, right, inWater);
    }

    private void advancePaddles(boolean forward, boolean back, boolean left, boolean right, boolean inWater) {
        boolean strokeLeft = forward || back || right;
        boolean strokeRight = forward || back || left;

        if (strokeLeft) {
            this.paddlePhaseLeft += (float) (Math.PI / 8.0D);
        } else {
            this.paddlePhaseLeft = 0.0F;
        }
        if (strokeRight) {
            this.paddlePhaseRight += (float) (Math.PI / 8.0D);
        } else {
            this.paddlePhaseRight = 0.0F;
        }

        this.dataWatcher.updateObject(Constants.DW_PADDLE_L, Float.valueOf(this.paddlePhaseLeft));
        this.dataWatcher.updateObject(Constants.DW_PADDLE_R, Float.valueOf(this.paddlePhaseRight));

        if (!this.worldObj.isRemote && (strokeLeft || strokeRight) && this.ticksExisted % 12 == 0) {
            this.playPaddleSound(inWater);
        }
    }

    private void playPaddleSound(boolean inWater) {
        String suffix = inWater ? "water" : "land";
        this.worldObj.playSoundAtEntity(
            this,
            Constants.MODID + ":entity.boat.paddle_" + suffix,
            1.0F,
            0.8F + this.rand.nextFloat() * 0.4F
        );
    }

    @SuppressWarnings("rawtypes")
    private void collideWithNearbyBoats() {
        List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(
            this,
            this.boundingBox.expand(0.2D, 0.0D, 0.2D)
        );
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = (Entity) entities.get(i);
            if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityModernBoat) {
                entity.applyEntityCollision(this);
            }
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        this.setInput(
            tag.getBoolean("InputForward"),
            tag.getBoolean("InputBack"),
            tag.getBoolean("InputLeft"),
            tag.getBoolean("InputRight")
        );
        this.setDamage(tag.getFloat("Damage"));
        this.setTimeSinceHit(tag.getInteger("TimeSinceHit"));
        this.setHurtDir(tag.getInteger("HurtDir"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        byte input = this.dataWatcher.getWatchableObjectByte(Constants.DW_INPUT_BITS);
        tag.setBoolean("InputForward", (input & 1) != 0);
        tag.setBoolean("InputBack", (input & 2) != 0);
        tag.setBoolean("InputLeft", (input & 4) != 0);
        tag.setBoolean("InputRight", (input & 8) != 0);
        tag.setFloat("Damage", this.getDamage());
        tag.setInteger("TimeSinceHit", this.getTimeSinceHit());
        tag.setInteger("HurtDir", this.getHurtDir());
    }
}
