package com.peperehobbits01.rejei.compat.rei;

import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ItemIcon implements Renderer {

	private final Supplier<ItemStack> supplier;
	private ItemStack stack;

	public ItemIcon(Supplier<ItemStack> stack) {
		this.supplier = stack;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
		if (stack == null) {
			stack = supplier.get();
		}

		// Ensure stack isn't empty before rendering to avoid GUI glitches
		if (stack.isEmpty()) return;

		int xOffset = bounds.x;
		int yOffset = bounds.y;

		RenderSystem.enableDepthTest();
		graphics.pose().pushPose();
		graphics.pose().translate(xOffset, yOffset, 0);

		GuiGameElement.of(stack)
				.render(graphics);

		graphics.pose().popPose();
	}
}