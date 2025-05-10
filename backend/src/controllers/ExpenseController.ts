import { Request, Response } from "express";
import { getUser } from "../utils/index.js";
import prisma from "../dbClient.js";

const categoryList = ["FOOD", "TRANSPORTATION", "ACCOMMODATION", "EXTERTAINMENT", "SHOPPING", "OTHER"]

export default class ExpenseController {
    static async createExpense(req: Request, res: Response) {
        const user = getUser(req);
        const { id } = req.params || {};
        const { name, amount, category, date, notes } = req.body || {};
        const dateDate = new Date(date)
        const amountNumber = parseFloat(amount);

        if (!id) {
            return res.status(400).json({
                message: "Trip ID is required"
            });
        }

        if (!name || !amount || isNaN(amountNumber) || amountNumber <= 0 || !category || !categoryList.includes((category as string).toUpperCase()) || !date || isNaN(dateDate.getTime())) {
            return res.status(400).json({ 
                name: !name ? "Name is required" : undefined,
                amount: !amount ? "Amount is required" : ((isNaN(amountNumber) || amountNumber <= 0) ? "Amount must be a positive integer" : undefined),
                category: !category ? "Category is required" : (!categoryList.includes((category as string).toUpperCase()) ? "Category is invalid" : undefined),
                date: !date ? "Date is required" : (isNaN(dateDate.getTime()) ? "Date is invalid" : undefined)
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

        if (dateDate < trip.startDate || dateDate > trip.endDate) {
            return res.status(400).json({
                date: "Date must be within trip timeline"
            })
        }

        const newExpense = await prisma.expense.create({
            data: {
                name,
                amount: amountNumber,
                category: category,
                date: dateDate,
                tripId: trip.id,
                notes: notes
            }
        });

        const tripExpenses = await prisma.expense.aggregate({
            where: {
                tripId: trip.id
            },
            _sum: {
                amount: true
            }
        });

        if (user && tripExpenses._sum.amount && (tripExpenses._sum.amount > trip.budget)) {
            await prisma.notification.create({
                data: {
                    userId: user.id,
                    tripId: trip.id,
                    surplus: (tripExpenses._sum.amount - trip.budget)
                }
            });
        }

        return res.status(201).json({
            expense: newExpense
        });
    }

    static async getTripExpenses(req: Request, res: Response) {
        const user = getUser(req);
        const { id } = req.params || {};

        if (!id) {
            res.status(400).json({
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

        return res.status(200).json({
            expenses: trip.expenses
        });
    }

    static async getExpense(req: Request, res: Response) {
        const user = getUser(req);
        const { id } = req.params || {};

        if (!id) {
            return res.status(400).json({
                message: "Expense ID is required"
            });
        }

        const expense = await prisma.expense.findUnique({
            where: {
                id,
                trip: {
                    userId: user?.id
                }
            }
        });

        if (!expense) {
            return res.status(404).json({
                message: "Expense not found"
            });
        }

        return res.status(200).json({
            expense
        });
    }

    static async updateExpense(req: Request, res: Response) {
        const user = getUser(req);
        const { id } = req.params || {};
        const { name, amount, category, date, notes } = req.body || {};

        const dateDate = new Date(date)
        const amountNumber = parseFloat(amount);

        if (!id) {
            return res.status(400).json({
                message: "Expense ID is required"
            });
        }

        if (!name || !amount || isNaN(amountNumber) || amountNumber <= 0 || !category || !categoryList.includes(category) || !date || isNaN(dateDate.getTime())) {
            return res.status(400).json({ 
                name: !name ? "Name is required" : undefined,
                amount: !amount ? "Amount is required" : ((isNaN(amountNumber) || amountNumber <= 0) ? "Amount must be a positive integer" : undefined),
                category: !category ? "Category is required" : (!categoryList.includes(category) ? "Category is invalid" : undefined),
                date: !date ? "Date is required" : (isNaN(dateDate.getTime()) ? "Date is invalid" : undefined)
             });
        }

        const expense = await prisma.expense.findUnique({
            where: {
                id,
                trip: {
                    userId: user?.id
                }
            },
            include: {
                trip: true
            }
        });

        if (!expense) {
            return res.status(404).json({
                message: "Couldn't find Expense"
            });
        }

        if (dateDate < expense.trip.startDate || dateDate > expense.trip.endDate) {
            return res.status(400).json({
                date: "Date must be within trip timeline"
            });
        }
        try {
            let updatedExpense;
            if (notes) {
                updatedExpense = await prisma.expense.update({
                    where: {
                        id,
                        trip: {
                            userId: user?.id
                        }
                    },
                    data: {
                        name,
                        amount: amountNumber,
                        category: category,
                        date: dateDate,
                        notes: notes
                    },
                    include: {
                        trip: true
                    }
                });
            } else {
                updatedExpense = await prisma.expense.update({
                    where: {
                        id,
                        trip: {
                            userId: user?.id
                        }
                    },
                    data: {
                        name,
                        amount: amountNumber,
                        category: category,
                        date: dateDate,
                    },
                    include: {
                        trip: true
                    }
                });                
            }
            const tripExpenses = await prisma.expense.aggregate({
                where: {
                    tripId: updatedExpense.tripId
                },
                _sum: {
                    amount: true
                }
            });
    
            if (user && tripExpenses._sum.amount) {
                if (tripExpenses._sum.amount > updatedExpense.trip.budget) {
                    await prisma.notification.create({
                        data: {
                            userId: user.id,
                            tripId: updatedExpense.trip.id,
                            surplus: (tripExpenses._sum.amount - updatedExpense.trip.budget)
                        }
                    });
                } else {
                    await prisma.notification.deleteMany({
                        where: {
                            userId: user.id,
                            tripId: updatedExpense.trip.id
                        }
                    });
                }
            }

            return res.status(200).json({
                expense: updatedExpense
            });
        } catch (e) {
            return res.status(404).json({
                message: "Couldn't find Expense"
            })
        }
    }

    static async deleteExpense(req: Request, res: Response) {
        const user = getUser(req);
        const { id } = req.params || {};

        if (!id) {
            return res.status(400).json({
                    message: "Expense ID is required"
            });
        }

        try {
            await prisma.expense.delete({
                where: {
                    id,
                    trip: {
                        userId: user?.id
                    }
                }
            });

            return res.status(200).json({
                message: "Expense was deleted successfully"
            })
        } catch (e) {
            return res.status(404).json({
                message: "Expense not found"
            });
        }
    }
}
