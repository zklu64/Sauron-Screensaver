//Program uses overlapping rectangles to create a motif of the fiery Eye of Sauron from Lord of the Rings
//Please note that all numbers (unless commented upon) are arbitrary and set to its current values after trial and error and approximations
ArrayList <Flame> particles;
//the two noise functions below are not correlated with each other as you will see later on
float noiseOne, noiseTwo;

void setup() {
  size(600, 600);
  particles = new ArrayList <Flame>();
  setupParticles();
}

void draw() {
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

void newNoise() {
  /*
  noiseOne has a range of 0-1000 while noiseTwo has a range of 5000-10000
   this is because each time program is run, only one noise graph is generated for any instances of noise(x value of noise graph)
   it's stated before that noiseOne and noiseTwo serve different purposes
   so noiseOne and noiseTwo have very contrasting possible values to avoid any correlation
   */
  noiseOne = random(1000);
  noiseTwo = random(5000, 10000);
}

void setupParticles() { 
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
            particles.add(new Debris(curveCoordinateX, 150+0.0024*pow(width/2-curveCoordinateX, 2), noiseOne, noiseTwo));
          } else if (randomQuadrant == 2) {
            float curveCoordinateX = random (width/2-250, width/2);
            particles.add(new Debris(curveCoordinateX, height-150-0.0024*pow(width/2-curveCoordinateX, 2), noiseOne, noiseTwo));
          } else if (randomQuadrant == 3) {
            float curveCoordinateX = random (width/2, width/2+250);
            particles.add(new Debris(curveCoordinateX, 150+0.0024*pow(width/2-curveCoordinateX, 2), noiseOne, noiseTwo));
          } else {
            float curveCoordinateX = random (width/2, width/2+250);
            particles.add(new Debris(curveCoordinateX, height-150-0.0024*pow(width/2-curveCoordinateX, 2), noiseOne, noiseTwo));
          }
        }
      }
    }
  }
}