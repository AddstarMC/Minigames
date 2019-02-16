package au.com.mineauz.minigames.objects;

import be.seeseemelk.mockbukkit.WorldMock;

/**
 * Created for the AddstarMC Project. Created by Narimm on 6/02/2019.
 */
public class TestWorld extends WorldMock {
    private Long worldTime = 0L;
    private Long fullTime = 0L;

    @Override
    public long getTime() {
        return worldTime;
    }

    @Override
    public void setTime(long time) {
        worldTime = time;
    }

    @Override
    public long getFullTime() {
        return fullTime;
    }

    @Override
    public void setFullTime(long time) {
        fullTime = time;
    }
}
