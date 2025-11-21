/*
 * This file is part of HopperFilterX.
 *
 * Copyright (C) 2025 MrH00k <https://github.com/MrH00k>
 *
 * HopperFilterX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 only,
 * as published by the Free Software Foundation.
 *
 * HopperFilterX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 3
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.mrh00k.hopperfilterx.managers;

import com.mrh00k.hopperfilterx.utils.HopperUtils;
import com.mrh00k.hopperfilterx.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public class RecipeManager {

  private static final Logger logger = Logger.getInstance();
  private static NamespacedKey recipeKey;

  public static void initialize(Plugin pluginInstance, NamespacedKey filteredHopperKey) {
    // Create recipe key using the plugin instance
    recipeKey = new NamespacedKey(pluginInstance, "filtered_hopper_recipe");

    // Check if recipe is enabled in config
    boolean recipeEnabled = pluginInstance.getConfig().getBoolean("recipe.enabled", true);

    if (!recipeEnabled) {
      logger.info(
          "Filtered hopper crafting recipe is disabled in config.yml - use /hopper give command instead");
      return;
    }

    try {
      registerFilteredHopperRecipe(filteredHopperKey);
      logger.success("Filtered hopper crafting recipe registered successfully");
    } catch (Exception e) {
      logger.error("Failed to register filtered hopper recipe: " + e.getMessage());
      logger.debug("Recipe registration error details: " + e.getClass().getSimpleName());
    }
  }

  private static void registerFilteredHopperRecipe(NamespacedKey filteredHopperKey) {
    // Create the filtered hopper item
    ItemStack filteredHopper = HopperUtils.createFilteredHopper(filteredHopperKey);

    // Create the shaped recipe
    ShapedRecipe recipe = new ShapedRecipe(recipeKey, filteredHopper);

    // Define the shape:
    // I R I  (Iron, Redstone, Iron)
    // I C I  (Iron, Chest, Iron)
    //   I    (Iron)
    recipe.shape("IRI", "ICI", " I ");

    // Define the ingredients
    recipe.setIngredient('I', Material.IRON_INGOT);
    recipe.setIngredient('R', Material.REDSTONE);
    recipe.setIngredient('C', Material.CHEST);

    // Register the recipe
    try {
      Bukkit.addRecipe(recipe);
      logger.info(
          "Registered crafting recipe: 5x Iron Ingot + 1x Redstone + 1x Chest = 1x Filtered Hopper");
    } catch (IllegalStateException e) {
      // Recipe might already exist from a previous load
      logger.warning("Recipe may already be registered: " + e.getMessage());
    }
  }

  public static void unregisterRecipes() {
    if (recipeKey != null) {
      try {
        Bukkit.removeRecipe(recipeKey);
        logger.info("Unregistered filtered hopper recipe");
      } catch (Exception e) {
        logger.warning("Failed to unregister recipe: " + e.getMessage());
      }
    }
  }
}
