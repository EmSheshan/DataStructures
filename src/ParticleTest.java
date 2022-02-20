import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class ParticleTest extends PApplet {

    int[] flamePalette = new int[256];
    Slider slider1;
    Slider slider2;
    Slider slider3;
    float wiggle = 45;
    float size = 70;
    ParticleSystem ps;

    public void settings() {
        size(600, 600);
        ps = new ParticleSystem(new PVector(300, 400));
        slider1 = new Slider(10,550, 160, 30, 10, 100, "Wiggle");
        slider2 = new Slider(430,550, 160, 30, 40, 100, "Size");
    }

    public void draw(){
        background(0);  // sets bg to black
        if( mousePressed) {
            boolean isChanged = slider1.checkPressed(mouseX, mouseY);
            if (isChanged) {
                wiggle = slider1.sliderVal;
            }
        }
        slider1.display();
        if( mousePressed) {
            boolean isChanged = slider2.checkPressed(mouseX, mouseY);
            if (isChanged) {
                size = slider2.sliderVal;
            }
        }
        slider2.display();
        ps.addParticle();
        ps.addParticle();   // second particle is just there to make the fire "meatier"
        ps.run();
    }

    public static void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "ParticleTest" };
        PApplet.main(appletArgs);
    }

    class Particle {
        PVector position;
        PVector velocity = new PVector(wiggle*random((float) -0.005, 0.005F), wiggle/50*random((float) -1, 0));
        final PVector acceleration = new PVector(0,-0.025F);
        int lifespan;
        int color;

        Particle(PVector l) {
            position = l.copy();
            lifespan = (int) (256*size/100);
        }

        void run() {
            update();
            display();
        }

        void update() {
            velocity.add(acceleration);
            position.add(velocity);
            position.add((randomGaussian()*wiggle/100), (float) (randomGaussian()*wiggle/100 - 1.0)); // wiggle factor
            lifespan -= 5.0/size*50;
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
                flamePalette[i]  = color(i*2, 0, 0, i*3); // Black to red
                flamePalette[i+64]  = color(255, i*2, 0);                   // Red to yellow
                flamePalette[i+128]  = color(255, 255, i*2);                // Yellow to white
                flamePalette[i+192]  = color(255, 255, 255);                 // White
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

            sliderX = x + (w/2);
            sliderVal = map( sliderX, x, x+w, min, max);

        }

        //display split into 2 methods, the background layer displays
        void display() {
            backgroundLayer();

            fill(200, 200, 200);
            rect( x, y, w, h);   //slider rectangle  - this is changed in child classes

            fill(150, 150, 150); //indicator rectangle
            rect( sliderX-2, y-3, 4, h + 6);
        }

        void backgroundLayer() {
            pushStyle();
            fill( 100);
            rect( x-10, y-20, w+20, h+40);  ////outer background rectangle
            fill( 200, 200, 200);  //fill for the text
            textAlign(CENTER);
            textSize(14);
            text( label, x+(w/2), y+h +15);
            popStyle();
        }

        //test mouse coordinates to determine if within the slider rectangle
        //if not changed, return false
        //set sliderX to current mouseX position
        boolean checkPressed(int mx, int my) {
            boolean isChanged = false;
            if ( mx >= x && mx <= x+w && my> y && my< y +h) { //test for >= so endpoints are included
                isChanged = true;
                sliderX = mx;
                sliderVal = map( sliderX, x, x+w, min, max);
            }
            return isChanged;
        }
    }
}