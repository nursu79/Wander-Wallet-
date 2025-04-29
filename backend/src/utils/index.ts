import { Request } from "express";
import { JwtPayload, verifyAccessToken } from "../auth/jwt.js";

export const getUser = (req: Request) => {
    const authHeader = req.headers["authorization"];
    const token = authHeader && authHeader.split(" ")[1];

    if (!token) {
        return null;
    }

    const user = verifyAccessToken(token) as JwtPayload;
    return user;
}