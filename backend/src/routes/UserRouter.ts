import express from "express";
import UserController from "../controllers/UserController.js";

const userRouter = express.Router();
userRouter.use(express.json())

userRouter.post("/register", (req, res) => {
    console.log(req);
    UserController.createUser(req, res);
});

export default userRouter;
