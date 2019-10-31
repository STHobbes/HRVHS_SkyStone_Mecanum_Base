package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.commands.TestMotor;
import org.firstinspires.ftc.teamcode.hrvhs.AHrvhsOpMode;
import org.firstinspires.ftc.teamcode.hrvhs.MecanumDriveSubsystem;

@TeleOp(name = "TestDriveMotors", group = "")
public class TestDriveMotors extends AHrvhsOpMode {

    MecanumDriveSubsystem driveSubsystem = new MecanumDriveSubsystem();


    @Override
    protected void preStartInitialize() {
        driveSubsystem.setDefaultCommand(new TestMotor(this, "Test Motors", driveSubsystem));

    }

    @Override
    protected void postStartInitialize() {
        reportLoopTime(true);
    }

//    @Override
//    public void runOpMode() throws InterruptedException {
//        // Setup the robot subsystems
//        registerSubsystem(driveSubsystem);
//        driveSubsystem.initialize(this);
//        waitForStart();
//        driveSubsystem.postStartInitialize();
//        while (opModeIsActive()) {
//        if (gamepad1.dpad_up) {
//            // Move forward the calibration distance
//            traction.move(calibration_distance, 0.0, 1.0);
//        } else if (gamepad1.dpad_down) {
//            // Move backwards the calibration distance
//            traction.move(-calibration_distance, 0.0, 1.0);
//        } else if (gamepad1.dpad_right) {
//            if (gamepad1.left_bumper) {
//                // Rotate 90 clockwise
//                traction.rotate(90.0, 1.0);
//            } else {
//                // Move right the calibration distance
//                traction.move(calibration_distance, 90.0, 1.0);
//            }
//        } else if (gamepad1.dpad_left) {
//
//        }
//    }
}
