/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.BitJunkies.RTS.src;

import DatabaseQueries.CreateJugadorEnPartida;
import com.BitJunkies.RTS.src.server.GameClient;
import com.jogamp.opengl.GL2;
import java.util.concurrent.ConcurrentHashMap;
import mikera.vectorz.Vector2;

/**
 *
 * @author brobz
 */

//Simple Worker class
public class Worker extends Unit{
    
    public static final int RUBY_COST = 30;
    
    private static final int MINING_TOP = 3;
    //Worker unique variables
    public static final int WORKER_WIDTH = 40, WORKER_HEIGHT = 40;
    private Timer hitingResourceTimer,buildingCasttleTimer;
    public boolean onMineCommand;
    public boolean onBuildCommand;
    private Resource targetMiningPatch;
    private Building targetBuilding;
    private int miningRange;
    private int creationImpact;
    
    
    //mining stuff
    private int currMining;
    private boolean onBringResourcesBackCommand;
    private Building nearestMiningBuilding;
    
    public Worker(){
        super();
    }
    
    public Worker(Vector2 dimension, Vector2 position, int id, Player owner){
       super(dimension, position, id, owner);
       this.speed = 4;
       this.maxHealth = 10;
       this.health = this.maxHealth;
       this.damage = 2;
       this.attackSpeed = 1;
       this.range = regularRange;
       this.hitingResourceTimer = new Timer(Game.getFPS());
       hitingResourceTimer.setUp(attackSpeed);
       this.buildingCasttleTimer = new Timer(Game.getFPS());
       buildingCasttleTimer.setUp(attackSpeed);
       this.miningRange = 60;
       this.buildingAttackRange = 65;
       this.unitAttackRange = 35;
       this.creationImpact = 5;
       this.texture = Assets.workerTexture;
       this.currMining = 0;
       this.onBringResourcesBackCommand = false;
    }
    
    public void tick(GridMap map){
        super.tick(map);
        
        if (onMoveCommand) {
            super.changeAnimationSide();
            animated = true;
        }
        else if(onMineCommand || onBuildCommand || onAttackCommand) {
            super.changeAttackingDirection();
            animated = true;
        }
        else {
            if(selected)
                texture = Assets.workersStandingSelectedTexture[owner.getID()-1];
            else
                texture = Assets.workersStandingTexture[owner.getID()-1];
            animated = false;
        }           
        
        //If the worker is designated to mine then...
        if(onMineCommand){
            double dist = Vector2.of(position.x, position.y).distance(targetMiningPatch.position);
            
            if(currMining == MINING_TOP){
                onMineCommand = false;
                onBringResourcesBackCommand = true;
                findNearesMiningBuilding();
            }
            
            //checking if the mining resource is still usable
            else if(!targetMiningPatch.isUsable()){
                stopMining();
                if(currMining != 0){
                    onBringResourcesBackCommand = true;
                    findNearesMiningBuilding();
                }
            }
            //otherwise check its already mining
            else if(dist < range){
                positionTarget = targetMiningPatch.position;
                if(hitingResourceTimer.doneWaiting()){
                    targetMiningPatch.singleAttack((int)damage);
                    hitingResourceTimer.setUp(attackSpeed);
                    currMining ++;
                }
            }else{
                moveTo(targetMiningPatch.position);
            }
        }
        //has to bring resources back
        else if(onBringResourcesBackCommand){
            if(nearestMiningBuilding == null) stopMining();
            else{
                //we are moving to the nearest building
                //if we've reached the building
                double dist = Vector2.of(position.x, position.y).distance(nearestMiningBuilding.position);
                if(dist < range){
                    owner.giveRubys(currMining);
                    currMining = 0;
                    onBringResourcesBackCommand = false;
                    
                    
                    CreateJugadorEnPartida.mapRecAd.put(owner.getID(), CreateJugadorEnPartida.getAcumRecAd(owner.getID()) + currMining);
                            
                    if(targetMiningPatch != null){
                       onMineCommand = true;
                       range = miningRange;
                    }
                }else{
                    moveTo(nearestMiningBuilding.position);
                }
            }
        }
        //If the worker is designated to build...
        else if(onBuildCommand){
            if(targetBuilding == null) stopBuilding();
            //check if the building is not built yet
            if(targetBuilding.isCreated()){               
                stopBuilding();
                
                int costo = 0;  
                if (targetBuilding instanceof Castle)
                    costo = Castle.RUBY_COST;
                else if (targetBuilding instanceof Barrack)
                    costo = Barrack.RUBY_COST;
                CreateJugadorEnPartida.mapRecGas.put(owner.getID(), CreateJugadorEnPartida.getAcumRecGas(owner.getID()) + costo);
                return;
            }
            //check if the worker is designated to build
            double dist = Vector2.of(position.x, position.y).distance(targetBuilding.position);
            if(dist < range){
                positionTarget = targetBuilding.position;
                if(buildingCasttleTimer.doneWaiting()){
                    targetBuilding.singleCreation(creationImpact);
                    buildingCasttleTimer.setUp(attackSpeed);
                }
            }else{
                moveTo(targetBuilding.position);
            }
        }
    }
    
