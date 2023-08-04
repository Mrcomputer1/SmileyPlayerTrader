package io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock;

public abstract class BedrockComponent<T> {

    private T value;
    private boolean isHidden;

    public abstract Object makeComponent();

    public T getValue(){
        return this.value;
    }
    protected void setValue(T value){
        this.value = value;
    }

    public boolean isHidden() {
        return isHidden;
    }
    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

}
