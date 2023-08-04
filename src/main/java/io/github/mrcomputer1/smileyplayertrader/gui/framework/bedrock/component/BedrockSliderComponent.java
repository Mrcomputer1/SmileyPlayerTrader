package io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockComponent;
import org.geysermc.cumulus.component.SliderComponent;

public class BedrockSliderComponent extends BedrockComponent<Float> {

    private final String text;
    private final float min;
    private final float max;
    private final float step;
    private final float defaultValue;

    public BedrockSliderComponent(String text, float min, float max){
        this(text, min, max, 1F);
    }

    public BedrockSliderComponent(String text, float min, float max, float step){
        this(text, min, max, step, Float.NaN);
    }

    public BedrockSliderComponent(String text, float min, float max, float step, float defaultValue){
        this.text = text;
        this.min = min;
        this.max = max;
        this.step = step;
        this.defaultValue = defaultValue;
    }

    public String getText() {
        return text;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getStep() {
        return step;
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Object makeComponent() {
        if(Float.isNaN(this.defaultValue)){
            return SliderComponent.of(this.text, this.min, this.max, this.step);
        }else{
            return SliderComponent.of(this.text, this.min, this.max, this.step, this.defaultValue);
        }
    }

}
