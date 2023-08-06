package io.github.mrcomputer1.smileyplayertrader.gui.bedrock;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.GUIProductList;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockSimpleGUI;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.GUIEnableDisableProduct;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.GUIItemStorage;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.GUIProduct;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.ProductState;
import io.github.mrcomputer1.smileyplayertrader.util.GeyserUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BedrockGUIEditProduct extends BedrockSimpleGUI {

    private static final int EDIT_BUTTON_ID = 0;
    private static final int VISIBILITY_BUTTON_ID = 1;
    private static final int MANAGE_STORED_ITEMS_BUTTON_ID = 2;
    private static final int DELETE_BUTTON_ID = 3;

    private final Player player;
    private final ProductState state;

    public BedrockGUIEditProduct(Player player, ProductState state) {
        super(I18N.translate("&2Editing Product %0%", state.id));
        this.player = player;
        this.state = state;

        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(
                StatementHandler.StatementType.GET_PRODUCT_BY_ID, this.state.id
        )) {

            if(!set.next())
                throw new RuntimeException("Product doesn't exist.");

            // BUTTON 0
            this.addButton(I18N.translate("Edit Product"));

            // BUTTON 1
            if(set.getBoolean("enabled") && set.getBoolean("available")){
                this.addButton(I18N.translate("Disable or Hide Product"));
            }else{
                this.addButton(I18N.translate("Enable or Show Product"));
            }

            // BUTTON 2
            if(SmileyPlayerTrader.getInstance().getConfiguration().getItemStorageEnabled())
                this.addButton(I18N.translate("Manage Stored Items"));
            else this.addOptionalButton();

            // BUTTON 3
            this.addButton(I18N.translate("Delete Product"));

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onClose() {
    }

    @Override
    protected void onSubmit(int button) {
        if(button == EDIT_BUTTON_ID){

            // Edit
            Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), () ->
                    GUIManager.getInstance().openGui(this.player, new GUIProduct(this.player, state))
            );

        }else if(button == VISIBILITY_BUTTON_ID){

            // Visibility
            try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_ENABLED, this.state.id)){
                if(set.next()) {
                    boolean enable = !set.getBoolean("enabled") || !set.getBoolean("available");
                    if(enable){
                        GeyserUtil.showConfirmationForm(
                                this.player, I18N.translate("&2Enable/Show Product %0%", this.state.id),
                                I18N.translate("Are you sure you want to enable this product?"),
                                result -> {
                                    if(result) {
                                        SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.ENABLE_PRODUCT, this.state.id);
                                    }
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), () ->
                                            GUIManager.getInstance().openGui(this.player, new GUIProductList(this.player, this.state.target, this.state.page, this.state.isMine))
                                    );
                                }
                        );
                    }else{
                        GeyserUtil.showFormDelayed(this.player, new BedrockGUIDisableProduct(
                                this.player, this.state.id, this.state.target, this.state.page, this.state.isMine
                        ));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }else if(button == MANAGE_STORED_ITEMS_BUTTON_ID){

            // Manage Stored Items
            Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), () ->
                    GUIManager.getInstance().openGui(this.player, new GUIItemStorage(
                            this.player, this.state.id, this.state.storedProduct, this.state.stack, this.state.page,
                            this.state.target, this.state.isMine
                    ))
            );

        }else if(button == DELETE_BUTTON_ID){

            // Delete
            GeyserUtil.showConfirmationForm(
                    this.player,
                    I18N.translate("&2Delete Product %0%", this.state.id),
                    I18N.translate("Are you sure you want to delete this product?"),
                    result -> {
                        if(result){
                            SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.DELETE_PRODUCT, this.state.id);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), () ->
                                    GUIManager.getInstance().openGui(this.player, new GUIProductList(this.player, this.state.target, this.state.page, this.state.isMine))
                            );
                        }else{
                            Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), () ->
                                    GUIManager.getInstance().openGui(this.player, new GUIProductList(this.player, this.state.target, this.state.page, this.state.isMine))
                            );
                        }
                    }
            );

        }
    }

}
