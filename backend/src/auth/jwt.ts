import jwt, { SignOptions } from "jsonwebtoken";
import prisma from "../dbClient";
import { PrismaClient, User } from "@prisma/client";

const accessTokenSecret = process.env.ACCESS_TOKEN_SECRET || "default";
const refreshTokenSecret = process.env.REFRESH_TOKEN_SECRET || "default";

export const generateAccessToken = (user: User) => {
    const payload = {
        username: user.id,
        email: user.email,
    }
    const options: SignOptions = {
        expiresIn: "15m"
    }

    return jwt.sign(payload, accessTokenSecret, options)
}

export const generateRefreshToken = (user: User) => {
    const payload = {
        id: user.id,
        email: user.email
    };
    const options: SignOptions = {
        expiresIn: '7d'
    }

    return jwt.sign(payload, refreshTokenSecret, options);
}

export const verifyAccessToken = (token: string) => {
    return jwt.verify(token, accessTokenSecret);
}

export const verifyRefreshToken = (token: string) => {
    return jwt.verify(token, refreshTokenSecret);
}
