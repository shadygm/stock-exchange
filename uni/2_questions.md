# Question 1

In the assignment, you had to create a `MessageHandler` interface. Please answer the following two questions:

1. Describe the benefits of using this `MessageHandler` interface. (~50-100 words)
2. Instead of creating an implementation of `MessageHandler` that invokes a command handler, we could also 
pass the command handler to the client/server directly without the middle man of the `MessageHandler` 
implementation. What are the implications of this? (~50-100 words)

___

**Answer**:
1. The benefits of using the `MessageHandler` interface are numerous. Firstly, the interface helps 
achieve abstraction as it provides a clear and standardized way to handle messages, abstracting away 
the specific implementation details. Moreover, code becomes easily extensible as one can create multiple 
implementations of `MessageHandler` to handle different types of messages or even perform different 
actions on those messages. Lastly, code separation is beneficial as it promotes separation of concerns 
by isolating message handling logic from the rest of the application, making the codebase more maintainable.
2. Passing the command handler directly to the client/server without using the `MessageHandler` can have 
several implications. Firstly, tt may lead to tight coupling between the client/server and the command handler. 
That means that the code becomes less flexible and harder to change or extend. Furthermore, reuse of the 
command handler in other contexts would become more challenging since it's directly tied to the client/server. 
Lastly, testing would become more complex as mocking or replacing of the command handler directly would have 
to occur, rather than simply providing different implementations of the interface for testing purposes.
___

# Question 2

One of your colleagues wrote the following class:

```java
public class RookieImplementation {

    private final Car car;

    public RookieImplementation(Car car) {
        this.car = car;
    }

    public void carEventFired(String carEvent) {
        if ("steer.left".equals(carEvent)) {
            car.steerLeft();
        } else if ("steer.right".equals(carEvent)) {
            car.steerRight();
        } else if ("engine.start".equals(carEvent)) {
            car.startEngine();
        } else if ("engine.stop".equals(carEvent)) {
            car.stopEngine();
        } else if ("pedal.gas".equals(carEvent)) {
            car.accelerate();
        } else if ("pedal.brake".equals(carEvent)) {
            car.brake();
        }
    }
}
```

This code makes you angry. Briefly describe why it makes you angry and provide the improved code below.

___

**Answer**: This code makes us furious because it violates the principles of maintainability and scalability
that we have been taught in this class. It uses a series of if-else statements to map different car events 
to corresponding methods. This can become unmanageable and prone to errors as the number of events and actions 
grows. Thus, this code lacks extensibility and is not easily maintainable.

Improved code:

```java
public class MasterMindImplementation {

    private final Map<String, Command> commands = new HashMap<>();
    private final Car car;

    public MasterMindImplementation(Car car) {
        this.car = car;
        initCommands();
    }

    private void initCommands() {
        commands.put("steer.left", new SteerLeftCommand(car));
        commands.put("steer.right", new SteerRightCommand(car));
        commands.put("engine.start", new StartEngineCommand(car));
        commands.put("engine.stop", new StopEngineCommand(car));
        commands.put("pedal.gas", new AccelerateCommand(car));
        commands.put("pedal.brake", new BrakeCommand(car));
    }

    public void carEventFired(String carEvent) {
        Command command = commands.get(carEvent);
        if (command != null) {
            command.execute();
        } else {
			return "Unknown command: " + carEvent; // or even log an error 
        }
    }
}
```
This time, each car event is associated with a specific command, encapsulating the corresponding action. 
The code, thus, becomes more maintainable, extensible and adheres to the principles of the Command Pattern, 
making it easier to add new events and actions in the future without modifying the existing code as well as 
test it. 
___

# Question 3

You have the following exchange with a colleague:

> **Colleague**: "Hey, look at this! It's super handy. Pretty simple to write custom experiments."

```java
class Experiments {
    public static Model runExperimentA(DataTable dt) {
        CommandHandler commandSequence = new CleanDataTableCommand()
            .setNext(new RemoveCorrelatedColumnsCommand())
            .setNext(new TrainSVMCommand())

        Config config = new Options();
        config.set("broadcast", true);
        config.set("svmdatatable", dt);

        commandSequence.handle(config);

        return (Model) config.get("svmmodel");
    }

    public static Model runExperimentB() {
        CommandHandler commandSequence = new CleanDataTableCommand()
            .setNext(new TrainSGDCommand())

        Config config = new Options();
        config.set("broadcast", true);
        config.set("sgddatatable", dt);

        commandSequence.handle(config);

        return (Model) config.get("sgdmodel");
    }
}
```

> **Colleague**: "I could even create this method to train any of the models we have. Do you know how Jane did it?"

```java
class Processor {
    public static Model getModel(String algorithm, DataTable dt) {
        CommandHandler commandSequence = new TrainSVMCommand()
            .setNext(new TrainSDGCommand())
            .setNext(new TrainRFCommand())
            .setNext(new TrainNNCommand())

        Config config = new Options();
        config.set("broadcast", false);
        config.set(algorithm + "datatable", dt);

        commandSequence.handle(config);

        return (Model) config.get(algorithm + "model");
    }
}
```

> **You**: "Sure! She is using the command pattern. Easy indeed."
>
> **Colleague**: "Yeah. But look again. There is more; she uses another pattern on top of it. I wonder how it works."

1. What is this other pattern? What advantage does it provide to the solution? (~50-100 words)

2. You know the code for `CommandHandler` has to be a simple abstract class in this case, probably containing four methods:
- `CommandHandler setNext(CommandHandler next)` (implemented in `CommandHandler`),
- `void handle(Config config)` (implemented in `CommandHandler`),
- `abstract boolean canHandle(Config config)`,
- `abstract void execute(Config config)`.

Please provide a minimum working example of the `CommandHandler` abstract class.

___

**Answer**:

1. The other pattern Jane is using in her code is the Factory Method pattern. This can be seen in her 
getModel method, which acts as a factory method that dynamically creates and configures instances of 
different model training commands (SVM, SDG, RF, NN) which are based on the provided algorithm parameter. 
The advantage this pattern provides to the solution is that dynamic creation of objects (models) 
without exposing the instantiation logic to the client code occurs. Encapsulation of the object creation 
logic, makes the code more flexible, extensible and maintainable. That is because it allows for new models 
to be easily added without modification of the existing code, promoting a more modular and scalable design 
as well as a clean separation of concerns between the client code and the specific algorithm implementations.

2.
```java
public abstract class CommandHandler {
    
	private CommandHandler nextHandler;

    public CommandHandler setNext(CommandHandler next) {
        this.nextHandler = next;
        return next;
    }

    public void handle(Config config) {
        if (canHandle(config)) {
            execute(config);
        } else if (nextHandler != null) {
            nextHandler.handle(config);
        } else {
            System.out.println("no handler can process the configuration");
        }
    }

    abstract boolean canHandle(Config config); // check if this handler can handler the given config
	abstract void execute(Config config); // execute the command based on the config 
}
```
This abstract class shows the basic structure for a command handler. It included the ability to set the 
next handler in the chain, handling the command (in case it can be handled), passing it to the next handler 
if needed and providing abstract methods for checking whether the handler can process the configuration and 
for executing the command. Subclasses of `CommandHandler` would implement the `canHandle` and execute different 
methods specific to their commands.

___