package io.github.mrcomputer1.smileyplayertrader.gui.bedrock;

import com.google.common.primitives.Ints;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockCustomGUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component.BedrockSliderComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.GUIProduct;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.GUISetCost;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.ProductState;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.entity.Player;

public class BedrockGUISetCostQuantity extends BedrockCustomGUI {

    private final Player player;
    private final ProductState state;
    private final boolean isPrimary;

    private final BedrockSliderComponent value;

    public BedrockGUISetCostQuantity(Player player, ProductState state, boolean isPrimary) {
        super(I18N.translate("&2Set Item Quantity"));

        this.player = player;
        this.state = state;
        this.isPrimary = isPrimary;

        this.value = new BedrockSliderComponent(
                I18N.translate("Item Quantity"),
                0, this.isPrimary ? this.state.costStack.getMaxStackSize() : this.state.costStack2.getMaxStackSize(),
                1, this.isPrimary ? this.state.costStack.getAmount() : this.state.costStack2.getAmount()
        );
        this.addChild(this.value);
    }

    @Override
    protected void onClose() {
        GUIManager.getInstance().openGui(this.player, new GUISetCost(this.player, this.state, this.isPrimary));
    }

    @Override
    protected void onInvalid(String error, int componentIndex) {
    }

    @Override
    protected void onSubmit() {
        int val = this.value.getValue().intValue();
        val = Ints.constrainToRange(val, 0, this.isPrimary ? this.state.costStack.getMaxStackSize() : this.state.costStack2.getMaxStackSize());

        if(this.isPrimary){
            this.state.costStack.setAmount(val);
        }else{
            this.state.costStack2.setAmount(val);
        }

        GUIManager.getInstance().openGui(this.player, new GUISetCost(this.player, this.state, this.isPrimary));
    }

}
