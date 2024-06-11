package de.bax.dysonsphere.capabilities.orbitalLaser;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class OrbitalLaserAttackPattern implements INBTSerializable<CompoundTag> {
    
    public static final List<Character> validCallInChars = List.of('w','a','s','d');

    //set by gui - length requirement based on complexity
    protected String callInSequence = "ddd";

    //set by gui
    public int strikeCount = 1;
    public float strikeSize = 10; //1
    public int strikeDuration = 50; //1
    public float aimingArea = 0;
    public float homingArea = 0;
    public float homingSpeed = 0;
    public float damage = 1;
    public float blockDamage = 1;
    public int callInDelay = 0;
    public int repeatDelay = 0;
    public float spreadRadius = 0;

    //calculated
    protected float complexity;
    protected int lasersRequired;
    protected int rechargeTime;


    public void finishPattern(){
        complexity = calcComplexity();
        lasersRequired = calcLasersRequired();
        rechargeTime = calcRechargeTime();
    }

    protected float calcComplexity(){
        return strikeCount * strikeSize * strikeDuration * Math.min(1, aimingArea / 4) * Math.min(1, homingArea / 8) * Math.min(1, homingSpeed / 2) * Math.min(1, damage - blockDamage / 6) / Math.min(1, callInDelay / 10) / Math.min(1, repeatDelay / 2) / Math.min(1, spreadRadius);
    }

    protected int calcLasersRequired(){
        return (int) Math.ceil(strikeCount * (strikeSize / 2) * (strikeCount / 5) * (Math.min(4, (damage - blockDamage)) / 4));
    }

    protected int calcRechargeTime(){
        return (int) (50 * damage) - (callInDelay * 5) - repeatDelay;
    }


    public int getMinSequenceSize(){
        return Math.min(3, (int) Math.ceil(complexity));
    }

    public float getComplexity() {
        return complexity;
    }

    public int getLasersRequired() {
        return lasersRequired;
    }

    public int getRechargeTime() {
        return rechargeTime;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        String settings = String.join("-", callInSequence, Integer.toString(strikeCount), Float.toString(strikeSize), Integer.toString(strikeDuration), Float.toString(aimingArea), Float.toString(homingArea), Float.toString(homingSpeed), Float.toString(damage), Float.toString(blockDamage), Integer.toString(callInDelay), Integer.toString(repeatDelay), Float.toString(spreadRadius));
        tag.putString("AttackPattern", settings);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        String settings = nbt.getString("AttackPattern");
        if(!settings.isEmpty()){
            String[] vaules = settings.split("-");
            callInSequence = vaules[0];
            strikeCount = Integer.valueOf(vaules[1]);
            strikeSize = Float.valueOf(vaules[2]);
            strikeDuration = Integer.valueOf(vaules[3]);
            aimingArea = Float.valueOf(vaules[4]);
            homingArea = Float.valueOf(vaules[5]);
            homingSpeed = Float.valueOf(vaules[6]);
            damage = Float.valueOf(vaules[7]);
            blockDamage = Float.valueOf(vaules[8]);
            callInDelay = Integer.valueOf(vaules[9]);
            repeatDelay = Integer.valueOf(vaules[10]);
            spreadRadius = Float.valueOf(vaules[11]);
        }
        this.finishPattern();
    }





}
