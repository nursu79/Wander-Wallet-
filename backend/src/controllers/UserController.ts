import { Request, Response } from "express";
import bcrypt from "bcrypt"
import prisma from "../dbClient.js";
import { generateAccessToken, generateRefreshToken, JwtPayload, verifyAccessToken, verifyRefreshToken } from "../auth/jwt.js";
import { getAccessToken, getUser } from "../utils/index.js";
import fs from "fs";
import path from "path";

export default class UserController {
    static async createUser(req: Request, res: Response) {
        const { username, email, password } = req.body || {};
        const avatarUrl = req.file?.filename || null;

        if (!username || !email || !password) {
            if (avatarUrl) {
                try {
                    fs.rm(path.join("public", "userAvatars", avatarUrl), (err) => {
                        if (err) {
                            return res.status(500).json({
                                message: "An unexpected error occured"
                            })
                        }
                    });
                } catch (e) {
                    return res.status(500).json({
                        message: "An unexpected error occured"
                    });
                }
            }
            return res.status(400).json({ 
                username: !username ? "Username is required" : undefined,
                email: !email ? "Email is required" : undefined,
                password: !password ? "Password is required" : undefined
             });
        }

        const existingUser = await prisma.user.findUnique({
            where: {
                email: email
            }
        });

        if (existingUser) {
            if (avatarUrl) {
                try {
                    fs.rm(path.join("public", "userAvatars", avatarUrl), (err) => {
                        if (err) {
                            return res.status(500).json({
                                message: "An unexpected error occured"
                            })
                        }
                    });
                } catch (e) {
                    return res.status(500).json({
                        message: "An unexpected error occured"
                    });
                }
            }
            return res.status(409).json({ 
                email: "User already exists"
            });
        }

        const hashedPassword = await bcrypt.hash(password, 10);
        const newUser = await prisma.user.create({
            data: {
                username,
                email,
                password: hashedPassword,
                avatarUrl
            }
        });

        const accessToken = generateAccessToken(newUser);
        const refreshToken = generateRefreshToken(newUser);

        await prisma.refreshToken.create({
            data: {
                token: refreshToken,
                user: {
                    connect: {
                        id: newUser.id
                    }
                }
            }
        });

        return res.status(201).json({
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
        });
    }

    static async loginUser(req: Request, res: Response) {
        const {email, password} = req.body || {};
        if (!email || !password) {
            return res.status(400).json({
                email: !email ? "Email is required" : undefined,
                password: !password ? "Password is required" : undefined
             });
        }

        const user = await prisma.user.findUnique({
            where: {
                email
            }
        });

        if (!user) {
            return res.status(401).json({
                message: "Invalid email or password" 
            });
        }

        const isPasswordValid = await bcrypt.compare(password, user.password);
        if (!isPasswordValid) {
            return res.status(401).json({
                message: "Invalid email or password" 
            });
        }

        const accessToken = generateAccessToken(user);
        const refreshToken = generateRefreshToken(user);

        await prisma.refreshToken.create({
            data: {
                token: refreshToken,
                user: {
                    connect: {
                        id: user.id
                    }
                }
            }
        });

        return res.status(200).json({
            accessToken,
            refreshToken
        });
    }

    static async refreshToken(req: Request, res: Response) {
        const { refreshToken } = req.body || {};

        if (!refreshToken) {
            return res.status(401).json({ 
                message: "Refresh token is required" 
            });
        }

        const user = verifyRefreshToken(refreshToken) as JwtPayload;

        if (!user) {
            return res.status(401).json({ 
                message: "Refresh token is expired or invalid" 
            });
        }

        const existingUser = await prisma.user.findUnique({
            where: {
                email: user.email
            }
        });
    
        if (!existingUser) {
            return res.status(401).json({ 
                message: "Refresh token is expired or invalid" 
            });
        }
    
        const allUserTokens = await prisma.refreshToken.findMany({
            where: {
                userId: existingUser.id
            }
        });
        const existingToken = allUserTokens.find((token) => token.token === refreshToken);
        try {
            await prisma.refreshToken.delete({
                where: {
                    token: existingToken?.token
                }
            });
        } catch (e) {
            return res.status(404).json({
                message: "Couldn't find Refresh Token"
            })
        }

        const newAccessToken = generateAccessToken(existingUser);
        const newRefreshToken = generateRefreshToken(existingUser);

        await prisma.refreshToken.create({
            data: {
                token: newRefreshToken,
                user: {
                    connect: {
                        id: existingUser.id
                    }
                }
            }
        });

        return res.status(200).json({
            accessToken: newAccessToken,
            refreshToken: newRefreshToken
        });
    }

    static async getProfile(req: Request, res: Response) {
        const user = getUser(req);

        if (!user) {
            return res.status(401).json({ 
                message: "Access token is expired or invalid" 
            });
        }

        const existingUser = await prisma.user.findUnique({
            where: {
                email: user.email
            },
            omit: {
                password: true,
            },
            include: {
                notifications: true
            }
        });

        if (!existingUser) {
            return res.status(401).json({ 
                message: "Access token is expired or invalid" 
            });
        }

        return res.status(200).json({
            user: existingUser
        });
    }

