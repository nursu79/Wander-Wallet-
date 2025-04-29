import jwt, { SignOptions } from "jsonwebtoken";
import prisma from "../dbClient";
import { PrismaClient, User } from "@prisma/client";

const accessTokenSecret = process.env.ACCESS_TOKEN_SECRET || "default";
const refreshTokenSecret = process.env.REFRESH_TOKEN_SECRET || "default";

export const generateAccessToken = (user: User) => {
    const options: SignOptions = {
        expiresIn: "15m"
    }

    return jwt.sign(user.email, accessTokenSecret, options)
}

export const generateRefreshToken = (user: User) => {
    const options: SignOptions = {
        expiresIn: '7d'
    }

    return jwt.sign(user.email, refreshTokenSecret, options);
}

export const verifyToken = (token: string) => {
    return jwt.verify(token, refreshTokenSecret);
}
