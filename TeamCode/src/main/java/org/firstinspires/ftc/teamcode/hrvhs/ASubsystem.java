package org.firstinspires.ftc.teamcode.hrvhs;

public abstract class ASubsystem {
    // Every subsystem should have a name so we can use it for messaging.
    private String m_name;
    // The default command for this subsystem
    private ACommand m_defaultCommand = null;
    // The current command.
    private ACommand m_currentCommand;

    ASubsystem(String name) {
        m_name = name;
        Scheduler.getInstance().registerSubsystem(this);
    }

    ASubsystem() {
        m_name = getClass().getSimpleName();
        Scheduler.getInstance().registerSubsystem(this);
    }

    public String getName() {
        return m_name;
    }

    public void setDefaultCommand(ACommand defaultCommand) {
        m_defaultCommand = defaultCommand;
    }

    public ACommand getDefaultCommand() {
        return m_defaultCommand;
    }

    /**
     * Called in the AHrvhsOpMode.runOpMode before the start (after you push init on the robot drive phone). Override this
     * for initialization that requires the hardware map to be setup and available.
     *
     * @param opMode (not null) The op mode, which gives you access to the hardware map.
     */
    public void preStartInitialize(AHrvhsOpMode opMode) {}

    /**
     * Called in the AHrvhsOpMode.runOpMode immediately after the start (after you push the run on the robot drive phone).
     * Override this for initialization that should be postponed until start (like gyro initialization).
     */
    public void postStartInitialize() {}

    /**
     * Sets the current command.
     *
     * @param command the new current command
     */
    void setCurrentCommand(ACommand command) {
        m_currentCommand = command;
    }

    /**
     * Returns the command which currently claims this subsystem.
     *
     * @return the command which currently claims this subsystem
     */
    public ACommand getCurrentCommand() {
        return m_currentCommand;
    }
}
