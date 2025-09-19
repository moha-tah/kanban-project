Init
# Diagramme de classes du système Kanban avec option "Message" 1 et sans gestion des modifications
```plantuml
@startuml
title Diagramme de classes du système Kanban avec option "Message" 1 et avec gestion des modifications

!define RECTANGLE class

class Task {
    - title: str
    - description: str
    + getTitle(): str
    + getDescription(): str
    + setTitle(title: str): void
    + setDescription(description: str): void
    + canBeModifiedBy(user: LightUser): boolean
    + modifyTask(user: LightUser, newTitle: str, newDescription: str): boolean
}

class Message {
    - id: uuid
    - content: str
    - date: date
    + getId(): uuid
    + getContent(): str
    + getDate(): date
}

class Column {
    - title: str
    - color: str
    + getTitle(): str
    + getColor(): str
    + setTitle(title: str): void
    + setColor(color: str): void
    + canBeModifiedBy(user: LightUser): boolean
    + modifyColumn(user: LightUser, newTitle: str, newColor: str): boolean
}

class Chat {
}

class LightKanban {
    - title: str
    - id: str
    # acces: Acces[]
    + getTitle(): str
    + getId(): str
    + getAcces(): Acces[]
    + setTitle(title: str): void
    + setId(id: str): void
    + setAcces(acces: Acces[]): void
    + hasModifyPermission(user: LightUser): boolean
    + getUserRole(user: LightUser): Role
}

class Kanban {
    - taskColumn: HashMap
    + getTaskColumn(): HashMap
    + setTaskColumn(taskColumn: HashMap): void
    + canBeModifiedBy(user: LightUser): boolean
    + modifyKanban(user: LightUser, newTitle: str): boolean
    + addTaskToColumn(user: LightUser, task: Task, column: Column): boolean
    + moveTask(user: LightUser, task: Task, fromColumn: Column, toColumn: Column): boolean
}

class SecureUser {
    - password: str
    + getPassword(): str
    + setPassword(password: str): void
}

class User {
    - firstName: str
    - lastName: str
    - birthDate: date
    + getFirstName(): str
    + getLastName(): str
    + getBirthDate(): date
    + setFirstName(firstName: str): void
    + setLastName(lastName: str): void
    + setBirthDate(birthDate: date): void
    + canModifyProfile(currentUser: LightUser): boolean
    + modifyProfile(currentUser: LightUser, newFirstName: str, newLastName: str, newBirthDate: date): boolean
}

class LightUser {
    - id: str
    - username: str
    + getId(): str
    + getUsername(): str
    + setId(id: str): void
    + setUsername(username: str): void
}

enum Role {
    VIEWER
    MODIFIER
}

class Acces {
    - roles: Role
    - user: LightUser
    - kanban: LightKanban
    + getRoles(): Role
    + setRoles(roles: Role): void
    + getUser(): LightUser
    + getKanban(): LightKanban
    + setUser(user: LightUser): void
    + setKanban(kanban: LightKanban): void
    + hasPermission(permission: Role): boolean
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
title Diagramme de classes du système Kanban avec option "Message" 2 et avec gestion des modifications

!define RECTANGLE class

class Task {
    - title: str
    - description: str
    + getTitle(): str
    + getDescription(): str
    + setTitle(title: str): void
    + setDescription(description: str): void
    + canBeModifiedBy(user: LightUser): boolean
    + modifyTask(user: LightUser, newTitle: str, newDescription: str): boolean
}

class Message {
    - id: uuid
    - content: str
    - date: date
    + getId(): uuid
    + getContent(): str
    + getDate(): date
}

class Column {
    - title: str
    - color: str
    + getTitle(): str
    + getColor(): str
    + setTitle(title: str): void
    + setColor(color: str): void
    + canBeModifiedBy(user: LightUser): boolean
    + modifyColumn(user: LightUser, newTitle: str, newColor: str): boolean
}

class LightKanban {
    - title: str
    - id: str
    # acces: Acces[]
    + getTitle(): str
    + getId(): str
    + getAcces(): Acces[]
    + setTitle(title: str): void
    + setId(id: str): void
    + setAcces(acces: Acces[]): void
    + hasModifyPermission(user: LightUser): boolean
    + getUserRole(user: LightUser): Role
}

class Kanban {
    - taskColumn: HashMap
    + getTaskColumn(): HashMap
    + setTaskColumn(taskColumn: HashMap): void
    + canBeModifiedBy(user: LightUser): boolean
    + modifyKanban(user: LightUser, newTitle: str): boolean
    + addTaskToColumn(user: LightUser, task: Task, column: Column): boolean
    + moveTask(user: LightUser, task: Task, fromColumn: Column, toColumn: Column): boolean
}

class SecureUser {
    - password: str
    + getPassword(): str
    + setPassword(password: str): void
}

class User {
    - firstName: str
    - lastName: str
    - birthDate: date
    + getFirstName(): str
    + getLastName(): str
    + getBirthDate(): date
    + setFirstName(firstName: str): void
    + setLastName(lastName: str): void
    + setBirthDate(birthDate: date): void
    + canModifyProfile(currentUser: LightUser): boolean
    + modifyProfile(currentUser: LightUser, newFirstName: str, newLastName: str, newBirthDate: date): boolean
}

class LightUser {
    - id: str
    - username: str
    + getId(): str
    + getUsername(): str
    + setId(id: str): void
    + setUsername(username: str): void
}

enum Role {
    VIEWER
    MODIFIER
}

class Acces {
    - roles: Role
    - user: LightUser
    - kanban: LightKanban
    + getRoles(): Role
    + setRoles(roles: Role): void
    + getUser(): LightUser
    + getKanban(): LightKanban
    + setUser(user: LightUser): void
    + setKanban(kanban: LightKanban): void
    + hasPermission(permission: Role): boolean
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
