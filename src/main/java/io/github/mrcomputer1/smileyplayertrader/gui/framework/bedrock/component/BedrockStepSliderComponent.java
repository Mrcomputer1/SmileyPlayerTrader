package io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockComponent;
import org.geysermc.cumulus.component.StepSliderComponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BedrockStepSliderComponent extends BedrockComponent<String> {

    private final String text;
    private final List<String> steps;
    private final int defaultStep;

    public BedrockStepSliderComponent(String text, String... steps){
        this(text, 0, steps);
    }

    public BedrockStepSliderComponent(String text, int defaultStep, String... steps){
        this(text, defaultStep, Arrays.asList(steps));
    }

    public BedrockStepSliderComponent(String text, List<String> steps){
        this(text, 0, steps);
    }

    public BedrockStepSliderComponent(String text, int defaultStep, List<String> steps){
        if(defaultStep >= steps.size())
            defaultStep = 0;

        this.text = text;
        this.defaultStep = defaultStep;
        this.steps = Collections.unmodifiableList(steps);
    }

    public String getText() {
        return text;
    }

    public List<String> getSteps() {
        return steps;
    }

    public int getDefaultStep() {
        return defaultStep;
    }

    @Override
    public Object makeComponent() {
        return StepSliderComponent.of(this.text, this.steps, this.defaultStep);
    }

}
