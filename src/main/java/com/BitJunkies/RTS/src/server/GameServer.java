/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.BitJunkies.RTS.src.server;

import com.BitJunkies.RTS.src.Game;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    // server tickrate in milis
    private static double tickrate = 30;
    private double tickTime = -1;
    
    // server command lists
    public Server server;
    public ArrayList<Connection> connectedPlayers;
    private ArrayList<MoveObject> movesIssued;
    private ArrayList<MineObject> minesIssued;
    private ArrayList<AttackObject> attacksIssued;
    private ArrayList<BuildObject> buildsIssued;
    private ArrayList<SpawnUnitObject> unitSpawnsIssued;
    private ArrayList<SpawnBuildingObject> buildingSpawnsIssued;
    
    private ArrayList<UnitInfoObject> playerUnitInfo;
    private ArrayList<BuildingInfoObject> playerBuildingInfo;
    private ArrayList<ResourceInfoObject> resourceInfo;
    private ArrayList<PlayerInfoObject> playerInfo;
    
    
    public GameServer() {
        connectedPlayers = new ArrayList<Connection>();
        movesIssued = new ArrayList<MoveObject>();
        minesIssued = new ArrayList<MineObject>();
        attacksIssued = new ArrayList<AttackObject>();
        buildsIssued = new ArrayList<BuildObject>();
        unitSpawnsIssued = new ArrayList<SpawnUnitObject>();
        buildingSpawnsIssued = new ArrayList<SpawnBuildingObject>();
        playerUnitInfo = new ArrayList<UnitInfoObject>();
        playerBuildingInfo = new ArrayList<BuildingInfoObject>();
        resourceInfo = new ArrayList<ResourceInfoObject>();
        playerInfo = new ArrayList<PlayerInfoObject>();

        Log.set(Log.LEVEL_ERROR);
 
        server = new Server();
        KryoUtil.registerServerClasses(server);
 
        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
            }
 
            @Override
            public void disconnected(Connection connection) {                
                for(int i = 0; i < connectedPlayers.size(); i++){
                    connection.sendUDP(new DisconnectionObject(connectedPlayers.get(i).toString()));
                    connectedPlayers.get(i).sendUDP(new DisconnectionObject(connection.toString()));
                }
                connectedPlayers.remove(connection);
            }
 
            @Override
            public void received(Connection connection, Object object) {
                
                if(object instanceof String){
                    connection.setName((String) object);
                    connectedPlayers.add(connection);
                    for(int i = 0; i < connectedPlayers.size(); i++){
                        if(connectedPlayers.get(i) != connection)
                            connection.sendUDP(new ConnectionObject(connectedPlayers.get(i).getID(), connectedPlayers.get(i).toString(), connectedPlayers.get(i).getRemoteAddressUDP().getHostString(), false));
                        connectedPlayers.get(i).sendUDP(new ConnectionObject(connection.getID(), connection.toString(), connection.getRemoteAddressUDP().getHostString(), connectedPlayers.get(i) == connection));
                    }
                    
                }
                
                else if (object instanceof MoveObject) {
                    movesIssued.add((MoveObject) object);
                }
                
                else if (object instanceof MineObject) {
                    minesIssued.add((MineObject) object);
                }
                
                else if (object instanceof AttackObject) {
                    attacksIssued.add((AttackObject) object);
                }
                
                else if (object instanceof BuildObject) {
                    buildsIssued.add((BuildObject) object);
                }
                
                else if (object instanceof SpawnUnitObject) {
                    unitSpawnsIssued.add((SpawnUnitObject) object);
                }
                
                else if (object instanceof SpawnBuildingObject) {
                    buildingSpawnsIssued.add((SpawnBuildingObject) object);
                }
                
                else if (object instanceof StartMatchObject) {
                    for(int i = 0; i < connectedPlayers.size(); i++){
                        connectedPlayers.get(i).sendUDP((StartMatchObject)object);
                    }
                }
                
                else if (object instanceof UnitInfoObject) {
                    playerUnitInfo.add((UnitInfoObject) object);
                }
                
                else if (object instanceof BuildingInfoObject) {
                    playerBuildingInfo.add((BuildingInfoObject) object);
                }
                
                else if (object instanceof PlayerInfoObject) {
                    playerInfo.add((PlayerInfoObject) object);
                }
                
                else if (object instanceof SpendRubysObject) {
                    Game.spendRubys((SpendRubysObject) object);
                }
                
                else if (object instanceof ResourceInfoObject) {
                    resourceInfo.add((ResourceInfoObject) object);
                }
                
                else if(object instanceof DisconnectionObject){
                    for(int i = 0; i < connectedPlayers.size(); i++){
                        connectedPlayers.get(i).sendUDP((DisconnectionObject) object);
                    }
                }
            }
        });
 
        try {
            server.bind(KryoUtil.TCP_PORT, KryoUtil.UDP_PORT);
        } catch (IOException ex) {
            System.out.println(ex);
        }
 
        server.start();
    }
    
    public void tick(){
        double delta = System.currentTimeMillis() - tickTime;
        if(tickTime == -1 || delta >= tickrate){
            
            for(int i = 0; i < unitSpawnsIssued.size(); i++){
                for(int j = 0; j < server.getConnections().length; j++){
                    server.getConnections()[j].sendUDP(unitSpawnsIssued.get(i));
                }
                unitSpawnsIssued.remove(i);
                i--;
            }
            
            for(int i = 0; i < buildingSpawnsIssued.size(); i++){
                for(int j = 0; j < server.getConnections().length; j++){
                    server.getConnections()[j].sendUDP(buildingSpawnsIssued.get(i));
                }
                buildingSpawnsIssued.remove(i);
                i--;
            }
            
            for(int i = 0; i < movesIssued.size(); i++){
                for(int j = 0; j < server.getConnections().length; j++){
                    server.getConnections()[j].sendUDP(movesIssued.get(i));
                }
                movesIssued.remove(i);
                i--;
            }
            
            for(int i = 0; i < minesIssued.size(); i++){
                for(int j = 0; j < server.getConnections().length; j++){
                    server.getConnections()[j].sendUDP(minesIssued.get(i));
                }
                minesIssued.remove(i);
                i--;
            }
            
            for(int i = 0; i < attacksIssued.size(); i++){
                for(int j = 0; j < server.getConnections().length; j++){
                    server.getConnections()[j].sendUDP(attacksIssued.get(i));
                }
                attacksIssued.remove(i);
                i--;
            }
            
            for(int i = 0; i < buildsIssued.size(); i++){
                for(int j = 0; j < server.getConnections().length; j++){
                    server.getConnections()[j].sendUDP(buildsIssued.get(i));
                }
                buildsIssued.remove(i);
                i--;
            }
            
            for(int i = 0; i < playerUnitInfo.size(); i++){
                for(int j = 0; j < server.getConnections().length; j++){
                    server.getConnections()[j].sendUDP(playerUnitInfo.get(i));
                }
                playerUnitInfo.remove(i);
                i--;
            }
            
            for(int i = 0; i < playerBuildingInfo.size(); i++){
                for(int j = 0; j < server.getConnections().length; j++){
                    server.getConnections()[j].sendUDP(playerBuildingInfo.get(i));
                }
                playerBuildingInfo.remove(i);
                i--;
            }
            
            for(int i = 0; i < playerInfo.size(); i++){
                for(int j = 0; j < server.getConnections().length; j++){
                    server.getConnections()[j].sendUDP(playerInfo.get(i));
                }
                playerInfo.remove(i);
                i--;
            }
            
            for(int i = 0; i < resourceInfo.size(); i++){
                for(int j = 0; j < server.getConnections().length; j++){
                    server.getConnections()[j].sendUDP(resourceInfo.get(i));
                }
                resourceInfo.remove(i);
                i--;
            }
            
           
            tickTime = System.currentTimeMillis();
        }
        
    }
    
    public void resetLobby(){
        for(int i = 0; i < connectedPlayers.size(); i++){
            connectedPlayers.get(i).sendUDP(new ResetLobbyObject());
            for(int j = 0; j < connectedPlayers.size(); j++){
                connectedPlayers.get(i).sendUDP(new ConnectionObject(connectedPlayers.get(j).getID(), connectedPlayers.get(j).toString(), connectedPlayers.get(j).getRemoteAddressUDP().getHostString(), connectedPlayers.get(i) == connectedPlayers.get(j)));
            }       
        }
    }
    
    public String getIP(){
        if(server.getConnections().length > 0)
            return server.getConnections()[0].getRemoteAddressUDP().getHostString();
        else return "NO IP";
    }
}