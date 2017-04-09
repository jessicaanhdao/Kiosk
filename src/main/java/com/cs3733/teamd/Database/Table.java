package com.cs3733.teamd.Database;

/**
 * Created by tom on 4/4/17.
 */
public enum Table {
    Nodes ("Node", "(\n" +
            " id INTEGER PRIMARY KEY generated by default as identity (START WITH 1000, INCREMENT BY 1),\n" +
            " x INTEGER NOT NULL,\n" +
            " y INTEGER NOT NULL,\n" +
            " floor INTEGER NOT NULL" +
            ")"),

    AdjacentNodes ("AdjacentNode", "(\n" +
            "  n1 INTEGER NOT NULL,\n" +
            "  n2 INTEGER NOT NULL\n" +
            ")"),

    Tags ("Tag", "(\n" +
            "id INTEGER PRIMARY KEY generated by default as identity (START WITH 1000, INCREMENT BY 1)," +
            "name VARCHAR(100)\n" +
            ")"),

    NodeTags ("NodeTag", "(\n" +
            "  nodeId INTEGER NOT NULL,\n" +
            "  tagId INTEGER NOT NULL\n" +
            ")"),





    HCPs ("HCP", "(\n" +
            "  id INTEGER PRIMARY KEY generated by default as identity (START WITH 1000, INCREMENT BY 1),\n" +
            "  lastName VARCHAR(25) NOT NULL\n" +
            ")"),

    HCPTags ("HCPTag", "(\n" +
            "  tagId INTEGER NOT NULL,\n" +
            "  hcpId INTEGER NOT NULL\n" +
            ")"),

    ProfessionalTitles ("ProTitle",  "(\n" +
            " id INTEGER PRIMARY KEY generated by default as identity (START WITH 1000, INCREMENT BY 1),\n" +
            "  acronym VARCHAR(5) NOT NULL,\n" +
            "  fullTitle VARCHAR(50) NOT NULL\n" +
            ")"),

    HCPTitles ("HCPTitle", "(\n" +
            "  hcpId INTEGER NOT NULL,\n" +
            "  titleId INTEGER NOT NULL\n" +
   ")" ),

    Users ("UserInfo", "(\n"+
        " id INTEGER PRIMARY KEY generated by default as identity (START WITH 1000, INCREMENT BY 1),\n" +
        " name VARCHAR(20) NOT NULL,\n" +
        " password VARCHAR(32) NOT NULL\n)"),

    UserRoles("UserRole","(\n" +
            " user_id INTEGER NOT NULL,\n" +
            " role VARCHAR(10) NOT NULL)");

    String name;
    String schema;
    Table(String name, String schema){
        this.name = name;
        this.schema = schema;
    }

    public String createStatement(){
        return "CREATE TABLE " + this.name + " " + this.schema;
    }

    public String selectAllStatement(){
        return "SELECT * FROM " + this.name;
    }

    public String emptyStatement(){
        return "DELETE FROM " + this.name;
    }

    public String dropStatement(){
        return "DROP TABLE " + this.name;
    }
}
