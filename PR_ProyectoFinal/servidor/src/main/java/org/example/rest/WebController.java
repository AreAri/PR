package org.example.rest;

import static spark.Spark.*;

public class WebController {

    public static void start() {

        // ================= LOGIN =================
        get("/", (req, res) -> """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Login Admin</title>
                <style>
                    body {
                        font-family: Arial;
                        background: #f0f2f5;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                    }
                    .card {
                        background: white;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 4px 10px rgba(0,0,0,0.1);
                        width: 300px;
                    }
                    h2 { text-align: center; }
                    input, button {
                        width: 100%;
                        padding: 8px;
                        margin-top: 10px;
                    }
                    button {
                        background: #1976d2;
                        color: white;
                        border: none;
                        border-radius: 5px;
                        cursor: pointer;
                    }
                </style>
            </head>
            <body>
                <div class="card">
                    <h2>Login Admin</h2>
                    <form method="post" action="/login">
                        <input name="user" placeholder="Usuario">
                        <input type="password" name="pass" placeholder="Contraseña">
                        <button type="submit">Entrar</button>
                    </form>
                </div>
            </body>
            </html>
        """);

        post("/login", (req, res) -> {
            if ("admin".equals(req.queryParams("user"))
                    && "1234".equals(req.queryParams("pass"))) {
                res.redirect("/admin");
                return null;
            }
            return "Credenciales incorrectas";
        });

        // ================= PANEL ADMIN =================
        get("/admin", (req, res) -> """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Panel Admin</title>
<style>
    body {
        font-family: Arial;
        background: #f4f6f8;
        padding: 20px;
    }
    .panel {
        max-width: 700px;
        margin: auto;
        background: white;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 4px 10px rgba(0,0,0,0.1);
    }
    h2, h3 { text-align: center; }

    input, button {
        padding: 8px;
        margin-top: 5px;
    }

    button {
        background: #388e3c;
        color: white;
        border: none;
        border-radius: 5px;
        cursor: pointer;
    }

    .udpBtn {
        background: #f57c00;
    }

    ul {
        list-style: none;
        padding: 0;
        max-height: 250px;
        overflow-y: auto;
    }

    li {
        padding: 6px;
        margin-bottom: 4px;
        border-radius: 5px;
        background: #eeeeee;
    }

    .udp {
        background: #fff3cd;
        border-left: 5px solid #ff9800;
    }

    .system {
        background: #e3f2fd;
    }
</style>
</head>
<body>

<div class="panel">

<h2>Panel Administrador</h2>

<input id="msg" placeholder="Mensaje">
<br>
<button onclick="enviarChat()">Enviar Chat</button>
<button class="udpBtn" onclick="enviarUDP()">Enviar UDP</button>

<ul id="log"></ul>

<h3>Clientes Conectados</h3>
<ul id="clientes"></ul>

</div>

<script>
    const ws = new WebSocket("ws://localhost:8080/ws");

    ws.onopen = () => {
        ws.send("LOGIN:ADMIN");
    };

    ws.onmessage = (event) => {

        // ----- Lista de clientes -----
        if (event.data.startsWith("LISTA_CLIENTES:")) {
            document.getElementById("clientes").innerHTML = "";
            const lista = event.data.replace("LISTA_CLIENTES:", "").split(",");
            lista.forEach(c => {
                if (c.trim() !== "") {
                    const li = document.createElement("li");
                    li.innerText = c;
                    document.getElementById("clientes").appendChild(li);
                }
            });
            return;
        }

        const li = document.createElement("li");
        li.innerText = event.data;

        if (event.data.includes("UDP"))
            li.classList.add("udp");
        else if (event.data.includes("ONLINE") || event.data.includes("OFFLINE"))
            li.classList.add("system");

        document.getElementById("log").appendChild(li);
    };

    function enviarChat() {
        const msg = document.getElementById("msg").value;
        ws.send(msg);
        document.getElementById("msg").value = "";
    }

    function enviarUDP() {
        const msg = document.getElementById("msg").value;

        fetch("/udp", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: "msg=" + encodeURIComponent(msg)
        });

        document.getElementById("msg").value = "";
    }
</script>

</body>
</html>
        """);

        // ================= ENDPOINT UDP =================
        post("/udp", (req, res) -> {
            String mensaje = req.queryParams("msg");
            System.out.println("[ADMIN → UDP] " + mensaje);
            org.example.udp.UDPServer.enviarMensaje("ADMIN: " + mensaje);
            return "OK";
        });
    }
}
