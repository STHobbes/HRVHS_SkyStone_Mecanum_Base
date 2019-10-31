package org.firstinspires.ftc.teamcode.hrvhs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * This is the base class for an HRVHS op mode for a robot.
 */
public abstract class AHrvhsOpMode extends LinearOpMode {

    // OK, we are going to try to do a uniform loop interval - as a default lets use the FRC interval of 20ms.
    long m_idealLoopInterval = 20;
    // <tt><rue</tt> if loop time should be reported in telemetry; <tt>false</tt> otherwise
    boolean m_reportLoopTime = false;
    // The tile runner mecanum drive
    protected MecanumDriveSubsystem m_driveSubsystem = new MecanumDriveSubsystem();


    /**
     * Set an 'ideal' loop interval in milliseconds. The default is 20ms asused in FRC code.
     * @param idealLoopInterval
     */
    public void setIdealLoopInterval(long idealLoopInterval) {
        m_idealLoopInterval = idealLoopInterval;
    }

    /**
     * Get the current 'ideal' loop interval in milliseconds.
     * @return The 'ideal' loop interval in milliseconds.
     */
    public long getIdealLoopInterval() {
        return m_idealLoopInterval;
    }

    /**
     * Set whether the loop time should be reported at the end of the telemetry output, the drfault is <tt>false</tt>.
     * @param reportLoopTime <tt>true</tt> if loop time should be reported in telemetry; <tt>false</tt> otherwise.
     */
    public void reportLoopTime(boolean reportLoopTime) {
        m_reportLoopTime = reportLoopTime;
    }

    @Override
    public final void runOpMode() throws InterruptedException {
        // get the scheduler, it controls how everything runs - set the op mode so the scheduler has
        // access to state, input, and telemetry.
        Scheduler scheduler = Scheduler.getInstance();
        scheduler.setOpMode(this);
        // pre-start initialize cycle through the subsystems and add all the default cammands to the
        // scheduled commands.
        preStartInitialize();
        scheduler.preStartInitialize();

        // wait for the start to be processed on the driver station
        waitForStart();

        // do post-start initialization (gyro initialization is often post-start
        postStartInitialize();
        scheduler.postStartInitialize();

        // run the control loop - keep track of the loop speed
        int controlLoopCt = 0;
        long startTime = System.currentTimeMillis();
        long loopEndTime = startTime + m_idealLoopInterval;
        while (opModeIsActive()) {
            // run the commands
            scheduler.run();
            // enforce a uniform loop time
            controlLoopCt++;
            long now = System.currentTimeMillis();
            if (now < loopEndTime) {
                Thread.sleep(loopEndTime - now);
                now = System.currentTimeMillis();
            }
            loopEndTime = now + m_idealLoopInterval;
            // report the loop time if desired
            if (m_reportLoopTime) {
                double loopTime = (double) (now - startTime) / controlLoopCt;
                telemetry.addData("loop time:", "%6.1f ms", loopTime);
            }
            // and update the telemetry on the robot control phone
            telemetry.update();
        }
    }

    /**
     * Called immediately in the runOpMode() before the waitForStart(). Override this method to perform initialization
     * required befor the start of the OpMode.
     */
    protected abstract void preStartInitialize();

    /**
     * Called immediately after waitForStart() returns and before the command loop is started. Override this method for
     * initialization that must wait until the control loop starts.
     */
    protected abstract void postStartInitialize();
}


