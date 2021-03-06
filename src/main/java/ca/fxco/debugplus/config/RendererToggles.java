package ca.fxco.debugplus.config;

import ca.fxco.debugplus.DebugPlus;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigNotifiable;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBooleanConfigWithMessage;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import org.jetbrains.annotations.Nullable;

public enum RendererToggles implements IHotkeyTogglable, IConfigNotifiable<IConfigBoolean> {
    DEBUG_SHAPE_UPDATES                  ("debugShapeUpdate",        "", "Renderer for Shape Updates", "Block Shape Updates"),
    DEBUG_COMPARATOR_UPDATES             ("debugComparatorUpdate",   "", "Renderer for Comparator Updates", "Block Comparator Updates"),
    DEBUG_RANDOM_TICKS                   ("debugRandomTicks",        "", "Renderer for Random Ticks", "Random Ticks"),
    DEBUG_GOAT_JUMPING                   ("debugGoatJumping",        "", "Renderer for Goat Jumping","Goat Jumping"),
    DEBUG_MOB_TARGET                     ("debugMobTarget",          "", "Renderer for Mob Target","Mob Target"),
    DEBUG_MOB_MEMORIES                   ("debugMobMemories",        "", "Renderer for Mob Memories","Mob Memories"),
    DEBUG_MOB_TASKS                      ("debugMobTasks",           "", "Renderer for Mob Tasks","Mob Tasks"),
    DEBUG_MOB_SENSORS                    ("debugMobSensors",         "", "Renderer for Mob Sensors","Mob Sensors"),
    DEBUG_MOB_ACTIVITIES                 ("debugMobActivities",      "", "Renderer for Mob Activities","Mob Activities"),
    DEBUG_ENTITY_COLLISION               ("debugEntityCollision",    "", "Renderer for Entity Collisions Prediction","Entity Collision Prediction"),
    DEBUG_OVERLAY_BOX                    ("debugOverlayBox",         "", "Debug Box Renderer","Debug Overlay Box");

    private final String name;
    private final String prettyName;
    private final String comment;
    private final IKeybind keybind;
    private final boolean defaultValueBoolean;
    private boolean valueBoolean;
    @Nullable private IValueChangeCallback<IConfigBoolean> callback;

    RendererToggles(String name, String defaultHotkey, String comment, String prettyName) {
        this(name, defaultHotkey, KeybindSettings.DEFAULT, comment, prettyName);
    }

    RendererToggles(String name, String defaultHotkey, KeybindSettings settings, String comment, String prettyName) {
        this.name = name;
        this.prettyName = prettyName;
        this.comment = comment;
        this.defaultValueBoolean = false;
        this.keybind = KeybindMulti.fromStorageString(defaultHotkey, settings);

        this.keybind.setCallback(new KeyCallbackToggleBooleanConfigWithMessage(this));
    }

    @Override
    public ConfigType getType()
    {
        return ConfigType.HOTKEY;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getPrettyName()
    {
        return this.prettyName;
    }

    @Override
    public String getStringValue()
    {
        return String.valueOf(this.valueBoolean);
    }

    @Override
    public String getDefaultStringValue()
    {
        return String.valueOf(this.defaultValueBoolean);
    }

    @Override
    public String getComment()
    {
        return comment != null ? this.comment : "";
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.valueBoolean;
    }

    @Override
    public boolean getDefaultBooleanValue()
    {
        return this.defaultValueBoolean;
    }

    @Override
    public void setBooleanValue(boolean value) {
        boolean oldValue = this.valueBoolean;
        this.valueBoolean = value;

        if (oldValue != this.valueBoolean) {
            this.onValueChanged();
        }
    }

    @Override
    public void setValueChangeCallback(IValueChangeCallback<IConfigBoolean> callback)
    {
        this.callback = callback;
    }

    @Override
    public void onValueChanged() {
        if (this.callback != null) {
            this.callback.onValueChanged(this);
        }
    }

    @Override
    public IKeybind getKeybind()
    {
        return this.keybind;
    }

    @Override
    public boolean isModified()
    {
        return this.valueBoolean != this.defaultValueBoolean;
    }

    @Override
    public boolean isModified(String newValue) {
        return String.valueOf(this.defaultValueBoolean).equals(newValue) == false;
    }

    @Override
    public void resetToDefault()
    {
        this.valueBoolean = this.defaultValueBoolean;
    }

    @Override
    public void setValueFromString(String value) {
        try {
            this.valueBoolean = Boolean.parseBoolean(value);
        } catch (Exception e) {
            DebugPlus.logger.warn("Failed to read config value for {} from the JSON config", this.getName(), e);
        }
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        try {
            if (element.isJsonPrimitive()) {
                this.valueBoolean = element.getAsBoolean();
            } else {
                DebugPlus.logger.warn("Failed to read config value for {} from the JSON config", this.getName());
            }
        } catch (Exception e) {
            DebugPlus.logger.warn("Failed to read config value for {} from the JSON config", this.getName(), e);
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.valueBoolean);
    }
}
