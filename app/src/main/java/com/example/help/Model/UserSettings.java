package com.example.help.Model;

public class UserSettings {
    private float weight;
    private boolean useKm;

    public UserSettings(float weight, boolean useKm) {
        this.weight = weight;
        this.useKm = useKm;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean isUseKm() {
        return useKm;
    }

    public void setUseKm(boolean useKm) {
        this.useKm = useKm;
    }
}
