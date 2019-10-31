package org.firstinspires.ftc.teamcode.hrvhs;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import static java.util.Objects.requireNonNull;

/**
 * A {@link CommandGroup} is a list of commands which are executed in sequence.
 *
 * <p> Commands in a {@link CommandGroup} are added using the {@link
 * CommandGroup#addSequential(ACommand) addSequential(...)} method and are called sequentially.
 * {@link CommandGroup CommandGroups} are themselves {@link ACommand commands} and can be given to
 * other {@link CommandGroup CommandGroups}. </p>
 *
 * <p> {@link CommandGroup CommandGroups} will carry all of the requirements of their {@link ACommand
 * subcommands}. Additional requirements can be specified by calling {@link
 * CommandGroup#requires(ASubsystem) requires(...)} normally in the constructor. </P>
 *
 * <p> CommandGroups can also execute commands in parallel, simply by adding them using {@link
 * CommandGroup#addParallel(ACommand) addParallel(...)}. </p>
 *
 * @see ACommand
 * @see ASubsystem
 * @see IllegalUseOfCommandException
 */
public class CommandGroup extends ACommand {
    /**
     * The commands in this group (stored in entries).
     */
    private final ArrayList<Entry> m_commands = new ArrayList<>();
    /*
     * Intentionally package private
     */
    /**
     * The active children in this group (stored in entries).
     */
    final ArrayList<Entry> m_children = new ArrayList<>();
    /**
     * The current command, -1 signifies that none have been run.
     */
    private int m_currentCommandIndex = -1;

    /**
     * Creates a new {@link CommandGroup CommandGroup}. The name of this command will be set to its
     * class name.
     *
     * @param opMode (not null) The op mode for the command - which has access to the input devices
     *               the command may require.
     * @param name The name of the command for logging/debugging. If <tt>null</tt> the class name
     *             is used as the name of the command
     */
    public CommandGroup(AHrvhsOpMode opMode, String name) {
        super(opMode, name);
    }

    /**
     * Adds a new {@link ACommand ACommand} to the group. The {@linkA Command ACommand} will be started
     * after all the previously added {@link ACommand ACommands}.
     *
     * <p> Note that any requirements the given {@link ACommand ACommand} has will be added to the
     * group. For this reason, a {@link ACommand ACommand's} requirements can not be changed after being
     * added to a group. </p>
     *
     * <p> It is recommended that this method be called in the constructor. </p>
     *
     * @param command The {@link ACommand ACommand} to be added
     * @throws IllegalUseOfCommandException if the group has been started before or been given to
     *                                      another group
     * @throws IllegalArgumentException     if command is null
     */
    public final void addSequential(ACommand command) {
        validate("Can not add new command to command group");
        if (command == null) {
            throw new IllegalArgumentException("Given null command");
        }

        command.setParent(this);

        m_commands.add(new Entry(command, Entry.IN_SEQUENCE));
        for (Iterator<ASubsystem> e = command.getRequirements(); e.hasNext(); ) {
            requires(e.next());
        }
    }

    /**
     * Adds a new {@link ACommand ACommand} to the group with a given timeout. The {@link ACommand
     * ACommand} will be started after all the previously added commands.
     *
     * <p> Once the {@link ACommand ACommand} is started, it will be run until it finishes or the time
     * expires, whichever is sooner. Note that the given {@link ACommand ACommand} will have no
     * knowledge that it is on a timer. </p>
     *
     * <p> Note that any requirements the given {@link ACommand ACommand} has will be added to the
     * group. For this reason, a {@link ACommand ACommand's} requirements can not be changed after being
     * added to a group. </p>
     *
     * <p> It is recommended that this method be called in the constructor. </p>
     *
     * @param command The {@link ACommand ACommand} to be added
     * @param timeout The timeout (in seconds)
     * @throws IllegalUseOfCommandException if the group has been started before or been given to
     *                                      another group or if the {@link ACommand ACommand} has been
     *                                      started before or been given to another group
     * @throws IllegalArgumentException     if command is null or timeout is negative
     */
    public final void addSequential(ACommand command, double timeout) {
        validate("Can not add new command to command group");
        if (command == null) {
            throw new IllegalArgumentException("Given null command");
        }
        if (timeout < 0) {
            throw new IllegalArgumentException("Can not be given a negative timeout");
        }

        command.setParent(this);

        m_commands.add(new Entry(command, Entry.IN_SEQUENCE, timeout));
        for (Iterator<ASubsystem> e = command.getRequirements(); e.hasNext(); ) {
            requires(e.next());
        }
    }

