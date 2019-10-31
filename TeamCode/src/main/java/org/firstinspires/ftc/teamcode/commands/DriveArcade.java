package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.hrvhs.AHrvhsOpMode;
import org.firstinspires.ftc.teamcode.hrvhs.ASubsystem;

import static org.firstinspires.ftc.teamcode.hrvhs.AConstants.STICK_DEAD_BAND;
import static org.firstinspires.ftc.teamcode.hrvhs.AConstants.STICK_TURN_SENSITIVITY;
import static org.firstinspires.ftc.teamcode.hrvhs.AConstants.STICK_FORWARD_SENSITIVITY;
import static org.firstinspires.ftc.teamcode.hrvhs.AConstants.STICK_SIDEWAYS_SENSITIVITY;
import static org.firstinspires.ftc.teamcode.hrvhs.AConstants.HEADING_CORRECTION_KP;

public class DriveArcade extends ADriveSticks {

    boolean m_bInTurn = true;

    /**
     * Instantiate the DriveArcade
     *
     * @param opMode       (not null) The op mode for the command - which has access to the input devices
     *                     the command may require.
     * @param name         The name of the command for logging/debugging. If <tt>null</tt> the class name
     *                     is used as the name of the command
     * @param requirements The subsystems this command requires.
     */
    public DriveArcade(AHrvhsOpMode opMode, String name, ASubsystem... requirements) {
        super(opMode, name, requirements);
    }

    /**
     * Read the sticks and set the drive power
     */
    @Override
    protected void execute() {
        // get the raw values - NOTE: when you push the stick forward the value is negative - that is counter-intuitive,
        // so negate those as the raw values.
        m_rawRightX = m_opMode.gamepad1.right_stick_x;
        m_rawRightY = -m_opMode.gamepad1.right_stick_y;
        m_rawLeftX = m_opMode.gamepad1.left_stick_x;

        m_conditionedRightX = lclConditionedStickValue(m_rawRightX, STICK_DEAD_BAND, STICK_SIDEWAYS_SENSITIVITY);
        m_conditionedRightY = lclConditionedStickValue(m_rawRightY, STICK_DEAD_BAND, STICK_FORWARD_SENSITIVITY);
        m_conditionedLeftX = lclConditionedStickValue(m_rawLeftX, STICK_DEAD_BAND, STICK_TURN_SENSITIVITY);
        // The right stick is forward and sideways, the left stick is turn. If there is no turn, then we want to
        // maintain the current heading as we move.
        if (m_conditionedLeftX == 0.0) {
            double heading = m_mecDrive.getHeading();
            // no turn
            if (m_bInTurn) {
                m_bInTurn = false;
                m_mecDrive.resetExpectedHeading();
            }
            double headingError = m_mecDrive.getExpectedHeading() - heading;
            double max = Math.abs(m_conditionedRightY) + Math.abs(m_conditionedRightX);
            m_mecDrive.setArcadePower(m_conditionedRightY, m_conditionedRightX, max * HEADING_CORRECTION_KP * headingError);
        } else {
            // the robot is turning
            m_bInTurn = true;
            m_mecDrive.setArcadePower(m_conditionedRightY, m_conditionedRightX, m_conditionedLeftX);
        }
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
