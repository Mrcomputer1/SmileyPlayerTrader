package io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockComponent;
import org.geysermc.cumulus.component.InputComponent;

public class BedrockInputComponent extends BedrockComponent<String> {

    private final String text;
    private final String placeholder;
    private final String defaultValue;

    public BedrockInputComponent(String text){
        this(text, "", "");
    }

    public BedrockInputComponent(String text, String placeholder){
        this(text, placeholder, "");
    }

    public BedrockInputComponent(String text, String placeholder, String defaultValue){
        this.text = text;
        this.placeholder = placeholder;
        this.defaultValue = defaultValue;
    }

    public String getText() {
        return text;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Object makeComponent() {
        return InputComponent.of(this.text, this.placeholder, this.defaultValue);
    }

}
