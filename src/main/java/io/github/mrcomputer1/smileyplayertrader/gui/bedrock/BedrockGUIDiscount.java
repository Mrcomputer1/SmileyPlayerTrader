package io.github.mrcomputer1.smileyplayertrader.gui.bedrock;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockCustomGUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component.BedrockInputComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component.BedrockLabelComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component.BedrockSliderComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.GUIProduct;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.ProductState;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BedrockGUIDiscount extends BedrockCustomGUI {

    private final Player player;
    private final ProductState state;

    private final BedrockInputComponent value;

    public BedrockGUIDiscount(Player player, ProductState state) {
        super(I18N.translate("&2Set Discount"));

        this.player = player;
        this.state = state;

        this.addChild(new BedrockLabelComponent(I18N.translate("&eNegative numbers will increase the price.")));
        this.value = new BedrockInputComponent(I18N.translate("Discount"), "", this.state.discount + "");
        this.addChild(this.value);
    }

    @Override
    protected void onClose() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), () ->
                GUIManager.getInstance().openGui(this.player, new GUIProduct(this.player, this.state))
        );
    }

    @Override
    protected void onInvalid(String error, int componentIndex) {
    }

    @Override
    protected void onSubmit() {
        try {
            int newDiscount = Integer.parseInt(this.value.getValue());

            int testValue = -newDiscount + this.state.costStack.getAmount();
            if (testValue >= 1 && testValue <= this.state.costStack.getMaxStackSize()) {
                this.state.discount = newDiscount;
                Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), () ->
                        GUIManager.getInstance().openGui(this.player, new GUIProduct(this.player, this.state))
                );
            } else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), () ->
                        GUIManager.sendErrorMessage(this.player, I18N.translate("&cDiscount would make price too small or too large."))
                );
            }
        }catch (NumberFormatException e){
            GUIManager.sendErrorMessage(this.player, I18N.translate("&cInvalid Number!"));
        }
    }

}
