package com.github.applejuiceyy.automa.client.lua.api.controls;

import com.github.applejuiceyy.automa.client.lua.api.listener.Event;
import com.github.applejuiceyy.automa.client.lua.api.listener.Future;

import java.util.LinkedList;
import java.util.List;

public abstract class MissionCritical {
    boolean missionCritical;
    String missionCriticalOwnerName;
    List<Future<?>> waiters = new LinkedList<>();

    public MissionCritical() {}

    public void completeOnAvailable(Future<?> future) {
        if(missionCritical) {
            waiters.add(future);
        }
        else {
            future.complete(null);
        }
    }

    public MissionCriticalRevoker tryRequest(String name) throws MissionCriticalRequestException {
        if (missionCritical) {
            throw new MissionCriticalRequestException();
        }

        missionCritical = true;
        missionCriticalOwnerName = name;

        return new MissionCriticalRevoker(this);
    }

    public boolean requested() {
        return missionCritical;
    }

    void revokeRequest() {
        missionCritical = false;
        missionCriticalOwnerName = null;

        while (waiters.size() > 0 && !missionCritical) {
            Future<?> waiter = waiters.remove(0);
            waiter.complete(null);
        }
    }

    public String getRequester() {
        return missionCriticalOwnerName;
    }

    public abstract String getControllingAspect();


    static public class MissionCriticalRevoker {
        MissionCritical owner;

        MissionCriticalRevoker(MissionCritical v) {
            owner = v;
        }

        void revoke() {
            owner.revokeRequest();
        }
    }

    static public class MissionCriticalRequestException extends Exception {}
}
