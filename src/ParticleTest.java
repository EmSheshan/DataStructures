import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;

public class ParticleTest extends PApplet {

    int[] flamePalette = new int[256];
    Slider slider1 = new Slider(10,550, 160, 30, 10, 100, "Intensity");
    Slider slider2 = new Slider(220,550, 160, 30, 40, 100, "Age");
    Slider slider3 = new Slider(430,550, 160, 30, 1, 7, "Strength");
    float intensity = 45;
    float age = 70;
    float strength = 4;
    ParticleSystem ps = new ParticleSystem(new PVector(300, 400));

    public void settings() {
        size(600, 600);
    }

    public void draw(){
        background(0);  // sets bg to black
        if(mousePressed) {
            boolean isChanged = slider1.checkPressed(mouseX, mouseY);
            if (isChanged) {
                intensity = slider1.sliderVal;
            }
        }
        slider1.display();
        if( mousePressed) {
            boolean isChanged = slider2.checkPressed(mouseX, mouseY);
            if (isChanged) {
                age = slider2.sliderVal;
            }
        }
        slider2.display();
        if( mousePressed) {
            boolean isChanged = slider3.checkPressed(mouseX, mouseY);
            if (isChanged) {
                strength = slider3.sliderVal;
            }
        }
        slider3.display();
        for (int i = 0; i < (int )strength; i++) {
            ps.addParticle();
        }   // extra particles are just there to make the fire "meatier"
        ps.run();
    }

    public static void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "ParticleTest" };
        PApplet.main(appletArgs);
    }

    class Particle {
        PVector position;
        PVector velocity = new PVector(intensity*random((float) -0.005, 0.005F), intensity/50*random((float)-1, 0));
        final PVector acceleration = new PVector(0,-0.025F);
        int lifespan;
        int color;
        PImage img;

        Particle(PVector l) {
            position = l.copy();
            lifespan = (int) (256*age/100);
        }

        void run() {
            update();
            display();
        }

        void update() {
            velocity.add(acceleration);
            position.add(velocity);
            position.add((randomGaussian()* intensity /100), (float) (randomGaussian()* intensity /100 - 1.0)); // wiggle factor
            lifespan -= 5.0/age*50;
            if (lifespan > 0) { // since flamePalette is only initialized from 0 to 256, the program will crash if lifespan is <0
                color = flamePalette[lifespan];
            }
        }

        void display() {
            stroke(color, lifespan);
            fill(color, lifespan);
            ellipse(position.x, position.y, 8, 8);
        }

        boolean isDead() {
            return (lifespan < 0.0);
        }
    }

    class ParticleSystem {
        ArrayList<Particle> particleArrayList;
        PVector origin;

        ParticleSystem(PVector position) {
            origin = position.copy();
            particleArrayList = new ArrayList<>();

            for (int i=0; i<64; i++)
            {
                flamePalette[i] = color(i*2, 0, 0, i*3); // Black to red
                flamePalette[i+64] = color(255, i*2, 0);                  // Red to yellow
                flamePalette[i+128] = color(255, 255, i*2);               // Yellow to white
                flamePalette[i+192] = color(255, 255, 255);               // White
            }
        }

        void addParticle() {
            particleArrayList.add(new Particle(origin));
        }

        void run() {
            for (int i = particleArrayList.size()-1; i >= 0; i--) {
                Particle p = particleArrayList.get(i);
                p.run();
                if (p.isDead()) {
                    particleArrayList.remove(i);
                }
            }
        }
    }

    class Slider {
        float x, y;
        float w, h;
        float min, max;
        float sliderX;
        float sliderVal;
        String label;

        Slider( float x, float y, float w, float h, float min, float max, String label) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.min = min;
            this.max = max;
            this.label = label;

            sliderX = x + (w/2); // makes it so that bar starts in the center of the slider
            sliderVal = map( sliderX, x, x+w, min, max);
        }

        void backgroundLayer() {
            fill(100);
            rect(x-10, y-20, w+20, h+40);  ////outer background rectangle
            fill(220);  //fill for the text
            textAlign(CENTER);
            textSize(14);
            text(label, x+(w/2), y+h +15);
        }

        void display() {
            backgroundLayer();

            fill(200);
            rect(x, y, w, h);   //slider rectangle

            fill(150); //slider bar/cursor
            rect(sliderX-2, y-3, 4, h + 6);
        }

        boolean checkPressed(int mx, int my) {
            boolean isChanged = false;
            if ( mx >= x && mx <= x+w && my> y && my< y +h) {
                isChanged = true;
                sliderX = mx;
                sliderVal = map(sliderX, x, x+w, min, max);
            }
            return isChanged;
        }
    }
}