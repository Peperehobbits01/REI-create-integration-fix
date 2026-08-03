package com.peperehobbits01.rejei.compat.rei.category;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.simibubi.create.Create;
import com.peperehobbits01.rejei.compat.rei.category.animations.AnimatedItemDrain;
import com.peperehobbits01.rejei.compat.rei.display.CreateDisplay;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe.Builder;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

public class ItemDrainCategory extends CreateRecipeCategory<EmptyingRecipe> {

	private final AnimatedItemDrain drain = new AnimatedItemDrain();

	public ItemDrainCategory(Info<EmptyingRecipe> info) {
		super(info);
	}

	public static void consumeRecipes(Consumer<EmptyingRecipe> consumer) {
		EntryRegistry.getInstance().getEntryStacks()
				.filter(stack -> Objects.equals(stack.getType(), VanillaEntryTypes.ITEM))
				.<EntryStack<ItemStack>>map(EntryStack::cast)
				.toList()
				.forEach(entryStack -> {
					ItemStack stack = entryStack.getValue();

					if (stack.getItem() == Items.POTION || stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION) {
						FluidStack fluidFromPotionItem = PotionFluidHandler.getFluidFromPotionItem(stack);
						Ingredient potion = Ingredient.of(stack);
						consumer.accept(new Builder<>(EmptyingRecipe::new, Create.asResource("potions"))
								.withItemIngredients(potion)
								.withFluidOutputs(fluidFromPotionItem)
								.withSingleItemOutput(new ItemStack(Items.GLASS_BOTTLE))
								.build());
						return;
					}

					ItemStack copy = stack.copy();
					var fluidHandlerOpt = FluidUtil.getFluidHandler(copy);

					if (!fluidHandlerOpt.isPresent())
						return;

					fluidHandlerOpt.ifPresent(handler -> {
						// Simulate draining to see what we can get
						FluidStack extracted = handler.drain(1000, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
						if (extracted.isEmpty())
							return;

						// Actually drain it
						FluidStack drained = handler.drain(extracted, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
						if (drained.isEmpty())
							return;

						// Get the empty container
						ItemStack emptyContainer = handler.getContainer();
						if (emptyContainer.isEmpty())
							return;

						Ingredient ingredient = Ingredient.of(stack);
						ResourceLocation itemName = BuiltInRegistries.ITEM.getKey(stack.getItem());
						ResourceLocation fluidName = BuiltInRegistries.FLUID.getKey(drained.getFluid());

						consumer.accept(new Builder<>(EmptyingRecipe::new,
								Create.asResource("empty_" + itemName.getNamespace() + "_" + itemName.getPath() + "_of_"
										+ fluidName.getNamespace() + "_" + fluidName.getPath()))
								.withItemIngredients(ingredient)
								.withFluidOutputs(drained)
								.withSingleItemOutput(emptyContainer)
								.build());
					});
				});
	}

	@Override
	public void addWidgets(CreateDisplay<EmptyingRecipe> display, List<Widget> ingredients, Point origin) {
		FluidStack fluidOutput = display.getRecipe().getResultingFluid();
		List<ItemStack> matchingIngredients = java.util.Arrays.asList(display.getRecipe().getIngredients()
				.getFirst()
				.getItems());

		Slot fluidSlot = basicSlot(origin.x + 132, origin.y + 8)
				.markOutput()
				.entries(EntryIngredients.of(CreateRecipeCategory.convertToREIFluid(fluidOutput)));
		CreateRecipeCategory.setFluidRenderRatio(fluidSlot);
		ingredients.add(fluidSlot);
		ingredients.add(basicSlot(origin.x + 27, origin.y + 8)
				.markOutput()
				.entries(EntryIngredients.ofItemStacks(matchingIngredients)));
		ingredients.add(basicSlot(origin.x + 132, origin.y + 27)
				.markInput()
				.entries(display.getOutputEntries().getFirst()));

		CreateRecipeCategory.addFluidTooltip(ingredients, Collections.emptyList(), java.util.Collections.singletonList(fluidOutput));
	}

	@Override
	public void draw(EmptyingRecipe recipe, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SLOT.render(graphics, 131, 7);
		AllGuiTextures.JEI_SLOT.render(graphics, 26, 7);
		getRenderedSlot(recipe, 0).render(graphics, 131, 26);
		AllGuiTextures.JEI_SHADOW.render(graphics, 62, 37);
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 73, 4);
		drain.withFluid(recipe.getResultingFluid())
				.draw(graphics, getDisplayWidth(null) / 2 - 13, 40);
	}

}