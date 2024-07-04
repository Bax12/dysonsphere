package de.bax.dysonsphere.capabilities.orbitalLaser;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class OrbitalLaserAttackPattern implements INBTSerializable<CompoundTag> {
    
    // public static final List<Character> validCallInChars = List.of('w','a','s','d');
    public static final String validCallInChars = "^[w,a,s,d]{1,}$";

    public static final OrbitalLaserAttackPattern EMPTY = new OrbitalLaserAttackPattern("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

    //set by gui - length requirement based on complexity
    protected String callInSequence = "";

    //set by gui
    public int strikeCount = 0;
    public float strikeSize = 0; 
    public int strikeDuration = 00;
    public float aimingArea = 0;
    public float homingArea = 0;
    public float homingSpeed = 0;
    public float damage = 0;
    public float blockDamage = 0;
    public int callInDelay = 0;
    public int repeatDelay = 0;
    public float spreadRadius = 0;

    //calculated
    protected float complexity;
    protected int lasersRequired;
    protected int rechargeTime;

    public OrbitalLaserAttackPattern(){}

    public OrbitalLaserAttackPattern(String callInSequence, int strikeCount, float strikeSize, int strikeDuration, float aimingArea, float homingArea, float homingSpeed, float damage, float blockDamage, int callInDelay, int repeatDelay, float spreadRadius){
        setCallInSequence(callInSequence);
        this.strikeCount = strikeCount;
        this.strikeSize = strikeSize;
        this.strikeDuration = strikeDuration;
        this.aimingArea = aimingArea;
        this.homingArea = homingArea;
        this.homingSpeed = homingSpeed;
        this.damage = damage;
        this.blockDamage = blockDamage;
        this.callInDelay = callInDelay;
        this.repeatDelay = repeatDelay;
        this.spreadRadius = spreadRadius;

        finishPattern();
    }




    public void finishPattern(){
        complexity = calcComplexity();
        lasersRequired = calcLasersRequired();
        rechargeTime = calcRechargeTime();
    }

    protected float calcComplexity(){
        // return (strikeCount * Math.max(1f, strikeSize) * strikeDuration * Math.max(1f, aimingArea / 4f) * Math.max(1f, homingArea / 8f) * Math.max(1f, homingSpeed / 2f) * Math.max(1f, damage - blockDamage / 6f) / Math.max(1f, callInDelay / 10f) / Math.max(1f, repeatDelay / 2f) / Math.max(1f, spreadRadius)) / 200f;
        return Math.min(9f, ((Math.max(1f, strikeCount) * 0.4f) + (Math.max(1.0F, strikeSize) * 0.25f) + (Math.max(20, strikeDuration) * 0.01f) + (Math.max(1f, aimingArea * aimingArea * 0.1f)) + (Math.max(1f, homingArea * homingArea * 0.1f)) + (Math.max(1f, homingSpeed * homingSpeed * 0.1f)) + (Math.max(1f, (damage - blockDamage) * damage * 0.05f)) - (Math.max(1, callInDelay / 8)) - (Math.max(1, repeatDelay / 7)) - spreadRadius));
    }

    protected int calcLasersRequired(){
        return (int) Math.ceil(strikeCount * (strikeSize / 2f) * (strikeCount / 5f) * (Math.min(4f, (damage - blockDamage)) / 4f));
    }

    protected int calcRechargeTime(){
        return (int) ((50 * damage) - (callInDelay * 5f) - repeatDelay);
    }


    public int getMinSequenceSize(){
        return Math.max(3, (int) Math.ceil(complexity));
    }

    public float getComplexity() {
        if(Float.isNaN(complexity)){
            finishPattern();
        }
        return complexity;
    }

    public int getLasersRequired() {
        if(lasersRequired == 0){
            finishPattern();
        }
        return lasersRequired;
    }

    public int getRechargeTime() {
        if(rechargeTime == 0){
            finishPattern();
        }
        return rechargeTime;
    }

    public void setCallInSequence(String callInSequence) {
        if(callInSequence.matches(validCallInChars)){
            this.callInSequence = callInSequence;
        }
    }

    public String getCallInSequence() {
        return callInSequence;
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

    public boolean isValid(){
        return callInSequence.length() >= getMinSequenceSize();
    }

    public boolean isEmpty() {
        return this.equals(EMPTY);
    }



}
