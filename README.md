Init
# Diagramme de classes du système Kanban avec option "Message" 1 et sans gestion des modifications
```plantuml
@startuml
title Diagramme de classes du système Kanban avec option "Message" 1 et sans gestion des modifications

!define RECTANGLE class

class Task {
    - title: str
    - description: str
}

class Message {
    - id: uuid
    - content: str
    - date: date
}

class Column {
    - title: str
    - color: str
}

class Chat {
}

class LightKanban {
    - title: str
    - id: str
    # acces: Acces[]
}

class Kanban {
    - taskColumn: HashMap
}

class SecureUser {
    - password: str
}

class User {
    - firstName: str
    - lastName: str
    - birthDate: date
}

class LightUser {
    - id: str
    - username: str
}

enum Role {
    VIEWER
    MODIFIER
}

class Acces {
    - roles: Role
}

Task "*" --> "1" LightUser: create a task
Task "*" --> "*" LightUser: assign a task

LightKanban "*" --> "*" LightUser: create a kanban
(LightKanban, LightUser) .. Acces
LightKanban "*" --> "*" User: visualize a kanban

Kanban "*" --> "1" LightUser: create a kanban
Kanban "*" --> "*" User: create a kanban

Kanban --|> LightKanban
Chat --|> Kanban
User --|> LightUser
SecureUser --|> User
Message --|> Chat
Task --|> Kanban
Column --|> Kanban

LightUser "1" --> "*" Message: send a message
LightUser "*" --> "*" Chat: participate to a chat


@enduml
```

# Diagramme de classes du système Kanban avec option "Message" 2 et sans gestion des modifications
```plantuml
@startuml
title Diagramme de classes du système Kanban avec option "Message" 2 et sans gestion des modifications

!define RECTANGLE class

class Task {
    - title: str
    - description: str
}

class Message {
    - id: uuid
    - content: str
    - date: date
}

class Column {
    - title: str
    - color: str
}

class LightKanban {
    - title: str
    - id: str
    # acces: Acces[]
}

class Kanban {
    - taskColumn: HashMap
}

class SecureUser {
    - password: str
}

class User {
    - firstName: str
    - lastName: str
    - birthDate: date
}

class LightUser {
    - id: str
    - username: str
}

enum Role {
    VIEWER
    MODIFIER
}

class Acces {
    - roles: Role
}

Task "*" --> "1" LightUser: create a task
Task "*" --> "*" LightUser: assign a task

LightKanban "*" --> "*" LightUser: create a kanban
(LightKanban, LightUser) .. Acces
LightKanban "*" --> "*" User: visualize a kanban

Kanban "*" --> "1" LightUser: create a kanban
Kanban "*" --> "*" User: create a kanban

Kanban --|> LightKanban
User --|> LightUser
SecureUser --|> User
Task --|> Kanban
Column --|> Kanban
Message --|> Kanban

LightUser "1" --> "*" Message: send a message
LightUser "1" --> "*" Message: send a message to a user
LightUser "1" --> "*" Message: receive a message

@enduml
```
