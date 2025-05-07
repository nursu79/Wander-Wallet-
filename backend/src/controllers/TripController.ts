import { Request, Response } from "express";
import { getUser } from "../utils/index.js";
import prisma from "../dbClient.js";
import fs from "fs";
import path from "path";

export default class TripController {
    static async createTrip(req: Request, res: Response) {
        const { name, destination, budget, startDate, endDate } = req.body || {};
        const tripImage = req.file?.filename || null;
        const budgetNumber = parseFloat(budget);
        const startDateDate = new Date(startDate);
        const endDateDate = new Date(endDate);

        if (!name || !destination || !budget || isNaN(budgetNumber) || budgetNumber <= 0 || !startDate || isNaN(startDateDate.getTime()) || !endDate || isNaN(endDateDate.getTime()) || startDateDate > endDateDate) {
            if (tripImage) {
                try {
                    fs.rm(path.join("public", "tripImages", tripImage), (err) => {
                        if (err) {
                            throw err;
                        }
                    });
                } catch (e) {
                    return res.status(500).json({
                        message: "An unexpected error occured"
                    });
                }
            }
            return res.status(400).json({
                name: !name ? "Name is required" : undefined,
                destination: !destination ? "Destination is required" : undefined,
                budget: !budget ? "Budget is required" : (isNaN(budgetNumber) || budgetNumber <= 0 ? "Budget must be a positive number" : undefined),
                startDate: !startDate ? "Start date is required" : (isNaN(startDateDate.getTime()) ? "Start date is not a valid date" : (startDateDate > endDateDate ? "Start date must be before end date" : undefined)),
                endDate: !endDate ? "End date is required" : (isNaN(endDateDate.getTime()) ? "End date is not a valid date" : (startDateDate > endDateDate ? "End date must be after start date" : undefined)),
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
                trip
            });
        }

        if (tripImage) {
            try {
                fs.rm(path.join("public", "tripImages", tripImage), (err) => {
                    if (err) {
                        throw err;
                    }
                });
            } catch (e) {
                return res.status(500).json({
                    message: "An unexpected error occured"
                });
            }
        }
        return res.status(500).json({
            message: "An error occurred while creating the trip"
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
                trips
            });
        }