    /**
     * Adds a new child {@link ACommand ACommand} to the group. The {@link ACommand ACommand} will be
     * started after all the previously added {@link ACommand ACommands}.
     *
     * <p> Instead of waiting for the child to finish, a {@link CommandGroup} will have it run at the
     * same time as the subsequent {@link ACommand ACommands}. The child will run until either it
     * finishes, a new child with conflicting requirements is started, or the main sequence runs a
     * {@link ACommand ACommand} with conflicting requirements. In the latter two cases, the child
     * will be canceled even if it says it can't be interrupted. </p>
     *
     * <p> Note that any requirements the given {@link ACommand ACommand} has will be added to the
     * group. For this reason, a {@link ACommand ACommand's} requirements can not be changed after being
     * added to a group. </p>
     *
     * <p> It is recommended that this method be called in the constructor. </p>
     *
     * @param command The command to be added
     * @throws IllegalUseOfCommandException if the group has been started before or been given to
     *                                      another command group
     * @throws IllegalArgumentException     if command is null
     */
    public final void addParallel(ACommand command) {
        requireNonNull(command, "Provided command was null");
        validate("Can not add new command to command group");

        command.setParent(this);

        m_commands.add(new Entry(command, Entry.BRANCH_CHILD));
        for (Iterator<ASubsystem> e = command.getRequirements(); e.hasNext(); ) {
            requires(e.next());
        }
    }

    /**
     * Adds a new child {@link ACommand ACommand} to the group with the given timeout. The {@link ACommand ACommand} will
     * be started after all the previously added {@link ACommand ACommands}.
     *
     * <p> Once the {@link ACommand ACommand} is started, it will run until it finishes, is interrupted,
     * or the time expires, whichever is sooner. Note that the given {@link ACommand ACommand} will have
     * no knowledge that it is on a timer. </p>
     *
     * <p> Instead of waiting for the child to finish, a {@link CommandGroup} will have it run at the
     * same time as the subsequent {@link ACommand ACommands}. The child will run until either it
     * finishes, the timeout expires, a new child with conflicting requirements is started, or the
     * main sequence runs a {@link ACommand ACommand} with conflicting requirements. In the latter two cases,
     * the child will be canceled even if it says it can't be interrupted. </p>
     *
     * <p> Note that any requirements the given {@link ACommand ACommand} has will be added to the
     * group. For this reason, a {@link ACommand ACommand's} requirements can not be changed after being
     * added to a group. </p>
     *
     * <p> It is recommended that this method be called in the constructor. </p>
     *
     * @param command The command to be added
     * @param timeout The timeout (in seconds)
     * @throws IllegalUseOfCommandException if the group has been started before or been given to
     *                                      another command group
     * @throws IllegalArgumentException     if command is null
     */
    public final void addParallel(ACommand command, double timeout) {
        requireNonNull(command, "Provided command was null");
        if (timeout < 0) {
            throw new IllegalArgumentException("Can not be given a negative timeout");
        }
        validate("Can not add new command to command group");

        command.setParent(this);

        m_commands.add(new Entry(command, Entry.BRANCH_CHILD, timeout));
        for (Iterator<ASubsystem> e = command.getRequirements(); e.hasNext(); ) {
            requires(e.next());
        }
    }

    @Override
    void _initialize() {
        m_currentCommandIndex = -1;
    }

