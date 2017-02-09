var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

server.listen(8080, function(){
	console.log("server is running...");
});

io.on('connection', function(socket) {
	console.log("Player connected");
	socket.emit("SOCKET_ID", { id : socket.id});
	socket.broadcast.emit('NEW_PLAYER', {id : socket.id});
	socket.on('disconnect', function() {
		console.log("Player disconnected");
	});
});