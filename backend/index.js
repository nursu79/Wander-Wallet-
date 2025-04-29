import express from "express";

const app = express();
app.use(express.json());

app.get("/", (req, res) => {
    res.json({ message: "Hello World!" });
})

const port = process.env.PORT || 3000;

app.listen(port, '0.0.0.0', () => {
    console.log("Server is running on port 3000");
})
