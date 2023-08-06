package io.github.mrcomputer1.smileyplayertrader.gui.bedrock;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.GUIProductList;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockCustomGUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockGUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component.BedrockLabelComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component.BedrockToggleComponent;
import io.github.mrcomputer1.smileyplayertrader.util.GeyserUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BedrockGUIDisableProduct extends BedrockCustomGUI {

    private final Player player;
    private final int id;


    private final OfflinePlayer target;
    private final int page;
    private final boolean isMine;

    private final BedrockToggleComponent hide;

    public BedrockGUIDisableProduct(Player player, int id, OfflinePlayer target, int page, boolean isMine) {
        super(I18N.translate("&2Disable/Hide Product %0%", id));

        this.player = player;
        this.id = id;

        this.target = target;
        this.page = page;
        this.isMine = isMine;

        this.addChild(new BedrockLabelComponent(I18N.translate("Are you sure you want to hide or disable this product?")));

        this.hide = new BedrockToggleComponent(I18N.translate("Hide Product"), true);
        this.addChild(this.hide);
    }

    @Override
    protected void onClose() {
    }

    @Override
    protected void onInvalid(String error, int componentIndex) {
    }

    @Override
    protected void onSubmit() {
        if(this.hide.getValue()){
            SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.HIDE_PRODUCT, this.id);
        }else{
            SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.DISABLE_PRODUCT, this.id);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), () ->
                GUIManager.getInstance().openGui(this.player, new GUIProductList(this.player, this.target, this.page, this.isMine))
        );
    }

}
