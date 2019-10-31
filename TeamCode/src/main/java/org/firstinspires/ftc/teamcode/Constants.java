package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.hrvhs.AConstants;

public class Constants extends AConstants {

    // Add constants specific to your robot here.

    /**
     * Initialize constants that have values specific to your robot. For example, your encoder tics-per-inch of
     * forward or sideways motion, motor direction, etc.
     */
    static void initForMyRobot() {
        // for Roy's robot.
        RIGHT_REAR_DIRECTION = DcMotor.Direction.FORWARD;
        LEFT_REAR_DIRECTION = DcMotor.Direction.REVERSE;
    }
}
