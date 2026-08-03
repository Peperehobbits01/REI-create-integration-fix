package com.peperehobbits01.rejei.compat.rei;

import java.util.Arrays;
import java.util.stream.Stream;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Block;

public final class ToolboxColoringRecipeMaker {

	public static Stream<CraftingRecipe> createRecipes() {
		String group = "create.toolbox.color";
		ItemStack baseShulkerStack = AllBlocks.TOOLBOXES.get(DyeColor.BROWN).asStack();
		Ingredient baseShulkerIngredient = Ingredient.of(baseShulkerStack);

		return Arrays.stream(DyeColor.values())
				.filter(dc -> dc != DyeColor.BROWN)
				.map(color -> {
					DyeItem dye = DyeItem.byColor(color);

					Ingredient combinedDyeIngredient = Ingredient.fromValues(Stream.of(
							new Ingredient.ItemValue(new ItemStack(dye)),
							new Ingredient.TagValue(color.getTag())));

					NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, baseShulkerIngredient, combinedDyeIngredient);

					Block coloredShulkerBox = AllBlocks.TOOLBOXES.get(color).get();
					ItemStack output = new ItemStack(coloredShulkerBox);
					ResourceLocation id = Create.asResource(group + "." + output.getDescriptionId());

					return new ShapelessRecipe(group, CraftingBookCategory.MISC, output, inputs);
				});
	}

	private ToolboxColoringRecipeMaker() {}

}
