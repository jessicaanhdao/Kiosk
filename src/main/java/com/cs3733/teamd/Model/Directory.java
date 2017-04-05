package com.cs3733.teamd.Model;

import com.cs3733.teamd.Database.DBHandler;
import com.cs3733.teamd.Model.Node;
import com.cs3733.teamd.Model.Professional;
import com.cs3733.teamd.Model.Tag;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Used by CONTROLLERS to store ALL ENTITIES
 * Knows the DB Connection so it can SAVE ENTITIES
 */
public class Directory {

    private static Directory dir = new Directory();

    public static Directory getInstance(){
        return dir;
    }

    private ArrayList<Node> allNodes = new ArrayList<Node>();
    private ArrayList<Tag> allTags = new ArrayList<Tag>();
    private ArrayList<Professional> allProfs = new ArrayList<Professional>();
    private DBHandler dbHandler;

    private Directory( ){}

    public void initialize(ArrayList<Node> nodes, ArrayList<Tag> tags, ArrayList<Professional> profs, DBHandler dbHandler){
        allNodes = nodes;
        allTags = tags;
        allProfs = profs;
        this.dbHandler = dbHandler;
    }

    public ArrayList<Tag> getAllTags() {
        return allTags;
    }

    public ArrayList<Node> getAllNodes() {
        return allNodes;
    }

    public ArrayList<Professional> getAllProfs() {
        return allProfs;
    }

    public void notifyUpdate() {
        try {
            dbHandler.save();
        } catch (SQLException e) {
            System.out.println("UNABLE TO PERSIST TO DATABASE");
            e.printStackTrace();
        }
    }


    public void createNewTag(String tagName){
        Tag newTag = new Tag(tagName);
        allTags.add(newTag);
        //SAVE TO DATABASE
        notifyUpdate();
    }
    public void createNewNode(int x, int y){
        Node newNode = new Node(x,y);
        //newNode.setID(dbHandler.generateKeyForNode());
        allNodes.add(newNode);
        //SAVE TO DATABASE
        notifyUpdate();
    }
    public void creaNewProf(String name, ArrayList<Title> titles){
        Professional newProf = new Professional(name,titles);
        //newProf.setID(dbHandler.generateKeyForProf());
        allProfs.add(newProf);
        //SAVE TO DATABASE
        notifyUpdate();
    }
    public void deleteTag(Tag t){
        allTags.remove(t);
        //REMOVE FROM DATABSE
        notifyUpdate();
    }
    public void deleteNode(Node n){
        allNodes.remove(n);
        //REMOVE FROM DATABASE
        notifyUpdate();
    }
    public void deleteProf(Professional p){
        allProfs.remove(p);
        //REMOVE FROM DATABASE
        notifyUpdate();
    }

}