package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.hrvhs.ACommand;
import org.firstinspires.ftc.teamcode.hrvhs.AHrvhsOpMode;
import org.firstinspires.ftc.teamcode.hrvhs.ASubsystem;
import org.firstinspires.ftc.teamcode.hrvhs.MecanumDriveSubsystem;

import static org.firstinspires.ftc.teamcode.hrvhs.AConstants.STICK_DEAD_BAND;
import static org.firstinspires.ftc.teamcode.hrvhs.AConstants.STICK_SENSITIVITY;

public abstract class ADriveSticks extends ACommand {

    // These are the 'raw' values from the gamepad1. NOTE, the gamepad Y value is negated since we things forware (positive)
    // is pushing the stick forward.
    double m_rawRightX;
    double m_rawRightY;
    double m_rawLeftX;
    double m_rawLeftY;

    double m_conditionedRightX;
    double m_conditionedRightY;
    double m_conditionedLeftX;
    double m_conditionedLeftY;

    MecanumDriveSubsystem m_mecDrive;

    /**
     * Instantiate the ADriveSticks
     *
     * @param opMode       (not null) The op mode for the command - which has access to the input devices
     *                     the command may require.
     * @param name         The name of the command for logging/debugging. If <tt>null</tt> the class name
     *                     is used as the name of the command
     * @param requirements The subsystems this command requires.
     */
    public ADriveSticks(AHrvhsOpMode opMode, String name, ASubsystem... requirements) {
        super(opMode, name, requirements);
        m_mecDrive = (MecanumDriveSubsystem)requirements[0];
        setInterruptible(true);
    }

    /**
     * This is a generic conditioning of the sticks (does not differentiate between forward, sideways, turn). Override
     * this method if there are differences in right, left, X, and Y conditioning.
     */
    void lclConditionSticks() {
        // get the raw values - NOTE: when you push the stick forward the value is negative - that is counter-intuitive,
        // so negate those as the raw values.
        m_rawRightX = m_opMode.gamepad1.right_stick_x;
        m_rawRightY = -m_opMode.gamepad1.right_stick_y;
        m_rawLeftX = m_opMode.gamepad1.left_stick_x;
        m_rawLeftY = -m_opMode.gamepad1.left_stick_y;

        m_conditionedRightX = lclConditionedStickValue(m_rawRightX, STICK_DEAD_BAND, STICK_SENSITIVITY);
        m_conditionedRightY = lclConditionedStickValue(m_rawRightY, STICK_DEAD_BAND, STICK_SENSITIVITY);
        m_conditionedLeftX = lclConditionedStickValue(m_rawLeftX, STICK_DEAD_BAND, STICK_SENSITIVITY);
        m_conditionedLeftY = lclConditionedStickValue(m_rawLeftY, STICK_DEAD_BAND, STICK_SENSITIVITY);
    }

    double lclConditionedStickValue(double stickValue, double deadBand, double sensitivity) {
        if (Math.abs(stickValue) <= deadBand) return 0.0;
        double sign = (stickValue < 0.0) ? -1.0 : 1.0;
        double deadbandCorrected = (Math.abs(stickValue) - deadBand) / (1.0 - deadBand);
        return sign * Math.pow(deadbandCorrected, sensitivity);
    }

    @Override
    protected boolean isFinished() {
        // driving is never finished - but it can be interrupted.
        return false;
    }
}

