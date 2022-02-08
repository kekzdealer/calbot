# calbot
Discord bot as interface for various programs

## Common command structure
```/<program> [target] <operation> [arg0...argN]```
### program
Name of the program you want to use. If the program uses a shorthand instead of the full name, it will be mentioned in the program description.
### target
Most programs will have a configurable default target that can be accessed without having to specify it.
Otherwise this is where you set what, for example, shopping list you want to target.
### operation
Listing, adding, modifying, deleting: There'll be a list of operations in the program description.
### arguments
Not all operations require additional input, but wherever that is the case, those are specified at the end.

## User profiles
Keeps track of your name and probably other stuff in the future. Basis for using the other programs.

## Calendar
A simple tool that can save Events with a title, start-, and end-date & -time. A user can create Events, and an Event can include any amount of users.

# Future plans
## Shopping List
Shopping List tracker. 
Users can create, modify, and delete their shopping lissts as well as share them with other users.
Each user can set one shopping list as default. The default list does not have to be referred to by name when using the commands.

## Simple Tamagotchi style thingy
idk always wanted to make one.