    @Override
    void _execute() {
        Entry entry = null;
        ACommand cmd = null;
        boolean firstRun = false;
        if (m_currentCommandIndex == -1) {
            firstRun = true;
            m_currentCommandIndex = 0;
        }

        while (m_currentCommandIndex < m_commands.size()) {
            if (cmd != null) {
                if (entry.isTimedOut()) {
                    cmd._cancel();
                }
                if (cmd.run()) {
                    break;
                } else {
                    cmd.removed();
                    m_currentCommandIndex++;
                    firstRun = true;
                    cmd = null;
                    continue;
                }
            }

            entry = m_commands.get(m_currentCommandIndex);
            cmd = null;

            switch (entry.m_state) {
                case Entry.IN_SEQUENCE:
                    cmd = entry.m_command;
                    if (firstRun) {
                        cmd.startRunning();
                        cancelConflicts(cmd);
                    }
                    firstRun = false;
                    break;
                case Entry.BRANCH_PEER:
                    m_currentCommandIndex++;
                    entry.m_command.start();
                    break;
                case Entry.BRANCH_CHILD:
                    m_currentCommandIndex++;
                    cancelConflicts(entry.m_command);
                    entry.m_command.startRunning();
                    m_children.add(entry);
                    break;
                default:
                    break;
            }
        }

        // Run Children
        for (int i = 0; i < m_children.size(); i++) {
            entry = m_children.get(i);
            ACommand child = entry.m_command;
            if (entry.isTimedOut()) {
                child._cancel();
            }
            if (!child.run()) {
                child.removed();
                m_children.remove(i--);
            }
        }
    }

    @Override
    void _end() {
        // Theoretically, we don't have to check this, but we do if teams override
        // the isFinished method
        if (m_currentCommandIndex != -1 && m_currentCommandIndex < m_commands.size()) {
            ACommand cmd = m_commands.get(m_currentCommandIndex).m_command;
            cmd._cancel();
            cmd.removed();
        }

        for (Iterator<Entry> e = m_children.iterator(); e.hasNext(); ) {
            ACommand cmd = e.next().m_command;
            cmd._cancel();
            cmd.removed();
        }
        m_children.clear();
    }

    @Override
    void _interrupted() {
        _end();
    }

    /**
     * Returns true if all the {@link ACommand ACommands} in this group have been started and have
     * finished.
     *
     * <p> Teams may override this method, although they should probably reference super.isFinished()
     * if they do. </p>
     *
     * @return whether this {@link CommandGroup} is finished
     */
    @Override
    protected boolean isFinished() {
        return m_currentCommandIndex >= m_commands.size() && m_children.isEmpty();
    }

    // Can be overwritten by teams
    @Override
    protected void initialize() {
    }

    // Can be overwritten by teams
    @Override
    protected void execute() {
    }

    // Can be overwritten by teams
    @Override
    protected void end() {
    }

    // Can be overwritten by teams
    @Override
    protected void interrupted() {
    }

    /**
     * Returns whether or not this group is interruptible. A command group will be uninterruptible if
     * {@link CommandGroup#setInterruptible(boolean) setInterruptable(false)} was called or if it is
     * currently running an uninterruptible command or child.
     *
     * @return whether or not this {@link CommandGroup} is interruptible.
     */
    @Override
    public synchronized boolean isInterruptible() {
        if (!super.isInterruptible()) {
            return false;
        }

        if (m_currentCommandIndex != -1 && m_currentCommandIndex < m_commands.size()) {
            ACommand cmd = m_commands.get(m_currentCommandIndex).m_command;
            if (!cmd.isInterruptible()) {
                return false;
            }
        }

        for (int i = 0; i < m_children.size(); i++) {
            if (!m_children.get(i).m_command.isInterruptible()) {
                return false;
            }
        }

        return true;
    }

    private void cancelConflicts(ACommand command) {
        for (int i = 0; i < m_children.size(); i++) {
            ACommand child = m_children.get(i).m_command;

            for (Iterator<ASubsystem> e = command.getRequirements(); e.hasNext(); ) {
                ASubsystem requirement = e.next();
                if (child.doesRequire(requirement)) {
                    child._cancel();
                    child.removed();
                    m_children.remove(i--);
                    break;
                }
            }
        }
    }

    private static class Entry {
        private static final int IN_SEQUENCE = 0;
        private static final int BRANCH_PEER = 1;
        private static final int BRANCH_CHILD = 2;
        private final ACommand m_command;
        private final int m_state;
        private final double m_timeout;

        Entry(ACommand command, int state) {
            m_command = command;
            m_state = state;
            m_timeout = -1;
        }

        Entry(ACommand command, int state, double timeout) {
            m_command = command;
            m_state = state;
            m_timeout = timeout;
        }

        boolean isTimedOut() {
            if (m_timeout == -1) {
                return false;
            } else {
                double time = m_command.timeSinceInitialized();
                return time != 0 && time >= m_timeout;
            }
        }
    }
}
