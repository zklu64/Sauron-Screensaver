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
  void update() {
    velocity.normalize();
    //velocity's speed is randomly determined through noise instead of random
    //to allow each fire particle to have some randomness without being chaotic
    velocity.mult(map(noise(speedNoise), 0, 1, 0.1, 2));
    location.add(velocity);
    colorNoise  +=0.01;
    speedNoise  +=0.01;
  }

  /**
   * This is a method that uses the method determineColor() to determine the fill color and then displays the particle using a small rectangle
   */
  void display() {
    determineColor();
    fill(255, greenGradient, 0);
    rect(location.x, location.y, 10, 10);
  }

  /**
   * This is a method that determines the color of fill() by calling method yellowFlame(), orangeFlame(), or redFlame
   * based on the distance between particle object and the middle of the screen
   */
  void determineColor() {
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
  void yellowFlame() {
    greenGradient = map(noise(colorNoise), 0, 1, 200, 255);
  }

  /**
   * This is a method that alters the g value of the fill(r,g,b) method to create hues of orange
   */
  void orangeFlame() {
    //As noise always generates a value close to the past value, green value is slowly forced to change, producing orange (and possibly red) through the below map parameters
    greenGradient = map(noise(colorNoise), 0, 1, greenGradient-2, greenGradient);
  }

  /**
   * This is a method that alters the g value of the fill(r,g,b) method to create hues of red
   */
  void redFlame() {
    greenGradient = map(noise(colorNoise), 0, 1, greenGradient-5, greenGradient);
  }

  /**
   * This is a method that determines the quadrant the particle is in, and then sets a direction to velocity using a point along a parabola as the focal
   * Four quadrants each having a differing parabola produces an eyelid outline of Sauron
   */
  void dispersion() {
    if (location.x>width/2) {
      //bottom right quadrant
      if (location.y>=height/2) {
        float curveCoordinate = random (width/2, width/2+250);
        focal = new PVector(curveCoordinate, 150+0.000004*height*pow(width/2-curveCoordinate, 2));
        velocity = PVector.sub(focal, location);
      } 
      //top right quadrant
      else if (location.y<height/2) {
        float curveCoordinate = random (width/2, width/2+250);
        focal = new PVector(curveCoordinate, height-150-0.000004*height*pow(width/2-curveCoordinate, 2));
        velocity = PVector.sub(focal, location);
      }
    } else if (location.x<=width/2) {
      //bottom left quadrant
      if (location.y>=height/2) {
        float curveCoordinate = random (width/2-250, width/2);
        focal = new PVector(curveCoordinate, 150+0.000004*height*pow(width/2-curveCoordinate, 2));
        velocity = PVector.sub(focal, location);
      } 
      //top left quadrant
      else if (location.y<height/2) {
        float curveCoordinate = random (width/2-250, width/2);
        focal = new PVector(curveCoordinate, height-150-0.000004*height*pow(width/2-curveCoordinate, 2));
        velocity = PVector.sub(focal, location);
      }
    }
  }
}