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
                error: {
                    message: "Trip ID is required"
                }
            });
        }

        if (!name || !amount || isNaN(amountNumber) || amountNumber <= 0 || !category || !categoryList.includes((category as string).toUpperCase()) || !date || isNaN(dateDate.getTime())) {
            return res.status(400).json({ 
                error: {
                    name: !name ? "Name is required" : undefined,
                    amount: !amount ? "Amount is required" : ((isNaN(amountNumber) || amountNumber <= 0) ? "Amount must be a positive integer" : undefined),
                    category: !category ? "Category is required" : (!categoryList.includes((category as string).toUpperCase()) ? "Category is invalid" : undefined),
                    date: !date ? "Date is required" : (isNaN(dateDate.getTime()) ? "Date is invalid" : undefined)
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

        if (dateDate < trip.startDate || dateDate > trip.endDate) {
            return res.status(400).json({
                error: {
                    date: "Date must be within trip timeline"
                }
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

        return res.status(201).json({
            payload: {
                expense: newExpense
            }
        });
    }

    static async getTripExpenses(req: Request, res: Response) {
        const user = getUser(req);
        const { id } = req.params || {};

        if (!id) {
            res.status(400).json({
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
                expenses: trip.expenses
            }
        });
    }

    static async getExpense(req: Request, res: Response) {
        const user = getUser(req);
        const { id } = req.params || {};

        if (!id) {
            return res.status(400).json({
                error: {
                    id: "Expense ID is required"
                }
            })
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
                error: {
                    message: "Expense not found"
                }
            });
        }

        return res.status(200).json({
            payload: {
                expense
            }
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
                error: {
                    message: "Expense ID is required"
                }
            });
        }

        if (!name || !amount || isNaN(amountNumber) || amountNumber <= 0 || !category || !categoryList.includes(category) || !date || isNaN(dateDate.getTime())) {
            return res.status(400).json({ 
                error: {
                    name: !name ? "Name is required" : undefined,
                    amount: !amount ? "Amount is required" : ((isNaN(amountNumber) || amountNumber <= 0) ? "Amount must be a positive integer" : undefined),
                    category: !category ? "Category is required" : (!categoryList.includes(category) ? "Category is invalid" : undefined),
                    date: !date ? "Date is required" : (isNaN(dateDate.getTime()) ? "Date is invalid" : undefined)
                }
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
                error: {
                    message: "Couldn't find Expense"
                }
            });
        }

        if (dateDate < expense.trip.startDate || dateDate > expense.trip.endDate) {
            return res.status(400).json({
                error: {
                    date: "Date must be within trip timeline"
                }
            })
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
                    }
                });                
            }

            return res.status(200).json({
                payload: {
                    expense: updatedExpense
                }
            });
        } catch (e) {
            return res.status(404).json({
                error: {
                    message: "Couldn't find Expense"
                }
            })
        }
    }

    static async deleteExpense(req: Request, res: Response) {
        const user = getUser(req);
        const { id } = req.params || {};

        if (!id) {
            return res.status(400).json({
                error: {
                    id: "Expense ID is required"
                }
            })
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
                payload: {
                    message: "Expense was deleted successfully"
                }
            })
        } catch (e) {
            return res.status(404).json({
                error: {
                    message: "Expense not found"
                }
            });
        }
    }
}
