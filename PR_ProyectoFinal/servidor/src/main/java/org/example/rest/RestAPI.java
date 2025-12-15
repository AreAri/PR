package org.example.rest;

import static spark.Spark.*;

public class RestAPI {

    public static void start() {

        port(8080);

        get("/usuarios", (req, res) -> {
            return "GET usuarios";
        });

        post("/usuarios", (req, res) -> {
            return "POST usuario";
        });

        put("/usuarios/:id", (req, res) -> {
            return "PUT usuario " + req.params(":id");
        });

        delete("/usuarios/:id", (req, res) -> {
            return "DELETE usuario " + req.params(":id");
        });
    }
}
