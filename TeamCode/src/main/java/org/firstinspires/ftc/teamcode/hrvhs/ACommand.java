package org.firstinspires.ftc.teamcode.hrvhs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *  This is the abstract class for a command. A command is at the very core of the entire command
 *  framework. Every command can be
 *  started with a call to {@link ACommand#start() start()}. Once a command is started it will call
 *  {@link ACommand#initialize() initialize()}, and then will repeatedly call {@link ACommand#execute()
 *  execute()} until the {@link ACommand#isFinished() isFinished()} returns true. Once it does, {@link
 *  ACommand#end() end()} will be called.
 *
 *  <p>However, if at any point while it is running {@link ACommand#cancel() cancel()} is called,
 *  then the command will be stopped and {@link ACommand#interrupted() interrupted()} will be called.
 *
 *  <p>If a command uses an {@link ASubsystem}, then it should specify that it does so by including
 *  those subsystems in the call to the constructor.
 *
 *  <p>If a command is running and a new command with shared requirements is started, then one of
 *  two things will happen. If the active command is interruptible, then {@link ACommand#cancel()
 *  cancel()} will be called and the command will be removed to make way for the new one. If the
 *  active command is not interruptible, the other one will not even be started, and the active one
 *  will continue functioning.
 *
 *  @see ASubsystem
 *  @see CommandGroup
 *  @see IllegalUseOfCommandException
 */
public abstract class ACommand {

    // The name for this command
    private String m_name = "unspecified";

    // The op mode - you get access to the gamepad through the op mode
    protected AHrvhsOpMode m_opMode = null;

    // A list of the subsystems required by this command
    private final Set<ASubsystem> m_requirements = new HashSet<>();

    // The time (in seconds) before this command "times out" (or -1 if no timeout).
    private double m_timeout = -1;

    // <tt>true</tt>> if this command is in an execution state where it cannot be altered; <tt>false</tt>> otherwise.
    private boolean m_locked = false;

    // Whether or not it is interruptible.
    private boolean m_interruptible = true;

    // Whether or not it is running.
    private boolean m_running;

    // Whether or not this command has been initialized.
    private boolean m_initialized;

    // The start time as a system time
    private long m_startTime = -1;

    // Whether or not it has been canceled.
    private boolean m_canceled;

    // Whether or not this command has completed running.
    private boolean m_completed;

    // The {@link CommandGroup} this is in.
    private CommandGroup m_parent;

    private ACommand() {
        m_name = getClass().getSimpleName();
    }

    /**
     * Instantiate the ACommand
     *
     * @param opMode (not null) The op mode for the command - which has access to the input devices
     *               the command may require.
     * @param name The name of the command for logging/debugging. If <tt>null</tt> the class name
     *             is used as the name of the command
     * @param requirements The subsystems this command requires.
     */
    public ACommand(AHrvhsOpMode opMode, String name, ASubsystem ... requirements) {
        m_opMode = opMode;
        m_name = name;
        m_requirements.addAll(Arrays.asList(requirements));
    }

    public String getName() {
        return m_name;
    }

    /**
     * This method specifies that the given {@link ASubsystem} is used by this command. This method is
     * crucial to the functioning of the Command System in general.
     *
     * <p>Note that the recommended way to call this method is in the constructor.
     *
     * @param subsystem the {@link ASubsystem} required
     * @throws IllegalArgumentException     if subsystem is null
     * @throws IllegalUseOfCommandException if this command has started before or if it has been given
     *                                      to a {@link CommandGroup}
     * @see ASubsystem
     */
    protected void requires(ASubsystem subsystem) {
        validate("Can not add new requirement to command");
        if (subsystem != null) {
            m_requirements.add(subsystem);
        } else {
            throw new IllegalArgumentException("Subsystem must not be null.");
        }
    }

    /**
     * Returns the requirements (as an {@link Iterator Iterator} of {@link ASubsystem
     * Subsystems}) of this command.
     *
     * @return the requirements (as an {@link Iterator Iterator} of {@link ASubsystem
     * Subsystems}) of this command
     */
    Iterator<ASubsystem> getRequirements() {
        return m_requirements.iterator();
    }

    /**
     * Checks if the command requires the given {@link ASubsystem}.
     *
     * @param system the system
     * @return whether or not the subsystem is required, or false if given null
     */
    public boolean doesRequire(ASubsystem system) {
        return m_requirements.contains(system);
    }

    /**
     * Sets the timeout of this command.
     *
     * @param seconds the timeout (in seconds)
     * @throws IllegalArgumentException if seconds is negative
     * @see ACommand#isTimedOut() isTimedOut()
     */
    protected final void setTimeout(double seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Seconds must be positive.  Given:" + seconds);
        }
        m_timeout = seconds;
    }

    /**
     * Prevents further changes from being made.
     */
    synchronized void lockChanges() {
        m_locked = true;
    }

    /**
     * If changes are locked, then this will throw an {@link IllegalUseOfCommandException}.
     *
     * @param message the message to say (it is appended by a default message)
     */
    void validate(String message) {
        if (m_locked) {
            throw new IllegalUseOfCommandException(message
                    + " after being started or being added to a command group");
        }
    }

    /**
     * Returns the {@link CommandGroup} that this command is a part of. Will return null if this
     * {@link ACommand} is not in a group.
     *
     * @return the {@link CommandGroup} that this command is a part of (or null if not in group)
     */
    public CommandGroup getGroup() {
        return m_parent;
    }

    /**
     * Sets the parent of this command. No actual change is made to the group.
     *
     * @param parent the parent
     * @throws IllegalUseOfCommandException if this {@link ACommand} already is already in a group
     */
    synchronized void setParent(CommandGroup parent) {
        if (m_parent != null) {
            throw new IllegalUseOfCommandException(
                    "Can not give command to a command group after already being put in a command group");
        }
        lockChanges();
        m_parent = parent;
    }

    /**
     * Returns whether the command has a parent.
     *
     * @return true if the command has a parent.
     */
    synchronized boolean isParented() {
        return m_parent != null;
    }
    /**
     * Starts up the command. Gets the command ready to start. <p> Note that the command will
     * eventually start, however it will not necessarily do so immediately, and may in fact be
     * canceled before initialize is even called. </p>
     *
     * @throws IllegalUseOfCommandException if the command is a part of a CommandGroup
     */
    public void start() {
        lockChanges();
        if (m_parent != null) {
            throw new IllegalUseOfCommandException(
                    "Can not start a command that is a part of a command group");
        }
        Scheduler.getInstance().add(this);
        m_completed = false;
    }

    /**
     * Called when the command is interrupted by another command that needs one or more of the required subsystems, or
     * the command has been removed from the list of running commands in a {@link CommandGroup}. This will
     * call {@link ACommand#interrupted() interrupted()} or {@link ACommand#end() end()}.
     */
    synchronized void removed() {
        if (m_initialized) {
            if (isCanceled()) {
                interrupted();
                _interrupted();
            } else {
                end();
                _end();
            }
        }
        m_initialized = false;
        m_canceled = false;
        m_running = false;
        m_completed = true;
    }
    /**
     * The run method is used internally to actually run the commands.
     *
     * @return whether or not the command should stay within the {@link Scheduler}.
     */
    boolean run() {
        if (isCanceled()) {
            return false;
        }
        if (!m_initialized) {
            m_initialized = true;
            startTiming();
            _initialize();
            initialize();
        }
        _execute();
        execute();
        return !isFinished();
    }

    /**
     * A shadow method called before {@link ACommand#initialize() initialize()}.
     */
    void _initialize() {
    }

    /**
     * Called the first time the command is run. This is an opportunity for the command to record current encoders, reset
     * encoders, initialize field position, etc.
     */
    protected void initialize() {

    }

    /**
     * A shadow method called before {@link ACommand#execute() execute()}.
     */
    void _execute() {
    }
    /**
     * The execute method is called repeatedly until this Command either finishes or is canceled.
     */
    protected void execute() {}

    /**
     * Returns whether this command is finished. If it is, then the command will be removed and {@link
     * ACommand#end() end()} will be called.
     *
     * <p>It may be useful for a team to reference the {@link ACommand#isTimedOut() isTimedOut()}
     * method for time-sensitive commands.
     *
     * <p>Returning false will result in the command never ending automatically. It may still be
     * cancelled manually or interrupted by another command. Returning true will result in the
     * command executing once and finishing immediately. We recommend using {@link InstantCommand}
     * for this.
     *
     * @return whether this command is finished.
     * @see ACommand#isTimedOut() isTimedOut()
     */
    protected abstract boolean isFinished();

    /**
     * Called when the command ended peacefully. This is where you may want to wrap up loose ends,
     * like shutting off a motor that was being used in the command.
     */
    protected void end() {}

    /**
     * A shadow method called after {@link ACommand#end() end()}.
     */
    @SuppressWarnings("MethodName")
    void _end() {
    }

    /**
     * Called when the command ends because somebody called {@link ACommand#cancel() cancel()} or
     * another command shared the same requirements as this one, and booted it out.
     *
     * <p>This is where you may want to wrap up loose ends, like shutting off a motor that was being
     * used in the command.
     *
     * <p>Generally, it is useful to simply call the {@link ACommand#end() end()} method within this
     * method, as done here.
     */
    protected void interrupted() {
        end();
    }

    /**
     * A shadow method called after {@link ACommand#interrupted() interrupted()}.
     */
    @SuppressWarnings("MethodName")
    void _interrupted() {}

    /**
     * Called to indicate that the timer should start. This is called right before {@link
     * ACommand#initialize() initialize()} is, inside the {@link ACommand#run() run()} method.
     */
    private void startTiming() {
        m_startTime = System.currentTimeMillis();
    }

    /**
     * Returns the time since this command was initialized (in seconds). This function will work even
     * if there is no specified timeout.
     *
     * @return the time since this command was initialized (in seconds).
     */
    public final double timeSinceInitialized() {
        return m_startTime < 0 ? 0 : ((System.currentTimeMillis() - m_startTime) / 1000.0);
    }
    /**
     * Returns whether or not the {@link ACommand#timeSinceInitialized() timeSinceInitialized()} method
     * returns a number which is greater than or equal to the timeout for the command. If there is no
     * timeout, this will always return false.
     *
     * @return whether the time has expired
     */
    protected boolean isTimedOut() {
        return m_timeout != -1 && ((System.currentTimeMillis() - m_startTime) / 1000.0) >= m_timeout;
    }

    /**
     * This is used internally to mark that the command has been started. The lifecycle of a command
     * is:
     *
     * <p>startRunning() is called. run() is called (multiple times potentially) removed() is called.
     *
     * <p>It is very important that startRunning and removed be called in order or some assumptions of
     * the code will be broken.
     */
    void startRunning() {
        m_running = true;
        m_startTime = -1;
    }

    /**
     * Returns whether or not the command is running. This may return true even if the command has
     * just been canceled, as it may not have yet called {@link ACommand#interrupted()}.
     *
     * @return whether or not the command is running
     */
    public boolean isRunning() {
        return m_running;
    }

    /**
     * This will cancel the current command. <p> This will cancel the current command eventually. It
     * can be called multiple times. And it can be called when the command is not running. If the
     * command is running though, then the command will be marked as canceled and eventually removed.
     * </p> <p> A command can not be canceled if it is a part of a command group, you must cancel the
     * command group instead. </p>
     *
     * @throws IllegalUseOfCommandException if this command is a part of a command group
     */
    public void cancel() {
        if (m_parent != null) {
            throw new IllegalUseOfCommandException("Can not manually cancel a command in a command "
                    + "group");
        }
        _cancel();
    }

    /**
     * This works like cancel(), except that it doesn't throw an exception if it is a part of a
     * command group. Should only be called by the parent command group.
     */
    @SuppressWarnings("MethodName")
    void _cancel() {
        if (isRunning()) {
            m_canceled = true;
        }
    }

    /**
     * Returns whether or not this has been canceled.
     *
     * @return whether or not this has been canceled
     */
    public boolean isCanceled() {
        return m_canceled;
    }

    /**
     * Whether or not this command has completed running.
     *
     * @return whether or not this command has completed running.
     */
    public boolean isCompleted() {
        return m_completed;
    }

    /**
     * Returns whether or not this command can be interrupted.
     *
     * @return whether or not this command can be interrupted
     */
    public boolean isInterruptible() {
        return m_interruptible;
    }

    /**
     * Sets whether or not this command can be interrupted.
     *
     * @param interruptible whether or not this command can be interrupted
     */
    protected void setInterruptible(boolean interruptible) {
        m_interruptible = interruptible;
    }


}
