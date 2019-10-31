package org.firstinspires.ftc.teamcode.hrvhs;

import com.qualcomm.robotcore.hardware.DcMotor;

public abstract class AConstants {

    // -----------------------------------------------------------------------------------------------
    // Configuration - If you built and configured your robot as described in the README.md
    // document then these will be the correct values.

    // These are the names in your configuration for wheel motors
    static public String CONFIG_FRONT_LEFT = "FL";
    static public String CONFIG_FRONT_RIGHT = "FR";
    static public String CONFIG_RIGHT_REAR = "RR";
    static public String CONFIG_LEFT_REAR = "LR";

    //  name in the your configuration for the IMU
    static public String CONFIG_IMU = "IMU";

    // These are the direction constants that program all of your motors to move forward
    // in the 'MotorTest' program. These should be correct for the TileRunner base.
    static public DcMotor.Direction FRONT_LEFT_DIRECTION = DcMotor.Direction.FORWARD;
    static public DcMotor.Direction FRONT_RIGHT_DIRECTION = DcMotor.Direction.REVERSE;
    static public DcMotor.Direction RIGHT_REAR_DIRECTION = DcMotor.Direction.REVERSE;
    static public DcMotor.Direction LEFT_REAR_DIRECTION = DcMotor.Direction.FORWARD;

    // -----------------------------------------------------------------------------------------------
    // Calibration values for the encoders and for acceleration and deceleration in autonomous
    // move and turn functions - use the CalibrateTest program to figure out the correct value
    // for these for your robot.
    static public double TICS_PER_INCH_FORWARD = 65.0;
    static public double TICS_PER_INCH_SIDEWAYS = 120.0;

    static public double STICK_SENSITIVITY = 2.0;
    static public double STICK_FORWARD_SENSITIVITY = 2.0;
    static public double STICK_SIDEWAYS_SENSITIVITY = 2.0;
    static public double STICK_TURN_SENSITIVITY = 2.0;
    static public double STICK_DEAD_BAND = 0.05;

    static public double HEADING_CORRECTION_KP = 0.05;
}
