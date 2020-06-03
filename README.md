# Support for volatile Websocket backends

Changes done in carbon-apimgt to support volatile Websocket backends. The API Gateway will manage Websocket clients
and will provide a REST endpoint to allow voaltile Websocket backends to communicate with Websocket clients.

## Files

WebsocketClientCallbackMediator.java - New mediator to handle REST communication from Websocket backends
_WebSocketClientService_.xml - Synapse REST API to be used by Websocket backends
WebsocketHandler.java - Existing class to support volatile Websocket backends
WebsocketInboundHandler.java - Existing class to support volatile Websocket backends



