package io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock;

import org.geysermc.cumulus.form.SimpleForm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BedrockSimpleGUI implements BedrockGUI{

    private static class SimpleButton{
        private final String text;
        private final boolean optional;

        public SimpleButton(String text, boolean optional){
            this.text = text;
            this.optional = optional;
        }
    }

    private final String name;
    private final List<SimpleButton> buttons = new ArrayList<>();
    private String content;

    public BedrockSimpleGUI(String name){
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addButton(String button){
        this.buttons.add(new SimpleButton(button, false));
    }

    public void addOptionalButton(){
        this.buttons.add(new SimpleButton(null, true));
    }

    public void removeButton(String button){
        this.buttons.removeIf(b -> b.text.equals(button));
    }

    @Override
    public final Object buildForm(){
        SimpleForm.Builder builder = SimpleForm.builder();

        builder.title(this.name);

        for(SimpleButton button : this.buttons) {
            if(button.optional){
                builder.optionalButton(button.text, false);
            }else {
                builder.button(button.text);
            }
        }

        builder.closedResultHandler(this::onClose);
        builder.validResultHandler(response -> this.onSubmit(response.clickedButtonId()));

        return builder.build();
    }

    protected abstract void onClose();
    protected abstract void onSubmit(int button);

}
