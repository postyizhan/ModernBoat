package com.github.postyizhan.modernboat.client.model;

import com.github.postyizhan.modernboat.Constants;
import com.github.postyizhan.modernboat.entity.EntityModernBoat;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelModernBoat extends ModelBase {
    private final ModelRenderer bottom;
    private final ModelRenderer back;
    private final ModelRenderer front;
    private final ModelRenderer right;
    private final ModelRenderer left;
    private final ModelRenderer leftPaddle;
    private final ModelRenderer rightPaddle;

    public ModelModernBoat() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.bottom = new ModelRenderer(this, 0, 0);
        this.bottom.setTextureSize(128, 64);
        this.bottom.addBox(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
        this.bottom.setRotationPoint(0.0F, 3.0F, 1.0F);
        this.bottom.rotateAngleX = (float) (Math.PI / 2.0D);

        this.back = new ModelRenderer(this, 0, 19);
        this.back.setTextureSize(128, 64);
        this.back.addBox(-13.0F, -7.0F, -1.0F, 18, 6, 2, 0.0F);
        this.back.setRotationPoint(-15.0F, 4.0F, 4.0F);
        this.back.rotateAngleY = (float) (Math.PI * 3.0D / 2.0D);

        this.front = new ModelRenderer(this, 0, 27);
        this.front.setTextureSize(128, 64);
        this.front.addBox(-8.0F, -7.0F, -1.0F, 16, 6, 2, 0.0F);
        this.front.setRotationPoint(15.0F, 4.0F, 0.0F);
        this.front.rotateAngleY = (float) (Math.PI / 2.0D);

        this.right = new ModelRenderer(this, 0, 35);
        this.right.setTextureSize(128, 64);
        this.right.addBox(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
        this.right.setRotationPoint(0.0F, 4.0F, -9.0F);
        this.right.rotateAngleY = (float) Math.PI;

        this.left = new ModelRenderer(this, 0, 43);
        this.left.setTextureSize(128, 64);
        this.left.addBox(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
        this.left.setRotationPoint(0.0F, 4.0F, 9.0F);

        this.leftPaddle = new ModelRenderer(this, 62, 0);
        this.leftPaddle.setTextureSize(128, 64);
        this.leftPaddle.addBox(-1.0F, 0.0F, -5.0F, 2, 2, 18, 0.0F);
        this.leftPaddle.addBox(-1.001F, -3.0F, 8.0F, 1, 6, 7, 0.0F);
        this.leftPaddle.setRotationPoint(3.0F, -5.0F, 9.0F);
        this.leftPaddle.rotateAngleZ = (float) (Math.PI / 16.0D);

        this.rightPaddle = new ModelRenderer(this, 62, 20);
        this.rightPaddle.setTextureSize(128, 64);
        this.rightPaddle.addBox(-1.0F, 0.0F, -5.0F, 2, 2, 18, 0.0F);
        this.rightPaddle.addBox(0.001F, -3.0F, 8.0F, 1, 6, 7, 0.0F);
        this.rightPaddle.setRotationPoint(3.0F, -5.0F, -9.0F);
        this.rightPaddle.rotateAngleY = (float) Math.PI;
        this.rightPaddle.rotateAngleZ = (float) (Math.PI / 16.0D);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount,
                       float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entity instanceof EntityModernBoat) {
            float rowingLeft = entity.getDataWatcher().getWatchableObjectFloat(Constants.DW_PADDLE_L);
            float rowingRight = entity.getDataWatcher().getWatchableObjectFloat(Constants.DW_PADDLE_R);
            animatePaddle(rowingLeft, 0, this.leftPaddle);
            animatePaddle(rowingRight, 1, this.rightPaddle);
        }

        this.bottom.render(scale);
        this.back.render(scale);
        this.front.render(scale);
        this.right.render(scale);
        this.left.render(scale);
        this.leftPaddle.render(scale);
        this.rightPaddle.render(scale);
    }

    private static void animatePaddle(float rowingTime, int side, ModelRenderer paddle) {
        paddle.rotateAngleX = clampedLerp(
            (float) (-Math.PI / 3.0D),
            (float) (-Math.PI / 12.0D),
            (MathHelper.sin(-rowingTime) + 1.0F) / 2.0F
        );
        paddle.rotateAngleY = clampedLerp(
            (float) (-Math.PI / 4.0D),
            (float) (Math.PI / 4.0D),
            (MathHelper.sin(-rowingTime + 1.0F) + 1.0F) / 2.0F
        );
        if (side == 1) {
            paddle.rotateAngleY = (float) Math.PI - paddle.rotateAngleY;
        }
    }

    private static float clampedLerp(float start, float end, float delta) {
        if (delta < 0.0F) {
            return start;
        }
        if (delta > 1.0F) {
            return end;
        }
        return start + (end - start) * delta;
    }
}
