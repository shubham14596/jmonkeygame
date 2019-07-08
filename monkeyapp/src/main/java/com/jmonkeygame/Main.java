package com.jmonkeygame;

import android.util.Log;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.TouchInput;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

import java.util.Vector;
import java.util.logging.Logger;

/**
 * Created by potterec on 3/17/2016.
 */
public class Main extends SimpleApplication {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
private float lastX=0, lastY = 0;
    private Node bluen;
    private BitmapText ch;
    private Vector3f loc;
    private Node pivot;
    private CollisionResults results;
    private Geometry closest;
    private Node last;
    private boolean drag=false;

    public void simpleUpdate(float tpf){
        guiNode.getChild(0).setLocalTranslation(cam.getScreenCoordinates(new Vector3f(0, -2, 0)));

    }

    private void initKeys() {
        // You can map one or several inputs to one named action
        inputManager.addMapping("Pick",  new TouchTrigger(TouchInput.ALL));
        // Add the names to the action listener.
        inputManager.addListener(touchListener, "Pick");

    }

    private final TouchListener touchListener = new TouchListener() {

        @Override
        public void onTouch(String name, TouchEvent evt, float tpf) {
            results = new CollisionResults();
            Vector2f click2d = inputManager.getCursorPosition().clone();
            Vector3f click3d = cam.getWorldCoordinates(
                    click2d, 0f).clone();
            Vector3f dir = cam.getWorldCoordinates(
                    click2d, 1f).subtractLocal(click3d).normalizeLocal();
            Ray ray = new Ray(click3d, dir);
            pivot.collideWith(ray,results);
            closest = results.getClosestCollision().getGeometry();
            Node parent = closest.getParent();
            switch(evt.getType())
            {
            case MOVE:
                ch.setText("Move");
                if (results.size() > 0 && closest.getName()=="Box") {

                    loc = closest.getParent().getLocalTranslation();
//                    ch.setText("" + (evt.getDeltaX()) / 10 + " " + (evt.getDeltaY()) / 10);
                    closest.getParent().setLocalTranslation(loc.x + (evt.getDeltaX()) / 100, loc.y + (evt.getDeltaY()) / 100, 2);
                }
                drag = true;
//                float p = evt.getPressure();
                break;

            case UP:
                ch.setText("UP");
                if (results.size() > 0 && closest.getName()=="Box") {
                    loc = parent.getLocalTranslation();
                    if(drag==true) {
                        parent.setLocalTranslation(loc.x, loc.y, 0);
                        drag = false;
                    }
                    else
                        parent.setLocalTranslation(loc.x,loc.y,2-loc.z);
                }
                break;
            case DOWN:

                break;
            case TAP:
                ch.setText("Tap");
                drag=false;
//                if (results.size() > 0 && closest.getName()=="Box") {
//                    loc = parent.getLocalTranslation();
//                    float z = loc.z;
//                    parent.setLocalTranslation(loc.x, loc.y, 2-z);
////                    ch.setText(""+parent.getLocalTranslation().z);
//                    last = parent;
//                }
                break;

            }
//            if (name.equals("Pick")) {

//                blue.setLocalTranslation(0,-1, 1);
//                Log.i("test pick",""+blue.getLocalTranslation());
//            }
        }
    };

    public void simpleInitApp() {
        setDisplayStatView(false);
        flyCam.setEnabled(false);
        initKeys();

        Geometry mainwb = getWireBox(new Vector3f(0,0,0),0.5f,0.3f,0.25f,ColorRGBA.Black);
        Material mat2 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        Geometry r = new Geometry("Right", (new Box(0f,0.3f,0.25f)));
        Geometry l = new Geometry("Left", new Box(0f, -0.3f, 0.25f));
        Geometry f = new Geometry("Far", new Box(0.5f,0f,0.25f));
        Geometry n = new Geometry("Near", new Box(0.5f,0f,0.25f));
        r.setMaterial(mat2);
        l.setMaterial(mat2);
        f.setMaterial(mat2);
        n.setMaterial(mat2);

        r.setLocalTranslation(-0.5f,0,0);
        l.setLocalTranslation(0.5f,0,0);
        f.setLocalTranslation(0,0.3f,0);
        n.setLocalTranslation(0,-0.3f,0);
        Node mainbox = new Node("mainbox");
        mainbox.attachChild(r);
        mainbox.attachChild(l);
        mainbox.attachChild(f);
        mainbox.attachChild(n);
        mainbox.attachChild(mainwb);
        mainbox.setLocalTranslation(0,2,0);
//Mesh m = new Mesh();
        Quad q = new Quad(0.5f, 1f);

        Box box1 = new Box(1,1,1);
        Geometry blue = new Geometry("Box", box1);
        blue.setLocalTranslation(new Vector3f(0,0,0));
        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        blue.setMaterial(mat1);
        Geometry blueg = getWireBox(new Vector3f(0,0,0),1,1,1,ColorRGBA.Black, "Box");

        Node bluen = new Node("Piece");
        bluen.attachChild(blueg);
        bluen.attachChild(blue);
        bluen.setLocalTranslation(0,-1,0);

        
        Box base = new Box(3,5,0f);
        Material basemat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        Geometry grey = new Geometry("Base", base);
        grey.setLocalTranslation(new Vector3f(0,0,-1));
        basemat.setColor("Color", ColorRGBA.Gray);
        grey.setMaterial(basemat);

        /** create a red box straight above the blue one at (1,3,1) */
//        Box box2 = new Box(1,0.35f,1);
//        Geometry red = new Geometry("Box", box2);
//        red.setLocalTranslation(new Vector3f(0,2,0));

//        red.setMaterial(mat2);

        /** Create a pivot node at (0,0,0) and attach it to the root node */
        pivot = new Node("pivot");
        rootNode.attachChild(pivot); // put this node in the scene

        /** Attach the two boxes to the *pivot* node. (And transitively to the root node.) */
        pivot.attachChild(bluen);
//        pivot.attachChild(red);
        pivot.attachChild(grey);
        pivot.attachChild(mainbox);
        /** Rotate the pivot node: Note that both boxes have rotated! */
        pivot.rotate(-15* FastMath.DEG_TO_RAD,15* FastMath.DEG_TO_RAD,0f);
        pivot.setLocalTranslation(new Vector3f(0.5f,1,-2));
//        pivot.scale(2f,2f,2f);
        mainbox.scale(4f,4f,4f);

        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize());
        ch.setText(""); // crosshairs
        ch.setColor(new ColorRGBA(1f,0.8f,0.3f,0.8f));
        guiNode.attachChild(ch);

    }

    public Geometry getWireBox(Vector3f pos, float x,float y,float z, ColorRGBA color){
        return getWireBox(pos,x,y,z,color,"Wireframe Cube");
    }
    public Geometry getWireBox(Vector3f pos, float x,float y,float z, ColorRGBA color, String name) {
        WireBox wb = new WireBox(x,y,z);
        wb.setLineWidth(2);
        Geometry g = new Geometry(name, wb);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);

        g.setMaterial(mat);
        g.setLocalTranslation(pos);
        return g;
    }
}
