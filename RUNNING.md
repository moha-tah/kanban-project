# Kanban Project - Client/Server Architecture

## Running the Application

### 1. Start the Server (Required First)

Run the standalone server in a separate terminal:

```powershell
mvn exec:java@server
```

Or with a custom port:

```powershell
mvn exec:java@server -Dexec.args="9090"
```

The server will display:

```
========================================
  Kanban Server Started
  Port: 8080
  Press Ctrl+C to stop
========================================
```

### 2. Start Client(s)

Once the server is running, launch one or more clients:

```powershell
# Default: connects to localhost:8080
mvn javafx:run

# Custom server host/port
mvn javafx:run -Dserver.host=192.168.1.100 -Dserver.port=9090
```

## Architecture

- **Server** (`server.ServerApp`): Standalone process handling all client connections
- **Client** (`client.MainApp`): JavaFX application connecting to the server
- **Multiple clients** can connect to the same server instance
- Server must be started before any clients

## Configuration

### Server

- Default port: `8080`
- Pass port as command-line argument: `-Dexec.args="9090"`

### Client

- Default: `127.0.0.1:8080`
- Override with system properties:
  - `-Dserver.host=<hostname>`
  - `-Dserver.port=<port>`

## Stopping

- **Server**: Press `Ctrl+C` in the server terminal
- **Client**: Close the JavaFX window

