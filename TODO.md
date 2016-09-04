TODO:

* Tests for long-polling getUpdates method
* Tests with timeout
* Poller actor
* Router actor
** Instantiating a processor for every new user interaction
* Processor actor
* A trait as a framework for responses. The trait may be embedded in the Processor actor. Or, this can even be a Java interface (??? this is scala, why bither with java?)
* Responder actor
* Inline updates

Architecture:

Poller --[update]--> Router -> Processor -> Responser

* Poller polls getUpdates method and eventually retrieves Updates, and then it sends each Update as a message to the router.
* Router determines the type of update and sends it to the corresponding processor.
* Processor is where the framework logic executes. There is a separate processor for each of the telegram user.
* Responder is responsible for sending messages via telegram http api.

Framework:

Framework is a set of rules that determines bot's reaction to a certain events. Can be implemented as an actor, or as a partial function with default behavior.
 Or simple interface with methods onMessage, onCommand etc (i.e. Subscriber)
 
Yeah, PubSub can be the most appropriate interaction model. 

Message -> response
Command -> response
Command with arguments -> response
