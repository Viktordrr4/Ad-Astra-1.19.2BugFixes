package net.mrscauthd.beyond_earth.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.mrscauthd.beyond_earth.util.ModIdentifier;
import net.mrscauthd.beyond_earth.util.ModUtils;
import vazkii.patchouli.api.PatchouliAPI;

public class GuideBook extends Item {

    public GuideBook(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            if (ModUtils.modLoaded("patchouli")) {
                PatchouliAPI.get().openBookGUI(player, new ModIdentifier("guide_book"));
            } else {
                user.sendMessage(new TranslatableText("info.beyond_earth.install_patchouli"), true);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
