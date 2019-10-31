package org.firstinspires.ftc.teamcode.hrvhs;

import java.util.*;

/**
 * This is the command scheduler. This scheduler is modelled after the FRC Scheduler in the WPILib library. It keeps track of
 * the currently registered subsystems and commands and is run in a loop by the Op Mode. The Scheduler is instantiated
 * by the first caller of getInstance(). As such, its life cycle is not bound to the lifecycle of other components.
 * <p>
 * It is probably instantiated by the first instantiated subsystem or first instantiated input event watcher.
 */
public final class Scheduler {

    private static Scheduler s_instance;

    // The OpMode that 'owns' this scheduler.
    private AHrvhsOpMode m_opMode = null;
    // The list of subsystems known to this scheduler.
    List<ASubsystem> m_subsystems = new ArrayList<>();
    // The commands that are currently running are in a doubly linked list with a hash table to help find specific
    //  elements in the list.
    Hashtable<ACommand, DoublyLinkedListElement> m_commands = new Hashtable<>();
    DoublyLinkedListElement m_firstCommand = null;
    DoublyLinkedListElement m_lastCommand = null;
    // The list of pending command additions
    Vector<ACommand> m_additions = new Vector<>(10, 10);      // NOTE: this is a synchronized implementation

//    Vector<ButtonScheduler> m_buttons =  new Vector<>(10,10);

    boolean m_runningCommandsChanged = false;

    // A state variable that prevents a recursive loop during command addition.
    boolean m_adding = false;

    public static synchronized Scheduler getInstance() {
        if (s_instance == null) {
            s_instance = new Scheduler();
        }
        return s_instance;
    }

    public void setOpMode(AHrvhsOpMode opModel) {
        m_opMode = opModel;
    }

    public AHrvhsOpMode getOpMode() {
        return m_opMode;
    }

    /**
     * Registers a {@link ASubsystem} to this {@link Scheduler}, so that the {@link Scheduler} might
     * know if a default {@link ACommand} needs to be run. All {@link ASubsystem Subsystems} should call
     * this.
     *
     * @param subsystem the subsystem
     */
    public void registerSubsystem(ASubsystem subsystem) {
        m_subsystems.add(subsystem);
    }

    public void preStartInitialize() {
        // initialize the subsystems
        for (ASubsystem subsystem : m_subsystems) {
            subsystem.preStartInitialize(m_opMode);
        }
        // Add the default commands
        for (ASubsystem subsystem : m_subsystems) {
            if ((subsystem.getCurrentCommand() == null) && (subsystem.getDefaultCommand() != null)) {
                _add(subsystem.getDefaultCommand());
            }
        }
    }

    public void postStartInitialize() {
        for (ASubsystem subsystem : m_subsystems) {
            subsystem.postStartInitialize();
        }
    }

    /**
     * Schedule a command to be added for the next commend execution cycle. Commands are added after the currently
     * scheduled commands execute.
     *
     * @param command (not null) The command to be scheduled.
     */
    public void add(ACommand command) {
        if (null != command) {
            m_additions.add(command);
        }
    }

//    public void addButton(ButtonScheduler button) {
//        m_buttons.add();
//    }

    /**
     * Adds a command immediately to the {@link Scheduler}. This should only be called in the {@link
     * Scheduler#run()} loop. Any command with conflicting requirements will be removed, unless it is
     * uninterruptable. Giving <code>null</code> does nothing.
     *
     * @param command the {@link ACommand} to add
     */
    private void _add(ACommand command) {
        if (command == null) {
            return;
        }

        // Check to make we are not already adding a command
        if (m_adding) {
            System.err.println("WARNING: Can not start command from cancel method.  Ignoring:" + command);
            return;
        }

        // Only add the command if it is not already running
        if (!m_commands.containsKey(command)) {
            // Check that the requirements can be obtained. If a required subsystem is in use check whether
            // the command using it is interruptable
            for (Iterator<ASubsystem> e = command.getRequirements(); e.hasNext(); ) {
                ASubsystem requirement = e.next();
                if (requirement.getCurrentCommand() != null && !requirement.getCurrentCommand().isInterruptible()) {
                    // The subsystem is in use by a command that is not interruptable, can't add this new command
                    return;
                }
            }

            // We know we can get all the required subsystems for this command, so cancel/remove any commands that
            // are currently running on those subsystems and reset the current command to this command
            m_adding = true;
            for (Iterator<ASubsystem> e = command.getRequirements(); e.hasNext(); ) {
                ASubsystem requirement = e.next();
                if (requirement.getCurrentCommand() != null) {
                    requirement.getCurrentCommand().cancel();
                    remove(requirement.getCurrentCommand());
                }
                requirement.setCurrentCommand(command);
            }
            m_adding = false;

            // Add this command to the table of currently running commands and to the end of the linked list
            // of currently running commands.
            DoublyLinkedListElement element = new DoublyLinkedListElement();
            element.setData(command);
            if (m_firstCommand == null) {
                m_firstCommand = m_lastCommand = element;
            } else {
                m_lastCommand.add(element);
                m_lastCommand = element;
            }
            m_commands.put(command, element);

            m_runningCommandsChanged = true;

            command.startRunning();
        }
    }

    /**
     * Runs a single iteration of the loop. This method should be called often in order to have a
     * functioning {@link ACommand} system. The loop has five stages:
     *
     * <ol>
     *     <li>Poll the Buttons</li>
     *     <li>Execute/Remove the Commands</li>
     *     <li>Add Commands</li>
     *     <li>Add Defaults</li> </ol>
     */
    public void run() {
        m_runningCommandsChanged = false;

//        // Get button input (going backwards preserves button priority)
//        if (m_buttons != null) {
//            for (int i = m_buttons.size() - 1; i >= 0; i--) {
//                m_buttons.elementAt(i).execute();
//            }
//        }

        // Loop through the commands that are currently on the command list
        DoublyLinkedListElement element = m_firstCommand;
        while (element != null) {
            ACommand command = element.getData();
            m_opMode.telemetry.addData("  req:", command.getName());
            element = element.getNext();
            if (!command.run()) {
                remove(command);
                m_runningCommandsChanged = true;
            }
        }

        // Add any commands that have been scheduled for addition
        for (int i = 0; i < m_additions.size(); i++) {
            _add(m_additions.elementAt(i));
        }
        m_additions.removeAllElements();

        // Add in the defaults
        for (ASubsystem subsystem : m_subsystems) {
            if ((subsystem.getCurrentCommand() == null) && (subsystem.getDefaultCommand() != null)) {
                _add(subsystem.getDefaultCommand());
            }
        }
    }

    /**
     * Removes the {@link ACommand} from the {@link Scheduler}.
     *
     * @param command the command to remove
     */
    void remove(ACommand command) {
        if (command == null || !m_commands.containsKey(command)) {
            return;
        }

        // Find the command in the command list and remove it from the command table and the lin ked list.
        DoublyLinkedListElement element = m_commands.get(command);
        m_commands.remove(command);

        if (element.equals(m_lastCommand)) {
            m_lastCommand = element.getPrevious();
        }
        if (element.equals(m_firstCommand)) {
            m_firstCommand = element.getNext();
        }
        element.remove();

        // Reset the current command on the required subsystems to null
        for (Iterator<ASubsystem> e = command.getRequirements(); e.hasNext(); ) {
            e.next().setCurrentCommand(null);
        }

        command.removed();
    }

    /**
     * Removes all commands.
     */
    public void removeAll() {
        // TODO: Confirm that this works with "uninteruptible" commands
        while (m_firstCommand != null) {
            remove(m_firstCommand.getData());
        }
    }

}
