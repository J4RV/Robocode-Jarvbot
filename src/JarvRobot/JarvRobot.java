package JarvRobot;

import jdk.nashorn.internal.runtime.regexp.joni.ScanEnvironment;
import robocode.*;
import robocode.Robot;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Jarv on 09/03/2017.
 */
public class JarvRobot extends AdvancedRobot {

    SecureRandom random;
    boolean runningAhead;

    public JarvRobot() {
        random = new SecureRandom();
    }


    public void run() {
        setBodyColor(Color.red);
        setGunColor(Color.CYAN);
        setRadarColor(Color.red);
        setScanColor(Color.red);
        setBulletColor(Color.CYAN);

        turnGunLeft(360);

        //Moverse caóticamente
        while (true) {
            dodge();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double bearing = track(e);
        if (bearing <= 3)
            shoot(e);
        execute();
    }

    public void onHitWall(HitWallEvent e) {
        double distance = 50;

        if (runningAhead) {
            back(distance);
        } else {
            ahead(distance);
        }

        runningAhead = !runningAhead;
    }

    public void onHitRobot(HitRobotEvent e) {
        if (e.isMyFault()) {
            back(80);
        }
    }

    public void shoot(ScannedRobotEvent e) {
        if (getGunHeat() == 0) {
            double power = 3;
            int maxDistance = 600;

            if (e.getDistance() <= maxDistance) {
                //if the target is almost still, shoot at max power
                if (e.getVelocity() <= 0.5) {
                    power = 3;
                } else {
                    //Max power on melee, less power if the enemy is distant
                    power = power * (1 - e.getDistance() / maxDistance);
                    power = clamp(power, 0.5, 3);
                }

                fire(power);
            } else {
                track(e);
            }
        }
    }

    double turnDegrees;
    public void dodge() {
        int maxDistance = 600;
        int minDistance = 200;

        double distance = random.nextDouble() * (maxDistance - minDistance) + minDistance;
        double turnDegrees = random.nextDouble() * 180 - 90;

        if(runningAhead)
            setAhead(distance);
        else
            setBack(distance);

        setTurnLeft(turnDegrees);
        setTurnGunRight(360);
        execute();
    }

    public void randomMovement(){

    }

    public void randomTurn(){
        
    }

    public double track(ScannedRobotEvent e) {
        //Rotación del arma, teniendo en cuenta la rotación de la base y el angulo del enemigo
        double angle = getHeading() + e.getBearing();
        angle -= getGunHeading();

        setTurnGunRight(angle);

        return angle;
    }


    //Utility
    public static double clamp(double value, double min, double max) {
        double res = value;

        if(value > max){
            res = max;
        } else if (value < min){
            res = min;
        }

        return res;
    }
}
