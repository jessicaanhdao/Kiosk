package com.cs3733.teamd.Model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

/**
 * Created by Stephen on 4/25/2017.
 */
public class HospitalLoader {
    private static HospitalLoader instance = null;
    private JSONParser parser;

    private JSONObject root;

    private HospitalLoader() {
        parser = new JSONParser();
    }


    public static HospitalLoader getInstance() {
        if(instance == null) {
            instance = new HospitalLoader();
        }
        return instance;
    }

    private JSONArray loadHospitalsObject() {
        try {
            String filename = getClass().getClassLoader().getResource("hospitals/hospitals.json").getFile();
            System.out.println(filename);
            File f2 = new File(filename);
            FileReader f = new FileReader(f2.getAbsolutePath());
            Object o = parser.parse(f);
            root = (JSONObject) o;
            JSONArray hospitalsJson = (JSONArray) root.get("hospitals");
            return hospitalsJson;
        } catch(ParseException pe) {
            pe.printStackTrace();
            return null;
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
            return null;
        } catch (IOException ie) {
            ie.printStackTrace();
            return null;
        }
    }

    public List<String> loadHospitals() {

        JSONArray hospitalsJson = loadHospitalsObject();
        if(hospitalsJson == null) {
            return null;
        }
        Iterator<Object> it = hospitalsJson.iterator();
        List<String> ret = new ArrayList<String>();
        while(it.hasNext()) {
            JSONObject hospitalJson = (JSONObject)it.next();
            ret.add((String)hospitalJson.get("hospitalId"));
        }
        return ret;
    }

    public Hospital loadHospitalFromId(String id) {
        JSONArray hospitalsJson = loadHospitalsObject();
        if(hospitalsJson == null) {
            return null;
        }

        Iterator<Object> it = hospitalsJson.iterator();

        while(it.hasNext()) {
            JSONObject hospitalJson = (JSONObject)it.next();
            System.out.println((String)hospitalJson.get("hospitalId"));
            if(((String)hospitalJson.get("hospitalId")).compareTo(id) == 0) {
                String name = (String)hospitalJson.get("name");
                String hospitalId = id;
                Integer dbVersion = ((Long)hospitalJson.get("dbVersion")).intValue();
                JSONArray floors = (JSONArray)hospitalJson.get("floors");
                Map<Integer, String> floorFiles = new HashMap<Integer, String>();
                Iterator<Object> it2 = floors.iterator();
                while(it2.hasNext()) {
                    JSONObject floorsJson = (JSONObject)it2.next();
                    floorFiles.put(((Long)floorsJson.get("number")).intValue(), (String)floorsJson.get("file"));
                }
                return new Hospital(name, hospitalId, dbVersion, floorFiles);
            }
        }
        return null;
    }

    public boolean saveHospital(Hospital h) {
        JSONArray hospitalsJson = loadHospitalsObject();
        if(hospitalsJson == null) {
            return false;
        }

        Iterator<Object> it = hospitalsJson.iterator();

        while(it.hasNext()) {
            JSONObject hospitalJson = (JSONObject)it.next();
            if(((String)hospitalJson.get("hospitalId")).compareTo(h.getHospitalId()) == 0) {
                hospitalJson.put("dbVersion",(Long)h.getDbVersion().longValue());
                System.out.println(root.toJSONString());
                // try-with-resources statement based on post comment below :)
                String filename = getClass().getClassLoader().getResource("hospitals/hospitals.json").getFile();
                System.out.println(filename);
                File f2 = new File(filename);
                try (FileWriter file = new FileWriter(f2.getAbsoluteFile())) {
                    file.write(root.toJSONString());
                    System.out.println("Successfully Copied JSON Object to File...");
                    System.out.println("\nJSON Object: " + root);
                    return true;
                } catch (IOException e) {
                    return false;
                }

            }
        }
        return false;
    }
}