package com.peperehobbits01.rejei.compat.rei;

import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class DoubleItemIcon implements Renderer {

	private final Supplier<ItemStack> primarySupplier;
	private final Supplier<ItemStack> secondarySupplier;
	private ItemStack primaryStack;
	private ItemStack secondaryStack;
	private Point pos;

	public DoubleItemIcon(Supplier<ItemStack> primary, Supplier<ItemStack> secondary) {
		this.primarySupplier = primary;
		this.secondarySupplier = secondary;
	}

	public DoubleItemIcon setPos(Point pos) {
		this.pos = pos;
		return this;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
		if (primaryStack == null) {
			primaryStack = primarySupplier.get();
			secondaryStack = secondarySupplier.get();
		}

		// Ensure stacks are not empty to prevent rendering errors
		if (primaryStack.isEmpty() && secondaryStack.isEmpty()) return;

		RenderSystem.enableDepthTest();
		graphics.pose().pushPose();

		// Handle positioning
		if (pos == null) {
			graphics.pose().translate(bounds.getCenterX() - 9, bounds.getCenterY() - 9, 0);
		} else {
			graphics.pose().translate(pos.getX(), pos.getY(), 0);
		}

		// Primary Stack (e.g., the Machine)
		graphics.pose().pushPose();
		graphics.pose().translate(1, 1, 0);
		GuiGameElement.of(primaryStack).render(graphics);
		graphics.pose().popPose();

		// Secondary Stack (e.g., the Catalyst/Ingredient)
		// Note the 100 on Z-axis ensures it renders in front of the primary icon
		graphics.pose().pushPose();
		graphics.pose().translate(10, 10, 100);
		graphics.pose().scale(.5f, .5f, .5f);
		GuiGameElement.of(secondaryStack).render(graphics);
		graphics.pose().popPose();

		graphics.pose().popPose();
	}
}