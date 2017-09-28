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

  void determineColor() {
    if (dist(width/2, height/2, location.x, location.y)>=100) {
      transparency(150, 255);
    } else if (dist(width/2, height/2, location.x, location.y)<100&&dist(width/2, height/2, location.x, location.y)>=50) {
      transparency(transparent-2, transparent);
    } else {
      transparency(transparent-5, transparent);
    }
  }

  void transparency(float min, float max) {
    transparent = map(noise(colorNoise), 0, 1, min, max);
  }

  void display() {
    fill(0, 0, 0, transparent);
    rect(location.x, location.y, 5, 5);
  }

  void dispersion() {
    focal = new PVector(width/2, height/2);
    velocity = PVector.sub(focal, location);
  }
}