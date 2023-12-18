package au.com.mineauz.minigamesregions.language;

import au.com.mineauz.minigames.managers.language.PlaceHolderKey;
import org.intellij.lang.annotations.Subst;

public enum RegionPlaceHolderKey implements PlaceHolderKey {
    REGION("region"),
    NODE("node");

    private final String key;

    RegionPlaceHolderKey(String key) {
        this.key = key;
    }

    @Subst("region")
    @Override
    public String getKey() {
        return key;
    }
}
