import express from "express";
import { authenticateToken } from "../middleware/authenticate.js";
import StatsController from "../controllers/StatsController.js";

const statsRouter = express.Router();
statsRouter.use(express.json())

statsRouter.get("/stats/totalSpending",
    (req, res, next) => {
        authenticateToken(req, res, next)
    },
    (req, res) => {
        StatsController.getTotalSpending(req, res);
    }
);

statsRouter.get("/stats/avgSpendingPerTrip",
    (req, res, next) => {
        authenticateToken(req, res, next)
    },
    (req, res) => {
        StatsController.getAverageSpendingPerTrip(req, res);
    }
);

statsRouter.get("/stats/avgSpendingPerDay",
    (req, res, next) => {
        authenticateToken(req, res, next)
    },
    (req, res) => {
        StatsController.getAverageSpendingPerDay(req, res);
    }
);

statsRouter.get("/stats/spendingByCategory",
    (req, res, next) => {
        authenticateToken(req, res, next)
    },
    (req, res) => {
        StatsController.getSpendingByCategory(req, res);
    }
)

statsRouter.get("/stats/spendingByMonth",
    (req, res, next) => {
        authenticateToken(req, res, next)
    },
    (req, res) => {
        StatsController.getMonthlySpending(req, res);
    }
)

statsRouter.get("/stats/budgetComparison",
    (req, res, next) => {
        authenticateToken(req, res, next)
    },
    (req, res) => {
        StatsController.getBudgetVsSpending(req, res);
    }
);

statsRouter.get("/stats/mostExpensiveTrip",
    (req, res, next) => {
        authenticateToken(req, res, next)
    },
    (req, res) => {
        StatsController.getMostExpensiveTrip(req, res);
    }
);

statsRouter.get("/stats/leastExpensiveTrip",
    (req, res, next) => {
        authenticateToken(req, res, next)
    },
    (req, res) => {
        StatsController.getLeastExpensiveTrip(req, res);
    }
);

export default statsRouter;
