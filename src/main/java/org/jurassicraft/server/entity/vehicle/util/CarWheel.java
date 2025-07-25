package org.jurassicraft.server.entity.vehicle.util;

import net.minecraft.world.phys.Vec3;

public class CarWheel {
    
    // TODO: Replace Vector2d with appropriate alternative 
    private final Object relativeWheelPosition; // Temporarily Object        
    private Vec3 currentWheelPos = Vec3.ZERO;
    private Vec3 prevCurrentWheelPos = Vec3.ZERO;
    private final int ID;
    
    private CarWheel oppositeWheel;
    
    public CarWheel(int id, Object relativeWheelPosition) { // TODO: Fix type
    	this.relativeWheelPosition = relativeWheelPosition;
		this.ID = id;
    }
    
    public Object getRelativeWheelPosition() { // TODO: Fix Vector2d replacement
    	return relativeWheelPosition;
    }
    
    public void setCurrentWheelPos(Vec3 currentWheelPos) {
    	this.prevCurrentWheelPos = this.currentWheelPos;
        this.currentWheelPos = currentWheelPos;
    }
    
    public Vec3 getPrevCurrentWheelPos() {
	return prevCurrentWheelPos;
    }
    
    public Vec3 getCurrentWheelPos() {
        return currentWheelPos;
    }
    
    public int getID() {
    	return ID;
    }
    
    public void setPair(CarWheel oppositeWheel) {
    	this.oppositeWheel = oppositeWheel;
    	oppositeWheel.oppositeWheel = this;
    }
    
    public CarWheel getOppositeWheel() {
    	return oppositeWheel;
    }
}