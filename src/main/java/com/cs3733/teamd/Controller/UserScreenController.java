package com.cs3733.teamd.Controller;

import com.cs3733.teamd.Controller.IterationOne.MapDirectionsController;
import com.cs3733.teamd.Main;
import com.cs3733.teamd.Model.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import org.controlsfx.control.textfield.TextFields;

//import javax.xml.soap.Text;

import java.awt.Point;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.List;

/**
 * Created by Anh Dao on 4/6/2017.
 */
public class UserScreenController extends AbsController{
    Directory dir = Directory.getInstance();
    public Button LoginButton;
    public Button SpanishButton;
    public Button SearchButton;
    public TextField TypeDestination;
    public Text EnterDest;
    public Text floor;
    public Label directionLabel;
    @FXML
    private Slider floorSlider;

    public ImageView floorMap;

    //proxy pattern
    ImageInterface imgInt = new ProxyImage();
    public int floorNum =4;

    public Canvas MapCanvas;
    public int imageW = 1091;
    public int imageH = 693;
    public double scale = 8.4;
    public int offset_x = 160*12;
    public int offset_y = 80*12;
    public AnchorPane MMGpane;
    @FXML
    private TextArea directions;
    public GraphicsContext gc;
    private static LinkedList<Node> pathNodes;
    private List<Tag> nodeList = dir.getTags();

    int onFloor = 4;

