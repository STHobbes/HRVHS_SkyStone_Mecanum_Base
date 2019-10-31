package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.hrvhs.AHrvhsOpMode;
import org.firstinspires.ftc.teamcode.hrvhs.ASubsystem;

public class DriveTank extends ADriveSticks {

    /**
     * Instantiate the DriveArcade
     *
     * @param opMode       (not null) The op mode for the command - which has access to the input devices
     *                     the command may require.
     * @param name         The name of the command for logging/debugging. If <tt>null</tt> the class name
     *                     is used as the name of the command
     * @param requirements The subsystems this command requires.
     */
    public DriveTank(AHrvhsOpMode opMode, String name, ASubsystem... requirements) {
        super(opMode, name, requirements);
    }

    /**
     * Read the sticks and set the drive power
     */
    @Override
    protected void execute() {
        lclConditionSticks();
        double sideways = (m_conditionedLeftX + m_conditionedRightX) / 2.0;
        m_mecDrive.SetTankPower(m_conditionedLeftY, m_conditionedRightY, sideways);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
