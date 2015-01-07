package com.satersoft.stardestroyer.domain.model.units;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;

public class EntityFactory implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4356319365490533375L;
	private String imageID;
	private int height, width, speed, armor, strength, score;
	private XmlPullParser xpp;
	private Ship ship;
	private HashMap<String, Ship> shipCatalog = new HashMap<>();
	private HashMap<String, Projectile> bulletCatalog = new HashMap<>();
    private Context cxt;

	public EntityFactory(String datastring, Context cxt, double factor){
        XmlPullParserFactory factory;
        this.cxt = cxt;
		try {
			factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xpp = factory.newPullParser();
            xpp.setInput(new StringReader (datastring));
            XmlFactory.processXML(xpp, cxt, shipCatalog, bulletCatalog, factor);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}

	public Ship createShip(boolean player,String type, int x, int y){
				if (player){
					type = "Player" + type.substring(0,1).toUpperCase() + type.substring(1);
				} else {
					type = "Enemy" + type.substring(0,1).toUpperCase() + type.substring(1);
				}
				Ship toBeCloned = shipCatalog.get(type);
				height = toBeCloned.getHeight();
				width = toBeCloned.getWidth();
				imageID = "" + toBeCloned.getImageID();
				speed =  toBeCloned.getSpeed();
				armor = toBeCloned.getArmor();
				strength = toBeCloned.getStrength();
				if (player){
					ship = new PlayerShip(x, y, toBeCloned.getImageID()/*getID(imageID)*/,type, width, height, speed, strength, armor);
				} else {
					score = speed+armor+strength;
					ship = new EnemyShip(x, y, toBeCloned.getImageID()/*getID(imageID)*/,type, width, height, speed, strength, armor, score);
				}
        Log.e("SHIP", "created!");
        return ship;
		}
	
	public Projectile createProjectile(int x, int y, boolean player, int speed, int strength){
		if (player){
			imageID = "player";
		} else {
			imageID = "enemy";
		}
		Projectile toBeCloned = bulletCatalog.get(imageID);
		height = toBeCloned.getHeight();
		width = toBeCloned.getWidth();
		Projectile projectile = new Projectile(x, y, width, height, speed, getID(imageID+"projectile"), imageID+"Bullet", strength, player);
		return projectile;
	}
	
	public void parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readFeed(parser);
        } finally {
            in.close();
        }
    }
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
	private Projectile readProjectile(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, null, "projectile");
	    String text = "";
	    Projectile p;
	    String type="", image="";
	    int height=0, width=0;
	    boolean player = false;
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("type")) {
	            type = readText(parser, "type");
	        }else if (name.equals("height")) {
	            height = Integer.parseInt(readText(parser, "height"));
	        }else if (name.equals("width")) {
	            width = Integer.parseInt(readText(parser, "width"));
	        }else if (name.equals("image")) {
	            image = readText(parser, "image");
	        }else {
	            skip(parser);
	        }
	    }
	    if (type.contains("player")){
	    	player = true;
	    }

	    return new Projectile(0, 0, width, height, 0, getID(image), type, 0, player);
	}
	private Ship readShip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, null, "ship");
	    Ship s;
	    String type ="", image="";
	    int speed=0, armor=0, strength=0,height=0,width=0, score=0;
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("type")) {
	            type = readText(parser, "type");
	        } else if (name.equals("speed")) {
	            speed= Integer.parseInt(readText(parser, "speed"));
	        }else if (name.equals("armor")) {
	            armor = Integer.parseInt(readText(parser, "armor"));
	        }else if (name.equals("strength")) {
	            strength = Integer.parseInt(readText(parser, "strength"));
	        }else if (name.equals("height")) {
	            height = Integer.parseInt(readText(parser, "height"));
	        }else if (name.equals("width")) {
	            width = Integer.parseInt(readText(parser, "width"));
	        }else if (name.equals("image")) {
	            image= readText(parser, "image");
	        }else if (name.equals("score")) {
	            score= Integer.parseInt(readText(parser, "score"));
	        }else {
	            skip(parser);
	        }
	    }
	    if (type.contains("player")){
	    	s = new PlayerShip(0, 0, getID(image), type, width, height, speed, strength, armor);
	    } else {
	    	s = new EnemyShip(0, 0, getID(image), type, width, height, speed, strength, armor, score);
	    }
	    return s;
	    }

    private int getID(String drawable) {
        return cxt.getResources().getIdentifier(drawable.toLowerCase(), "drawable", cxt.getPackageName());
    }

	// Processes title tags in the feed.
	private String readText(XmlPullParser parser, String limiter) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, null, limiter);
	    String text = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, limiter);
	    return text;
	}

	// For the tags title and summary, extracts their text values.
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}
	
	private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		Ship s;
		Projectile p;
	    parser.require(XmlPullParser.START_TAG, null, "entity");
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        // Starts by looking for the entry tag
	        if (name.equals("ship")) {
	            s = readShip(parser);
	            shipCatalog.put(s.getType(), s);
	        } else if (name.equals("projectile")){
	        	p = readProjectile(parser);
	        	bulletCatalog.put(p.getType(), p);
	        } else {
	            skip(parser);
	        }
	    }  
	    
	}

}