    //method to deretmine where to mine
    public void mineAt(int playerID, GameClient client, Resource resourcePatch){
        client.sendMineCommand(owner.getID(), id, resourcePatch.id, owner.getUsername());
    }
    
    public void mineAt(Resource resourcePatch){
        stopBuilding();
        stopAttacking();
        onMineCommand = true;
        targetMiningPatch = resourcePatch;
        range = miningRange;
        nearestMiningBuilding = null;
    }
    
    //simple render method
    public void render(GL2 gl, Camera cam){
        if (animated){
            if(onMoveCommand){
                if(selected) texture = Assets.workersWalkingSelectedTexture[owner.getID()-1];
                else texture = Assets.workersWalkingTexture[owner.getID()-1];
            }
            else{
                if(selected) texture = Assets.workersMiningSelectedTexture[owner.getID()-1];
                else texture = Assets.workersMiningTexture[owner.getID()-1];
            }
            if(runningTimer.doneWaiting()){
                // cambio
                runningCnt ++;
                runningCnt %= 4;
                this.runningTimer.setUp(0.2);
            }
            super.renderAnimation(gl, cam, runningCnt, direction);
        }
        else
            super.render(gl, cam);
    }
    
    //method to stop minning
    public void stopMining(){
        onMineCommand = false;
        onBringResourcesBackCommand = false;
        targetMiningPatch = null;
        stopMoving();
        range = regularRange;
    }
    
    public void buildAt(int playerID, GameClient client, Building target){
        client.sendBuildCommand(owner.getID(), id, target.id, owner.getUsername());
    }
    
    //method to tell worker where to go build
    public void buildAt(Building building){
        stopMining();
        stopAttacking();
        onBuildCommand = true;
        targetBuilding = building;
        range = miningRange;
    }
    
    
    //method to tell worker to stop building
    public void stopBuilding(){
        onBuildCommand = false;
        targetBuilding = null;
        stopMoving();
        range = regularRange;
    }
    
    public void findNearesMiningBuilding(){
        //BFS
        //AQUI deberia ir una bfs
        ConcurrentHashMap<Integer, Building> currBuildings = owner.buildings;
        double distance = 10000000;
        for(Building build : currBuildings.values()){
            if(!build.created || !build.isAlive()) continue;
            if(build instanceof Castle){
                double currDist = position.distance(build.position);
                if(currDist < distance){
                    nearestMiningBuilding = build;
                    distance = currDist;
                }
            }
        }
    }
    
    @Override
    public void attackAt(Building buildingToAtack){
        stopMining();
        stopBuilding();
        super.attackAt(buildingToAtack);
    }
    
    @Override
    public void attackAt(Unit unitToAttack){
        stopMining();
        stopBuilding();
        super.attackAt(unitToAttack);
    }
    

    public boolean isBusy(){
        return onMoveCommand || onMineCommand || onBuildCommand || onAttackCommand;
    }
    
    public boolean isAnimated() {
        return animated;
    }
    
}