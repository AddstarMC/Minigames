package au.com.mineauz.minigames.managers.language;

import org.intellij.lang.annotations.Subst;

public interface PlaceHolderKey {
    @Subst("number")
    String getKey();
}
