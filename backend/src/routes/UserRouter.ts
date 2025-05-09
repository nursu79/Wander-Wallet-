import express from "express";
import UserController from "../controllers/UserController.js";
import upload from "../middleware/multer.js";
import { authenticateToken } from "../middleware/authenticate.js";

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

userRouter.get("/profile",
    (req, res, next) => {
        authenticateToken(req, res, next);
    },
    (req, res) => {
        UserController.getProfile(req, res);
    }
);

userRouter.put("/profile",
    (req, res, next) => {
        authenticateToken(req, res, next);
    },
    upload.single("avatar"),
    (req, res) => {
        UserController.updateProfile(req, res);
    }
);

userRouter.post("/logout",
    (req, res, next) => {
        authenticateToken(req, res, next);
    },
    (req, res) => {
        UserController.logoutUser(req, res);
    }
);

userRouter.get("/notifications",
    (req, res, next) => {
        authenticateToken(req, res, next);
    },
    (req, res) => {
        UserController.getNotifications(req, res);
    }
)

userRouter.delete("/notifications/:id",
    (req, res, next) => {
        authenticateToken(req, res, next);
    },
    (req, res) => {
        UserController.deleteNotification(req, res);
    }   
)

export default userRouter;