        return res.status(500).json({
            message: "An error occurred while fetching the trips"
        });
    }

    static async getPendingTrips(req: Request, res: Response) {
        const user = getUser(req);
        if (user) {
            const trips = await prisma.trip.findMany({
                where: {
                    userId: user.id,
                    startDate: {
                        gt: new Date()
                    }
                }
            });

            return res.status(200).json({
                trips
            });
        }

        return res.status(500).json({
            message: "An error occurred while fetching the trips"
        });
    }

    static async getCurrentTrips(req: Request, res: Response) {
        const user = getUser(req);
        if (user) {
            const trips = await prisma.trip.findMany({
                where: {
                    userId: user.id,
                    startDate: {
                        lte: new Date()
                    },
                    endDate: {
                        gt: new Date()
                    }
                }
            });

            return res.status(200).json({
                trips
            });
        }

        return res.status(500).json({
            message: "An error occurred while fetching the trips"
        });
    }

    static async getPastTrips(req: Request, res: Response) {
        const user = getUser(req);
        if (user) {
            const trips = await prisma.trip.findMany({
                where: {
                    userId: user.id,
                    endDate: {
                        lt: new Date()
                    }
                }
            });

            return res.status(200).json({
                trips
            });
        }

        return res.status(500).json({
            message: "An error occurred while fetching the trips"
        });
    }

    static async getTrip(req: Request, res: Response) {
        const user = getUser(req);
        const { id } = req.params || {};

        if (!id) {
            return res.status(400).json({
                message: "Trip ID is required"
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
                message: "Trip not found"
            });
        }

        const totalExpenditure = await prisma.expense.aggregate({
            where: {
                tripId: trip.id
            },
            _sum: {
                amount: true
            }
        });

        const expensesByCategory = await prisma.expense.groupBy({
            by: ["category"],
            where: {
                tripId: trip.id
            },
            _sum: {
                amount: true
            }
        });

        return res.status(200).json({
            trip,
            totalExpenditure: totalExpenditure._sum.amount,
            expensesByCategory
        });
    }

    static async deleteTrip(req: Request, res: Response) {
        const user = getUser(req);
        const { id } = req.params || {};

        if (!id) {
            return res.status(400).json({
                message: "Trip ID is required"
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
                message: "Trip not found"
            });
        }
        
        try {
            const tripImgUrl = trip.imgUrl;

            await prisma.trip.delete({
                where: {
                    id: trip.id
                }
            });

            if (tripImgUrl) {
                fs.rm(path.join("public", "tripImages", tripImgUrl), {force: true}, (err) => {
                    if (err) {
                        throw err;
                    }
                });
            }

            return res.json({
                message: "Trip deleted successfully"
            });
        } catch (e) {
            return res.status(500).json({
                message: "An error occurred while deleting the trip"
            });
        }
    }

    static async updateTrip(req: Request, res: Response) {
        const user = getUser(req);
        const { id } = req.params || {};
        const { name, destination, budget, startDate, endDate } = req.body || {};
        const tripImage = req.file?.filename;
        const budgetNumber = parseFloat(budget);
        const startDateDate = new Date(startDate);
        const endDateDate = new Date(endDate);

        if (!name || !destination || !budget || isNaN(budgetNumber) || budgetNumber <= 0 || !startDate || isNaN(startDateDate.getTime()) || !endDate || isNaN(endDateDate.getTime()) || startDateDate > endDateDate) {
            if (tripImage) {
                try {
                    fs.rm(path.join("public", "tripImages", tripImage), (err) => {
                        if (err) {
                            throw err;
                        }
                    });
                } catch (e) {
                    return res.status(500).json({
                        message: "An unexpected error occured"
                    });
                }
            }
            return res.status(400).json({
                name: !name ? "Name is required" : undefined,
                destination: !destination ? "Destination is required" : undefined,
                budget: !budget ? "Budget is required" : (isNaN(budgetNumber) || budgetNumber <= 0 ? "Budget must be a positive number" : undefined),
                startDate: !startDate ? "Start date is required" : (isNaN(startDateDate.getTime()) ? "Start date is not a valid date" : (startDateDate > endDateDate ? "Start date must be before end date" : undefined)),
                endDate: !endDate ? "End date is required" : (isNaN(endDateDate.getTime()) ? "End date is not a valid date" : (startDateDate > endDateDate ? "End date must be after start date" : undefined)),
            });
        }

        if (user) {
            const existingTrip = await prisma.trip.findUnique({
                where: {
                    id,
                    userId: user.id
                }
            });
            const oldImageUrl = existingTrip?.imgUrl;
        
            try {
                const updatedTrip = await prisma.trip.update({
                    where: {
                        id,
                        userId: user.id
                    },
                    data: {
                        name,
                        destination,
                        budget: budgetNumber,
                        startDate: startDateDate,
                        endDate: endDateDate,
                        imgUrl: tripImage || existingTrip?.imgUrl
                    }
                });

                if (tripImage && oldImageUrl) {
                    fs.rm(path.join("public", "tripImages", oldImageUrl), {force: true}, (err) => {
                        if (err) {
                            throw err;
                        }
                    });
                }

                return res.status(200).json({
                    trip: updatedTrip
                });
            }
            catch (e) {
                if (tripImage) {
                    try {
                        fs.rm(path.join("public", "tripImages", tripImage), (err) => {
                            if (err) {
                                throw err;
                            }
                        });
                    } catch (e) {
                        return res.status(500).json({
                            message: "An unexpected error occured"
                        })
                    }
                }
                return res.status(400).json({
                    message: "Couldn't find trip"
                });
            }
        }
    }
}