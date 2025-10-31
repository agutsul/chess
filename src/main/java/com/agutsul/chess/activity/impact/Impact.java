package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.Activity;

public interface Impact<SOURCE>
        extends Positionable, Activity<Impact.Type,SOURCE> {

    enum Type implements Activity.Type {
        CONTROL,
        PROTECT,
        MONITOR,
        STAGNANT,
        PIN,
        CHECK,
        ATTACK,
        FORK,
        SKEWER,
        UNDERMINING,
        BLOCK,
        INTERFERENCE,
        DEFLECTION,
        OVERLOADING,
        BATTERY,
        OUTPOST
    }

    // utilities

    static boolean isControl(Impact<?> impact) {
        return isControl(impact.getType());
    }

    static boolean isControl(Impact.Type impactType) {
        return Impact.Type.CONTROL.equals(impactType);
    }

    static boolean isProtect(Impact<?> impact) {
        return isProtect(impact.getType());
    }

    static boolean isProtect(Impact.Type impactType) {
        return Impact.Type.PROTECT.equals(impactType);
    }

    static boolean isMonitor(Impact<?> impact) {
        return isMonitor(impact.getType());
    }

    static boolean isMonitor(Impact.Type impactType) {
        return Impact.Type.MONITOR.equals(impactType);
    }

    static boolean isStagnant(Impact<?> impact) {
        return isStagnant(impact.getType());
    }

    static boolean isStagnant(Impact.Type impactType) {
        return Impact.Type.STAGNANT.equals(impactType);
    }

    static boolean isPin(Impact<?> impact) {
        return isPin(impact.getType());
    }

    static boolean isPin(Impact.Type impactType) {
        return Impact.Type.PIN.equals(impactType);
    }

    static boolean isCheck(Impact<?> impact) {
        return isCheck(impact.getType());
    }

    static boolean isCheck(Impact.Type impactType) {
        return Impact.Type.CHECK.equals(impactType);
    }

    static boolean isAttack(Impact<?> impact) {
        return isAttack(impact.getType());
    }

    static boolean isAttack(Impact.Type impactType) {
        return Impact.Type.ATTACK.equals(impactType);
    }

    static boolean isFork(Impact<?> impact) {
        return isFork(impact.getType());
    }

    static boolean isFork(Impact.Type impactType) {
        return Impact.Type.FORK.equals(impactType);
    }

    static boolean isSkewer(Impact<?> impact) {
        return isSkewer(impact.getType());
    }

    static boolean isSkewer(Impact.Type impactType) {
        return Impact.Type.SKEWER.equals(impactType);
    }

    static boolean isUndermining(Impact<?> impact) {
        return isUndermining(impact.getType());
    }

    static boolean isUndermining(Impact.Type impactType) {
        return Impact.Type.UNDERMINING.equals(impactType);
    }

    static boolean isBlock(Impact<?> impact) {
        return isBlock(impact.getType());
    }

    static boolean isBlock(Impact.Type impactType) {
        return Impact.Type.BLOCK.equals(impactType);
    }

    static boolean isDeflection(Impact<?> impact) {
        return isDeflection(impact.getType());
    }

    static boolean isDeflection(Impact.Type impactType) {
        return Impact.Type.DEFLECTION.equals(impactType);
    }

    static boolean isOverloading(Impact<?> impact) {
        return isOverloading(impact.getType());
    }

    static boolean isOverloading(Impact.Type impactType) {
        return Impact.Type.OVERLOADING.equals(impactType);
    }

    static boolean isBattery(Impact<?> impact) {
        return isBattery(impact.getType());
    }

    static boolean isBattery(Impact.Type impactType) {
        return Impact.Type.BATTERY.equals(impactType);
    }

    static boolean isOutpost(Impact<?> impact) {
        return isOutpost(impact.getType());
    }

    static boolean isOutpost(Impact.Type impactType) {
        return Impact.Type.OUTPOST.equals(impactType);
    }
}