const express = require('express'),
http = require('http'),
app = express(),
server = http.createServer(app),
io = require('socket.io')(server);

app.get('/', (req, res) => {
	res.send('Chat Server is running on port 3000')
});

server.listen(3000,()=>{
	console.log('Server is running on port 3000')
});

var arrNameUser = [];
var check = true;

io.on('connection', (socket) => {
	console.log('user connected')

	socket.on("client-send-data", (data) => {
		if(arrNameUser.indexOf(data) >= 0) {
			check = true;
			console.log("Tai khoan da ton tai: " + data)
		} else {
			arrNameUser.push(data);
			socket.un = data;
			check = false;
			console.log("Dang ki thanh cong: " +  data)
			io.sockets.emit("server-send-register", {username: arrNameUser})
		}
		socket.emit("server-send-data", { kiemtra: check } );
	});
	socket.on("client-send-message", (mess) => {
		io.sockets.emit("server-send-message", { message: socket.un + ": " + mess });
	});
});