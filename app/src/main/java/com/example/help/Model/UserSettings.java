package com.example.help.Model;

/**
 * The UserSettings class stores user preferences such as weight and distance units.
 * It allows retrieval and modification of these settings.
 */
public class UserSettings {

    // Static field for user's weight in kilograms (shared across all instances).
    private static float weight;

    // Boolean field indicating whether to use kilometers (true) or miles (false).
    private boolean useKm;

    /**
     * Constructor for the UserSettings class.
     *
     * @param weight The user's weight in kilograms.
     * @param useKm  A boolean indicating the user's preferred distance unit (true for km, false for miles).
     */
    public UserSettings(float weight, boolean useKm) {
        UserSettings.weight = weight; // Set the static weight field.
        this.useKm = useKm;           // Set the instance-specific distance unit preference.
    }

    /**
     * Retrieves the user's weight.
     *
     * @return The user's weight in kilograms.
     */
    public static float getWeight() {
        return weight;
    }

    /**
     * Sets the user's weight.
     *
     * @param weight The new weight value to set (in kilograms).
     */
    public void setWeight(float weight) {
        UserSettings.weight = weight; // Update the static weight field.
    }

    /**
     * Retrieves the user's preferred distance unit.
     *
     * @return true if the user prefers kilometers, false for miles.
     */
    public boolean isUseKm() {
        return useKm;
    }

    /**
     * Sets the user's preferred distance unit.
     *
     * @param useKm true to prefer kilometers, false for miles.
     */
    public void setUseKm(boolean useKm) {
        this.useKm = useKm; // Update the instance-specific distance unit preference.
    }
}
