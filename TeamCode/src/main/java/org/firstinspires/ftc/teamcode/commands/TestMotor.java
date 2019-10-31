package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.hrvhs.ACommand;
import org.firstinspires.ftc.teamcode.hrvhs.AHrvhsOpMode;
import org.firstinspires.ftc.teamcode.hrvhs.ASubsystem;
import org.firstinspires.ftc.teamcode.hrvhs.MecanumDriveSubsystem;

public class TestMotor  extends ACommand {

    MecanumDriveSubsystem m_mecDrive;

    /**
     * Instantiate the TestMotor command.
     *
     * @param opMode       (not null) The op mode for the command - which has access to the input devices
     *                     the command may require.
     * @param name         The name of the command for logging/debugging. If <tt>null</tt> the class name
     *                     is used as the name of the command
     * @param requirements The subsystems this command requires.
     */
    public TestMotor(AHrvhsOpMode opMode, String name, ASubsystem... requirements) {
        super(opMode, name, requirements);
        m_mecDrive = (MecanumDriveSubsystem)requirements[0];
        setInterruptible(true);
    }

    /**
     * The execute method is called repeatedly until this Command either finishes or is canceled.
     */
    @Override
    protected void execute() {

        double speedFL = m_opMode.gamepad1.dpad_up ? 1.0 : 0.0;
        double speedFR = m_opMode.gamepad1.dpad_right ? 1.0 : 0.0;
        double speedRR = m_opMode.gamepad1.dpad_down ? 1.0 : 0.0;
        double speedLR = m_opMode.gamepad1.dpad_left ? 1.0 : 0.0;
        m_mecDrive.setMotorPower(speedFL, speedFR, speedRR, speedLR);

    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
