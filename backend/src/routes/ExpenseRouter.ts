import express from "express";
import { authenticateToken } from "../middleware/authenticate.js";
import ExpenseController from "../controllers/ExpenseController.js";

const expenseRouter = express.Router();
expenseRouter.use(express.json())

expenseRouter.post("/trips/:id/expenses",
    (req, res, next) => {
        authenticateToken(req, res, next);
    },
    (req, res) => {
        ExpenseController.createExpense(req, res)
    }
);

expenseRouter.get("/trips/:id/expenses",
    (req, res, next) => {
        authenticateToken(req, res, next);
    },
    (req, res) => {
        ExpenseController.getTripExpenses(req, res);
    }
);

expenseRouter.get("/expenses/:id",
    (req, res, next) => {
        authenticateToken(req, res, next);
    },
    (req, res) => {
        ExpenseController.getExpense(req, res);
    }
);

expenseRouter.put("/expenses/:id",
    (req, res, next) => {
        authenticateToken(req, res, next);
    },
    (req, res) => {
        ExpenseController.updateExpense(req, res);
    }
);

expenseRouter.delete("/expenses/:id",
    (req, res, next) => {
        authenticateToken(req, res, next);
    },
    (req, res) => {
        ExpenseController.deleteExpense(req, res);
    }
);

export default expenseRouter;
