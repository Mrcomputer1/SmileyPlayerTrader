package io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockComponent;
import org.geysermc.cumulus.component.DropdownComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BedrockDropdownComponent extends BedrockComponent<String> {

    private final String text;
    private final List<String> options;
    private final int defaultOption;

    public BedrockDropdownComponent(String text, int defaultOption, String... options){
        this(text, defaultOption, Arrays.asList(options));
    }

    public BedrockDropdownComponent(String text, String... options){
        this(text, 0, options);
    }

    public BedrockDropdownComponent(String text, List<String> options){
        this(text, 0, options);
    }

    public BedrockDropdownComponent(String text, int defaultOption, List<String> options){
        if(defaultOption >= options.size())
            defaultOption = 0;

        this.text = text;
        this.options = Collections.unmodifiableList(options);
        this.defaultOption = defaultOption;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getDefaultOption() {
        return defaultOption;
    }

    @Override
    public Object makeComponent() {
        return DropdownComponent.of(this.text, this.options, this.defaultOption);
    }

}
