/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.BitJunkies.RTS.src.server;

/**
 *
 * @author brobz
 */
public class AttackObject {
    public int playerID, unitID, targetPlayerID, targetUnitID, targetBuildingID;
    
    public AttackObject() {
    } 
 
    public AttackObject(int playerID, int unitID, int targetPlayerID, int targetUnitID, int targetBuildingID) {
        this.playerID = playerID;
        this.unitID = unitID;
        this.targetPlayerID = targetPlayerID;
        this.targetUnitID = targetUnitID;
        this.targetBuildingID = targetBuildingID;
    }
}
