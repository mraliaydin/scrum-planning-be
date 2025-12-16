# Scrum Poker (Planning Poker) Backend

This is a Spring Boot application for a Scrum Poker tool, designed for internal team use.

## Requirements

- Java 21
- Maven

## Setup

1.  **Run**:
    ```bash
    ./mvnw spring-boot:run
    ```
    The application uses an in-memory H2 database, so no external database setup is required.

## API Documentation

### Public Endpoints (`/api/rooms`)

- `POST /api/rooms` - Create a new room
  - Body: `{ "roomName": "Sprint 10", "username": "ScrumMaster" }`
- `GET /api/rooms/{roomId}` - Get room details
- `POST /api/rooms/{roomId}/join` - Join a room
  - Body: `{ "username": "Developer1" }`
- `DELETE /api/rooms/{roomId}/leave?userId={userId}` - Leave a room
- `POST /api/rooms/{roomId}/kick/{userId}` - Kick a user (Moderator only)
- `POST /api/rooms/{roomId}/tasks` - Create a new task
  - Body: `{ "name": "Login Page", "description": "Implement login" }`
- `GET /api/rooms/{roomId}/tasks/current` - Get the active task
- `POST /api/rooms/{roomId}/tasks/{taskId}/vote` - Vote on a task
  - Body: `{ "userId": "...", "username": "...", "point": "5" }`
- `POST /api/rooms/{roomId}/tasks/{taskId}/reveal` - Reveal votes
- `POST /api/rooms/{roomId}/tasks/{taskId}/revote` - Restart voting for the task
- `POST /api/rooms/{roomId}/tasks/{taskId}/complete` - Complete the task
  - Body: `"8"` (Final point as string)
- `GET /api/rooms/{roomId}/history` - Get task history

### Admin Endpoints (`/admin/rooms`)

- `GET /admin/rooms` - List all rooms
- `DELETE /admin/rooms/{roomId}` - Delete a room
- `POST /admin/rooms/{roomId}/kick/{userId}` - Kick a user
- `GET /admin/rooms/{roomId}/users` - List users in a room

### WebSocket

- Endpoint: `/ws`
- Subscribe to: `/topic/rooms/{roomId}`
- Events:
  - `USER_JOINED`: Payload `User`
  - `USER_LEFT`: Payload `userId`
  - `USER_KICKED`: Payload `userId`
  - `NEW_TASK`: Payload `Task`
  - `USER_VOTED`: Payload `userId`
  - `VOTES_REVEALED`: Payload `List<Vote>`
  - `REVOTE_STARTED`: Payload `taskId`
  - `TASK_COMPLETED`: Payload `Task`

## Architecture

- **Framework**: Spring Boot 3.x
- **Database**: H2 (In-Memory)
- **Real-time**: WebSocket (STOMP)
- **Structure**: Controller -> Service -> Repository (JPA)

## Notes

- Users are session-based and stored in the `room_users` table.
- No authentication is implemented as per requirements.
- Clean Code and KISS principles are followed.
