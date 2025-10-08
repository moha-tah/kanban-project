classDiagram
    title Kanban System Class Diagram

    class Task {
        - id: uuid
        - title: str
        - description: str
        - startDate: date
        - endDate: date
        + getId(): uuid
        + getTitle(): str
        + getDescription(): str
        + getStartDate(): date
        + getEndDate(): date
        + setTitle(title: str)
        + setDescription(description: str)
        + setStartDate(startDate: date)
        + setEndDate(endDate: date)
        + canBeModifiedBy(user: LightUser): boolean
        + modifyTask(user: LightUser, newTitle: str, newDescription: str, newStartDate: date, newEndDate: date): boolean
    }

    class Message {
        - id: uuid
        - content: str
        - date: datetime
        + getId(): uuid
        + getContent(): str
        + getDate(): date
    }

    class Column {
        - id: uuid
        - title: str
        - color: str
        - number: int
        + getId(): uuid
        + getTitle(): str
        + getColor(): str
        + getNumber(): int
        + setTitle(title: str)
        + setColor(color: str)
        + setNumber(number: int)
        + canBeModifiedBy(user: LightUser): boolean
        + modifyColumn(user: LightUser, newTitle: str, newColor: str): boolean
    }

    class LightKanban {
        - title: str
        - id: uuid
        + getTitle(): str
        + getId(): uuid
        + setTitle(title: str)
        + setId(id: uuid)
        + hasModifyPermission(user: LightUser): boolean
        + getUserRole(user: LightUser): Role
    }

    class Kanban {
        - taskColumn: HashMap
        + getTaskColumn(): HashMap
        + setTaskColumn(taskColumn: HashMap)
        + canBeModifiedBy(user: LightUser): boolean
        + modifyKanban(user: LightUser, newTitle: str): boolean
        + addTaskToColumn(user: LightUser, task: Task, column: Column): boolean
        + moveTask(user: LightUser, task: Task, fromColumn: Column, toColumn: Column): boolean
    }

    class SecureUser {
        - password: str (encoded hash)
        + getPassword(): str
        + setPassword(password: str)
    }

    class User {
        - firstName: str
        - lastName: str
        - birthDate: date
        - avatar: image
        - myKanban: list<Kanban>
        + getFirstName(): str
        + getLastName(): str
        + getBirthDate(): date
        + getAvatar(): str
        + getMyKanban(): list<Kanban>
        + setFirstName(firstName: str)
        + setLastName(lastName: str)
        + setBirthDate(birthDate: date)
        + setAvatar(avatar: str)
        + setMyKanban(myKanban: list<Kanban>)
        + canModifyProfile(currentUser: LightUser): boolean
        + modifyProfile(currentUser: LightUser, newFirstName: str, newLastName: str, newBirthDate: date): boolean
    }

    class LightUser {
        - id: uuid
        - username: str
        + getId(): uuid
        + getUsername(): str
        + setId(id: uuid)
        + setUsername(username: str)
    }

    class Access {
        - roles: Role
        - user: LightUser
        + getRoles(): Role
        + getUser(): LightUser
        + setUser(user: LightUser)
        + setRoles(roles: Role)
        + hasPermission(permission: Role): boolean
    }

    class Modification {
        <<abstract>>
        - id: uuid
        + getId(): uuid
        + setId(id: uuid)
    }

    class SnapshotInfo {
        - id: uuid
        - name: str
        - creationDate: datetime
        - filePath: str
        + getId(): uuid
        + getName(): str
        + getCreationDate(): datetime
        + getFilePath(): str
        + setName(name: str)
        + setFilePath(path: str)
    }

    class DataClient {
        - snapshotDirectory: str
        + getSnapshotDirectory(): str
        + setSnapshotDirectory(directory: str)
        + saveSnapshot(kanbanData: KanbanBoard): boolean
        + listSnapshots(): List<SnapshotInfo>
        + getSnapshot(snapshotId: str): KanbanBoard
        + deleteSnapshot(snapshotId: str): boolean
    }

    class ModifyTask
    class ModifyColumn
    class ModifyKanban
    class CreateTask
    class CreateColumn
    class DeleteTask
    class DeleteColumn
    class MoveTask
    class CreateMessage
    class AddAccess
    class RemoveAccess

    %% ==== ENUM ====
    class Role {
        <<enumeration>>
        VIEWER
        MODIFIER
    }

    %% ==== INHERITANCE ====
    User <|-- LightUser
    SecureUser <|-- User
    Kanban <|-- LightKanban

    ModifyTask <|-- Modification
    ModifyColumn <|-- Modification
    ModifyKanban <|-- Modification
    CreateTask <|-- Modification
    CreateColumn <|-- Modification
    DeleteTask <|-- Modification
    DeleteColumn <|-- Modification
    MoveTask <|-- Modification
    CreateMessage <|-- Modification
    AddAccess <|-- Modification
    RemoveAccess <|-- Modification

    %% ==== COMPOSITIONS / ASSOCIATIONS ====
    Kanban *-- Task
    Kanban *-- Column
    Kanban *-- Message

    Task --> LightUser : creator
    Task --> LightUser : affectedUser
    Message --> LightUser : sender
    Message --> LightUser : receiver

    User --> Kanban : creates
    LightUser --> Kanban : creates
    User --> LightKanban : joins
    LightUser --> LightKanban : accesses

    ModifyTask --> Task
    ModifyColumn --> Column
    ModifyKanban --> LightKanban
    CreateTask --> Task
    CreateColumn --> Column
    CreateMessage --> Message
    DeleteTask --> Task
    DeleteColumn --> Column
    MoveTask --> Task
    AddAccess --> LightKanban
    RemoveAccess --> LightKanban

    DataClient --> SnapshotInfo
    DataClient --> Kanban

    LightKanban -- Access
    LightUser -- Access
