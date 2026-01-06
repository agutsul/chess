package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.Activity;

public interface Impact<SOURCE>
        extends Positionable, Activity<Impact.Type,SOURCE> {

    enum Type implements Activity.Type {
        CONTROL,
        PROTECT,
        MONITOR,
        BLOCKADE,
        ISOLATION,
        BACKWARD,
        ACCUMULATION,
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
        OUTPOST,
        SACRIFICE,
        LUFT,
        DESPERADO,
        DOMINATION,
        XRAY
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

    static boolean isBlockade(Impact<?> impact) {
        return isBlockade(impact.getType());
    }

    static boolean isBlockade(Impact.Type impactType) {
        return Impact.Type.BLOCKADE.equals(impactType);
    }

    static boolean isIsolation(Impact<?> impact) {
        return isIsolation(impact.getType());
    }

    static boolean isIsolation(Impact.Type impactType) {
        return Impact.Type.ISOLATION.equals(impactType);
    }

    static boolean isBackward(Impact<?> impact) {
        return isBackward(impact.getType());
    }

    static boolean isBackward(Impact.Type impactType) {
        return Impact.Type.BACKWARD.equals(impactType);
    }

    static boolean isAccumulation(Impact<?> impact) {
        return isAccumulation(impact.getType());
    }

    static boolean isAccumulation(Impact.Type impactType) {
        return Impact.Type.ACCUMULATION.equals(impactType);
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

    static boolean isSacrifice(Impact<?> impact) {
        return isSacrifice(impact.getType());
    }

    static boolean isSacrifice(Impact.Type impactType) {
        return Impact.Type.SACRIFICE.equals(impactType);
    }

    static boolean isLuft(Impact<?> impact) {
        return isLuft(impact.getType());
    }

    static boolean isLuft(Impact.Type impactType) {
        return Impact.Type.LUFT.equals(impactType);
    }

    static boolean isDesperado(Impact<?> impact) {
        return isDesperado(impact.getType());
    }

    static boolean isDesperado(Impact.Type impactType) {
        return Impact.Type.DESPERADO.equals(impactType);
    }

    static boolean isDomination(Impact<?> impact) {
        return isDomination(impact.getType());
    }

    static boolean isDomination(Impact.Type impactType) {
        return Impact.Type.DOMINATION.equals(impactType);
    }

    static boolean isXRay(Impact<?> impact) {
        return isXRay(impact.getType());
    }

    static boolean isXRay(Impact.Type impactType) {
        return Impact.Type.XRAY.equals(impactType);
    }
}