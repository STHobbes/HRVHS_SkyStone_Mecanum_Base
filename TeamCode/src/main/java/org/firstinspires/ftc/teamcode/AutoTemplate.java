package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.commands.DriveTank;
import org.firstinspires.ftc.teamcode.hrvhs.AHrvhsOpMode;
import org.firstinspires.ftc.teamcode.hrvhs.MecanumDriveSubsystem;

public class AutoTemplate  extends AHrvhsOpMode {
    MecanumDriveSubsystem driveSubsystem = new MecanumDriveSubsystem();

    @Override
    protected void preStartInitialize() {
        // initialize the constants for my robot
        Constants.initForMyRobot();
    }

    @Override
    protected void postStartInitialize() {
        reportLoopTime(true);
    }

}
