# Question 1

Suppose you are developing a similar (if not identical) project for a company. One teammate poses the following:

> "We do not have to worry about logging. The application is very small and tests should take care of any potential bugs. If we really need it, we can print some important data and just comment it out later."

Do you agree or disagree with the proposition? Please elaborate on your reason to agree or disagree. (~50-100 words)

___

**Answer**:
We disagree with the specific prompt. Logging is a means of tracking events when a specific software runs.
Although logging may indeed seem unnecessary for a small application, it is still a good practice to implement it.
As a fundamental practice in software development, it is important to keep track of the valuable insights into the
application's behavior and events that occur during its execution. Using logging helps with debugging issues and
can easily be used to monitor and diagnose problems in code production. The prompt suggests that the developers
should solely rely on tests to detect bugs. However, there may be cases where the tests do not cover all the
possible scenarios and bugs may still occur. Logging allows for identifying the source of the problem and the
circumstances in which it occurs. Including it from the beginning, makes it easier to identify and address any
issues that may arise later on.
___

# Question 2

Suppose you have the following `LinkedList` implementation:

![LinkedList](images/LinkedList.png)

How could you modify the `LinkedList` class so that the value could be any different data type? Preferably, provide the code of the modified class in the answer.
___

**Answer**:

```java
public class LinkedList<T> {
    private Node<T> head;
    private int size;
    
    public LinkedList() {
        head = null;
        size = 0;
    }

    //insert a value at the end of the linked list
    public void insert(T value) {
        Node<T> newNode = new Node<>(value);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    //deleting the first occurrence of a value in the linked list
    public void delete(T value) {
        if (head == null) {
            return;
        }

        if (head.data.equals(value)) {
            head = head.next;
            size--;
            return; 
        }

        Node<T> current = head;
        while (current.next != null) {
            if (current.next.data.equals(value)) {
                current.next = current.next.next;
                size--;
                return;
            }
            current = current.next;
        }
    }

    //getting the current size
    public int size() {
        return size;
    }

    //checking if the linked list is empty
    public boolean isEmpty() {
        return size == 0;
    }
}

//defining the Node class
public class Node<T> {
    T data;
    Node<T> next;

    public Node(T data) {
        this.data = data;
        this.next = null;
    }
}

```

___

# Question 3

How is Continuous Integration applied to (or enforced on) your assignment? (~30-100 words)

___

**Answer**:
Continuous Integration refers to the automated build and unit testing stages of the software development process. 
It is a practice that encourages developers to integrate their code into a shared repository several times a day.
In our assignment, Continuous Integration is applied through frequently merging our code changes into a central 
GitHub repository where builds and tests then run. Thus, building, testing and merging of all new code changes 
occur regularly to a shared repository, avoiding the existence of too many branches of our code that might conflict 
with each other.

___

# Question 4

One of your colleagues wrote the following class:

```java
import java.util.*;

public class MyMenu {

    private Map<Integer, PlayerAction> actions;

    public MyMenu() {
        actions = new HashMap<>();
        actions.put(0, DoNothingAction());
        actions.put(1, LookAroundAction());
        actions.put(2, FightAction());
    }

    public void printMenuOptions(boolean isInCombat) {
        List<String> menuOptions = new ArrayList<>();
        menuOptions.add("What do you want to?");
        menuOptions.add("\t0) Do nothing");
        menuOptions.add("\t1) Look around");
        if (isInCombat) {
            menuOptions.add("\t2) Fight!");
        }
        menuOptions.forEach(System.out::println);
    }

    public void doOption() {
        int option = getNumber();
        if (actions.containsKey(option)) {
            actions.get(option).execute();
        }
    }

    public int getNumber() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
}
```
List at least 2 things that you would improve, how it relates to test-driven development and why you would improve these things. Provide the improved code below.

___

**Answer**:

- The first improvement made that relates to test-driven development is the 
separation of the method **doOption()** to **getPlayerAction()** and a new **doOption()**. 
This improves testability as it creates a seperation of concern between the methods. 
Previously, the method doOption() was responsible for both getting the input from
the **getNumber()** and executing the option. However, now, with the separation,
each method is responsible for one thing. The **doOption()** is now responsible 
solely for executing the option, whilst the **getPlayerAction()** is responsible 
for getting the input from the user. This relates to testing as it reduces 
coupling, and allows for testing of individual components (executing the right 
option, reading the right input). 
- The second improvement made that relates to test-driven development is the 
injection of the **PlayerAction** dependencies directly within the constructor 
rather than initializing them at the initialization of the hashmap. The reason 
this improves testability is that, now, the **MyMenu** class no longer needs to 
have any information about how to set up the **PlayerAction** objects. Additionally, 
by passing the **PlayerAction** objects into the constructor of the **MyMenu** class,
it makes the testing easier as mocking it using _Mockito_ and initialize the mock 
player actions and be able to verify them. 

Improved code:

```java
import java.util.*;

public class MyMenu {

    private Map<Integer, PlayerAction> actions;

    public MyMenu(PlayerAction doNothingAction, PlayerAction lookAroundAction, PlayerAction fightAction) {
        actions = new HashMap<>();
        actions.put(0, DoNothingAction);
        actions.put(1, LookAroundAction);
        actions.put(2, FightAction);
    }

    public void printMenuOptions(boolean isInCombat) {
        List<String> menuOptions = new ArrayList<>();
        menuOptions.add("What do you want to?");
        menuOptions.add("\t0) Do nothing");
        menuOptions.add("\t1) Look around");
        if (isInCombat) {
            menuOptions.add("\t2) Fight!");
        }
        menuOptions.forEach(System.out::println);
    }

    public PlayerAction getPlayerAction() {
        int option = getNumber();
        if (actions.containsKey(option)) {
            return actions.get(option);
        } else {
            throw new IllegalArgumentException("Invalid option: " + option);
        }
    }

    public void doOption() {
        PlayerAction action = getPlayerAction();
        action.execute();
    }

    public int getNumber() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
}
```

Below you will find examples of code that will be used for testing and how it is beneficial.

```java
public class TestMyMenu {
    private MyMenu menu;
    
    private PlayerAction doNothingAction;
    private PlayerAction lookAroundAction;
    private PlayerAction fightAction;
    
    @BeforeEach
    private void setUp() {
        doNothingAction = mock(PlayerAction.class);
        lookAroundAction = mock(PlayerAction.class);
        fightAction = mock(PlayerAction.class);
        menu = new MyMenu(doNothingAction, lookAroundAction, fightAction);
    }

    @Test
    public void testDoOptionDoesNothing() {
        Mockito.when(menu.getNumber()).thenReturn(0);

        menu.doOption();
        Mockito.verify(doNothingAction).execute();
    }
}

```
As seen from this code, we can see the benefit of passing the **PlayerAction** objects as constructor parameters as they allow us to define
our own mock objects which can be used to test the methods of the MyMenu class. 
___