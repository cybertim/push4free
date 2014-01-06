# push4free
If all you want is a simple and fast (push) notification on your mobile when an event was triggered without spamming your inbox and keeping track of mailinglists etc... read on....

Push Service For Free is a very clean and simple push service as a server and client solution. It is my answer to all the "free" push services (including [GCM](http://developer.android.com/google/gcm/index.html)) where you eventually have to pay for each message send (!!).
***

## Example?
![Scheme](https://raw.github.com/cybertim/push4free/master/images/scheme.png)
* Install at least one P4F Server (ex. *Tech*)
* Set the client to the P4F server's `/read` address
* Send a message (with HTTP GET) to the P4F (ex. *Tech*) server's `/push` address and instantly receive the content on the client
* Add another P4F server (ex. *Main*) and let the first server (ex. *Tech*) listen to the `/read` address of the new server (ex. *Main*) by using the `/follow` address
* Send a message to the `/push` of the new server (ex. *Main*) and instantly receive the message on the client
* This way you can scale out even further (ex. *Support*)
* You can also install a personal P4F server and only follow tag's you are interested in (ex. different departments/servers within the company or around the web).

***
## Installation

### Client
* Use the default [Android](http://www.android.com/) P4F client from [Google Play]() and add your own P4F Server Address into the client's settings.
* Alter / compile the client yourself, install the [Android SDK](http://developer.android.com/sdk/index.html) and just compile the [Maven](http://maven.apache.org/) android module.

### Server
* Use the Precompiled Spring Webapp and deploy it to a [Tomcat](http://tomcat.apache.org/) server.
* Or checkout the code and compile the [Maven](http://maven.apache.org/) webapp module.
***

## Technical Specifications

### Push Protocol

