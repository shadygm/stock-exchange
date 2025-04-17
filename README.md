<br />
<p align="center">
  <h1 align="center">Stock Market Simulation</h1>

  <p align="center">
  Simulating a simplified stock market with stock exchange and trading bots.
  </p>
</p>

## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
  * [Running](#running)
* [Modules](#modules)
  * [Command](#command)
  * [Message Queue](#message-queue)
  * [Networking](#networking)
  * [Stock Application](#stock-application)
  * [Stock Market UI](#stock-market-ui)
  * [Trader Application](#trader-application)
* [Design](#design)
  * [Singleton](#singleton)
  * [Consumer - Producer](#consumer---producer)
  * [Layer](#layer)
  * [Facade](#facade)
  * [Command](#command)
  * [Proxy](#proxy)
  * [Chain of Responsibility](#chain-of-responsibility)
  * [Model - View - Controller](#model---view---controller)
* [Evaluation](#evaluation)
* [Extras](#extras)
* [Contributing](#contributing)
* [Contact](#contact)
* [Acknowledgements](#acknowledgements)

## About The Project

This project involves the simulating a simplified version of the stock market which consists of two main components; the stock exchange
and a group of traders (bots). Each stock is represented by information such as the name of the corporation associated with it or the
price of a single share. In this simulation, traders will interact with the stock exchange to buy and sell shares based on predefined
strategies, aiming to replicate the dynamics of a real-world stock market.

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

* [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or higher
* [Maven 3.6](https://maven.apache.org/download.cgi) or higher

### Installation

1. Navigate to the `stocks` directory.
2. Clean and build the project using:
```sh
mvn install
```

### Running

1. Navigate to the `stocks` directory.
2. Type in:
```sh
docker compose up
```
3. Navigate to `http://localhost:3000/` in your browser.

## Modules

Our project consists of the following modules:
* [Command](#command)
* [Message Queue](#message-queue)
* [Networking](#networking)
* [Stock Application](#stock-application)
* [Stock Market UI](#stock-market-ui)
* [Trader Application](#trader-application)

### Command

The Command module is a fundamental part of our project's architecture as it enables precise, controlled, 
and efficient command execution within the Message Queue, while providing a structured approach to command 
management.

The Command interface executes essential Message Queue interactions like polling and putting a message in the 
queue in order to ensure seamless communication. Simultaneously, the CommandHandler interface dictates command 
execution, offering precise control over the software's behavior. This abstraction guarantees that the right
commands are executed at the right times. Lastly, reliability, flexibility and maintainability are enhanced.

### Message Queue

The purpose of the message queue module is to facilitate efficient and organized communication between
multiple interconnected services. Its main job as an intermediary service simplifies the data exchange from
a trader and guarantees reliable delivery of these data to the stock application. This module contributes to
a more streamlined and responsive network.

A ThreadSafe Message Queue is implemented and used to store messages that are sent from the trader
application. The Queue is an ordered Message Queue whose ordering is done first by the timestamp of the
messages and second by the Last-In-First-Out (LIFO) principle.

Data is fetched and put onto the Message Queue through the Consumer-Producer design, which allows for abstraction
of the Message Queue from the rest of the application. In our case, the Producer is the Trader Application, which 
sends messages to the Message Queue and the Consumer is the Stock Application, which fetches messages from the 
Message Queue.

### Networking

The goal of the Networking module is to provide a way for the Stock Application and the Trader Application to 
communicate without directly accessing each other. This is extremely useful as it allows for continuous and 
bidirectional communication (client -> server) due to the usage of the Message Queue as well as direct message
sending (server -> client) without the usage of the Message Queue.

The  module utilizes a Server and a Client which communicate through Transmission Control Protocol (TCP). In order 
to abstract the connection between Server and Client more precisely, the Client Handler class was introduced. This 
class allows for a one-to-one connection as the socket is shared by both of them. 

In principle, the Client receives an JSON string which represents an object that they want to send through 
the network. Once it gets sent over, the Client Handler receives it and performs the necessary operations in order to
add it in the Message Queue. 

### Stock Application

The Stock Application is the main entry point for the Graphical Interface that a user will be met with upon launching 
the application. The complex logic which connects the Networking, Message Queue, and Trade AApplication modules, also, 
takes place here. 

The Stock Application is tasked with loading stock and trader data directly from a YAML file, initializing the Stock 
Exchange Model within the Stock Market UI, and diligently managing the continuous update and resolution of valid trade 
orders. Its pivotal role ensures a seamless user experience, connecting various underlying components and ensuring 
efficient Stock and Trader data management.

### Stock Market UI

The Stock Market UI module is important as it establishes the Graphical User Interface (GUI) that the users interact 
with. Additionally, it serves as the foundation for our View model, promoting an intuitive and user-friendly experience.

Its primary objective is to allow users to have access to convenient and efficient means of monitoring the stock market. 
This is achieved by offering real-time insights into the status of various traders and providing comprehensive price 
histories for a wide range of stocks. The module not only enhances user engagement but also allows a deeper understanding 
of the Stock Market dynamics, making it an essential component of our project.

### Trader Application
The Trader Application module is responsible for manipulating Bots which are autonomous Traders. These Bots are able to 
send orders through the network to the Stock Application in order to make purchases or sell their currently owned stocks.

Currently, the bots implement a random trading strategy where they randomly pick a stock, amount of shares, and a
price which is based on certain boundaries. While random trading may seem simplistic, it provides a baseline for bot 
performance and can serve as a starting point for further development and refinement of more sophisticated trading 
strategies. This approach introduces an element of unpredictability into the trading process, which can be 
beneficial in certain market conditions.

## Design

Our project implements the following Design Patterns:
* [Singleton](#singleton)
* [Consumer-Producer](#consumer---producer)
* [Layer](#layer)
* [Facade](#facade)
* [Command](#command)
* [Proxy](#proxy)
* [Chain of Responsibility](#chain-of-responsibility)
* [Model-View-Controller](#model---view---controller)

### Singleton
The Singleton pattern was used in the Thread Safe Queue and the Stock Exchange Model. It is useful in the Thread
Safe Queue as it allows for data integrity as well as prevention of race conditions in multithreaded environments. 
Without this pattern, many synchronization issues could occur. Moreover, it is useful in the Stock Exchange model, 
as it allows for a single, centralized instance that represents the Stock Exchange. This ensures that any class 
that needs information regarding the stock exchange will be able to access it in one centralized place.

### Consumer - Producer
The Consumer-Producer pattern is used throughout the entire project as it is the basis of the message queue. This 
allows for reliable communication to and from different applications. A producer is used in order to produce (*put*) 
messages to the message queue, whilst the consumer is used to consume (*poll*) the aforementioned messages from 
the message queue.

### Layer
In this assignment the Layer pattern was used in order to create an abstraction between different layers. For instance, 
Networking is a layer, whilst Trader Application and the Stock Application are different layers. The direct implementation of
each is hidden from the other. This is clearly seen in the Stock Application and Trader Application.

### Facade
The Facade pattern is widely used in our project due to the fact that it abstracts many complicated operations.
One key example of this is the resolving of orders. In the specific class that deals with this, there are very few 
public methods that are accessible to other classes. This is used to hide the complicated logic behind handling orders.

### Command
The Command pattern is used in the Networking (and Command) module as a way to handle different types of messages without 
a large chain of 'if' statements which are neither extendable nor expandable. This was key to the handling of messages as 
it allows us to simply call the execute command which would then handle the necessary command.

### Proxy
The Proxy pattern is used for testing and, more specifically, mocking. This is utilized very often in our program to 
abstract the different dependencies of the object we are testing without having to worry about the underlying code. 
It allowed for decoupling functionality as well as optimization. 

### Chain of Responsibility
The Chain of Responsibility is used in our Networking module to handle messages that are being passed along. The messages
that are being sent over the network are passed along to many different handlers, some of which are used to convert the
message to their JSON format whilst others are used to wrap the original message to a more general form.

### Model - View - Controller
Our program uses the Model View Controller design pattern to abstract different implementations of the View such that
we never directly alter the View but the Model which, then, changes the View. 

## Evaluation

Based on the comprehensive testing we have conducted, it's evident that our program exhibits a remarkable level of 
stability, with no discernible bugs that would disrupt its functionality. The core trading processes involving the 
bots, order execution, and the GUI's responsiveness have all been rigorously tested and found to work seamlessly, 
reflecting positively on the overall user experience. While our testing is thorough, we acknowledge that it cannot
guarantee the absence of all potential bugs. As prof. Edsger W. Dijkstra once said, "Program testing can be used to
the presence of bus, but never to show their absence!" So, even though we believe that there has been a significant 
test coverage, correctness is still not guaranteed. 

A noteworthy observation from our testing is the consistent increase in the net worth of individual users, 
with values reaching as high as $15 billion within just 15 minutes of trading. Although this phenomenon is not 
a stability issue, it underscores the efficiency of the trading strategies implemented by the bots and suggests
a positive user experience.

In terms of future improvements, several features were considered but not yet implemented. These include network 
consumers to enhance data exchange, stop-loss orders for risk management, realistic trading scenarios to simulate 
real market conditions, and the ability to distribute dividends to traders. With more time, we would also work on
enhancing the separation of our limit orders class, further refining and optimizing this critical component for 
even more precise and efficient trading operations.

## Extras

As a bonus, we decided to implement a deployment feature using Docker. Docker provides us with the capability to run
the application in a containerized environment, which is isolated from the host machine. By doing this, we aimed to
eliminate the need for the user to install the required dependencies, to provide a more consistent experience across
different environments and to ensure reliability across different operating systems. Thus, our simulation project was
brought closer to the real-world scenario of a stock market as we managed to achieve consistency, portability and
reproducibility in our application.

___

## Contributing
Shady Gmira | Chrysanthi Aikaterini Mandraveli

## Contact 
Shady Gmira - s.gmira@student.rug.nl

Chrysanthi Aikaterini Mandraveli - c.mandraveli@student.rug.nl

## Acknowledgements
We would like to thank our lovely traders, Jack Ingoff and Ben Dover, for obtaining over $15 billion under 20 minutes. 
Their support throughout the completion of this project was invaluable. 

Last but not least, we would like to showcase our deepest gratitude to our TA Dogukan Tuna for his continuous support 
throughout the assignment and his indispensable feedback and suggestions. 

For him, we offer this: **><((((`>** *(tuna fish)*