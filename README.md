# Confluence Chat  ![alt text](http://muchino.github.com/confluence.chat/images/plugin-logo.png "")

The confluence chat plugin  brings confluence users more together.  This built-in confluence chat for realtime collaboration gives confluence users the posibility to chat. In addition it shows the browsed page of the chat partner and the current online state.
If the user uses confluence with multiple Browser Tabs or Browsers at the same time, the chat takes care that all chat windows are synced. 

![alt text](http://muchino.github.com/confluence.chat/images/chat.png "")

## Features

* Chat between confluence users
* "Where is my chat partner" Info
* State: Online, Offline, Away, DND
* Smiley support
* Link detection
* "New message" notification
* Autoaway
* Multilingualism

## Releases
**IMPORTANT: If you upgrade from <= 1.1.1 and the chat doesn't work, please remove all chatboxes (could be deleted in the chat admin area => backend)  and restart your confluence.**
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
* Permissions and Restrictions on Space level
* Compatibility with Terms and Condition Plugin
* Compatibility with Scaffolding Plugin
* Some minor issues
* Added french

### 1.3.0
* multilingualism added

### 1.2.2
Because of a restructuring the old messages are deleted. It may appear warnings in the log file.

* the height of the textarea is now dynamic (like gtalk or facebook)
* play sound when new message arrives (could be gloabl disabled by an administrator or by an user for his self )
* alphabetical sorting of the users
* ability to delete the chat data. The data could be deleted selectively by an admin or in the new dopmenu of the chatbox 

### 1.1.1 
* Add configuration to hide the configuration in edit mode. 
* Add configuration to enable javascript debug mode.
* At the "Where I am Info" Url parser, I had forgotten the contextPath

### 1.1.0
* Add a chat user link macro
* Add Configuration of chat restriction (All or by groups)
* Add possibility to switch off the "where I am Info"
* New small Javascript API, to start chats from outside  "ConfluenceChatAPI"

* Change window behaviour (close oldest, if there is not enough space  - similar to gtalk))
* Improved request permission
* Improved the js
* Added version number to js 

### 1.0.1 
This release fixes the  important contextPath issue. If confluence is  running under a certain contextPath, like "/confluence" the chat doesn't work. Additional fixes:
the chat doesn't worked)

* Stop chat heartbeat when ajax url's are for more than three times unavaible (if chat is disabled or uninstalled by an admin)
* encapsulated the javascript of the chat to avoid sideeffects
* Usability: Stop chatbox flashing, after a chatbox mouseover or click into the chatbox
* Suppressing deactivation of dynamic task list macro input elements, while an Ajax request

### 1.0 

initial release

## URLs

* Atlassian Marketplace: https://marketplace.atlassian.com/plugins/confluence.chat
* GitHub Pages. http://muchino.github.com/confluence.chat/