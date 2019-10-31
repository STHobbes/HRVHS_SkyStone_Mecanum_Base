package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.commands.DriveArcade;
import org.firstinspires.ftc.teamcode.hrvhs.AHrvhsOpMode;
import org.firstinspires.ftc.teamcode.hrvhs.MecanumDriveSubsystem;

@TeleOp(name = "DriveTemplate", group = "")
public class DriveTemplate extends AHrvhsOpMode {

    @Override
    protected void preStartInitialize() {
        // initialize the constants for my robot
        Constants.initForMyRobot();

        // Set the drive type by setting the default drive command for the drive subsystem.
        // Uncomment the next line for arcade drive, comment the next line for tank drive
        m_driveSubsystem.setDefaultCommand(new DriveArcade(this,"Arcade Drive", m_driveSubsystem));
        // Uncomment the next line for tank drive, comment the next line for arcade drive
//        driveSubsystem.setDefaultCommand(new DriveTank(this, "Tank Drive", driveSubsystem));
    }

    @Override
    protected void postStartInitialize() {
        reportLoopTime(true);
    }
}
