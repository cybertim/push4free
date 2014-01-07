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
## Information

### Client
* Use the default [Android](http://www.android.com/) [P4F on Google Play](https://play.google.com/store/apps/details?id=com.mizusoft.android.push4free) client and add your own P4F Server Address into the client's settings.
* Alter / compile the client yourself, install the [Android SDK](http://developer.android.com/sdk/index.html) and just compile the [Maven](http://maven.apache.org/) android module.

### Server
* Use the [Precompiled Spring Webapp](https://github.com/cybertim/push4free/raw/master/build/push4free.war) and deploy it to a [Tomcat](http://tomcat.apache.org/) server.
* Don't forget to change the password and tag in the `push4free.properties` file.
* Or checkout the code and compile the [Maven](http://maven.apache.org/) webapp module.

### Setup

#### Installation
- Be sure to own a [server](http://en.wikipedia.org/wiki/Cloud_computing) with [Tomcat](http://tomcat.apache.org/) installed 
- Download the [push4free.war](https://github.com/cybertim/push4free/raw/master/build/push4free.war) and deploy to Tomcat
- Edit the `push4free/WEB-INF/classes/push4free.properties` and set a custom `tag` and `password`
- Download the [Android client](https://play.google.com/store/apps/details?id=com.mizusoft.android.push4free) on your mobile
- Edit the P4F URL in your android client and set it to `http://your_tomcat_host/push4free/read`

#### Send
- Visit `http://your_tomcat_host/push4free/push?pwd=your_password&body=bodytext&title=titletext`
- `pwd` is your password set in the properties file, `title` and `body` is the text shown in the notification
- optionally you can also set the `id` manually, the webapp will increment this id automatically when omitted
- this url can easily be called from your shell scripts with `curl` 

#### Follow
- Connect your server with another server you need to know the `/read` url of the server you want to follow
- For example `http://other_tomcat_host/push4free/read` don't forget to `URLEncode` this before sending it to the `/follow`
- Visit `http://your_tomcat_host/push4free/follow?url=http%3A%2F%2Fother_tomcat_host%2Fpush4free%2Fread`
- You will see `ADDED`
- To stop following a host just visit the above URL again and you will see `REMOVED`

## Technical Specifications

### Push Protocol
If you would like to create your custom server or client follow these simple rules.

#### Messages
They consit of:
- `id` to identify the message, can be used to overwrite a previous message
- `tag` used to identify the source, set on the server side
- `title` this is the title of the notification
- `body` this is the body message of the notification

When send from the server every part is encoded with [Base64](http://en.wikipedia.org/wiki/Base64) and concatenated with `:`
**example**
contactenated message:
`0:test tag:test title:test body`
will become:
`MA==:dGVzdCB0YWc=:dGVzdCB0aXRsZQ==:dGVzdCBib2R5`

#### Polling
The polling for a message is done with [HTTP Long Polling](http://en.wikipedia.org/wiki/Push_technology).
In `JAVA` this is done with the [HTTPClient](http://hc.apache.org/httpclient-3.x/) library.

**example**  
``HttpClient httpClient = HttpClientBuilder.create().build();``  
``HttpGet httpGet = new HttpGet("http://yourhost/push4free/read");``  
``HttpResponse httpResponse = httpClient.execute(httpGet);``  
``String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");``  

This polling mechanism is already a part of the Android client and the Webapp. The webapp can `follow` other webapp's by just polling.

#### Publishing
While a polling takes place on an [URL](http://nl.wikipedia.org/wiki/Uniform_Resource_Locator) the server needs to keep the [HTTP Request](http://nl.wikipedia.org/wiki/Hypertext_Transfer_Protocol) open until a message is pushed.

In the webapp this is done with [Spring Asynchronous Execution](http://docs.spring.io/spring/docs/3.0.x/reference/scheduling.html) by [Deferring the result](http://docs.spring.io/spring/docs/3.2.0.BUILD-SNAPSHOT/api/org/springframework/web/context/request/async/DeferredResult.html).

In general (when implementing this):
- When a `http request` is made, keep the connection alive
- When a push messages comes in (on another location) let the first request know about this (ex. with deffering results or maybe a database or locale file-polling etc.)
- Output the result to the `http request` by creating the message and close the connection

This can be created in every programming language - if it's done correctly the [Android Client](https://play.google.com/store/apps/details?id=com.mizusoft.android.push4free) will be compatible with your custom server.
