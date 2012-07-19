# Confluence Chat 

The confluence chat plugin  brings confluence users more together.  This built-in confluence chat for realtime collaboration gives confluence users the posibility to chat. In addition it shows the browsed page of the chat partner and the current online state.
If the user uses confluence with multiple Browser Tabs or Browsers at the same time, the chat takes care that all chat windows are synced. 

## Features

* Chat between confluence users
* "Where is my chat partner" Info
* State: Online, Offline, Away, DND
* Smiley support
* Link detection
* "New message" notification
* Autoaway

## Releases

### 1.0.1 
This release fixes the  important contextPath issue. If confluence is  running under a certain contextPath, like "/confluence" the chat doesn't work. Additional fixes:
the chat doesn't worked)
* Stop chat heartbeat when ajax url's are for more than three times unavaible (if chat is disabled or uninstalled by an admin)
* encapsulated the javascript of the chat to avoid sideeffects
* Usability: Stop chatbox flashing, after a chatbox mouseover or click into the chatbox
* Suppressing deactivation of dynamic task list macro input elements, while an Ajax request

### 1.0 

initial release