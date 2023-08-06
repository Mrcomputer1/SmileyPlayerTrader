package io.github.mrcomputer1.smileyplayertrader.gui.bedrock;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockCustomGUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component.BedrockInputComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component.BedrockToggleComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.GUIProduct;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.ProductState;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BedrockGUIPurchaseLimit extends BedrockCustomGUI {

    private final Player player;
    private final ProductState state;

    private final BedrockInputComponent value;
    private final BedrockToggleComponent disabled;
    private final BedrockToggleComponent resetCount;

    public BedrockGUIPurchaseLimit(Player player, ProductState state) {
        super(I18N.translate("&2Set Purchase Limit"));

        this.player = player;
        this.state = state;

        this.disabled = new BedrockToggleComponent(I18N.translate("Disabled"), this.state.purchaseLimit == -1);
        this.addChild(this.disabled);

        this.value = new BedrockInputComponent(I18N.translate("Purchase Limit"), "0",
                this.state.purchaseLimit == -1 ? "0" : (this.state.purchaseLimit + ""));
        this.addChild(this.value);

        this.resetCount = new BedrockToggleComponent(I18N.translate("Reset Purchase Count"), false);
        this.addChild(this.resetCount);
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
            int num = Integer.parseInt(this.value.getValue());

            if(num < 0){
                GUIManager.sendErrorMessage(this.player, I18N.translate("&cYou cannot have a negative purchase limit."));
                return;
            }

            if(this.disabled.getValue()){
                num = -1;
            }

            if(this.resetCount.getValue() && !this.state.isNew){
                SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.RESET_PURCHASE_COUNT, this.state.id);
            }

            this.state.purchaseLimit = num;
            Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), () ->
                    GUIManager.getInstance().openGui(this.player, new GUIProduct(this.player, this.state))
            );
        }catch (NumberFormatException e){
            GUIManager.sendErrorMessage(this.player, I18N.translate("&cInvalid Number!"));
        }
    }

}
