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
public class BuildObject {
    public int playerID, workerID, targetID;
    
    public BuildObject() {
    } 
 
    public BuildObject(int playerID, int workerID, int targetID) {
        this.playerID = playerID;
        this.workerID = workerID;
        this.targetID = targetID;
    }
}
