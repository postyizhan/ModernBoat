package com.github.postyizhan.modernboat.client.render;

import com.github.postyizhan.modernboat.Constants;
import com.github.postyizhan.modernboat.client.model.ModelModernBoat;
import com.github.postyizhan.modernboat.entity.EntityModernBoat;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

// Backports the high-version AbstractBoatRenderer transform order to 1.6.4 GL11.
public class RenderModernBoat extends Render {

    private static final ResourceLocation TEXTURE =
        new ResourceLocation(Constants.MODID, "textures/entity/boat/oak.png");

    private final ModelModernBoat model = new ModelModernBoat();

    public RenderModernBoat() {
        this.shadowSize = 0.5F;
    }

    public void renderModernBoat(EntityModernBoat boat, double x, double y, double z,
                                 float yaw, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);

        // DW_TIME_HIT and DW_HURT_DIR are Integer slots; DW_DAMAGE is Float.
        float timeHit = (float) boat.getDataWatcher().getWatchableObjectInt(Constants.DW_TIME_HIT)
            - partialTicks;
        float damage  = boat.getDataWatcher().getWatchableObjectFloat(Constants.DW_DAMAGE)
            - partialTicks;
        int hurtDir   = boat.getDataWatcher().getWatchableObjectInt(Constants.DW_HURT_DIR);

        if (damage < 0.0F) {
            damage = 0.0F;
        }
        if (timeHit > 0.0F) {
            float shake = MathHelper.sin(timeHit) * timeHit * damage / 10.0F * (float) hurtDir;
            GL11.glRotatef(shake, 1.0F, 0.0F, 0.0F);
        }

        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);

        this.bindEntityTexture(boat);
        this.model.render(boat, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z,
                         float yaw, float partialTicks) {
        this.renderModernBoat((EntityModernBoat) entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }
}
