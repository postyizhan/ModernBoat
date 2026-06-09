package com.github.postyizhan.modernboat.item;

import com.github.postyizhan.modernboat.Constants;
import com.github.postyizhan.modernboat.entity.EntityModernBoat;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemModernBoat extends Item {
    public ItemModernBoat(int id) {
        super(id);
        this.maxStackSize = 1;
        setUnlocalizedName("modern_oak_boat");
        setTextureName(Constants.MODID + ":oak_boat");
        setCreativeTab(CreativeTabs.tabTransport);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        float partialTicks = 1.0F;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
        double px = player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks;
        double py = player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks
            + 1.62D - (double) player.yOffset;
        double pz = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks;
        Vec3 origin = world.getWorldVec3Pool().getVecFromPool(px, py, pz);

        float cosYaw = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float sinYaw = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float cosPitch = -MathHelper.cos(-pitch * 0.017453292F);
        float sinPitch = MathHelper.sin(-pitch * 0.017453292F);
        float dx = sinYaw * cosPitch;
        float dy = sinPitch;
        float dz = cosYaw * cosPitch;
        double range = 5.0D;
        Vec3 end = origin.addVector((double) dx * range, (double) dy * range, (double) dz * range);

        MovingObjectPosition hit = world.rayTraceBlocks_do_do(origin, end, true, false);
        if (hit == null || hit.typeOfHit != EnumMovingObjectType.TILE) {
            return stack;
        }

        int bx = hit.blockX;
        int by = hit.blockY;
        int bz = hit.blockZ;
        if (world.getBlockMaterial(bx, by, bz) != Material.water) {
            return stack;
        }

        EntityModernBoat boat = createBoat(world, (double) bx + 0.5D, (double) by + 1.0D, (double) bz + 0.5D);
        boat.rotationYaw = (float) (((MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) - 1) * 90);

        if (!world.getCollidingBoundingBoxes(boat, boat.boundingBox.expand(-0.1D, -0.1D, -0.1D)).isEmpty()) {
            return stack;
        }

        if (!world.isRemote) {
            world.spawnEntityInWorld(boat);
        }
        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return stack;
    }

    protected EntityModernBoat createBoat(World world, double x, double y, double z) {
        return new EntityModernBoat(world, x, y, z);
    }
}
