package com.satersoft.stardestroyer.domain.model.units;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

public class XmlFactory {
    private static final String ENEMY = "Enemy",
                                PLAYER = "Player";
    private static Ship ship;
    private static Projectile projectile;
    private static Integer speed = 0, armor = 0, strength = 0, height = 0, width = 0, score = 0;
    private static String image = "", description = "", type = "";
    private static Object current;

    public static void processXML(XmlPullParser xpp, Context cxt, HashMap<String, Ship> shipCatalog, HashMap<String, Projectile> bulletCatalog, double factor) {
        int eventType = 0;
        try {
            eventType = xpp.getEventType();

            do {
                if(eventType == xpp.END_TAG) {
                    processEndTag(xpp, cxt, shipCatalog, bulletCatalog, factor);
                } else if(eventType == xpp.TEXT) {
                    processText(xpp);
                }
                eventType = xpp.next();
            } while (eventType != xpp.END_DOCUMENT);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void processText(XmlPullParser xpp) throws IOException, XmlPullParserException {
        current = "" + xpp.getText();
    }

    private static void processEndTag(XmlPullParser xpp, Context cxt, HashMap<String, Ship> shipCatalog, HashMap<String, Projectile> bulletCatalog, double factor) {
        String name = xpp.getName();
        if(name.equals("type")) {
            type = (String) current;
        } else if (name.equals("speed")) {
            speed = Integer.parseInt((String) current);
        } else if (name.equals("armor")) {
            armor = Integer.parseInt((String) current);
        } else if (name.equals("strength")) {
            strength = Integer.parseInt((String) current);
        } else if (name.equals("height")) {
            height = Integer.parseInt((String) current);
        } else if (name.equals("width")) {
            width = Integer.parseInt((String) current);
        } else if (name.equals("image")) {
            image = (String) current;
        } else if (name.equals("description")) {
            description = (String) current;
        } else if (name.equals("score")) {
            score = Integer.parseInt((String) current);
        } else if(name.equals("ship")) {
            if(type.contains(ENEMY)) {
                ship = new EnemyShip(0,0,getID(image, cxt), type, (int) (width*factor), (int) (height*factor), (int) (speed*factor),strength,armor,score);
            } else if(type.contains(PLAYER)) {
                ship = new PlayerShip(0, 0, getID(image, cxt), type, (int) (width*factor), (int) (height*factor), (int) (speed*factor), strength, armor);
            }
            shipCatalog.put(type, ship);
        } else if(name.equals("projectile")) {
            bulletCatalog.put(type, new Projectile(0, 0, (int) (width*factor), (int) (height*factor), (int) (speed*factor), getID(image, cxt),type, strength, (type.contains("player")? true:false)));
        }
    }

    private static int getID(String drawable, Context cxt) {
        return cxt.getResources().getIdentifier(drawable.toLowerCase(), "drawable", cxt.getPackageName());
    }
}
