import { NextFunction, Request, Response } from "express";
import jwt from "jsonwebtoken"
import prisma from "../dbClient.js";
import { getAccessToken } from "../utils/index.js";

const accessTokenSecret = process.env.ACCESS_TOKEN_SECRET || "default";

export const authenticateToken = (req: Request, res: Response, next: NextFunction) => {
    const token = getAccessToken(req);

    if (!token) {
        return res.status(401).json({ message: "Token not found" })
    }

    jwt.verify(token, accessTokenSecret, async (err, user) => {
        if (err) {
            return res.status(403).json({ message: "Token is not valid" })
        }
    
        const accessTokenBlackListed = await prisma.blackListToken.findFirst({
            where: {
                accessToken: token
            }
        });

        if (accessTokenBlackListed) {
            return res.status(403).json({ message: "Token is not valid" })
        }
        next();
    });
}