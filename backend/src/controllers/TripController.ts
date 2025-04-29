import { Request, Response } from "express";
import { getUser } from "../utils/index.js";
import prisma from "../dbClient.js";
import fs from "fs";
import path from "path";

export default class TripController {
    static async createTrip(req: Request, res: Response) {
        const { name, destination, budget, startDate, endDate } = req.body || {};
        const tripImage = req.file?.filename || null;

        if (!name || !destination || !budget || !startDate || !endDate) {
            return res.status(400).json({
                error: {
                    name: !name ? "Name is required" : undefined,
                    destination: !destination ? "Destination is required" : undefined,
                    budget: !budget ? "Budget is required" : undefined,
                    startDate: !startDate ? "Start date is required" : undefined,
                    endDate: !endDate ? "End date is required" : undefined
                }
            });
        }
        const budgetNumber = parseFloat(budget);
        if (isNaN(budgetNumber) || budgetNumber <= 0) {
            return res.status(400).json({
                error: {
                    budget: "Budget must be a positive number"
                }
            });
        }
        const startDateDate = new Date(startDate);
        const endDateDate = new Date(endDate);

        if (isNaN(startDateDate.getTime())) {
            return res.status(400).json({
                error: {
                    startDate: "Start date is not a valid date"
                }
            });
        }
        if (isNaN(endDateDate.getTime())) {
            return res.status(400).json({
                error: {
                    endDate: "End date is not a valid date"
                }
            });
        }

        if (startDateDate > endDateDate) {
            return res.status(400).json({
                error: {
                    startDate: "Start date must be before end date",
                    endDate: "End date must be after start date"
                }
            });
        }

        const user = getUser(req);
 
        if (user) {
            const trip = await prisma.trip.create({
                data: {
                    name,
                    destination,
                    budget: budgetNumber,
                    startDate: startDateDate,
                    endDate: endDateDate,
                    user: {
                        connect: {
                            id: user.id
                        }
                    },
                    imgUrl: tripImage
                }
            });

            return res.status(201).json({
                payload: {
                    trip
                } 
            });
        }

        return res.status(500).json({
            error: {
                message: "An error occurred while creating the trip"
            }
        });
    }

    static async getTrips(req: Request, res: Response) {
        const user = getUser(req);
        if (user) {
            const trips = await prisma.trip.findMany({
                where: {
                    userId: user.id
                }
            });

            return res.status(200).json({
                payload: {
                    trips
                }
            });
        }

        return res.status(500).json({
            error: {
                message: "An error occurred while fetching the trips"
            }
        });
    }

    static async getTrip(req: Request, res: Response) {
        const user = getUser(req);
        const { id } = req.params || {};

        if (!id) {
            return res.status(400).json({
                error: {
                    message: "Trip ID is required"
                }
            });
        }

        const trip = await prisma.trip.findUnique({
            where: {
                id,
                userId: user?.id
            },
            include: {
                expenses: true
            }
        });

        if (!trip) {
            return res.status(404).json({
                error: {
                    message: "Trip not found"
                }
            });
        }

        return res.status(200).json({
            payload: {
                trip
            }
        });
    }

    static async deleteTrip(req: Request, res: Response) {
        const user = getUser(req);
        const { id } = req.params || {};

        if (!id) {
            return res.status(400).json({
                error: {
                    message: "Trip ID is required"
                }
            });
        }
    
        const trip = await prisma.trip.findUnique({
            where: {
                id,
                userId: user?.id
            }
        }); 
        if (!trip) {
            return res.status(404).json({
                error: {
                    message: "Trip not found"
                }
            });
        }
        
        try {
            const filePath = path.join("public", trip.imgUrl || "");
            fs.rm(path.join("public", trip.imgUrl || ""), {force: true}, (err) => {
                if (err) {
                    throw err;
                }
            })
            await prisma.trip.delete({
                where: {
                    id: trip.id
                }
            });

            return res.json({
                payload: {
                    message: "Trip deleted successfully"
                }
            })
        } catch (e) {
            return res.status(500).json({
                error: {
                    message: "An error occurred while deleting the trip"
                }
            });
        }
    }
}