package edu.ufl.cise.cs1.robots;

import robocode.*;
import robocode.util.Utils;

import java.awt.*;

/**
 * Used the firing mechanism from Fire.java to aim the radar and gun.
 * Used MyFirstLeader.java to not target enemies.
 */

public class Stoned extends TeamRobot
{
    int direction = 1; //Setting the variables for the robot
    double energy = 100;
    int strafe = 1;

    public void run()
    {
        setBodyColor(Color.black); //Setting the colors for the robot
        setGunColor(Color.white);
        setRadarColor(Color.black);
        setScanColor(Color.black);
        setBulletColor(Color.green);
        while(true)
        {
            setAdjustRadarForGunTurn(true);
            setAdjustRadarForRobotTurn(true);
            setAdjustGunForRobotTurn(true);
            turnRadarRight(999999); //Turns around until enemy is scanned
            scan();
        }

    }
    public void onScannedRobot(ScannedRobotEvent e)
    {
        if(isTeammate(e.getName())) //Checks if scanned robot is teammate
        {
            return; //If robot is teammate, don't fire
        }
        double radarAim = getHeading()-getRadarHeading()+e.getBearing(); //Sets a variable for the aiming of the radar
        setTurnRadarRight(Utils.normalRelativeAngleDegrees(radarAim)); //Sets the radar to turn to that variable, using the normalRelativeAngle call
        setTurnRight(e.getBearing()+90-20*direction); //Sets the robot to turn so that it is almost perpendicular to the enemy
        double EnergyChange = energy - e.getEnergy(); //Calculate the enemies energy change in order to tell if the enemy has fired a bullet
        energy = e.getEnergy(); //Sets the energy variable equal to the new energy of the enemy
        if(EnergyChange>0 && EnergyChange<=3) //If the enemies energy change is between 0-3, the enemy fired a bullet, so dodge the bullet
        {
            if(e.getDistance()<250) //If the distance is less than 300, move backwards while dodging
            {
                direction *= -1;
                setBack((e.getDistance()/2)*direction);

            }
            if(e.getDistance() >= 250) //If the distance is greater than 300, strafe back and forth to dodge
            {
                strafe *= -1;
                setBack((e.getDistance() / 5) * strafe);
            }
        }
        double gunAim = getHeading()-getGunHeading()+e.getBearing(); //Setting a variable for the aiming of the gun
        setTurnGunRight(Utils.normalRelativeAngleDegrees(gunAim)); //Telling the gun to turn according to the variable set

        if(e.getDistance()>800) //If the distance is greater than 800, fire bullet with power 1 (high chance of missing)
            fire(1);
        if(e.getDistance()>300 && e.getDistance()<=800) //If distance is between 300 and 800, fire bullet of power 2 (lower chance of missing while strafing in place)
            fire(2);
        if(e.getDistance()>100 && e.getDistance()<=300) //If distance is betwwen 100 and 300, fire bullet of power 1 (high chance to miss while strafing back)
            fire(1);
        if(e.getDistance() <= 100) //If distance is less than 100, fire bullet of max power
            fire(2);
        if(getEnergy() <=30) //If the robots energy is less than 30, fire bullet of power 1 in order to conserve energy
            fire(1);
        scan();
    }
    public void onHitRobot(HitRobotEvent e)
    {
        setTurnRadarRight(getHeading()-getRadarHeading()+e.getBearing()); //Turning the radar to find the enemy
        setTurnGunRight(getHeading()-getGunHeading()+e.getBearing()); //Turning the gun to target the enemy
        fire(3); //Fire with max power
        setBack(100); //Retreat backwards
    }

    public void onHitWall(HitWallEvent e)
    {
        setTurnRight(e.getBearing() + 90); //Turn to be perpendicular to enemy
        setBack(100); //Retreat backwards and away from wall
    }


}
