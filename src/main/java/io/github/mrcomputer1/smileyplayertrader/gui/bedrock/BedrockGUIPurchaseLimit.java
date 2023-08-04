package io.github.mrcomputer1.smileyplayertrader.gui.bedrock;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockCustomGUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component.BedrockInputComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component.BedrockToggleComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.GUIProduct;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.ProductState;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.entity.Player;

public class BedrockGUIPurchaseLimit extends BedrockCustomGUI {

    private final Player player;
    private final ProductState state;

    private final BedrockInputComponent value;
    private final BedrockToggleComponent disabled;

    public BedrockGUIPurchaseLimit(Player player, ProductState state) {
        super(I18N.translate("&2Set Purchase Limit"));

        this.player = player;
        this.state = state;

        this.disabled = new BedrockToggleComponent("Disabled", this.state.purchaseLimit == -1);
        this.addChild(this.disabled);
        this.value = new BedrockInputComponent("Purchase Limit", "0",
                this.state.purchaseLimit == -1 ? "0" : (this.state.purchaseLimit + ""));
        this.addChild(this.value);
    }

    @Override
    protected void onClose() {
        GUIManager.getInstance().openGui(this.player, new GUIProduct(this.player, this.state));
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

            this.state.purchaseLimit = num;
            GUIManager.getInstance().openGui(this.player, new GUIProduct(this.player, this.state));
        }catch (NumberFormatException e){
            GUIManager.sendErrorMessage(this.player, I18N.translate("&cInvalid Number!"));
        }
    }

}
