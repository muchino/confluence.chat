# Confluence Chat <img width="72px" src="https://raw.github.com/muchino/confluence.chat/master/src/main/resources/confluence/chat/img/plugin-logo.png"> 


**Neues Repository auf [Bitbucket](https://bitbucket.org/muchino/chat-for-confluence/overview)**

**Neues Issue Tracking auf [Bitbucket](https://bitbucket.org/muchino/chat-for-confluence/issues?status=new&status=open)**


[Features](#features)  
[FAQ](#faq)  
[Releases](#releases)  
[URLs](#urls) 

The confluence chat plugin  brings confluence users more together.  This built-in confluence chat for realtime collaboration gives confluence users the posibility to chat. In addition it shows the browsed page of the chat partner and the current online state.
If the user uses confluence with multiple Browser Tabs or Browsers at the same time, the chat takes care that all chat windows are synced. 

![alt text](http://muchino.github.com/confluence.chat/images/chat.png "")

## Features

* Chat between confluence users
* "Where is my chat partner" Info
* State: Online, Offline, Away, DND
* History
* Manage permission
* Smiley support
* Link detection
* "New message" notification
* Autoaway


## FAQ

[Read more in the FAQ](../master/src/main/resources/faq.md)

## Releases

### 2.0.6
* Fix compilation error within in Confluence 5.7

### 2.0.5
* Change icon order in chatbar. #102

### 2.0.2
* Pre Confluence 5.0: Instead of usernames, the chatboxId was schown in the chat history overview page (#80 reported by mikmouk )

### 2.0.1
* Improve admin section to increase config page performance
* Add page navigation in chat history overview
* fix endoding problem on chat history page
* less database interaction on overview pages

### 2.0.0
* **The stored data structure changed, because of the support for confluence 5.3**
* Massive performance improvements for the server , because only one tab is polling the server now!
* The milliseconds of the heartbeat are now customizable 

### 1.6.0
* Added a chat history UI 
* Added Tab in User profile to see all histories

### 1.5.4
* Fix Transparency Issue, if the color of the confluence header couldn't detected
* Move Colordetection into own web-resource module -> so it could be deactivated


### 1.5.3

New Features
 * New UI for the chatbar (smaller one, to have less problems with the editor)

Bugfixes   
* Sometimes messages could not be received or sent
* The current status could be lost

### 1.5.2
This is a bugfixing release 	
* problems in Chrome 12.0.742.112 (#49 reported by keithw1305 )
* Play Sound setting was displayed wrong in the configuration screen (#48 reported by Memonen)
* Users disapears sometimes (#38 reported by thomykay and gargouri22)
* Messages received twice randomly  (reported by  gargouri22)

**Thank you gargouri22 for testing and your researching!**

### 1.5.1
* Page name was truncated at by hyphen  (#45 reported by anthony3123 )

### 1.5.0
* Autoconfigure multiple spaces at once
* Overviewtable with all space settings
* Change global / space permission check from an AND to an OR
* fix invalid breadcrumb in space admininstration

### 1.4.0
