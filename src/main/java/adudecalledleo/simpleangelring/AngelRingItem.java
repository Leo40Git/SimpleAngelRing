package adudecalledleo.simpleangelring;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class AngelRingItem extends Item {
    public AngelRingItem(Settings settings) {
        super(settings);
    }

    public static boolean isRingEnabled(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() != Initializer.ANGEL_RING)
            return false;
        boolean ringEnabled = true;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("enabled", NbtType.BYTE))
            ringEnabled = tag.getBoolean("enabled");
        return ringEnabled;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!user.isSneaking())
            return TypedActionResult.pass(stack);
        boolean wasRingEnabled = isRingEnabled(stack);
        stack.getOrCreateTag().putBoolean("enabled", !wasRingEnabled);
        user.sendMessage(new TranslatableText(getTranslationKey() + (wasRingEnabled ? ".disabled" : ".enabled")),
                true);
        if (world.isClient)
            world.playSound(user, user.getX(), user.getY(), user.getZ(),
                    wasRingEnabled ? ModSoundEvents.ANGEL_RING_DISABLED
                            : ModSoundEvents.ANGEL_RING_ENABLED,
                    SoundCategory.PLAYERS, 1, wasRingEnabled ? 1.2F : 1);
        return TypedActionResult.consume(stack);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return isRingEnabled(stack);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        for (int i = 0; i < 3; i++)
            tooltip.add(new TranslatableText(getTranslationKey() + ".tooltip[" + i + "]")
                    .styled(style -> style.withColor(Formatting.DARK_GRAY).withItalic(true)));
        if (!isRingEnabled(stack))
            tooltip.add(new TranslatableText(getTranslationKey() + ".tooltip.disabled")
                    .styled(style -> style.withColor(Formatting.RED).withBold(true)));
    }
}