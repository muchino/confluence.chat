### FAQ

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