    static async updateProfile(req: Request, res: Response) {
        const user = getUser(req);
        const { username, email, newPassword, oldPassword } = req.body || {};

        const avatarUrl = req.file?.filename || null;

        if (!username || !email || !oldPassword) {
            if (avatarUrl) {
                try {
                    fs.rm(path.join("public", "userAvatars", avatarUrl), (err) => {
                        if (err) {
                            return res.status(500).json({
                                message: "An unexpected error occured"
                            })
                        }
                    });
                } catch (e) {
                    return res.status(500).json({
                        message: "An unexpected error occured"
                    });
                }
            }
            return res.status(400).json({ 
                username: !username ? "Username is required" : undefined,
                email: !email ? "Email is required" : undefined,
                message: !oldPassword ? "Your Password is required" : undefined
            });
        }

        const existingUser = await prisma.user.findUnique({
            where: {
                id: user?.id
            }
        });

        const isPasswordValid = await bcrypt.compare(oldPassword, existingUser?.password || "")

        if (!isPasswordValid) {
            if (avatarUrl) {
                try {
                    fs.rm(path.join("public", "userAvatars", avatarUrl), (err) => {
                        if (err) {
                            return res.status(500).json({
                                message: "An unexpected error occured"
                            })
                        }
                    });
                } catch (e) {
                    return res.status(500).json({
                        message: "An unexpected error occured"
                    });
                }
            }
            return res.status(400).json({
                message: "Password is incorrect"
            });
        }

        const emailInUse = await prisma.user.findUnique({
            where: {
                email: email
            }
        });

        if (emailInUse && (email !== existingUser?.email)) {
            if (avatarUrl) {
                try {
                    fs.rm(path.join("public", "userAvatars", avatarUrl), (err) => {
                        if (err) {
                            return res.status(500).json({
                                message: "An unexpected error occured"
                            })
                        }
                    });
                } catch (e) {
                    return res.status(500).json({
                        message: "An unexpected error occured"
                    });
                }
            }
            return res.status(400).json({
                email: "This email is already registered"
            });
        }

        const oldAvatarUrl = existingUser?.avatarUrl

        try {
            const updatedUser = await prisma.user.update({
                where: {
                    id: existingUser?.id
                },
                omit: {
                    password: true
                },
                data: {
                    username: username,
                    email: email,
                    password: newPassword ? (await bcrypt.hash(newPassword, 10)) : existingUser?.password,
                    avatarUrl: avatarUrl || existingUser?.avatarUrl
                }
            });

            if (avatarUrl && oldAvatarUrl) {
                fs.rm(path.join("public", "userAvatars", oldAvatarUrl), { force: true }, (err) => {
                    if (err) {
                        throw err;
                    }
                });
            }

            return res.status(200).json({
                user: updatedUser
            });
        } catch (e) {
            if (avatarUrl) {
                try {
                    fs.rm(path.join("public", "userAvatars", avatarUrl), (err) => {
                        if (err) {
                            return res.status(500).json({
                                message: "An unknown error occured"
                            });
                        }
                    });
                } catch (e) {
                    return res.status(500).json({
                        message: "An unexpected error occured"
                    });
                }
            }
            return res.status(500).json({
                message: "An unknown error occured"
            });
        }
    }

    static async logoutUser(req: Request, res: Response) {
        const user = getUser(req);
        const { refreshToken } = req.body || {};
        const accessToken = getAccessToken(req);

        if (!refreshToken || !accessToken) {
            return res.status(400).json({
                accessToken: !accessToken ? "Access token is required" : undefined,
                refreshToken: !refreshToken ? "Refresh token is required" : undefined
            });
        }

        const allRefreshTokens = await prisma.refreshToken.findMany({
            where: {
                userId: user?.id
            }
        });

        const matchingToken = allRefreshTokens.find((token) => token.token === refreshToken);

        try {
            await prisma.refreshToken.delete({
                where: {
                    id: matchingToken?.id
                }
            });
        } catch (e) {
            return res.status(404).json({
                message: "Couldn't find Refresh Token"
            });
        }

        await prisma.blackListToken.create({
            data: {
                accessToken: accessToken
            }
        });

        return res.status(200).json({
            message: "User logged out successfully"
        });
    }

    static async getNotifications(req: Request, res: Response) {
        const user = getUser(req);

        const notifications = await prisma.notification.findMany({
            where: {
                userId: user?.id
            },
            include: {
                trip: true
            }
        });

        return res.status(200).json({
            notifications
        });
    }

    static async deleteNotification(req: Request, res: Response) {
        const user = getUser(req);
        const { id } = req.params;

        try {
            await prisma.notification.delete({
                where: {
                    id: id,
                    userId: user?.id
                }
            });

            return res.status(200).json({
                message: "Notification was deleted successfully"
            })
        } catch (e) {
            return res.status(404).json({
                message: "Notification was not found"
            });
        }
    }
}
