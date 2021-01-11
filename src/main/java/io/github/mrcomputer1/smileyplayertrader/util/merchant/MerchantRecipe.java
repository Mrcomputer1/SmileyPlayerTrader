package io.github.mrcomputer1.smileyplayertrader.util.merchant;

import org.bukkit.inventory.ItemStack;

public class MerchantRecipe extends org.bukkit.inventory.MerchantRecipe{
    public MerchantRecipe(ItemStack result, int maxUses) {
        super(result, maxUses);
    }

    public MerchantRecipe(ItemStack result, int uses, int maxUses, boolean experienceReward) {
        super(result, uses, maxUses, experienceReward);
    }

    public MerchantRecipe(ItemStack result, int uses, int maxUses, boolean experienceReward, int villagerExperience, float priceMultiplier) {
        super(result, uses, maxUses, experienceReward, villagerExperience, priceMultiplier);
    }

    public MerchantRecipe(ItemStack result, int uses, int maxUses, boolean experienceReward, int villagerExperience, float priceMultiplier, int specialPrice){
        super(result, uses, maxUses, experienceReward, villagerExperience, priceMultiplier);
        this.specialPrice = specialPrice;
    }

    private int specialPrice;

    public void setSpecialPrice(int specialPrice) {
        this.specialPrice = specialPrice;
    }

    public int getSpecialPrice() {
        return specialPrice;
    }
}
