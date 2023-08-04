package io.github.mrcomputer1.smileyplayertrader.gui.bedrock;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockCustomGUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component.BedrockInputComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component.BedrockLabelComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.GUIProduct;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.ProductState;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.entity.Player;

public class BedrockGUIPriority extends BedrockCustomGUI {

    private final Player player;
    private final ProductState state;

    private final BedrockInputComponent priority;

    public BedrockGUIPriority(Player player, ProductState state) {
        super(I18N.translate("&2Set Priority"));

        this.player = player;
        this.state = state;

        this.addChild(new BedrockLabelComponent(I18N.translate("&eHigher priorities appear higher in the trade list.")));
        this.priority = new BedrockInputComponent(I18N.translate("Priority (must be a positive whole number)"), "0", state.priority + "");
        this.addChild(this.priority);
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
            int num = Integer.parseInt(this.priority.getValue());

            if(num < 0){
                GUIManager.sendErrorMessage(this.player, I18N.translate("&cYou cannot have a negative priority."));
                return;
            }

            this.state.priority = num;
            GUIManager.getInstance().openGui(this.player, new GUIProduct(this.player, this.state));
        }catch (NumberFormatException e){
            GUIManager.sendErrorMessage(this.player, I18N.translate("&cInvalid Number!"));
        }
    }

}
