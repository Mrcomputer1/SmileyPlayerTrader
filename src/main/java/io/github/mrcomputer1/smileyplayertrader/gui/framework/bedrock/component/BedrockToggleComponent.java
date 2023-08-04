package io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockComponent;
import org.geysermc.cumulus.component.ToggleComponent;

public class BedrockToggleComponent extends BedrockComponent<Boolean> {

    private final String text;
    private final boolean defaultValue;

    public BedrockToggleComponent(String text){
        this(text, false);
    }

    public BedrockToggleComponent(String text, boolean defaultValue){
        this.text = text;
        this.defaultValue = defaultValue;
    }

    public String getText() {
        return text;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Object makeComponent() {
        return ToggleComponent.of(this.text, this.defaultValue);
    }
}
