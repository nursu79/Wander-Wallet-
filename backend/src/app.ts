import express, {Request, Response } from "express";
import cors from "cors";
import path from "path";
import { fileURLToPath } from "url";

import userRouter from "./routes/UserRouter.js";

const app = express();
app.use(express.json());
app.use(cors({
    origin: "*",
    methods: ["GET", "POST", "PUT", "DELETE"],
    allowedHeaders: ["Content-Type", "Authorization"],
    credentials: true
}));
app.use(express.urlencoded({ extended: true }));

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
app.use(express.static(path.join(__dirname, '../public')));

app.use(userRouter);

app.get("/", (req, res) => {
    res.json({ message: "Hello World!" });
})

const port = process.env.PORT || 3000;

app.listen(Number(port), "0.0.0.0", () => {
    console.log("Server is alive");
})
