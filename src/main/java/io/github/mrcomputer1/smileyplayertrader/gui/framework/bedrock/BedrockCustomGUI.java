package io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.component.*;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.DropdownComponent;
import org.geysermc.cumulus.component.util.ComponentType;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.util.AbsentComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BedrockCustomGUI implements BedrockGUI {

    private final String name;
    private final List<BedrockComponent<?>> components = new ArrayList<>();

    public BedrockCustomGUI(String name){
        this.name = name;
    }

    public final void addChild(BedrockComponent<?> component){
        this.components.add(component);
    }

    public final void removeChild(BedrockComponent<?> component){
        this.components.remove(component);
    }

    public final List<BedrockComponent<?>> getComponents() {
        return Collections.unmodifiableList(this.components);
    }

    @SuppressWarnings("deprecation")
    @Override
    public final Object buildForm(){
        CustomForm.Builder builder = CustomForm.builder();

        builder.title(this.name);

        for(BedrockComponent<?> component : this.components)
            builder.component((Component) component.makeComponent());

        builder.closedResultHandler(this::onClose);
        builder.invalidResultHandler(result -> this.onInvalid(result.errorMessage(), result.componentIndex()));
        builder.validResultHandler(response -> {
            response.includeLabels(true);

            int i = 0;
            while(response.hasNext()){
                response.next();
                if(!response.isPresent()){
                    i++;
                    continue;
                }

                if(response.getComponentTypes().get(i) == ComponentType.DROPDOWN){
                    BedrockDropdownComponent bdc = (BedrockDropdownComponent) this.components.get(i);
                    //noinspection DataFlowIssue
                    bdc.setValue(bdc.getOptions().get(response.valueAt(i)));
                }else if(response.getComponentTypes().get(i) == ComponentType.INPUT){
                    BedrockInputComponent bic = (BedrockInputComponent) this.components.get(i);
                    bic.setValue(response.valueAt(i));
                }else if(response.getComponentTypes().get(i) == ComponentType.SLIDER){
                    BedrockSliderComponent bsc = (BedrockSliderComponent) this.components.get(i);
                    bsc.setValue(response.valueAt(i));
                }else if(response.getComponentTypes().get(i) == ComponentType.STEP_SLIDER){
                    BedrockStepSliderComponent bssc = (BedrockStepSliderComponent) this.components.get(i);
                    //noinspection DataFlowIssue
                    bssc.setValue(bssc.getSteps().get(response.valueAt(i)));
                }else if(response.getComponentTypes().get(i) == ComponentType.TOGGLE){
                    BedrockToggleComponent btc = (BedrockToggleComponent) this.components.get(i);
                    btc.setValue(response.valueAt(i));
                }else{
                    throw new IllegalStateException("Unknown component type.");
                }

                i++;
            }

            this.onSubmit();
        });

        return builder.build();
    }

    protected abstract void onClose();
    protected abstract void onInvalid(String error, int componentIndex);
    protected abstract void onSubmit();

}
