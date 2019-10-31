## Command Based Programming

This project aims at providing Hood River High School FTC teams a common coding starting point using
a command-based programming model similar to FRC. Many of our FTC programmers will move to the FRC
team once the FTC season is complete. We would like to give those programmer's the best opportunity
for success in both FTC and FRC.

## TeamCode Module

Your team code goes here. Jeff and I imagine there are 5 programs you will need:
* autonomous - make a copy of the `AutoTemplate` and customize it for each of these:
  * red runner
  * red builder
  * blue runner
  * blue builder
  
  NOTE: red and blue are mirror images - if a team is particularly creative, they could combine the
  red-blue programs to minimize the code they write.
* driver - make a copy of the `DriverTemplate` and customize it for this - There will usually be a
  single driver program, or, there may be builder and runner specific driver programs.
  
## Command Based Programming
  
This base library (in the `hrvhs` folder) borrows heavily from the
[WPILib](https://github.com/wpilibsuite/allwpilib) library used in FRC. This library is
greatly simplified by the assumption that there is a single control thread.

Refer to [Command based programming](https://wpilib.screenstepslive.com/s/currentCS/m/java/c/88893wpilib%20github)
for an overview or command based programming in the context of FRC. The main points are that:
* There are commands that cause the robot to do something
* There are collections of actuators (motors, servos) that should only be controlled by one command at a time.
  These collections of actuators are subsystems.
* Simple commands can be aggregated into command groups to perform very complex multi-operation tasks.
* It should be simple to connect commands to buttons and triggers for driver control

The next sections describe the specifics of this library for handling the points above.

### Building an Op Mode

Command-based programming is a little strange after typical FTC blocks or on-bot-java programming. The big
difference is that in blocks or on-bot-java programming you are programming the command loop. In command-based
programming the framework controls the loop, and the commands are run by the framework - you just worry about
building and scheduling commands. The commands control what happens in every control loop cycle - which
is run by the framework.

The most basic (i.e. has no functionality) OpMode would be an extension of the `AHrvhsOpMode`; but, we have
built some code on top of that to help you get started. These Op Modes have been programmed and tested, and
may give you a good base class your your programming.

### Commands

### Subsystems

### Command Groups

### Buttons and Triggers in Driver Control
