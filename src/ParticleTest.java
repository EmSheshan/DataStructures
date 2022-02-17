import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class ParticleTest extends PApplet {

    int[] flamePalette = new int[256];

    ParticleSystem ps;

    public void settings() {
        size(500, 500);
        ps = new ParticleSystem(new PVector(250, 300));
    }

    public void draw(){
        background(0);  // sets bg to black
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
        PVector velocity = new PVector(random((float) -0.5, 0.5F), random(-2, 0));
        final PVector acceleration = new PVector(0,-0.025F);
        int lifespan;
        int color;

        Particle(PVector l) {
            position = l.copy();
            lifespan = 225;
        }

        void run() {
            update();
            display();
        }

        void update() {
            velocity.add(acceleration);
            position.add(velocity);
            position.add((float) (randomGaussian()*0.7), (float) (randomGaussian()*0.2 - 1.0)); // wiggle factor
            lifespan -= 5.0;
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
                flamePalette[i]  = color(i<<2, 0, 0, i<<3); // Black to red
                flamePalette[i+64]  = color(255, i<<2, 0);                   // Red to yellow
                flamePalette[i+128]  = color(255, 255, i<<2);                // Yellow to white
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
}