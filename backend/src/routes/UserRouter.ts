import express from "express";
import UserController from "../controllers/UserController.js";
import upload from "../middleware/multer.js";

const userRouter = express.Router();
userRouter.use(express.json())

userRouter.post("/register", 
    upload.single("avatar"),
    (req, res) => {
        UserController.createUser(req, res);
    }
);

userRouter.post("/login", (req, res) => {
    UserController.loginUser(req, res);
});

userRouter.post("/token", (req, res) => {
    UserController.refreshToken(req, res);
});

userRouter.get("/profile", (req, res) => {
    UserController.getProfile(req, res);
})

export default userRouter;
