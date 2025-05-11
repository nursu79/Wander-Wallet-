import { Request, Response } from "express";
import prisma from "../dbClient.js";
import { getUser } from "../utils/index.js";

export default class StatsController {
    static async getTotalSpending(req: Request, res: Response) {
        const user = getUser(req);
        const totalSpending = await prisma.expense.aggregate({
            where: {
                trip: {
                    userId: user?.id,
                }
            },
            _sum: {
                amount: true
            }
        });

        const totalBudget = await prisma.trip.aggregate({
            where: {
                userId: user?.id,
            },
            _sum: {
                budget: true
            }
        })

        return res.status(200).json({
            totalSpending: totalSpending._sum.amount || 0,
            totalBudget: totalBudget._sum.budget
        });
    }

    static async getAverageSpendingPerTrip(req: Request, res: Response) {
        const user = getUser(req);
        const currentDate = (new Date()).toISOString().split("T")[0]
        
        const avgSpending = await prisma.expense.aggregate({
            where: {
                trip: {
                    userId: user?.id,
                }
            },
            _avg: {
                amount: true
            }
        });

        return res.status(200).json({
            avgSpending: avgSpending._avg.amount
        })
    }

    static async getAverageSpendingPerDay(req: Request, res: Response) {
        const user = getUser(req);
        const allTrips = await prisma.trip.findMany({
            where: {
                userId: user?.id,
            },
            include: {
                expenses: true
            }
        });

        let totalSpendingAllTrips = 0;
        let totalDaysAllTrips = 0;

        for (const trip of allTrips) {
            for (const expense of trip.expenses) {
                totalSpendingAllTrips += expense.amount;
            }
            const duration = trip.endDate.getTime() - trip.startDate.getTime();
            const days = Math.ceil(duration / (1000 * 60 * 60 * 24)) + 1;
            totalDaysAllTrips += days;
        }

        return res.status(200).json({
            avgSpending: totalDaysAllTrips > 0 ? totalSpendingAllTrips / totalDaysAllTrips : 0
        });
    }

    static async getSpendingByCategory(req: Request, res: Response) {
        const user = getUser(req);
        const spendingByCategory = await prisma.expense.groupBy({
            by: ["category"],
            where: {
                trip: {
                    userId: user?.id,
                }
            },
            _sum: {
                amount: true
            },
            orderBy: {
                _sum: {
                    amount: "desc"
                }
            }
        });
        const categories = [];
        for (const categorySpending of spendingByCategory) {
            categories.push({
                category: categorySpending.category,
                amount: categorySpending._sum.amount || 0
            });
        }
        return res.json({
            categories
        });
    }

    static async getMonthlySpending(req: Request, res: Response) {
        const user = getUser(req);
        let currentMonth = new Date();
        currentMonth.setDate(1);
        currentMonth.setHours(0, 0, 0, 0);
        let nextMonth = new Date(currentMonth);
        nextMonth.setMonth(currentMonth.getMonth() + 1);

        const spendingByMonth = [];

        for (let i = 0; i < 5; i++) {
            const aggregateObject = await prisma.expense.aggregate({
                where: {
                    trip: {
                        userId: user?.id
                    },
                    date: {
                        gte: currentMonth,
                        lt: nextMonth
                    }
                },
                _sum: {
                    amount: true
                }
            });
            const currentMonthSpending = aggregateObject?._sum?.amount;

            spendingByMonth.push({
                [currentMonth.toLocaleString("default", { month: "short" })]: currentMonthSpending ? currentMonthSpending : 0
            });
            currentMonth.setMonth(currentMonth.getMonth() - 1)
            nextMonth.setMonth(nextMonth.getMonth() - 1)
        }

        return res.json({
            expensesByMonth: spendingByMonth
        });
    }

    static async getBudgetVsSpending(req: Request, res: Response) {
        const user = getUser(req);
        const allTrips = await prisma.trip.findMany({
            where: {
                userId: user?.id,
            },
            select: {
                id: true,
                budget: true,
                name: true,
                expenses: {
                    select: {
                        amount: true
                    }
                }
            }
        });

        const budgetComparison = allTrips.map((trip) => ({
            tripId: trip.id,
            name: trip.name,
            budget: trip.budget,
            expenditure: (trip.expenses.reduce((prev, curr) => ({ amount:  prev.amount + curr.amount}))).amount
        }));

        return res.status(200).json({
            budgetComparison: budgetComparison
        });
    }

    static async getMostExpensiveTrip(req: Request, res: Response) {
        const user = getUser(req);
        const tripExpenses = await prisma.expense.groupBy({
            by: ['tripId'],
            where: {
                trip: {
                    userId: user?.id
                }
            },
            _sum: {
                amount: true
            },
            orderBy: {
                _sum: {
                    amount: 'desc'
                }
            },
            take: 1
        });

        const mostExpensiveTripId = tripExpenses[0]?.tripId
        const mostExpensiveTrip = await prisma.trip.findUnique({
            where: {
                id: mostExpensiveTripId
            },
            select: {
                name: true,
                id: true,
                budget: true
            }
        });

        return res.status(200).json({
            trip: mostExpensiveTrip,
            totalExpenditure: tripExpenses[0]?._sum.amount || 0
        });
    }

    static async getLeastExpensiveTrip(req: Request, res: Response) {
        const user = getUser(req);
        const tripExpenses = await prisma.expense.groupBy({
            by: ['tripId'],
            where: {
                trip: {
                    userId: user?.id
                }
            },
            _sum: {
                amount: true
            },
            orderBy: {
                _sum: {
                    amount: 'asc'
                }
            },
            take: 1
        });

        const leastExpensiveTripId = tripExpenses[0]?.tripId
        const leastExpensiveTrip = await prisma.trip.findUnique({
            where: {
                id: leastExpensiveTripId
            },
            select: {
                name: true,
                id: true,
                budget: true
            }
        });

        return res.status(200).json({
            trip: leastExpensiveTrip,
            totalExpenditure: tripExpenses[0]?._sum.amount || 0
        });
    }
}