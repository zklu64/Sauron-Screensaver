import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Sauron extends PApplet {

//Program uses overlapping rectangles to create a motif of the fiery Eye of Sauron from Lord of the Rings
//Please note that all numbers (unless commented upon) are arbitrary and set to its current values after trial and error and approximations
ArrayList <Flame> particles;
//the two noise functions below are not correlated with each other as you will see later on
float noiseOne, noiseTwo;

public void setup() {
  
  particles = new ArrayList <Flame>();
  setupParticles();
}

public void draw() {
  background(0);
  //section that deals with creating the pillar crest structure
  fill(15);
  pushMatrix();
  translate(width/2, height/2);
  rotate(radians(6));
  rectMode(CENTER);
  translate(-width/2, -height/2);
  rect(130, height/2+100, width/2-100, height-100);
  popMatrix();
  pushMatrix();
  translate(width/2, height/2);
  rotate(radians(354));
  rectMode(CENTER);
  translate(-width/2, -height/2);
  rect(width-130, height/2+100, width/2-100, height-100);
  popMatrix();
  fill(50);
  ellipse(width-180, height-350, 270, 750);
  ellipse(180, 250, 270, 750);
  fill(0);
  ellipse(width-280, 150, 460, 1000);
  ellipse(280, 150, 460, 1000);
  //end of section that deals with sauron's crest
  //for loop in charge of going through all elements of particles for updating its instances and then displaying that particle
  for (int c=particles.size ()-1; c>=0; c--) {
    particles.get(c).update();
    particles.get(c).display();
    /*the particle is removed if it goes past the right or left of the screen
     it is often first removed due to its being very close to the focal point that it is supposed to move towards
     RNG is set from 0-5 so that when it generates a lower value such as 0.5 or 1
     the object may be far away and have a great magnitude for velocity, thereby skipping that removal criteria
     This is intensionally done to simulate the flickering of fire
     */
    if (dist(particles.get(c).location.x, particles.get(c).location.y, particles.get(c).focal.x, particles.get(c).focal.y)<=random(5)||particles.get(c).location.x>width||particles.get(c).location.x<0) {
      particles.remove(c);
    }
  }
  //continues spawn new flame particles as old ones are being deleted upon reaching above criteria
  if (frameCount%20==0) {
    setupParticles();
  }
  fill(0);
  //creates the "iris" of sauron
  ellipse(width/2, height/2, 10, 100);
}

public void newNoise() {
  /*
  noiseOne has a range of 0-1000 while noiseTwo has a range of 5000-10000
   this is because each time program is run, only one noise graph is generated for any instances of noise(x value of noise graph)
   it's stated before that noiseOne and noiseTwo serve different purposes
   so noiseOne and noiseTwo have very contrasting possible values to avoid any correlation
   */
  noiseOne = random(1000);
  noiseTwo = random(5000, 10000);
}

public void setupParticles() { 
  for (int y=0; y < height; y++)
  {
    for (int x = 0; x < width; x++)
    {
      int i = x + y * width;
      //checks all coordinates in opened window and spawns flame particles in coordinates around centre of screen 
      //creating a pseudo circle in the middle
      if (dist(width/2, height/2, x, y)<=10) {
        //both noises are reset upon spawning a new particle
        newNoise();
        //particles have coordinates (x,y) and noises for two different purposes passed onto the object instance
        float particleRandomizer = random(10);
        if (particleRandomizer>1) {
          particles.add(new Flame(x, y, noiseOne, noiseTwo));
        } else {
          int randomQuadrant = (int)random(4);
          if (randomQuadrant == 1) {
            float curveCoordinateX = random (width/2-250, width/2);
            particles.add(new Debris(curveCoordinateX, 150+0.0024f*pow(width/2-curveCoordinateX, 2), noiseOne, noiseTwo));
          } else if (randomQuadrant == 2) {
            float curveCoordinateX = random (width/2-250, width/2);
            particles.add(new Debris(curveCoordinateX, height-150-0.0024f*pow(width/2-curveCoordinateX, 2), noiseOne, noiseTwo));
          } else if (randomQuadrant == 3) {
            float curveCoordinateX = random (width/2, width/2+250);
            particles.add(new Debris(curveCoordinateX, 150+0.0024f*pow(width/2-curveCoordinateX, 2), noiseOne, noiseTwo));
          } else {
            float curveCoordinateX = random (width/2, width/2+250);
            particles.add(new Debris(curveCoordinateX, height-150-0.0024f*pow(width/2-curveCoordinateX, 2), noiseOne, noiseTwo));
          }
        }
      }
    }
  }
}
/** This class will be used to create the debris Sauron absorbs
 * 
 * @author Kevin Lu
 * @version 1.0
 * @since Processing 2.2.1
 * @since April 2, 2015
 */
class Debris extends Flame {
  float transparent=255;
  PImage img;
  Debris(float getx, float gety, float noiseOne, float noiseTwo) {
    super(getx, gety, noiseOne, noiseTwo);
    img = loadImage("particle.png");
  }

  public void determineColor() {
    if (dist(width/2, height/2, location.x, location.y)>=100) {
      transparency(150, 255);
    } else if (dist(width/2, height/2, location.x, location.y)<100&&dist(width/2, height/2, location.x, location.y)>=50) {
      transparency(transparent-2, transparent);
    } else {
      transparency(transparent-5, transparent);
    }
  }

  public void transparency(float min, float max) {
    transparent = map(noise(colorNoise), 0, 1, min, max);
  }

  public void display() {
    fill(0, 0, 0, transparent);
    rect(location.x, location.y, 5, 5);
  }

  public void dispersion() {
    focal = new PVector(width/2, height/2);
    velocity = PVector.sub(focal, location);
  }
}
/**
 * This Flame will be used as a yellow, orange, or red rectangle that simulates a flame particle
 * 
 * @author Kevin Lu
 * @version 1.5
 * @since Processing 2.2.1
 * @since Mar 16
 */
class Flame {
  //focal is a unique point that a particle travels towards
  PVector location, velocity, focal;
  float colorNoise, speedNoise, greenGradient;
  /**
   * This is a constructor for a flame particle
   * @param getx this is a passed in x value for the x value of PVector 'location' of the Flame object 
   * @param gety this is a passed in y value for the y value of PVector 'location' of the Flame object
   * @param noiseOne this is a passed in noise reference for the Flame object
   * @param noiseTwo this is a passed in noise reference with a difference purpose from the former for the Flame object 
   * The constructor uses dispersion() method to determine velocity
   */
  Flame (float getx, float gety, float noiseOne, float noiseTwo) {
    location = new PVector (getx, gety);
    colorNoise = noiseOne;
    speedNoise = noiseTwo;
    dispersion();
    noStroke();
  }

  /**
   * This is a method that updates the particle's velocity, location, noise reference for color, and noise reference for speed
   */
  public void update() {
    velocity.normalize();
    //velocity's speed is randomly determined through noise instead of random
    //to allow each fire particle to have some randomness without being chaotic
    velocity.mult(map(noise(speedNoise), 0, 1, 0.1f, 2));
    location.add(velocity);
    colorNoise  +=0.01f;
    speedNoise  +=0.01f;
  }

  /**
   * This is a method that uses the method determineColor() to determine the fill color and then displays the particle using a small rectangle
   */
  public void display() {
    determineColor();
    fill(255, greenGradient, 0);
    rect(location.x, location.y, 10, 10);
  }

  /**
   * This is a method that determines the color of fill() by calling method yellowFlame(), orangeFlame(), or redFlame
   * based on the distance between particle object and the middle of the screen
   */
  public void determineColor() {
    if (dist(width/2, height/2, location.x, location.y)<75) {
      yellowFlame();
    } else if (dist(width/2, height/2, location.x, location.y)<125&&dist(width/2, height/2, location.x, location.y)>=75) {
      orangeFlame();
    } else {
      redFlame();
    }
  }

  /**
   * This is a method that alters the g value of the fill(r,g,b) method to create hues of yellow
   * because green is the only value that changes when differentiating yellow from red
   */
  public void yellowFlame() {
    greenGradient = map(noise(colorNoise), 0, 1, 200, 255);
  }

  /**
   * This is a method that alters the g value of the fill(r,g,b) method to create hues of orange
   */
  public void orangeFlame() {
    //As noise always generates a value close to the past value, green value is slowly forced to change, producing orange (and possibly red) through the below map parameters
    greenGradient = map(noise(colorNoise), 0, 1, greenGradient-2, greenGradient);
  }

  /**
   * This is a method that alters the g value of the fill(r,g,b) method to create hues of red
   */
  public void redFlame() {
    greenGradient = map(noise(colorNoise), 0, 1, greenGradient-5, greenGradient);
  }

  /**
   * This is a method that determines the quadrant the particle is in, and then sets a direction to velocity using a point along a parabola as the focal
   * Four quadrants each having a differing parabola produces an eyelid outline of Sauron
   */
  public void dispersion() {
    if (location.x>width/2) {
      //bottom right quadrant
      if (location.y>=height/2) {
        float curveCoordinate = random (width/2, width/2+250);
        focal = new PVector(curveCoordinate, 150+0.000004f*height*pow(width/2-curveCoordinate, 2));
        velocity = PVector.sub(focal, location);
      } 
      //top right quadrant
      else if (location.y<height/2) {
        float curveCoordinate = random (width/2, width/2+250);
        focal = new PVector(curveCoordinate, height-150-0.000004f*height*pow(width/2-curveCoordinate, 2));
        velocity = PVector.sub(focal, location);
      }
    } else if (location.x<=width/2) {
      //bottom left quadrant
      if (location.y>=height/2) {
        float curveCoordinate = random (width/2-250, width/2);
        focal = new PVector(curveCoordinate, 150+0.000004f*height*pow(width/2-curveCoordinate, 2));
        velocity = PVector.sub(focal, location);
      } 
      //top left quadrant
      else if (location.y<height/2) {
        float curveCoordinate = random (width/2-250, width/2);
        focal = new PVector(curveCoordinate, height-150-0.000004f*height*pow(width/2-curveCoordinate, 2));
        velocity = PVector.sub(focal, location);
      }
    }
  }
}
  public void settings() {  size(600, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "Sauron" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
