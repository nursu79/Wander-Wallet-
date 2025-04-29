import { NextFunction, Request, Response } from "express";
import jwt from "jsonwebtoken"

const accessTokenSecret = process.env.ACCESS_TOKEN_SECRET || "default";

export const authenticateToken = (req: Request, res: Response, next: NextFunction) => {
    const authHeader = req.headers["authorization"]
    const token = authHeader && authHeader.split(" ")[1]

    if (!token) {
        return res.status(401).json({ message: "Token not found" })
    }

    jwt.verify(token, accessTokenSecret, (err, user) => {
        if (err) {
            return res.status(403).json({ message: "Token is not valid" })
        }
    
        next();
    });
}