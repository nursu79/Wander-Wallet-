import jwt, { SignOptions } from "jsonwebtoken";
import { User } from "@prisma/client";

export type JwtPayload = {
    id: string;
    email: string;
}

const accessTokenSecret = process.env.ACCESS_TOKEN_SECRET || "default";
const refreshTokenSecret = process.env.REFRESH_TOKEN_SECRET || "default";

export const generateAccessToken = (user: User) => {
    const payload: JwtPayload = {
        id: user.id,
        email: user.email,
    }
    const options: SignOptions = {
        expiresIn: "2h"
    }

    return jwt.sign(payload, accessTokenSecret, options)
}

export const generateRefreshToken = (user: User) => {
    const payload: JwtPayload = {
        id: user.id,
        email: user.email
    };
    const options: SignOptions = {
        expiresIn: '7d'
    }

    return jwt.sign(payload, refreshTokenSecret, options);
}

export const verifyAccessToken = (token: string) => {
    try {
        return jwt.verify(token, accessTokenSecret) as JwtPayload;
    } catch (e) {
        if (e instanceof jwt.JsonWebTokenError) {
            return null;
        }
        throw e;
    }
}

export const verifyRefreshToken = (token: string) => {
    try {
        return jwt.verify(token, refreshTokenSecret) as JwtPayload;
    } catch (e) {
        if (e instanceof jwt.JsonWebTokenError) {
            return null;
        }
        throw e;
    }
}
