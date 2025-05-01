import express, {Request, Response } from "express";
import cors from "cors";
import path from "path";
import { fileURLToPath } from "url";

import userRouter from "./routes/UserRouter.js";
import tripRouter from "./routes/TripRouter.js";
import prisma from "./dbClient.js";
import expenseRouter from "./routes/ExpenseRouter.js";
import statsRouter from "./routes/StatsRouter.js";

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
app.use(tripRouter);
app.use(expenseRouter);
app.use(statsRouter);

app.get("/", (req, res) => {
    res.json({ message: "Hello World!" });
})

app.get("/deleteAll", async (req, res) => {
    await prisma.expense.deleteMany({});
    await prisma.refreshToken.deleteMany({});
    await prisma.blackListToken.deleteMany({})
    await prisma.trip.deleteMany({});
    await prisma.user.deleteMany({});

    res.json({ message: "All data deleted" });
})

const port = process.env.PORT || 3000;

app.listen(Number(port), "0.0.0.0", () => {
    console.log("Server is alive");
})
