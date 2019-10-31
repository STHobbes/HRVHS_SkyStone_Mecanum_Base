package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.hrvhs.AHrvhsOpMode;
import org.firstinspires.ftc.teamcode.hrvhs.MecanumDriveSubsystem;

@TeleOp(name = "MyRobotBase", group = "")
public class MyRobotBase extends AHrvhsOpMode {

    MecanumDriveSubsystem driveSubsystem = new MecanumDriveSubsystem();


    @Override
    protected void preStartInitialize() {

    }

    @Override
    protected void postStartInitialize() {
        reportLoopTime(true);
    }
}
