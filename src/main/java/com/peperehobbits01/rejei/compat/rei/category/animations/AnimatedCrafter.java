package com.peperehobbits01.rejei.compat.rei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.gui.GuiGraphics;

public class AnimatedCrafter extends AnimatedKinetics {

	@Override
	public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
		try {
			PoseStack matrixStack = graphics.pose();
			matrixStack.pushPose();
			matrixStack.translate(xOffset, yOffset, 0);

			// Draw shadow first
			AllGuiTextures.JEI_SHADOW.render(graphics, -16, 13);

			matrixStack.translate(3, 16, 0);
			TransformStack.of(matrixStack)
					.rotateXDegrees(-12.5f)
					.rotateYDegrees(-22.5f);
			int scale = 22;

			// Render cogwheel
			try {
				blockElement(cogwheel())
						.rotateBlock(90, 0, getCurrentAngle())
						.scale(scale)
						.render(graphics);
			} catch (Exception e) {
				System.err.println("[ReJEI] Failed to render cogwheel in AnimatedCrafter");
				e.printStackTrace();
			}

			// Render mechanical crafter block
			try {
				blockElement(AllBlocks.MECHANICAL_CRAFTER.getDefaultState())
						.rotateBlock(0, 180, 0)
						.scale(scale)
						.render(graphics);
			} catch (Exception e) {
				System.err.println("[ReJEI] Failed to render mechanical crafter in AnimatedCrafter");
				e.printStackTrace();
			}

			matrixStack.popPose();
		} catch (Exception e) {
			System.err.println("[ReJEI] Critical error in AnimatedCrafter.draw()");
			e.printStackTrace();
		}
	}

}