import express from "express";
import { authenticateToken } from "../middleware/authenticate.js";
import TripController from "../controllers/TripController.js";
import upload from "../middleware/multer.js";

const tripRouter = express.Router()

tripRouter.post("/trips",
    (req, res, next) => {
        authenticateToken(req, res, next);
    },
    upload.single("tripImage"),
    (req, res) => {
        TripController.createTrip(req, res);
    }
);

tripRouter.get("/trips",
    (req, res, next) => {
        authenticateToken(req, res, next);
    },
    (req, res) => {
        TripController.getTrips(req, res);
    }
);

tripRouter.get("/trips/:id",
    (req, res, next) => {
        authenticateToken(req, res, next);
    },
    (req, res) => {
        TripController.getTrip(req, res);
    }
)


tripRouter.delete("/trips/:id",
    (req, res, next) => {
        authenticateToken(req, res, next);
    },
    (req, res) => {
        TripController.deleteTrip(req, res);
    }
)
export default tripRouter;