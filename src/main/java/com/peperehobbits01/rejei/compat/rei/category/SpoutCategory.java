package com.peperehobbits01.rejei.compat.rei.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.peperehobbits01.rejei.compat.rei.category.animations.AnimatedSpout;
import com.peperehobbits01.rejei.compat.rei.display.CreateDisplay;
import com.peperehobbits01.rejei.util.FluidHandlerConverter;
import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.Ingredient;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class SpoutCategory extends CreateRecipeCategory<FillingRecipe> {

	public SpoutCategory(CreateRecipeCategory.Info<FillingRecipe> info) {
		super(info);
	}

	@SuppressWarnings("UnstableApiUsage")
	public static void consumeRecipes(Consumer<FillingRecipe> consumer) {
		// Collect all fluid stacks, handling both Architectury and Forge types
		List<FluidStack> fluidStacks = new ArrayList<>();
		EntryRegistry.getInstance().getEntryStacks()
				.filter(stack -> Objects.equals(stack.getType(), VanillaEntryTypes.FLUID))
				.forEach(fluidEntry -> {
					try {
						Object value = fluidEntry.getValue();
						// Handle Architectury FluidStack
						if (value instanceof dev.architectury.fluid.FluidStack archFluid) {
							FluidStack forgeFluid = new FluidStack(
									BuiltInRegistries.FLUID.wrapAsHolder(archFluid.getFluid()),
									(int) archFluid.getAmount(),
									archFluid.getPatch()
							);
							fluidStacks.add(forgeFluid);
						}
						// Handle Forge FluidStack
						else if (value instanceof FluidStack forgeFluid) {
							fluidStacks.add(forgeFluid.copy());
						}
					} catch (Exception e) {
						// Skip fluids that can't be converted
					}
				});

		EntryRegistry.getInstance().getEntryStacks()
				.filter(stack -> Objects.equals(stack.getType(), VanillaEntryTypes.ITEM))
				.<EntryStack<ItemStack>>map(EntryStack::cast)
				.toList().forEach(entryStack -> {
					ItemStack stack = entryStack.getValue();
					if (stack.getItem() instanceof PotionItem) {
						FluidStack fluidFromPotionItem = PotionFluidHandler.getFluidFromPotionItem(stack);
						Ingredient bottle = Ingredient.of(Items.GLASS_BOTTLE);
						consumer.accept(new StandardProcessingRecipe.Builder<>(FillingRecipe::new, Create.asResource("potions"))
								.withItemIngredients(bottle)
								.withFluidIngredients(new SizedFluidIngredient(DataComponentFluidIngredient.of(false, fluidFromPotionItem), fluidFromPotionItem.getAmount()))
								.withSingleItemOutput(stack)
								.build());
						return;
					}

					for (FluidStack fluidStack : fluidStacks) {

						FluidStack fluidStackCopy = fluidStack.copy();
						ItemStack copy = stack.copy();

						fluidStackCopy.setAmount(1000);

						ItemStack testCopy = copy.copy();

						IFluidHandler fakeSource = FluidHandlerConverter.of(fluidStackCopy);
						IItemHandler fakeInventory = new InvWrapper(new net.minecraft.world.SimpleContainer(1));

						var result = FluidUtil.tryFillContainerAndStow(
								testCopy,
								fakeSource,
								fakeInventory,
								1000,
								null,
								false
						);

						if (!result.isSuccess())
							continue;

						ItemStack output = result.getResult();

						// skip invalid or unchanged results
						if (ItemStack.isSameItem(output, copy))
							continue;

						if (output.isEmpty())
							continue;

						Ingredient bucket = Ingredient.of(stack);

						ResourceLocation itemName = BuiltInRegistries.ITEM.getKey(stack.getItem());
						ResourceLocation fluidName = BuiltInRegistries.FLUID.getKey(fluidStackCopy.getFluid());

						consumer.accept(
								new StandardProcessingRecipe.Builder<>(FillingRecipe::new,
										Create.asResource(
												"fill_" + itemName.getNamespace() + "_" + itemName.getPath()
														+ "_with_" + fluidName.getNamespace() + "_" + fluidName.getPath()
										)
								)
										.withItemIngredients(bucket)
										.withFluidIngredients(new SizedFluidIngredient(DataComponentFluidIngredient.of(false, fluidStackCopy), fluidStackCopy.getAmount()))
										.withSingleItemOutput(output)
										.build()
						);
					}
				});
	}

	@Override
	public List<Widget> setupDisplay(CreateDisplay<FillingRecipe> display, Rectangle bounds) {
		Point origin = new Point(bounds.getX(), bounds.getY() + 4);
		List<Widget> widgets = new ArrayList<>();
		widgets.add(Widgets.createRecipeBase(bounds));
		// Create slots

		FluidStack fluidStack = display.getRecipe().getRequiredFluid().ingredient().getStacks()[0];
		widgets.add(WidgetUtil.textured(AllGuiTextures.JEI_SLOT, origin.getX() + 26, origin.getY() + 31));
		Slot fluidSlot = basicSlot(27, 32, origin).disableBackground().markInput().entries(EntryIngredients.of(CreateRecipeCategory.convertToREIFluid(fluidStack)));
		CreateRecipeCategory.setFluidRenderRatio(fluidSlot);
		widgets.add(fluidSlot);
		addFluidTooltip(widgets, List.of(display.getRecipe().getRequiredFluid()), Collections.emptyList());

		widgets.add(WidgetUtil.textured(AllGuiTextures.JEI_SLOT, origin.getX() + 26, origin.getY() + 50));
		widgets.add(Widgets.createSlot(point(origin.getX() + 27, origin.getY() + 51)).disableBackground().markInput().entries(display.getInputEntries().get(0)));


		widgets.add(WidgetUtil.textured(getRenderedSlot(display.getRecipe(), 0), origin.getX() + 131, origin.getY() + 50));
		// Draw arrow with shadow
		widgets.add(Widgets.createSlot(new Point(origin.getX() + 132, origin.getY() + 51)).disableBackground().markOutput().entries(display.getOutputEntries().get(0)));
		widgets.add(WidgetUtil.textured(AllGuiTextures.JEI_SHADOW, origin.getX() + 62, origin.getY() + 57));
		widgets.add(WidgetUtil.textured(AllGuiTextures.JEI_DOWN_ARROW, origin.getX() + 126, origin.getY() + 29));

		AnimatedSpout spout = new AnimatedSpout();

		spout.setPos(new Point(origin.getX() + (getDisplayWidth(display) / 2 - 13), origin.getY() + 22));
		spout.withFluids(java.util.Arrays.asList(display.getRecipe().getRequiredFluid()
				.ingredient().getStacks()));
		widgets.add(spout);
		return widgets;
	}
}