    String output = "";
    @FXML private void initialize()
    {
        TextFields.bindAutoCompletion(TypeDestination,nodeList);
        setText();
        gc = MapCanvas.getGraphicsContext2D();
        if(pathNodes != null) {
            draw();
        }

        directions.setText(output);
        floorMap.setImage(imgInt.display(floorNum));

        floorSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                if (!floorSlider.isValueChanging()) {
                    onFloor = new_val.intValue();
                    floorSlider.setValue(onFloor);
                    //floorMap.setImage(imageHashMap.get(onFloor));
                    floorMap.setImage(imgInt.display(onFloor));

                }
            }
        });
    }


    //Spanish button to change language to Spanish
    @FXML
    public void onSpanish(ActionEvent actionEvent) throws  IOException{
        //TODO : CHANGE INTO SWITCH STATEMENT FOR MULTIPLE LANGUAGES
        if(Main.Langugage == "English") {
            Main.Langugage = "Spanish";
            Main.bundle = ResourceBundle.getBundle("MyLabels", Main.spanish);
        }
        else{
            Main.Langugage = "English";

            Main.bundle = ResourceBundle.getBundle("MyLabels", Main.local);
        }

        switchScreen(MMGpane,"/Views/UserScreen.fxml");

        setText();
    }

    @FXML
    public void onLogin(ActionEvent actionEvent) throws IOException {
        switchScreen(MMGpane, "/Views/LoginScreen.fxml");
    }

    //Spanish translation
    public void setText(){
        SpanishButton.setText(Main.bundle.getString("spanish"));
        SearchButton.setText(Main.bundle.getString("search"));
        LoginButton.setText(Main.bundle.getString("login"));
        directionLabel.setText(Main.bundle.getString("directions"));
        EnterDest.setText(Main.bundle.getString("enterDes"));
        floor.setText(Main.bundle.getString("floor"));

        if(Main.Langugage =="Spanish"){
            LoginButton.setFont(Font.font("System",14));
        }
        else{
            LoginButton.setFont(Font.font("System",20));
        }

    }

    //Search button, generates path and directions on submit
    @FXML
    public void onSearch(ActionEvent actionEvent) throws IOException{
        //stores the destination inputted
        Main.DestinationSelected = TypeDestination.getText();
        //Gets nodes and tags from directory
        int numtags = dir.getTags().size();
        int numnodes = dir.getNodes().size();
        //Makes a temporary holder for values
        Tag curtag;
        //Iterates through all existing tags
        for(int itr = 0; itr < numtags; itr++){
            curtag = dir.getTags().get(itr);
            //If match is found create path to node from start nodes
            if(Main.DestinationSelected.equals(curtag.getTagName())){
                Pathfinder pathfinder =
                        //will get updated to actually be the starting node
                        new Pathfinder(dir.getNodes().get(numnodes-1),
                                curtag.getNodes().getFirst());
                //use the shortest path
                pathNodes = pathfinder.shortestPath();
            }

        }
        //Refresh page with path
        switchScreen(MMGpane, "/Views/UserScreen.fxml");
    }

    @FXML
    //Starts path displaying process
    private void draw(){
        plotPath(UserScreenController.pathNodes);
    }

    //Converts a node to a point to display on map
    private Point getConvertedPoint(Node node) { //conversion from database to canvas
        int x = node.getX();
        int y = node.getY();
        Point p = new Point((int) ((x-offset_x)/scale), (int) (imageH-(y-offset_y)/scale));
        return p;
    }

    //Converts a given path of nodes to a path of points and then draws it
    public void plotPath(LinkedList<Node> path){
        LinkedList<Point> points = new LinkedList<>();
        for (Node node: path) {
            points.add(getConvertedPoint(node));
        }
        drawShapes(gc, points);
    }

    //Function to actually draw a path
    private void drawShapes(GraphicsContext gc, LinkedList<Point> path) {
        //color for start node
        gc.setFill(javafx.scene.paint.Color.GREEN);
        //color for edges
        gc.setStroke(javafx.scene.paint.Color.BLUE);
        //intialize point previous as null due to start not having a previous
        Point previous = null;
        //Create a list to hold text directions
        LinkedList<String> TextDirections = new LinkedList<String>();
        //Set line width
        gc.setLineWidth(3);
        //Saving the length of the path
        int pathlength = path.size();
        //holder for straight indicator
        String curdir = "";
        //Generate placeholders for text directions
        for (int str = 0; str < pathlength; str++){
            TextDirections.add("");
        }
        //Set radius of first node
        int radius = 7;

        //Iterate through the path
        for  (int i = 0; i < pathlength; i++){
            //get the point for current iterations
            Point current = path.get(i);
            //If it is the last point in the path
            if(i == pathlength-1){
                radius = 7;
                gc.setFill(javafx.scene.paint.Color.RED);
            }
            //draw the point
            gc.fillOval(current.getX(), current.getY(), radius*2, radius*2);
            //draw line from previous to current
            if(previous != null){
                gc.strokeLine(previous.getX() + radius, previous.getY() + radius,
                        current.getX() + radius, current.getY() + radius);
            }


            //first point directions
            if(i == 0){
                String temp = "Starting at and facing the kiosk " + "\n";
                TextDirections.set(i, temp);
            }
            // every point between first and second to last
            if(i > 0 && i+2 < pathlength) {
                //Assign point
                Point oldnode = path.get(i - 1);
                Point currnode = path.get(i);
                Point nextnode = path.get(i + 1);
                //Run helper functions to update text
                TextDirections = getText(oldnode, currnode, nextnode, curdir, TextDirections, i);
                curdir = setCurdir(oldnode, currnode, nextnode, curdir, i);
            }
            // second to last point
            if(i == pathlength - 2) {
                //Assign point
                Point oldnode = path.get(i - 1);
                Point currnode = path.get(i);
                Point nextnode = path.get(i + 1);
                //Run helper functions to update text
                TextDirections = getTextMidHallway(oldnode, currnode, nextnode, curdir, TextDirections, i);
                curdir = setCurdir(oldnode, currnode, nextnode, curdir, i);
            }
            //last point
            if(i == pathlength -1){
                String temp = "Ending at " + Main.DestinationSelected;
                TextDirections.set(i, temp);
            }

            //Update for next loop
            previous = current;
            //Set intermediate points to be smaller and blue
            gc.setFill(javafx.scene.paint.Color.BLUE);
            radius = 5;
        }

        //iterate through to find directions that are not blanks
        for( int stritr = 0; stritr < pathlength; stritr++){
            String tempstr;
            //Make text directions without blanks
            if(stritr == 0){
                tempstr  = TextDirections.get(stritr);
                if(tempstr != "i"){
                    output = output + tempstr;
                }
            }
            if(stritr > 0 && stritr < pathlength - 1) {
                tempstr = TextDirections.get(stritr);
                if (tempstr != "i") {
                    output = output + tempstr;
                }
            }
            if(stritr == pathlength-1){
                tempstr  = TextDirections.get(stritr);
                if(tempstr != "i"){
                    output = output + tempstr;
                }
            }
        }
        // print output to show path directions
        System.out.println(output);
    }

    //helper function to determine if the change in x direction if more significant than the change in y direction
    public int moresig(double x, double y){
        //System.out.println("Xvalue = " + x + " Yvalue = " + y);
        if(x >= 0){
            if(y >= 0){
                if(x >= y){
                    return 1; // X more signifcant
                }
                if(y > x){
                    return 2; // Ymore significant
                }
            }
            if(y < 0){
                if(x >= y*-1){
                    return 1; // X more signifcant
                }
                if(y*-1 > x){
                    return 2; // Y more signifcant
                }
            }
        }
        if(x < 0){
            if(y >= 0){
                if(x*-1 >= y){
                    return 1; // X more signifcant
                }
                if(y > x*-1){
                    return 2; // Y more signifcant
                }
            }
            if(y < 0){
                if(x*-1 >= y*-1){
                    return 1; // X more signifcant
                }
                if(y*-1 > x*-1){
                    return 2; // Y more signifcant
                }
            }
        }
        return 0; // Doesn't occur
    }

    //Helper function to generate text for most cases
    private LinkedList<String> getText(Point oldnode, Point currnode, Point nextnode,
                                       String curdir, LinkedList<String> TextDirections, int i ){
        //Get values
        double oldnodex = oldnode.getX();
        double oldnodey = oldnode.getY();
        double currentnodex = currnode.getX();
        double currentnodey = currnode.getY();
        double nextnodex = nextnode.getX();
        double nextnodey = nextnode.getY();

        //Get transitions
        double oldcurx = currentnodex - oldnodex;
        double oldcury = currentnodey - oldnodey;
        double curnextx = nextnodex - currentnodex;
        double curnexty = nextnodey - currentnodey;

        double ocmorsig = 0;
        double cnmorsig = 0;

        //Get orientations
        ocmorsig = moresig(oldcurx, oldcury);
        cnmorsig = moresig(curnextx, curnexty);

        // ocx more significant than ocy
        if(ocmorsig == 1){
            // cnx more significant than cny
            if(cnmorsig == 1){
                // oc is movement to the right
                if(oldcurx >= 0){
                    // cn is movement to the right
                    if(curnextx >= 0){
                        if(curdir != "Straight"){
                            curdir = "Straight";
                            TextDirections.set(i, "Go straight until end of hallway " + "\n");
                        }
                    }
                    // cn is movement to the left
                    if(curnextx < 0){
                        // turn around case shouldnt activate
                    }
                }
                // oc is movement to the left
                if(oldcurx < 0){
                    // cn is movement to the right
                    if(curnextx >= 0){
                        //turn around case
                    }
                    // cn is movement to the left
                    if(curnextx < 0){
                        if(curdir != "Straight"){
                            curdir = "Straight";
                            TextDirections.set(i, "Go straight until end of hallway " + "\n");
                        }
                    }
                }
            }
            // cny more significant than cnx
            if(cnmorsig == 2){
                // oc is movement to the right
                if(oldcurx >= 0){
                    // cn is movement upwards
                    if(curnexty >= 0){
                        curdir = "";
                        TextDirections.set(i, "Turn right " + "\n");
                    }
                    // cn is movement downwards
                    if(curnexty < 0){
                        curdir = "";
                        TextDirections.set(i, "Turn left " + "\n");
                    }
                }
                // oc is movement to the left
                if(oldcurx < 0){
                    // cn is movement upwards
                    if(curnexty >= 0){
                        curdir = "";
                        TextDirections.set(i, "Turn left " + "\n");
                    }
                    // cn is movement downwards
                    if(curnexty < 0){
                        curdir = "";
                        TextDirections.set(i, "Turn right " + "\n");
                    }
                }
            }
        }

        // ocy more significant than ocx
        if(ocmorsig == 2){
            // cnx more significant than cny
            if(cnmorsig == 1){
                // oc is movement upwards
                if(oldcury >= 0){
                    // cn is movement to the right
                    if(curnextx >= 0){
                        curdir = "";
                        TextDirections.set(i, "Turn left " + "\n");
                    }
                    // cn is movement to the left
                    if(curnextx < 0){
                        curdir = "";
                        TextDirections.set(i, "Turn right " + "\n");
                    }
                }
                // oc is movement downwards
                if(oldcury < 0){
                    // ocn is movement to the right
                    if(curnextx >= 0){
                        curdir = "";
                        TextDirections.set(i, "Turn right " + "\n");
                    }
                    // cn is movement to the left
                    if(curnextx < 0){
                        curdir = "";
                        TextDirections.set(i, "Turn left " + "\n");
                    }
                }
            }
            // cny more significant than cnx
            if(cnmorsig == 2){
                // oc is movement upwards
                if(oldcury >= 0){
                    // cn is movement downwards
                    if(curnexty < 0){
                        // turn around case
                    }
                    // cn is movement upwards
                    if(curnexty >= 0){
                        if(curdir != "Straight"){
                            curdir = "Straight";
                            TextDirections.set(i, "Go straight until end of hallway " + "\n");
                        }
                    }
                }
                // oc is movement downwards
                if(oldcury < 0){
                    // cn is movement downwards
                    if(curnexty < 0){
                        if(curdir != "Straight"){
                            curdir = "Straight";
                            TextDirections.set(i, "Go straight until end of hallway " + "\n");
                        }
                    }
                    // cn is movement upwards
                    if(curnexty >= 0){
                        //turn around case
                    }
                }
            }
        }
        //Return the updated text directions
        return TextDirections;
    }

    //Helper function to generate text for case of reaching an elevator or the destination
    private LinkedList<String> getTextMidHallway(Point oldnode, Point currnode, Point nextnode,
                                                 String curdir, LinkedList<String> TextDirections, int i ){
        double oldnodex = oldnode.getX();
        double oldnodey = oldnode.getY();
        double currentnodex = currnode.getX();
        double currentnodey = currnode.getY();
        double nextnodex = nextnode.getX();
        double nextnodey = nextnode.getY();

        double oldcurx = currentnodex - oldnodex;
        double oldcury = currentnodey - oldnodey;
        double curnextx = nextnodex - currentnodex;
        double curnexty = nextnodey - currentnodey;

        double ocmorsig = 0;
        double cnmorsig = 0;

        ocmorsig = moresig(oldcurx, oldcury);
        cnmorsig = moresig(curnextx, curnexty);

        // ocx more significant than ocy
        if(ocmorsig == 1){
            // cnx more significant than cny
            if(cnmorsig == 1){
                // shouldnt happen in node layout
            }
            // cny more significant than cnx
            if(cnmorsig == 2){
                // oc is movement to the right
                if(oldcurx >= 0){
                    // cn is movement upwards
                    if(curnexty >= 0){
                        curdir = "";
                        TextDirections.set(i, "Turn right in current hallway " + "\n");
                    }
                    // cn is movement downwards
                    if(curnexty < 0){
                        curdir = "";
                        TextDirections.set(i, "Turn left in current hallway " + "\n");
                    }
                }
                // oc is movement to the left
                if(oldcurx < 0){
                    // cn is movement upwards
                    if(curnexty >= 0){
                        curdir = "";
                        TextDirections.set(i, "Turn left in current hallway " + "\n");
                    }
                    // cn is movement downwards
                    if(curnexty < 0){
                        curdir = "";
                        TextDirections.set(i, "Turn right in current hallway " + "\n");
                    }
                }
            }
        }

        // ocy more significant than ocx
        if(ocmorsig == 2){
            // cnx more significant than cny
            if(cnmorsig == 1){
                // oc is movement upwards
                if(oldcury >= 0){
                    // cn is movement to the right
                    if(curnextx >= 0){
                        curdir = "";
                        TextDirections.set(i, "Turn left in current hallway " + "\n");
                    }
                    // cn is movement to the left
                    if(curnextx < 0){
                        curdir = "";
                        TextDirections.set(i, "Turn right in current hallway " + "\n");
                    }
                }
                // oc is movement downwards
                if(oldcury < 0){
                    // ocn is movement to the right
                    if(curnextx >= 0){
                        curdir = "";
                        TextDirections.set(i, "Turn right in current hallway " + "\n");
                    }
                    // cn is movement to the left
                    if(curnextx < 0){
                        curdir = "";
                        TextDirections.set(i, "Turn left in current hallway " + "\n");
                    }
                }
            }
            // cny more significant than cnx
            if(cnmorsig == 2){
                //shouldnt happen due to node layout
            }
        }
        return TextDirections;
    }

    //Helper function to update curdir for other functions
    private String setCurdir(Point oldnode, Point currnode, Point nextnode,
                             String curdir, int i){
        double oldnodex = oldnode.getX();
        double oldnodey = oldnode.getY();
        double currentnodex = currnode.getX();
        double currentnodey = currnode.getY();
        double nextnodex = nextnode.getX();
        double nextnodey = nextnode.getY();

        double oldcurx = currentnodex - oldnodex;
        double oldcury = currentnodey - oldnodey;
        double curnextx = nextnodex - currentnodex;
        double curnexty = nextnodey - currentnodey;

        double ocmorsig = 0;
        double cnmorsig = 0;

        ocmorsig = moresig(oldcurx, oldcury);
        cnmorsig = moresig(curnextx, curnexty);

        // ocx more significant than ocy
        if(ocmorsig == 1){
            // cnx more significant than cny
            if(cnmorsig == 1){
                // oc is movement to the right
                if(oldcurx >= 0){
                    // cn is movement to the right
                    if(curnextx >= 0){
                        if(curdir != "Straight"){
                            curdir = "Straight";
                        }
                    }
                    // cn is movement to the left
                    if(curnextx < 0){
                        // turn around case shouldnt activate
                    }
                }
                // oc is movement to the left
                if(oldcurx < 0){
                    // cn is movement to the right
                    if(curnextx >= 0){
                        //turn around case
                    }
                    // cn is movement to the left
                    if(curnextx < 0){
                        if(curdir != "Straight"){
                            curdir = "Straight";
                        }
                    }
                }
            }
            // cny more significant than cnx
            if(cnmorsig == 2){
                // oc is movement to the right
                if(oldcurx >= 0){
                    // cn is movement upwards
                    if(curnexty >= 0){
                        curdir = "";
                    }
                    // cn is movement downwards
                    if(curnexty < 0){
                        curdir = "";
                    }
                }
                // oc is movement to the left
                if(oldcurx < 0){
                    // cn is movement upwards
                    if(curnexty >= 0){
                        curdir = "";
                    }
                    // cn is movement downwards
                    if(curnexty < 0){
                        curdir = "";
                    }
                }
            }
        }

        // ocy more significant than ocx
        if(ocmorsig == 2){
            // cnx more significant than cny
            if(cnmorsig == 1){
                // oc is movement upwards
                if(oldcury >= 0){
                    // cn is movement to the right
                    if(curnextx >= 0){
                        curdir = "";
                    }
                    // cn is movement to the left
                    if(curnextx < 0){
                        curdir = "";
                    }
                }
                // oc is movement downwards
                if(oldcury < 0){
                    // ocn is movement to the right
                    if(curnextx >= 0){
                        curdir = "";
                    }
                    // cn is movement to the left
                    if(curnextx < 0){
                        curdir = "";
                    }
                }
            }
            // cny more significant than cnx
            if(cnmorsig == 2){
                // oc is movement upwards
                if(oldcury >= 0){
                    // cn is movement downwards
                    if(curnexty < 0){
                        // turn around case
                    }
                    // cn is movement upwards
                    if(curnexty >= 0){
                        if(curdir != "Straight"){
                            curdir = "Straight";
                        }
                    }
                }
                // oc is movement downwards
                if(oldcury < 0){
                    // cn is movement downwards
                    if(curnexty < 0){
                        if(curdir != "Straight"){
                            curdir = "Straight";
                        }
                    }
                    // cn is movement upwards
                    if(curnexty >= 0){
                        //turn around case
                    }
                }
            }
        }
        return curdir;
    }
}
