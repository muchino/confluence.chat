### FAQ


#### Hide the Chat Histories
There is no config for this. but if it's possible to hide the menu entries to enter the chat history:

#### To hide the dropdown menu entry in a chatbox:
```css
.cb-dropdown .aui-dd-parent .aui-dropdown li.dropdown-item:first-child {display:none;}
```
#### To hide the menu entries in the ser profile you have to go into the confluence admin -> Manage Addons -> Confluence Chat -> Modules and deactivate the following modules 
* Confluence Chat Histories (The global menu entry in the Confluence Admin)
* 2 x Chat Histoty Tab (The Tab in the user profile / menu entry)

After you have deactivated this three modules, the user has no entry point to see his histories



#### The editor hides the save button
You could activate the option "Hide in editor" in the chat configursation or you could add the following css rule in the global css styles:

```css
.contenteditor #chatbar {
display: none !important;
}
```

#### Change Backgroundcolor of the header

```css
body div.cb-head,
body  #chatbar{
background-color: #75A0C5;
}
 
body div.cb-head{
color: #ffffff;
}

```
###Reset all

First: Shutdown confluence

To Remove all configurations (global and space setting), execute this SQL statement:
<!-- language: sql -->
```sql
DELETE FROM  bandana
WHERE  BANDANAKEY LIKE  'confluence.chat.configuration%'
```
To remove complete History: 

```sql
FROM  bandana
WHERE  BANDANACONTEXT LIKE  'confluence.chat.history.%'
```
To remove all user settings (Status, ... )
```sql
FROM  bandana
WHERE  BANDANACONTEXT LIKE  'confluence.chat.preferences.%'
```


At the end, start confluence
