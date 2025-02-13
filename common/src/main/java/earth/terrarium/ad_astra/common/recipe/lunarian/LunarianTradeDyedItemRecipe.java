package earth.terrarium.ad_astra.common.recipe.lunarian;

import earth.terrarium.ad_astra.common.entity.LunarianMerchantOffer;
import earth.terrarium.ad_astra.common.registry.ModRecipeSerializers;
import earth.terrarium.ad_astra.common.registry.ModRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.BiFunction;

public class LunarianTradeDyedItemRecipe extends LunarianTradeRecipe {

    public LunarianTradeDyedItemRecipe(ResourceLocation id) {
        super(id);
    }

    public LunarianTradeDyedItemRecipe(ResourceLocation id, Builder<LunarianTradeDyedItemRecipe> builder) {
        super(id, builder);
    }

    @Override
    public ItemListing toItemListing() {
        return new LunarianMerchantOffer.SellDyedArmorFactory(this.getBuyA(), this.getBuyB(), this.getSell(), this.getMaxUses(), this.getExperience(), this.getMultiplier());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.LUNARIAN_TRADE_DYED_ITEM_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.LUNARIAN_TRADE_DYED_ITEM_RECIPE.get();
    }

    @Override
    protected ItemStack getDefaultBuyA() {
        return LunarianMerchantOffer.SellDyedArmorFactory.DEFAULT_BUY_A;
    }

    @Override
    protected ItemStack getDefaultBuyB() {
        return LunarianMerchantOffer.SellDyedArmorFactory.DEFAULT_BUY_B;
    }

    @Override
    protected int getDefaultMaxUses() {
        return LunarianMerchantOffer.SellDyedArmorFactory.DEFAULT_MAX_USES;
    }

    @Override
    protected int getDefaultExperience() {
        return LunarianMerchantOffer.SellDyedArmorFactory.DEFAULT_EXPERIENCE;
    }

    @Override
    protected float getDefaultMultiplier() {
        return LunarianMerchantOffer.SellDyedArmorFactory.DEFAULT_MULTIPLIER;
    }

    public static class Builder<RECIPE extends LunarianTradeDyedItemRecipe> extends LunarianTradeRecipe.Builder<RECIPE> {

        public Builder(BiFunction<ResourceLocation, ? extends Builder<RECIPE>, RECIPE> function) {
            super(function);
        }
    }
}