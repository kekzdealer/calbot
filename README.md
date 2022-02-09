# calbot
Discord bot as interface for various programs

## Common command structure
```/<program> [target] <operation> [arg0...argN]```
### program
Name of the program you want to use. If the program uses a shorthand instead of the full name, it will be mentioned in the program description.
### target
Most programs will have a configurable default target that can be accessed without having to specify it.
Otherwise, this is where you set what, for example, shopping list you want to target.
### operation
Listing, adding, modifying, deleting: There'll be a list of operations in the program description.
### arguments
Not all operations require additional input, but wherever that is the case, those are specified at the end.
## User profiles
Keeps track of your name and probably other stuff in the future. Basis for using the other programs.
### Targets
No targets here. The target is always the default target: You
### Operations
`hello` Starts the sign-up process

`list` Print all user data

`nickname`: Change how the bot refers to you. Default will be the discord name you had during the "hello" operation 
- `nickname`: Your preferred new nickname

`delete` Permanently delete all your data from the database

## Calendar
A simple tool that can save Events with a title, start-, and end-date & -time. A user can create Events, and an Event can include any amount of users.
### Targets
Events can be targets. They are referred to by their id. *(How do you get an event id?)*
### Operations
`create`: Create a new event
- `<title>`: Name of the event
- `<start>`: Date and time when the event should start
- `<end>`: Date and time when the event should end

`today`: List all events due today, in chronological order

`delete`: Delete an event
- `<id>`: The target event's id

# Future plans
## Shopping List
Shopping List tracker. 
Users can create, modify, and delete their shopping lists as well as share them with other users.
Each user can set one shopping list as default. The default list does not have to be referred to by name when using the commands.
### Targets
Shopping lists are targets. There shouldn't be too many lists per user, so they can be referred to by name.
### Operations
`create`: Create a new shopping list
- `title`: The title of the shopping list

`add`: Add an item to a shopping list
- `TARGET`: Name of the shopping list the item should be added to. If not specified, the default list will be used
- `item`: The item name that should be added

`show`: Display a shopping list
- `TARGET`: Name of the shopping list to display

`list`: List all available shopping lists

`default`: Set a shopping list as default
- `name`: Name of the preferred default shopping list

## Simple Tamagotchi style thingy
idk always wanted to make one.
