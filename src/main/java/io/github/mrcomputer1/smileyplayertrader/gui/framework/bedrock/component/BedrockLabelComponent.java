package io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockComponent;
import org.geysermc.cumulus.component.LabelComponent;

public class BedrockLabelComponent extends BedrockComponent<Void> {

    private final String text;

    public BedrockLabelComponent(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public Object makeComponent() {
        return LabelComponent.of(this.text);
    }

}
