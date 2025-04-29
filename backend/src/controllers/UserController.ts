import { Request, Response } from "express";
import bcrypt from "bcrypt"
import prisma from "../dbClient.js";
import { generateAccessToken, generateRefreshToken, verifyAccessToken, verifyRefreshToken } from "../auth/jwt.js";

type JwtPayload = {
    email: string;
    username: string;
}

export default class UserController {
    static async createUser(req: Request, res: Response) {
        const { username, email, password } = req.body || {};

        if (!username || !email || !password) {
            return res.status(400).json({ 
                error: {
                    username: !username ? "Username is required" : undefined,
                    email: !email ? "Email is required" : undefined,
                    password: !password ? "Password is required" : undefined
                }
             });
        }

        const existingUser = await prisma.user.findUnique({
            where: {
                email: email
            }
        });

        if (existingUser) {
            return res.status(409).json({ 
                error: {
                    email: "User already exists"
                }
            });
        }

        const hashedPassword = await bcrypt.hash(password, 10);
        const newUser = await prisma.user.create({
            data: {
                username,
                email,
                password: hashedPassword
            }
        });

        const accessToken = generateAccessToken(newUser);
        const refreshToken = generateRefreshToken(newUser);
        const refreshTokenHashed = await bcrypt.hash(refreshToken, 10);

        await prisma.refreshToken.create({
            data: {
                token: refreshTokenHashed,
                user: {
                    connect: {
                        id: newUser.id
                    }
                }
            }
        });

        return res.status(201).json({
            payload: {
                user: await prisma.user.findUnique({
                    where: {
                        id: newUser.id
                    },
                    omit: {
                        password: true,
                    }
                }),
                accessToken,
                refreshToken
            }
        });
    }

    static async loginUser(req: Request, res: Response) {
        const {email, password} = req.body || {};
        if (!email || !password) {
            return res.status(400).json({ 
                error: {
                    email: !email ? "Email is required" : undefined,
                    password: !password ? "Password is required" : undefined
                }
             });
        }

        const user = await prisma.user.findUnique({
            where: {
                email
            }
        });

        if (!user) {
            return res.status(401).json({ 
                error: { 
                    message: "Invalid email or password" 
                } 
            });
        }

        const isPasswordValid = await bcrypt.compare(password, user.password);
        if (!isPasswordValid) {
            return res.status(401).json({ 
                error: { 
                    message: "Invalid email or password" 
                } 
            });
        }

        const accessToken = generateAccessToken(user);
        const refreshToken = generateRefreshToken(user);
        const refreshTokenHashed = await bcrypt.hash(refreshToken, 10);

        await prisma.refreshToken.create({
            data: {
                token: refreshTokenHashed,
                user: {
                    connect: {
                        id: user.id
                    }
                }
            }
        });

        return res.status(200).json({
            payload: {
                accessToken,
                refreshToken
            }
        });
    }

    static async refreshToken(req: Request, res: Response) {
        const { refreshToken } = req.body || {};

        if (!refreshToken) {
            return res.status(401).json({ 
                error: { 
                    message: "Refresh token is required" 
                } 
            });
        }

        const user = verifyRefreshToken(refreshToken) as JwtPayload;

        if (!user) {
            return res.status(401).json({ 
                error: { 
                    message: "Refresh token is expired or invalid" 
                } 
            });
        }

        const refreshTokenHashed = await bcrypt.hash(refreshToken, 10);
        const existingUser = await prisma.user.findUnique({
            where: {
                email: user.email
            }
        });
    
        if (!existingUser) {
            return res.status(401).json({ 
                error: { 
                    message: "Refresh token is expired or invalid" 
                } 
            });
        }
    
        const allUserTokens = await prisma.refreshToken.findMany({
            where: {
                userId: existingUser.id
            }
        });
        const existingToken = allUserTokens.find(async token => await bcrypt.compare(refreshTokenHashed, token.token));
        
        await prisma.refreshToken.delete({
            where: {
                token: existingToken?.token
            }
        });

        const newAccessToken = generateAccessToken(existingUser);
        const newRefreshToken = generateRefreshToken(existingUser);

        const newRefreshTokenHashed = await bcrypt.hash(newRefreshToken, 10);
        await prisma.refreshToken.create({
            data: {
                token: newRefreshTokenHashed,
                user: {
                    connect: {
                        id: existingUser.id
                    }
                }
            }
        });

        return res.status(200).json({
            payload: {
                accessToken: newAccessToken,
                refreshToken: newRefreshToken
            }
        });
    }

    static async getProfile(req: Request, res: Response) {
        const accessToken = req.headers["authorization"]?.split(" ")[1];
        if (!accessToken) {
            return res.status(401).json({ 
                error: { 
                    message: "Access token is required"
                }
            });
        }

        const user = verifyAccessToken(accessToken) as JwtPayload;
        if (!user) {
            return res.status(401).json({ 
                error: { 
                    message: "Access token is expired or invalid" 
                } 
            });
        }

        const existingUser = await prisma.user.findUnique({
            where: {
                email: user.email
            },
            include: {
                refreshTokens: true
            },
            omit: {
                password: true,
            }
        });

        if (!existingUser) {
            return res.status(401).json({ 
                error: { 
                    message: "Access token is expired or invalid" 
                } 
            });
        }

        return res.status(200).json({
            payload: {
                user: existingUser
            }
        });
    }
}
