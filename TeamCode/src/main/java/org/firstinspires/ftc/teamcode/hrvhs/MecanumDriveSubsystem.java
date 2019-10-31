package org.firstinspires.ftc.teamcode.hrvhs;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class MecanumDriveSubsystem extends ASubsystem {

    // The op mode that is using this subsystem. Wee need this to get the hardware map so we can find the motors and IMU
    protected AHrvhsOpMode m_opMode;

    // This is the physical hardware - motors and sensors - that provide the physical implementation of the traction.
    protected BNO055IMU m_imu;      // primary IMU
    protected DcMotor m_motorFL;    // front left motor
    protected DcMotor m_motorFR;    // front right motor
    protected DcMotor m_motorRR;    // right rear motor
    protected DcMotor m_motorLR;    // left rear motor

    double m_powerFL;
    double m_powerFR;
    double m_powerRR;
    double m_powerLR;

    // tracking the heading of the robot
    double m_heading;               // the current heading of the robot
    int m_headingRevs = 0;          // the complete revolutions of the robot
    double m_headingRawLast;        // the last raw heading from the IMU
    double m_expectedHeading;       // the expected heading of the robot

    public MecanumDriveSubsystem() {
        super("Mecanum Drive");
    }

    /**
     * Setup a motor.
     *
     * @param motor               (DcMotor) The motor to be setup.
     * @param direction           (DcMotor.Direction) The motor direction.
     * @param run_mode            (DcMotor.RunMode) The run mode for the motor.
     * @param zero_power_behavior (DcMotor.ZeroPowerBehavior) The zero-power behaviour.
     */
    protected void lclMotorSetup(DcMotor motor, DcMotor.Direction direction,
                                 DcMotor.RunMode run_mode, DcMotor.ZeroPowerBehavior zero_power_behavior) {
        motor.setDirection(direction);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(run_mode);
        motor.setZeroPowerBehavior(zero_power_behavior);
    }

    /**
     * This is a ramp-in and ramp-out generator function that returns a power
     * for the current position in the move. This method assumes the start
     * position is 0, end is the target, and current is the current position.
     *
     * @param current (double) The current position in the range 0 to target.
     * @param target (double) The target final position.
     * @param mtrAccelMin (double) The minimum acceleration motor speed - to
     *  assure the robot gets to the target.
     * @param mtrDecelMin (double) The minimum deceleration motor speed - to
     *  assure the robot gets to the target.
     * @param accel (double) The acceleration distance with power at current=0
     *  starting at mtr_min and power at current=accel reaching 1.0.
     * @param decel (double) The deceleration distance with power at
     *  current=target-decel being 1.0 and power at current=target being mtr_min.
     * @return (double) Returns the power that will be in the range
     *  of 0.0 to 1.0
     */
    protected double powerAccelDecel(double current, double target,
                                     double mtrAccelMin, double mtrDecelMin,
                                     double accel, double decel) {
        if (current <= 0.0) {
            // Not yet at the expected start. This could happen if there was some robot
            // motion (was hit or coasting) that confused the sensor/logic. In this
            // case, move at the minimum power until the caller knows what's happening.
            return mtrAccelMin;
        } else if (current >= target) {
            // Past the expected target. This could happen if there was some robot motion
            // (was hit or coasting) that confused the sensor/logic. In this case stop.
            return 0.0;
        }
        double mtr_tmp = 1.0;
        if (current < accel) {
            // in the acceleration zone
            mtr_tmp = mtrAccelMin + (1.0 - mtrAccelMin) * (current / accel);
        }
        if (current > target - decel) {
            // in the deceleration zone
            double mtr_tmp_2 = mtrDecelMin +
                    (1.0 - mtrDecelMin) * ((target - current) / decel);
            if (mtr_tmp_2 < mtr_tmp) {
                // Could also be in the acceleration zone - in this case the deceleration
                // value is less than the acceleration or the 1.0 default.
                mtr_tmp = mtr_tmp_2;
            }
        }
        return mtr_tmp;
    }

    /**
     * Initialize the Mecanum drive subsystem, which normally means find the drive motors in the
     * <tt>AHrvhsOpMode</tt> and initialize them for use. Setup IMUs, etc. This should be called from
     * the <tt>runOpMode</tt> method of your Op Mode before the waitForStart().
     *
     * @param opMode (AHrvhsOpMode, readonly) The liner operation mode this traction is being used in.
     */
    @Override
    public void preStartInitialize(AHrvhsOpMode opMode) {
        this.m_opMode = opMode;
        HardwareMap hardware_map = opMode.hardwareMap;

        // find the primary IMU
        m_imu = hardware_map.get(BNO055IMU.class, AConstants.CONFIG_IMU);

        // find the motors
        m_motorFL = hardware_map.get(DcMotor.class, AConstants.CONFIG_FRONT_LEFT);
        m_motorFR = hardware_map.get(DcMotor.class, AConstants.CONFIG_FRONT_RIGHT);
        m_motorRR = hardware_map.get(DcMotor.class, AConstants.CONFIG_RIGHT_REAR);
        m_motorLR = hardware_map.get(DcMotor.class, AConstants.CONFIG_LEFT_REAR);

        // initialize the motors
        final DcMotor.RunMode run_mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
        final DcMotor.ZeroPowerBehavior at_zero_power = DcMotor.ZeroPowerBehavior.BRAKE;
        lclMotorSetup(m_motorFL, AConstants.FRONT_LEFT_DIRECTION, run_mode, at_zero_power);
        lclMotorSetup(m_motorFR, AConstants.FRONT_RIGHT_DIRECTION, run_mode, at_zero_power);
        lclMotorSetup(m_motorRR, AConstants.RIGHT_REAR_DIRECTION, run_mode, at_zero_power);
        lclMotorSetup(m_motorLR, AConstants.LEFT_REAR_DIRECTION, run_mode, at_zero_power);
    }

    /**
     * The post-start initialization. This is here for things like IMU calibration which should be delayed until
     * start because of gyro precession, which will change orientation over time and can cause real problems if
     * there is a long delay between robot initialization and the start of play. This should be called after the
     * waitForStart().
     */
    @Override
    public void postStartInitialize() {
        // initialize the primary and secondary IMUs
        BNO055IMU.Parameters imu_params = new BNO055IMU.Parameters();
        imu_params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu_params.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imu_params.calibrationDataFile = "BNO055IMUCalibration.json";
        imu_params.loggingEnabled = false;
        m_imu.initialize(imu_params);
        //imu_0.startAccelerationIntegration(new Position(), new Velocity(), 100);
        while (true) {
            if (m_imu.isGyroCalibrated()) {
                break;
            }
        }
        // initialize the heading tracking
        m_headingRevs = 0;
        Orientation angles = m_imu.getAngularOrientation();
        m_headingRawLast = angles.firstAngle;
        m_heading = -m_headingRawLast;
    }

    /**
     * Sample the IMU and get the current heading.
     *
     * @return Returns the current robot heading.
     */
    public double getHeading() {
        // Any time there is power to the wheels or the robot is bumped the heading
        // will probably change. The IMU goes from
        // -180.0 to 180.0. The discontinuity at 180,-180 is a programming headache.
        // if you rotate through that is takes a bunch of special programming logic
        // to figure out where you are. Instead, we will monitor going through that
        // discontinuity and increment a rotation counter so our heading will start
        // at 0 when the IMU is initialized, and be a continuous function from
        // -infinity to +infinity.
        Orientation angles = m_imu.getAngularOrientation();
        double heading_raw = angles.firstAngle;
        if (m_headingRawLast < -140.0 && heading_raw > 0.0) {
            m_headingRevs -= 1;
        } else if (m_headingRawLast > 140.0 && heading_raw < 0.0) {
            m_headingRevs += 1;
        }
        // Our mental model says clockwise rotation (turning right) is a positive
        // rotation for the power, so we will sign correct heading to match.
        m_heading = -(m_headingRevs * 360.0 + heading_raw);
        m_headingRawLast = heading_raw;
        return m_heading;
    }

    public double getExpectedHeading() {
        return m_expectedHeading;
    }

    public void resetExpectedHeading() {
        m_expectedHeading = m_heading;
    }

    public void resetExpectedHeading(double newExpectedHeading) {
        m_expectedHeading = newExpectedHeading;
    }

    public void SetTankPower(double left, double right, double sideways) {
        // figure out if the right and left values need to be scaled. If you have full X and full Y, then the sum
        // could be greater than 1.
        double scale = 1.0;
        double maxLeft = Math.abs(left) + Math.abs(sideways);
        double maxRight = Math.abs(right) + Math.abs(sideways);
        if (maxLeft > maxRight) {
            if (maxLeft > 1.0) {
                scale = 1.0 / maxLeft;
            }
        } else {
            if (maxRight > 1.0) {
                scale = 1.0 / maxRight;
            }
        }
        // Now apply the scaled power.
        double powerRF = scale * (right - sideways);
        double powerRR = scale * (right + sideways);
        double powerFL = scale * (left + sideways);
        double powerLR = scale * (left - sideways);
        // set the power to each of the motors
        setMotorPower(powerFL, powerRF, powerRR, powerLR);
    }

    public void setArcadePower(double forward, double sideways, double rotate) {
        // OK, so the maximum-minimum is the sum of the absolute values of forward, side, and turn
        double scale = 1.0;
        double max = Math.abs(forward) + Math.abs(sideways) + Math.abs(rotate);
        if (max > 1.0) {
            scale = 1.0 / max;
        }
        // Compute the power for each of the motors
        double powerRF = scale * (forward - sideways - rotate);
        double powerRR = scale * (forward + sideways - rotate);
        double powerFL = scale * (forward + sideways + rotate);
        double powerLR = scale * (forward - sideways + rotate);
        // set the power to each of the motors
        setMotorPower(powerFL, powerRF, powerRR, powerLR);
    }

    /**
     * Explicitly set the power for each of the motors.
     *
     * @param powerFL (double) The power for the left-front motor.
     * @param powerRF (double) The power for the right-front motor.
     * @param powerRR (double) The power for the right-rear motor.
     * @param powerLR (double) The power for the left-rear motor.
     */
    public void setMotorPower(double powerFL, double powerRF, double powerRR, double powerLR) {
        m_motorFL.setPower(m_powerFL = powerFL);
        m_motorFR.setPower(m_powerFR = powerRF);
        m_motorRR.setPower(m_powerRR = powerRR);
        m_motorLR.setPower(m_powerLR = powerLR);
    }

    public double getFrontLeftPower() { return m_powerFL; }

    public double getRightLeftPower() { return m_powerFR; }

    public double getRightRearPower() { return m_powerRR; }

    public double getLeftRearPower() { return m_powerLR; }

    public int getFrontLeftEncoder() { return m_motorFL.getCurrentPosition(); }

    public int getFrontRightEncoder() { return m_motorFR.getCurrentPosition(); }

    public int getRightRearEncoder() { return m_motorRR.getCurrentPosition(); }

    public int getLeftRearEncoder() { return m_motorLR.getCurrentPosition(); }
}
